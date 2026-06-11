package com.wolflang.database;

import com.wolflang.WolfLangPlugin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MySQLProvider implements DatabaseProvider {

    private final WolfLangPlugin plugin;
    private HikariDataSource dataSource;

    public MySQLProvider(WolfLangPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void connect() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format(
            "jdbc:mysql://%s:%d/%s",
            plugin.getConfig().getString("database.host"),
            plugin.getConfig().getInt("database.port"),
            plugin.getConfig().getString("database.database")
        ));
        config.setUsername(plugin.getConfig().getString("database.username"));
        config.setPassword(plugin.getConfig().getString("database.password"));
        config.setMaximumPoolSize(plugin.getConfig().getInt("database.pool-size", 10));
        config.setMinimumIdle(2);
        config.setConnectionTimeout(5000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(600000);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS wolf_languages (
                player_uuid VARCHAR(36) PRIMARY KEY,
                language VARCHAR(10) NOT NULL DEFAULT 'en',
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
        """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create table: " + e.getMessage());
        }
    }

    @Override
    public void saveLanguage(UUID playerId, String language) {
        String sql = """
            INSERT INTO wolf_languages (player_uuid, language)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE language = VALUES(language)
        """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerId.toString());
            stmt.setString(2, language);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to save language: " + e.getMessage());
        }
    }

    @Override
    public Map<UUID, String> loadAllLanguages() {
        Map<UUID, String> result = new HashMap<>();
        String sql = "SELECT player_uuid, language FROM wolf_languages";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                String lang = rs.getString("language");
                result.put(uuid, lang);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load languages: " + e.getMessage());
        }
        return result;
    }
}
