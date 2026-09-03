package dev.wolfstudios.wolflang.api;

import dev.wolfstudios.wolflang.WolfLangPlugin;
import dev.wolfstudios.wolflang.manager.LanguageManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Implementação da API do WolfLang
 */
public class WolfLangAPIImpl implements WolfLangAPI {

    private static WolfLangAPIImpl instance;
    private final WolfLangPlugin plugin;
    private final LanguageManager languageManager;
    private final Map<String, Map<String, String>> translations = new ConcurrentHashMap<>();

    public WolfLangAPIImpl(WolfLangPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
        instance = this;
    }

    public static WolfLangAPIImpl getInstance() {
        return instance;
    }

    @Override
    public String translate(String key, UUID player) {
        return translate(key, player, Collections.emptyMap());
    }

    @Override
    public String translate(String key, UUID player, Map<String, String> placeholders) {
        String lang = getLanguage(player);
        String translated = getTranslation(key, lang);

        // Se não encontrou, tenta no idioma padrão
        if (translated.equals(key)) {
            String defaultLang = getDefaultLanguage();
            if (!lang.equals(defaultLang)) {
                translated = getTranslation(key, defaultLang);
            }
        }

        // Substitui placeholders
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            translated = translated.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return translated;
    }

    @Override
    public String translate(String key, Locale locale) {
        return getTranslation(key, locale.toString());
    }

    @Override
    public String getLanguage(UUID player) {
        return languageManager.getLanguage(player);
    }

    @Override
    public void setLanguage(UUID player, String language) {
        languageManager.setLanguage(player, language);
    }

    @Override
    public boolean isLanguageSupported(String language) {
        return languageManager.getSupportedLanguages().containsKey(language);
    }

    @Override
    public Map<String, String> getSupportedLanguages() {
        return languageManager.getSupportedLanguages();
    }

    @Override
    public String getDefaultLanguage() {
        return languageManager.getDefaultLanguage();
    }

    @Override
    public void registerTranslations(String pluginName, Map<String, Map<String, String>> newTranslations) {
        for (Map.Entry<String, Map<String, String>> entry : newTranslations.entrySet()) {
            String key = pluginName + "." + entry.getKey();
            Map<String, String> langMap = translations.computeIfAbsent(key, k -> new HashMap<>());
            langMap.putAll(entry.getValue());
        }
        plugin.getLogger().info("Registered " + newTranslations.size() + " translations from: " + pluginName);
    }

    @Override
    public void unregisterTranslations(String pluginName) {
        translations.entrySet().removeIf(entry -> entry.getKey().startsWith(pluginName + "."));
        plugin.getLogger().info("Unregistered translations from: " + pluginName);
    }

    @Override
    public boolean hasTranslation(String key) {
        return translations.containsKey(key);
    }

    private String getTranslation(String key, String lang) {
        Map<String, String> langMap = translations.get(key);
        if (langMap != null) {
            String translated = langMap.get(lang);
            if (translated != null) return translated;
        }
        return key;
    }

    /**
     * Limpa todas as traduções (usado no reload)
     */
    public void clearTranslations() {
        translations.clear();
    }
}
