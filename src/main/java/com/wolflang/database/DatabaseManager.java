package com.wolflang.database;

import com.wolflang.WolfLangPlugin;

import java.util.Map;
import java.util.UUID;

public class DatabaseManager implements DatabaseProvider {

    private final DatabaseProvider provider;

    public DatabaseManager(WolfLangPlugin plugin) {
        String type = plugin.getConfig().getString("database.type", "sqlite");
        this.provider = switch (type.toLowerCase()) {
            case "mysql" -> new MySQLProvider(plugin);
            default -> new SQLiteProvider(plugin);
        };
    }

    @Override
    public void connect() {
        provider.connect();
    }

    @Override
    public void disconnect() {
        provider.disconnect();
    }

    @Override
    public void createTable() {
        provider.createTable();
    }

    @Override
    public void saveLanguage(UUID playerId, String language) {
        provider.saveLanguage(playerId, language);
    }

    @Override
    public Map<UUID, String> loadAllLanguages() {
        return provider.loadAllLanguages();
    }
}
