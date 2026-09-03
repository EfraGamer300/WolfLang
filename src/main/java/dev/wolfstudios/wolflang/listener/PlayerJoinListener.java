package dev.wolfstudios.wolflang.listener;

import dev.wolfstudios.wolflang.WolfLangPlugin;
import dev.wolfstudios.wolflang.geoip.GeoIPService;
import dev.wolfstudios.wolflang.manager.LanguageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;

public class PlayerJoinListener implements Listener {

    private final WolfLangPlugin plugin;
    private final LanguageManager languageManager;

    public PlayerJoinListener(WolfLangPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Só detecta se o jogador não tem idioma definido
        String currentLang = languageManager.getLanguage(player);
        String defaultLang = languageManager.getDefaultLanguage();

        if (!currentLang.equals(defaultLang)) {
            return; // Já tem idioma definido
        }

        // Detecta em async
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String ip = player.getAddress().getAddress().getHostAddress();
                String detectedLang = GeoIPService.detectLanguage(ip);

                if (detectedLang != null) {
                    Map<String, String> supported = languageManager.getSupportedLanguages();
                    String finalLang = supported.containsKey(detectedLang) ? detectedLang : defaultLang;

                    // Seta o idioma
                    languageManager.setLanguage(player, finalLang);

                    // Notifica o jogador
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        String langName = supported.getOrDefault(finalLang, finalLang);
                        String country = GeoIPService.detectCountry(ip);

                        player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", TextColor.color(0x55FFFF)));
                        player.sendMessage(Component.text("  Wolf Network", TextColor.color(0xFFAA00)));
                        player.sendMessage(Component.text("  Idioma detectado: " + langName, TextColor.color(0x55FF55)));
                        if (country != null) {
                            player.sendMessage(Component.text("  País: " + country, TextColor.color(0xAAAAAA)));
                        }
                        player.sendMessage(Component.text("  Use /wlang <código> para trocar", TextColor.color(0xAAAAAA)));
                        player.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", TextColor.color(0x55FFFF)));
                    });

                    plugin.getLogger().info("Auto-detected language for " + player.getName() + ": " + finalLang + " (IP: " + ip + ")");
                }

            } catch (Exception e) {
                plugin.getLogger().warning("Error auto-detecting language for " + player.getName() + ": " + e.getMessage());
            }
        });
    }
}
