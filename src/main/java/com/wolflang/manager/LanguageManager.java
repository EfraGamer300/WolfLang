package com.wolflang.manager;

import com.wolflang.WolfLangPlugin;
import com.wolflang.database.DatabaseManager;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LanguageManager {

    private final WolfLangPlugin plugin;
    private final DatabaseManager databaseManager;
    private final Map<UUID, String> cache = new ConcurrentHashMap<>();

    public LanguageManager(WolfLangPlugin plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }

    public void loadAllFromDatabase() {
        Map<UUID, String> dbData = databaseManager.loadAllLanguages();
        cache.putAll(dbData);
        plugin.getLogger().info("Loaded " + dbData.size() + " player languages from database.");
    }

    public String getLanguage(Player player) {
        return cache.getOrDefault(player.getUniqueId(), getDefaultLanguage());
    }

    public void setLanguage(Player player, String language) {
        UUID uuid = player.getUniqueId();
        cache.put(uuid, language);
        databaseManager.saveLanguage(uuid, language);
    }

    public void removePlayer(UUID playerId) {
        cache.remove(playerId);
    }

    public String getLanguageName(String code) {
        return getSupportedLanguages().getOrDefault(code, code);
    }

    public String getDefaultLanguage() {
        return plugin.getConfig().getString("languages.default", "en");
    }

    public Map<String, String> getSupportedLanguages() {
        Map<String, String> languages = new HashMap<>();
        var section = plugin.getConfig().getConfigurationSection("languages.supported");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                languages.put(key, section.getString(key));
            }
        }
        return languages;
    }

}
