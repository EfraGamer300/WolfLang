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
        COUNTRY_TO_LANG.put("PT", "pt_PT");
        COUNTRY_TO_LANG.put("AO", "pt_BR");
        COUNTRY_TO_LANG.put("MZ", "pt_BR");
        COUNTRY_TO_LANG.put("CV", "pt_PT");
        COUNTRY_TO_LANG.put("GW", "pt_PT");
        COUNTRY_TO_LANG.put("ST", "pt_PT");
        COUNTRY_TO_LANG.put("TL", "pt_PT");

        // Espanhol
        COUNTRY_TO_LANG.put("ES", "es_ES");
        COUNTRY_TO_LANG.put("MX", "es_MX");
        COUNTRY_TO_LANG.put("AR", "es_AR");
        COUNTRY_TO_LANG.put("CL", "es_CL");
        COUNTRY_TO_LANG.put("CO", "es_CO");
        COUNTRY_TO_LANG.put("PE", "es_ES");
        COUNTRY_TO_LANG.put("VE", "es_ES");
        COUNTRY_TO_LANG.put("EC", "es_ES");
        COUNTRY_TO_LANG.put("GT", "es_ES");
        COUNTRY_TO_LANG.put("CU", "es_ES");
        COUNTRY_TO_LANG.put("BO", "es_ES");
        COUNTRY_TO_LANG.put("DO", "es_ES");
        COUNTRY_TO_LANG.put("HN", "es_ES");
        COUNTRY_TO_LANG.put("PY", "es_ES");
        COUNTRY_TO_LANG.put("SV", "es_ES");
        COUNTRY_TO_LANG.put("NI", "es_ES");
        COUNTRY_TO_LANG.put("CR", "es_ES");
        COUNTRY_TO_LANG.put("PA", "es_ES");
        COUNTRY_TO_LANG.put("UY", "es_AR");

        // Inglês (padrão para muitos países)
        COUNTRY_TO_LANG.put("US", "en_US");
        COUNTRY_TO_LANG.put("GB", "en_GB");
        COUNTRY_TO_LANG.put("CA", "en_US");
        COUNTRY_TO_LANG.put("AU", "en_US");
        COUNTRY_TO_LANG.put("NZ", "en_US");
        COUNTRY_TO_LANG.put("IE", "en_GB");
        COUNTRY_TO_LANG.put("ZA", "en_US");
        COUNTRY_TO_LANG.put("JM", "en_US");
        COUNTRY_TO_LANG.put("TT", "en_US");
        COUNTRY_TO_LANG.put("BS", "en_US");
        COUNTRY_TO_LANG.put("NG", "en_US");
        COUNTRY_TO_LANG.put("KE", "en_US");
        COUNTRY_TO_LANG.put("GH", "en_US");
        COUNTRY_TO_LANG.put("SG", "en_US");
        COUNTRY_TO_LANG.put("PH", "en_US");
        COUNTRY_TO_LANG.put("IN", "en_GB");
        COUNTRY_TO_LANG.put("PK", "en_GB");

        // Francês
        COUNTRY_TO_LANG.put("FR", "fr_FR");
        COUNTRY_TO_LANG.put("BE", "fr_FR");
        COUNTRY_TO_LANG.put("CH", "fr_FR");
        COUNTRY_TO_LANG.put("CA", "fr_CA");
        COUNTRY_TO_LANG.put("LU", "fr_FR");
        COUNTRY_TO_LANG.put("MC", "fr_FR");
        COUNTRY_TO_LANG.put("CI", "fr_FR");
        COUNTRY_TO_LANG.put("SN", "fr_FR");
        COUNTRY_TO_LANG.put("ML", "fr_FR");

        // Alemão
        COUNTRY_TO_LANG.put("DE", "de_DE");
        COUNTRY_TO_LANG.put("AT", "de_AT");
        COUNTRY_TO_LANG.put("CH", "de_DE");
        COUNTRY_TO_LANG.put("LI", "de_DE");
        COUNTRY_TO_LANG.put("LU", "de_DE");

        // Italiano
        COUNTRY_TO_LANG.put("IT", "it_IT");
        COUNTRY_TO_LANG.put("SM", "it_IT");
        COUNTRY_TO_LANG.put("VA", "it_IT");
        COUNTRY_TO_LANG.put("CH", "it_IT");

        // Russo
        COUNTRY_TO_LANG.put("RU", "ru_RU");
        COUNTRY_TO_LANG.put("BY", "ru_RU");
        COUNTRY_TO_LANG.put("KZ", "ru_RU");
        COUNTRY_TO_LANG.put("KG", "ru_RU");

        // Japonês
        COUNTRY_TO_LANG.put("JP", "ja_JP");

        // Chinês
        COUNTRY_TO_LANG.put("CN", "zh_CN");
        COUNTRY_TO_LANG.put("TW", "zh_TW");
        COUNTRY_TO_LANG.put("HK", "zh_TW");
        COUNTRY_TO_LANG.put("MO", "zh_TW");
        COUNTRY_TO_LANG.put("SG", "zh_CN");

        // Coreano
        COUNTRY_TO_LANG.put("KR", "ko_KR");

        // Árabe
        COUNTRY_TO_LANG.put("SA", "ar_SA");
        COUNTRY_TO_LANG.put("AE", "ar_SA");
        COUNTRY_TO_LANG.put("EG", "ar_SA");
        COUNTRY_TO_LANG.put("IQ", "ar_SA");
        COUNTRY_TO_LANG.put("JO", "ar_SA");
        COUNTRY_TO_LANG.put("KW", "ar_SA");
        COUNTRY_TO_LANG.put("LB", "ar_SA");
        COUNTRY_TO_LANG.put("LY", "ar_SA");
        COUNTRY_TO_LANG.put("MA", "ar_SA");
        COUNTRY_TO_LANG.put("OM", "ar_SA");
        COUNTRY_TO_LANG.put("QA", "ar_SA");
        COUNTRY_TO_LANG.put("SY", "ar_SA");
        COUNTRY_TO_LANG.put("TN", "ar_SA");
        COUNTRY_TO_LANG.put("YE", "ar_SA");

        // Hindi
        COUNTRY_TO_LANG.put("IN", "hi_IN");
        COUNTRY_TO_LANG.put("NP", "hi_IN");

        // Turco
        COUNTRY_TO_LANG.put("TR", "tr_TR");
        COUNTRY_TO_LANG.put("CY", "tr_TR");

        // Holandês
        COUNTRY_TO_LANG.put("NL", "nl_NL");
        COUNTRY_TO_LANG.put("BE", "nl_NL");
        COUNTRY_TO_LANG.put("SR", "nl_NL");

        // Polonês
        COUNTRY_TO_LANG.put("PL", "pl_PL");

        // Ucraniano
        COUNTRY_TO_LANG.put("UA", "uk_UA");

        // Sueco
        COUNTRY_TO_LANG.put("SE", "sv_SE");
        COUNTRY_TO_LANG.put("FI", "sv_SE");

        // Norueguês
        COUNTRY_TO_LANG.put("NO", "nb_NO");

        // Dinamarquês
        COUNTRY_TO_LANG.put("DK", "da_DK");
        COUNTRY_TO_LANG.put("GL", "da_DK");

        // Finlandês
        COUNTRY_TO_LANG.put("FI", "fi_FI");

        // Grego
        COUNTRY_TO_LANG.put("GR", "el_GR");
        COUNTRY_TO_LANG.put("CY", "el_GR");

        // Tcheco
        COUNTRY_TO_LANG.put("CZ", "cs_CZ");
        COUNTRY_TO_LANG.put("SK", "cs_CZ");

        // Húngaro
        COUNTRY_TO_LANG.put("HU", "hu_HU");

        // Romeno
        COUNTRY_TO_LANG.put("RO", "ro_RO");
        COUNTRY_TO_LANG.put("MD", "ro_RO");

        // Tailandês
        COUNTRY_TO_LANG.put("TH", "th_TH");

        // Vietnamita
        COUNTRY_TO_LANG.put("VN", "vi_VN");

        // Indonésio
        COUNTRY_TO_LANG.put("ID", "id_ID");

        // Malaio
        COUNTRY_TO_LANG.put("MY", "ms_MY");
        COUNTRY_TO_LANG.put("BN", "ms_MY");
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
