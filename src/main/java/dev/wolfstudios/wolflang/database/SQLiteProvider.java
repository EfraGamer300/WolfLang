package dev.wolfstudios.wolflang.database;

import dev.wolfstudios.wolflang.WolfLangPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SQLiteProvider implements DatabaseProvider {

    private final WolfLangPlugin plugin;
    private Connection connection;

    public SQLiteProvider(WolfLangPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void connect() {
        try {
            File dbFile = new File(plugin.getDataFolder(), "database.db");
            plugin.getDataFolder().mkdirs();
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to connect to SQLite: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to close SQLite: " + e.getMessage());
        }
    }

    @Override
    public void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS wolf_languages (
                player_uuid TEXT PRIMARY KEY,
                language TEXT NOT NULL DEFAULT 'en',
                updated_at TEXT DEFAULT CURRENT_TIMESTAMP
            )
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
            ON CONFLICT(player_uuid) DO UPDATE SET language = excluded.language
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
        try (PreparedStatement stmt = connection.prepareStatement(sql);
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
