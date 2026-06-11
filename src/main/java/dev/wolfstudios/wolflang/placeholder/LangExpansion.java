package dev.wolfstudios.wolflang.placeholder;

import dev.wolfstudios.wolflang.manager.LanguageManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LangExpansion extends PlaceholderExpansion {

    private final LanguageManager languageManager;
    private final Plugin plugin;
    private final String identifier;
    private final Map<String, String> values;

    public LangExpansion(LanguageManager languageManager, Plugin plugin, String identifier, Map<String, String> values) {
        this.languageManager = languageManager;
        this.plugin = plugin;
        this.identifier = identifier;
        this.values = values;
    }

    @Override
    public @NotNull String getIdentifier() {
        return identifier;
    }

    @Override
    public @NotNull String getAuthor() {
        return "WolfTranslator";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean register() {
        return plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && super.register();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";
        if (!params.isEmpty()) return null;
        String lang = languageManager.getLanguage(player);
        return values.getOrDefault(lang, values.get(languageManager.getDefaultLanguage()));
    }
}
