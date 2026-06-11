package dev.wolfstudios.wolflang.command;

import dev.wolfstudios.wolflang.WolfLangPlugin;
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

        String lang = args[0].toLowerCase();
        Map<String, String> supported = languageManager.getSupportedLanguages();

        if (!supported.containsKey(lang)) {
            player.sendMessage(Component.text("Unsupported language! Use /wlang to see available languages.", TextColor.color(0xFF5555)));
            return true;
        }

        languageManager.setLanguage(player, lang);
        player.sendMessage(Component.text("Language set to: " + supported.get(lang), TextColor.color(0x55FF55)));
        return true;
    }

    private void sendLanguageList(Player player) {
        player.sendMessage(Component.text("Supported languages:", TextColor.color(0x55FFFF)));
        for (Map.Entry<String, String> entry : languageManager.getSupportedLanguages().entrySet()) {
            String current = languageManager.getLanguage(player).equals(entry.getKey()) ? " \u2714" : "";
            player.sendMessage(Component.text("  " + entry.getKey() + " - " + entry.getValue() + current, TextColor.color(0xAAAAAA)));
        }
        player.sendMessage(Component.text("Usage: /wlang <code>", TextColor.color(0xAAAAAA)));
    }
}
