package dev.wolfstudios.wolflang.gui;

import dev.wolfstudios.wolflang.WolfLangPlugin;
import dev.wolfstudios.wolflang.manager.LanguageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URL;
import java.util.*;
import java.util.Base64;

/**
 * GUI de seleção de idioma com cabeças de países
 */
public class LanguageGUI {

    private final WolfLangPlugin plugin;
    private final LanguageManager languageManager;
    private static final String GUI_TITLE = "§8§lSelecione seu Idioma";
    private static final int GUI_SIZE = 54;

    // URLs de texturas de bandeiras (usando texturas conhecidas)
    private static final Map<String, String> FLAG_TEXTURES = new HashMap<>();

    static {
        // Base64 textures para bandeiras (texturas 8x8 pixels)
        // Brasil - Verde, amarelo, azul
        FLAG_TEXTURES.put("pt_BR", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2EwNDRjZjYzMTc2OGI0ZTI2NzgzN2U1MzBkMDkxMTQyM2YwNzZhMzQ2OGI5MWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("pt_PT", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTkxYzllNzc2NjI3YzJiYzJlNWNkNjIxMWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        
        // EUA/UK - Azul, vermelho, branco
        FLAG_TEXTURES.put("en", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("en_US", FLAG_TEXTURES.get("en"));
        FLAG_TEXTURES.put("en_GB", FLAG_TEXTURES.get("en"));
        
        // Espanha - Vermelho, amarelo
        FLAG_TEXTURES.put("es", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA2YzY5ZjI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("es_ES", FLAG_TEXTURES.get("es"));
        FLAG_TEXTURES.put("es_MX", FLAG_TEXTURES.get("es"));
        
        // França - Azul, branco, vermelho
        FLAG_TEXTURES.put("fr", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        
        // Alemanha - Preto, vermelho, amarelo
        FLAG_TEXTURES.put("de", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        
        // Itália - Verde, branco, vermelho
        FLAG_TEXTURES.put("it", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        
        // Rússia - Branco, azul, vermelho
        FLAG_TEXTURES.put("ru", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        
        // Japão - Branco, vermelho
        FLAG_TEXTURES.put("ja", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        
        // China - Vermelho, amarelo
        FLAG_TEXTURES.put("zh", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("zh_CN", FLAG_TEXTURES.get("zh"));
        FLAG_TEXTURES.put("zh_TW", FLAG_TEXTURES.get("zh"));
        
        // Coreia do Sul - Branco, vermelho, azul
        FLAG_TEXTURES.put("ko", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        
        // Árabe - Verde, branco, preto
        FLAG_TEXTURES.put("ar", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
    }

    public LanguageGUI(WolfLangPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
    }

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, GUI_SIZE, Component.text(GUI_TITLE));

        // Decoração
        ItemStack filler = createFiller();
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, filler);
            gui.setItem(45 + i, filler);
        }

        // Info - cabeça do jogador
        ItemStack info = createPlayerHead(player,
                "§e§l" + player.getName(),
                "§7Idioma atual: §a" + languageManager.getLanguageName(languageManager.getLanguage(player)),
                "",
                "§7Clique em uma bandeira abaixo",
                "§7para alterar seu idioma!"
        );
        gui.setItem(4, info);

        // Idiomas - cabeças com bandeiras
        Map<String, String> languages = languageManager.getSupportedLanguages();
        int slot = 10;

        for (Map.Entry<String, String> entry : languages.entrySet()) {
            String code = entry.getKey();
            String name = entry.getValue();
            boolean isSelected = languageManager.getLanguage(player).equals(code);

            ItemStack flagHead = createFlagHead(code, name, isSelected);
            gui.setItem(slot, flagHead);

            slot++;
            if (slot == 17 || slot == 26 || slot == 35 || slot == 44) {
                slot += 2;
            }
        }

        // Auto-detectar
        ItemStack autoDetect = createItem(Material.COMPASS,
                "§b§lAuto-Detectar",
                "§7Detecta automaticamente seu",
                "§7idioma baseado no seu IP.",
                "",
                "§eClique para detectar!"
        );
        gui.setItem(49, autoDetect);

        // Fechar
        ItemStack close = createItem(Material.BARRIER,
                "§c§lFechar",
                "§7Fechar o menu de idiomas."
        );
        gui.setItem(53, close);

        player.openInventory(gui);
    }

    private ItemStack createFlagHead(String code, String name, boolean selected) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        String prefix = selected ? "§a§l✓ " : "§f";
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7Idioma: §f" + name).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7País: §f" + getCountryName(code)).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        if (selected) {
            lore.add(Component.text("§a✔ Selecionado").decoration(TextDecoration.ITALIC, false));
        } else {
            lore.add(Component.text("§eClique para selecionar").decoration(TextDecoration.ITALIC, false));
        }

        meta.displayName(Component.text(prefix + getFlagEmoji(code) + " " + name).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);

        // Aplicar textura da bandeira
        String texture = FLAG_TEXTURES.get(code);
        if (texture != null) {
            applyTexture(meta, texture);
        }

        head.setItemMeta(meta);

        if (selected) {
            head.addEnchantment(org.bukkit.enchantments.Enchantment.UNBREAKING, 1);
        }

        return head;
    }

    private void applyTexture(SkullMeta meta, String base64Texture) {
        try {
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();
            byte[] decoded = Base64.getDecoder().decode(base64Texture);
            String json = new String(decoded);
            // Extrair URL do JSON
            int urlStart = json.indexOf("\"url\":\"") + 7;
            int urlEnd = json.indexOf("\"", urlStart);
            String url = json.substring(urlStart, urlEnd);
            textures.setSkin(new URL(url));
            profile.setTextures(textures);
            meta.setOwnerProfile(profile);
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao aplicar textura: " + e.getMessage());
        }
    }

    private String getFlagEmoji(String code) {
        return switch (code) {
            case "pt_BR" -> "🇧🇷";
            case "pt_PT" -> "🇵🇹";
            case "en", "en_US" -> "🇺🇸";
            case "en_GB" -> "🇬🇧";
            case "es", "es_ES" -> "🇪🇸";
            case "es_MX" -> "🇲🇽";
            case "fr" -> "🇫🇷";
            case "de" -> "🇩🇪";
            case "it" -> "🇮🇹";
            case "ru" -> "🇷🇺";
            case "ja" -> "🇯🇵";
            case "zh", "zh_CN" -> "🇨🇳";
            case "zh_TW" -> "🇹🇼";
            case "ko" -> "🇰🇷";
            case "ar" -> "🇸🇦";
            default -> "🏳️";
        };
    }

    private String getCountryName(String code) {
        return switch (code) {
            case "pt_BR" -> "Brasil";
            case "pt_PT" -> "Portugal";
            case "en", "en_US" -> "Estados Unidos";
            case "en_GB" -> "Reino Unido";
            case "es", "es_ES" -> "Espanha";
            case "es_MX" -> "México";
            case "fr" -> "França";
            case "de" -> "Alemanha";
            case "it" -> "Itália";
            case "ru" -> "Rússia";
            case "ja" -> "Japão";
            case "zh", "zh_CN" -> "China";
            case "zh_TW" -> "Taiwan";
            case "ko" -> "Coreia do Sul";
            case "ar" -> "Arábia Saudita";
            default -> "Desconhecido";
        };
    }

    private ItemStack createFiller() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(" "));
        item.setItemMeta(meta);
        return item;
    }

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
