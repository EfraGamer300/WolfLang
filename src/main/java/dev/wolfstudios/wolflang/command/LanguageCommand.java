package dev.wolfstudios.wolflang.command;

import dev.wolfstudios.wolflang.WolfLangPlugin;
import dev.wolfstudios.wolflang.geoip.GeoIPService;
import dev.wolfstudios.wolflang.manager.LanguageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LanguageCommand implements CommandExecutor {

    private final WolfLangPlugin plugin;
    private final LanguageManager languageManager;

    public LanguageCommand(WolfLangPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", TextColor.color(0xFF5555)));
            return true;
        }

        if (args.length == 0) {
            sendLanguageList(player);
            return true;
        }

        String arg = args[0].toLowerCase();

        // Comando /lang auto
        if (arg.equals("auto")) {
            handleAutoDetect(player);
            return true;
        }

        String lang = arg;
        Map<String, String> supported = languageManager.getSupportedLanguages();

        if (!supported.containsKey(lang)) {
            player.sendMessage(Component.text("Unsupported language! Use /wlang to see available languages.", TextColor.color(0xFF5555)));
            return true;
        }

        languageManager.setLanguage(player, lang);
        player.sendMessage(Component.text("Language set to: " + supported.get(lang), TextColor.color(0x55FF55)));
        return true;
    }

    private void handleAutoDetect(Player player) {
        String ip = player.getAddress().getAddress().getHostAddress();

        player.sendMessage(Component.text("Detecting language from your IP...", TextColor.color(0xFFFF55)));

        // Detecta em async pra não travar o servidor
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String country = GeoIPService.detectCountry(ip);
                String detectedLang = GeoIPService.detectLanguage(ip);

                if (detectedLang == null) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        player.sendMessage(Component.text("Could not detect language from IP. Using default.", TextColor.color(0xFF5555)));
                    });
                    return;
                }

                // Verifica se o idioma é suportado
                Map<String, String> supported = languageManager.getSupportedLanguages();
                String finalLang = supported.containsKey(detectedLang) ? detectedLang : languageManager.getDefaultLanguage();

                // Seta o idioma
                languageManager.setLanguage(player, finalLang);

                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    String langName = supported.getOrDefault(finalLang, finalLang);
                    player.sendMessage(Component.text("Detected country: " + (country != null ? country : "Unknown"), TextColor.color(0x55FFFF)));
                    player.sendMessage(Component.text("Language auto-set to: " + langName, TextColor.color(0x55FF55)));
                });

            } catch (Exception e) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    player.sendMessage(Component.text("Error detecting language: " + e.getMessage(), TextColor.color(0xFF5555)));
                });
            }
        });
    }

    private void sendLanguageList(Player player) {
        player.sendMessage(Component.text("Supported languages:", TextColor.color(0x55FFFF)));
        for (Map.Entry<String, String> entry : languageManager.getSupportedLanguages().entrySet()) {
            String current = languageManager.getLanguage(player).equals(entry.getKey()) ? " ✔" : "";
            player.sendMessage(Component.text("  " + entry.getKey() + " - " + entry.getValue() + current, TextColor.color(0xAAAAAA)));
        }
        player.sendMessage(Component.text("Usage: /wlang <code>", TextColor.color(0xAAAAAA)));
        player.sendMessage(Component.text("       /wlang auto - Auto-detect from IP", TextColor.color(0xAAAAAA)));
    }
}
