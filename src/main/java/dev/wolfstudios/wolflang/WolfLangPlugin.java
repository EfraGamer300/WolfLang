package dev.wolfstudios.wolflang;

import dev.wolfstudios.wolflang.api.WolfLangAPI;
import dev.wolfstudios.wolflang.api.WolfLangAPIImpl;
import dev.wolfstudios.wolflang.command.LanguageCommand;
import dev.wolfstudios.wolflang.command.ReloadCommand;
import dev.wolfstudios.wolflang.database.DatabaseManager;
import dev.wolfstudios.wolflang.listener.PlayerJoinListener;
import dev.wolfstudios.wolflang.manager.LanguageManager;
import dev.wolfstudios.wolflang.placeholder.LangExpansion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WolfLangPlugin extends JavaPlugin {

    private DatabaseManager databaseManager;
    private LanguageManager languageManager;
    private WolfLangAPIImpl api;
    private final List<LangExpansion> expansions = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        this.databaseManager = new DatabaseManager(this);
        this.languageManager = new LanguageManager(this, databaseManager);

        databaseManager.connect();
        databaseManager.createTable();

        languageManager.loadAllFromDatabase();

        // Initialize API
        this.api = new WolfLangAPIImpl(this, languageManager);

        getCommand("wlang").setExecutor(new LanguageCommand(this, languageManager));
        getCommand("wlangreload").setExecutor(new ReloadCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, languageManager), this);

        registerPlaceholders();

        getLogger().info("WolfLang enabled!");
        getLogger().info("WolfLangAPI ready for plugins!");
    }

    private void registerPlaceholders() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) return;
        registerCustomPlaceholders();
        getLogger().info("PlaceholderAPI expansions registered!");
    }

    private void registerCustomPlaceholders() {
        ConfigurationSection section = getConfig().getConfigurationSection("placeholders");
        if (section == null) return;

        int count = 0;
        for (String key : section.getKeys(false)) {
            ConfigurationSection values = section.getConfigurationSection(key);
            if (values == null) continue;

            Map<String, String> langMap = new HashMap<>();
            for (String langKey : values.getKeys(false)) {
                langMap.put(langKey, values.getString(langKey));
            }

            LangExpansion exp = new LangExpansion(languageManager, this, key, langMap);
            exp.register();
            expansions.add(exp);
            count++;
        }
        getLogger().info("Registered " + count + " custom placeholders.");
    }

    public void reloadPlaceholders() {
        for (LangExpansion exp : expansions) {
            exp.unregister();
        }
        expansions.clear();
        registerCustomPlaceholders();
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        getLogger().info("WolfLang disabled!");
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public WolfLangAPIImpl getApi() {
        return api;
    }

    public static WolfLangPlugin getInstance() {
        return getPlugin(WolfLangPlugin.class);
    }
}
