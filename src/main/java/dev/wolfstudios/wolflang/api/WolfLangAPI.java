package dev.wolfstudios.wolflang.api;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * API pública do WolfLang para outros plugins usarem
 */
public interface WolfLangAPI {

    /**
     * Traduz uma chave para o idioma do jogador
     * @param key Chave de tradução (ex: "race.start")
     * @param player UUID do jogador
     * @return Texto traduzido
     */
    String translate(String key, UUID player);

    /**
     * Traduz uma chave para o idioma do jogador com placeholders
     * @param key Chave de tradução
     * @param player UUID do jogador
     * @param placeholders Mapa de placeholders (valor -> substituição)
     * @return Texto traduzido com placeholders substituídos
     */
    String translate(String key, UUID player, Map<String, String> placeholders);

    /**
     * Traduz uma chave para um idioma específico
     * @param key Chave de tradução
     * @param locale Idioma destino
     * @return Texto traduzido
     */
    String translate(String key, Locale locale);

    /**
     * Obtém o idioma atual do jogador
     * @param player UUID do jogador
     * @return Código do idioma (ex: "pt_BR", "en")
     */
    String getLanguage(UUID player);

    /**
     * Define o idioma do jogador
     * @param player UUID do jogador
     * @param language Código do idioma
     */
    void setLanguage(UUID player, String language);

    /**
     * Verifica se um idioma é suportado
     * @param language Código do idioma
     * @return true se suportado
     */
    boolean isLanguageSupported(String language);

    /**
     * Obtém lista de idiomas suportados
     * @return Mapa de código -> nome do idioma
     */
    Map<String, String> getSupportedLanguages();

    /**
     * Obtém o idioma padrão do servidor
     * @return Código do idioma padrão
     */
    String getDefaultLanguage();

    /**
     * Registra traduções de um plugin
     * @param pluginName Nome do plugin
     * @param translations Mapa de chave -> traduções por idioma
     */
    void registerTranslations(String pluginName, Map<String, Map<String, String>> translations);

    /**
     * Remove traduções de um plugin
     * @param pluginName Nome do plugin
     */
    void unregisterTranslations(String pluginName);

    /**
     * Verifica se uma chave de tradução existe
     * @param key Chave de tradução
     * @return true se existe
     */
    boolean hasTranslation(String key);

    /**
     * Obtém a instância da API
     * @return Instância do WolfLangAPI
     */
    static WolfLangAPI getInstance() {
        return WolfLangAPIImpl.getInstance();
    }
}
