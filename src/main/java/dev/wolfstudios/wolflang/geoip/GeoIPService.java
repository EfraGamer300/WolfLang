package dev.wolfstudios.wolflang.geoip;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Serviço de detecção de país por IP usando APIs gratuitas
 */
public class GeoIPService {

    private static final Logger LOGGER = Logger.getLogger(GeoIPService.class.getName());

    // Mapeamento de código de país para idioma
    private static final Map<String, String> COUNTRY_TO_LANG = new HashMap<>();

    static {
        // Português
        COUNTRY_TO_LANG.put("BR", "pt_BR");
        COUNTRY_TO_LANG.put("PT", "pt_BR");
        COUNTRY_TO_LANG.put("AO", "pt_BR");
        COUNTRY_TO_LANG.put("MZ", "pt_BR");

        // Espanhol
        COUNTRY_TO_LANG.put("ES", "es");
        COUNTRY_TO_LANG.put("MX", "es");
        COUNTRY_TO_LANG.put("AR", "es");
        COUNTRY_TO_LANG.put("CL", "es");
        COUNTRY_TO_LANG.put("CO", "es");
        COUNTRY_TO_LANG.put("PE", "es");
        COUNTRY_TO_LANG.put("VE", "es");
        COUNTRY_TO_LANG.put("EC", "es");
        COUNTRY_TO_LANG.put("GT", "es");
        COUNTRY_TO_LANG.put("CU", "es");
        COUNTRY_TO_LANG.put("BO", "es");
        COUNTRY_TO_LANG.put("DO", "es");
        COUNTRY_TO_LANG.put("HN", "es");
        COUNTRY_TO_LANG.put("PY", "es");
        COUNTRY_TO_LANG.put("SV", "es");
        COUNTRY_TO_LANG.put("NI", "es");
        COUNTRY_TO_LANG.put("CR", "es");
        COUNTRY_TO_LANG.put("PA", "es");
        COUNTRY_TO_LANG.put("UY", "es");

        // Inglês (padrão para muitos países)
        COUNTRY_TO_LANG.put("US", "en");
        COUNTRY_TO_LANG.put("GB", "en");
        COUNTRY_TO_LANG.put("CA", "en");
        COUNTRY_TO_LANG.put("AU", "en");
        COUNTRY_TO_LANG.put("NZ", "en");
        COUNTRY_TO_LANG.put("IE", "en");
        COUNTRY_TO_LANG.put("ZA", "en");

        // Francês
        COUNTRY_TO_LANG.put("FR", "fr");
        COUNTRY_TO_LANG.put("BE", "fr");
        COUNTRY_TO_LANG.put("CH", "fr");

        // Alemão
        COUNTRY_TO_LANG.put("DE", "de");
        COUNTRY_TO_LANG.put("AT", "de");

        // Italiano
        COUNTRY_TO_LANG.put("IT", "it");

        // Russo
        COUNTRY_TO_LANG.put("RU", "ru");

        // Japonês
        COUNTRY_TO_LANG.put("JP", "ja");

        // Chinês
        COUNTRY_TO_LANG.put("CN", "zh");
        COUNTRY_TO_LANG.put("TW", "zh");
        COUNTRY_TO_LANG.put("HK", "zh");

        // Coreano
        COUNTRY_TO_LANG.put("KR", "ko");

        // Árabe
        COUNTRY_TO_LANG.put("SA", "ar");
        COUNTRY_TO_LANG.put("AE", "ar");
        COUNTRY_TO_LANG.put("EG", "ar");
    }

    /**
     * Detecta o código do país pelo IP
     */
    public static String detectCountry(String ip) {
        // Tenta várias APIs gratuitas
        String country = detectWithIpApi(ip);
        if (country != null) return country;

        country = detectWithIpWhois(ip);
        if (country != null) return country;

        return null;
    }

    /**
     * API: ip-api.com (gratuita, sem limite)
     */
    private static String detectWithIpApi(String ip) {
        try {
            URL url = new URL("http://ip-api.com/json/" + ip + "?fields=countryCode");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse JSON simples
                String json = response.toString();
                int idx = json.indexOf("\"countryCode\":\"");
                if (idx != -1) {
                    int start = idx + 15;
                    int end = json.indexOf("\"", start);
                    return json.substring(start, end);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro na API ip-api.com: " + e.getMessage());
        }
        return null;
    }

    /**
     * API: ipwhois.io (gratuita)
     */
    private static String detectWithIpWhois(String ip) {
        try {
            URL url = new URL("https://ipwhois.app/json/" + ip);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                String json = response.toString();
                int idx = json.indexOf("\"country_code\":\"");
                if (idx != -1) {
                    int start = idx + 16;
                    int end = json.indexOf("\"", start);
                    return json.substring(start, end);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro na API ipwhois.app: " + e.getMessage());
        }
        return null;
    }

    /**
     * Converte código de país para idioma
     */
    public static String getLanguageFromCountry(String countryCode) {
        return COUNTRY_TO_LANG.getOrDefault(countryCode, "en");
    }

    /**
     * Detecta o idioma baseado no IP
     */
    public static String detectLanguage(String ip) {
        String country = detectCountry(ip);
        if (country == null) return null;
        return getLanguageFromCountry(country);
    }
}
