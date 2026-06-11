package com.wolflang.command;

import com.wolflang.WolfLangPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    private final WolfLangPlugin plugin;

    public ReloadCommand(WolfLangPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("wolflang.reload")) {
            sender.sendMessage(Component.text("No permission.", TextColor.color(0xFF5555)));
            return true;
        }

        plugin.reloadConfig();
        plugin.getLanguageManager().loadAllFromDatabase();
        plugin.reloadPlaceholders();
        sender.sendMessage(Component.text("WolfLang config reloaded!", TextColor.color(0x55FF55)));
        return true;
    }
}
