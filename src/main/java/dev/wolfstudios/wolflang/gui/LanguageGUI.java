package dev.wolfstudios.wolflang.gui;

import dev.wolfstudios.wolflang.WolfLangPlugin;
import dev.wolfstudios.wolflang.manager.LanguageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GUI de seleção de idioma
 */
public class LanguageGUI {

    private final WolfLangPlugin plugin;
    private final LanguageManager languageManager;
    private static final String GUI_TITLE = "§8§lSelecione seu Idioma";
    private static final int GUI_SIZE = 54; // 6 rows

    public LanguageGUI(WolfLangPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    /**
     * Abre a GUI de seleção de idioma
     */
    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, GUI_SIZE, Component.text(GUI_TITLE));

        // Decoração - vidro colorido nas bordas
        ItemStack filler = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, filler);
            gui.setItem(45 + i, filler);
        }

        // Info item (cabeça do jogador)
        ItemStack info = createPlayerHead(player,
                "§e§l" + player.getName(),
                "§7Idioma atual: §a" + languageManager.getLanguageName(languageManager.getLanguage(player)),
                "",
                "§7Clique em um idioma abaixo",
                "§7para alterar sua linguagem!"
        );
        gui.setItem(4, info);

        // Idiomas disponíveis
        Map<String, String> languages = languageManager.getSupportedLanguages();
        int slot = 10; // Começa na segunda linha

        for (Map.Entry<String, String> entry : languages.entrySet()) {
            String code = entry.getKey();
            String name = entry.getValue();
            boolean isSelected = languageManager.getLanguage(player).equals(code);

            ItemStack langItem = createLanguageItem(code, name, isSelected);
            gui.setItem(slot, langItem);

            slot++;
            // Pula para próxima linha quando chegar no slot 17, 26, 35
            if (slot == 17 || slot == 26 || slot == 35 || slot == 44) {
                slot += 2;
            }
        }

        // Botão de auto-detectar
        ItemStack autoDetect = createItem(Material.COMPASS,
                "§b§lAuto-Detectar",
                "§7Detecta automaticamente seu",
                "§7idioma baseado no seu IP.",
                "",
                "§eClique para detectar!"
        );
        gui.setItem(49, autoDetect);

        // Botão de fechar
        ItemStack close = createItem(Material.BARRIER,
                "§c§lFechar",
                "§7Fechar o menu de idiomas."
        );
        gui.setItem(53, close);

        player.openInventory(gui);
    }

    /**
     * Cria o item de idioma com bandeira
     */
    private ItemStack createLanguageItem(String code, String name, boolean selected) {
        Material material = getMaterialForLanguage(code);
        String prefix = selected ? "§a§l✓ " : "§f";

        List<String> lore = new ArrayList<>();
        lore.add("§7Idioma: §f" + name);
        lore.add("§7Código: §f" + code);
        lore.add("");

        if (selected) {
            lore.add("§a✔ Selecionado");
        } else {
            lore.add("§eClique para selecionar");
        }

        ItemStack item = createItem(material, prefix + name, lore.toArray(new String[0]));

        // Adiciona glow se selecionado
        if (selected) {
            item.addEnchantment(org.bukkit.enchantments.Enchantment.UNBREAKING, 1);
        }

        return item;
    }

    /**
     * Retorna o material baseado no idioma
     */
    private Material getMaterialForLanguage(String code) {
        return switch (code) {
            case "pt_BR", "pt_PT" -> Material.GREEN_WOOL;      // Brasil/Portugal - Verde
            case "en", "en_US", "en_GB" -> Material.BLUE_WOOL;  // Inglês - Azul
            case "es", "es_ES", "es_MX" -> Material.YELLOW_WOOL; // Espanhol - Amarelo
            case "fr" -> Material.LIGHT_BLUE_WOOL;               // Francês - Azul claro
            case "de" -> Material.YELLOW_WOOL;                   // Alemão - Amarelo
            case "it" -> Material.GREEN_WOOL;                    // Italiano - Verde
            case "ru" -> Material.RED_WOOL;                      // Russo - Vermelho
            case "ja" -> Material.PINK_WOOL;                     // Japonês - Rosa
            case "zh", "zh_CN", "zh_TW" -> Material.RED_WOOL;    // Chinês - Vermelho
            case "ko" -> Material.WHITE_WOOL;                    // Coreano - Branco
            case "ar" -> Material.LIME_WOOL;                     // Árabe - Verde limão
            default -> Material.PAPER;
        };
    }

    /**
     * Cria um item com nome e lore
     */
    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));

        List<Component> loreComponents = new ArrayList<>();
        for (String line : lore) {
            loreComponents.add(Component.text(line).decoration(TextDecoration.ITALIC, false));
        }
        meta.lore(loreComponents);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Cria uma cabeça de jogador
     */
    private ItemStack createPlayerHead(Player player, String name, String... lore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        meta.setOwningPlayer(player);
        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));

        List<Component> loreComponents = new ArrayList<>();
        for (String line : lore) {
            loreComponents.add(Component.text(line).decoration(TextDecoration.ITALIC, false));
        }
        meta.lore(loreComponents);

        head.setItemMeta(meta);
        return head;
    }
}
