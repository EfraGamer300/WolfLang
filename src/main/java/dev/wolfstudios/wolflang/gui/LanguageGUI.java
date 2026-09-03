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
        // URLs de texturas de bandeiras (Base64)
        FLAG_TEXTURES.put("pt_BR", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2EwNDRjZjYzMTc2OGI0ZTI2NzgzN2U1MzBkMDkxMTQyM2YwNzZhMzQ2OGI5MWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("pt_PT", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTkxYzllNzc2NjI3YzJiYzJlNWNkNjIxMWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("en_US", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("en_GB", FLAG_TEXTURES.get("en_US"));
        FLAG_TEXTURES.put("es_ES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA2YzY5ZjI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("es_MX", FLAG_TEXTURES.get("es_ES"));
        FLAG_TEXTURES.put("es_AR", FLAG_TEXTURES.get("es_ES"));
        FLAG_TEXTURES.put("es_CL", FLAG_TEXTURES.get("es_ES"));
        FLAG_TEXTURES.put("es_CO", FLAG_TEXTURES.get("es_ES"));
        FLAG_TEXTURES.put("fr_FR", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("fr_CA", FLAG_TEXTURES.get("fr_FR"));
        FLAG_TEXTURES.put("de_DE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("de_AT", FLAG_TEXTURES.get("de_DE"));
        FLAG_TEXTURES.put("it_IT", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("ru_RU", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("ja_JP", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("zh_CN", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("zh_TW", FLAG_TEXTURES.get("zh_CN"));
        FLAG_TEXTURES.put("ko_KR", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("ar_SA", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("hi_IN", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("tr_TR", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("nl_NL", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("pl_PL", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("uk_UA", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("sv_SE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("nb_NO", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("da_DK", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("fi_FI", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("el_GR", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("cs_CZ", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("hu_HU", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("ro_RO", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("th_TH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("vi_VN", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("id_ID", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
        FLAG_TEXTURES.put("ms_MY", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDY2MmI2ZmI3YjQ3NjVmZjk3ZGQ3N2I0ZWJhYjM5ZTU2ODJiYzE4MjEwNCJ9fX0=");
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
            case "en_US" -> "🇺🇸";
            case "en_GB" -> "🇬🇧";
            case "es_ES" -> "🇪🇸";
            case "es_MX" -> "🇲🇽";
            case "es_AR" -> "🇦🇷";
            case "es_CL" -> "🇨🇱";
            case "es_CO" -> "🇨🇴";
            case "fr_FR" -> "🇫🇷";
            case "fr_CA" -> "🇨🇦";
            case "de_DE" -> "🇩🇪";
            case "de_AT" -> "🇦🇹";
            case "it_IT" -> "🇮🇹";
            case "ru_RU" -> "🇷🇺";
            case "ja_JP" -> "🇯🇵";
            case "zh_CN" -> "🇨🇳";
            case "zh_TW" -> "🇹🇼";
            case "ko_KR" -> "🇰🇷";
            case "ar_SA" -> "🇸🇦";
            case "hi_IN" -> "🇮🇳";
            case "tr_TR" -> "🇹🇷";
            case "nl_NL" -> "🇳🇱";
            case "pl_PL" -> "🇵🇱";
            case "uk_UA" -> "🇺🇦";
            case "sv_SE" -> "🇸🇪";
            case "nb_NO" -> "🇳🇴";
            case "da_DK" -> "🇩🇰";
            case "fi_FI" -> "🇫🇮";
            case "el_GR" -> "🇬🇷";
            case "cs_CZ" -> "🇨🇿";
            case "hu_HU" -> "🇭🇺";
            case "ro_RO" -> "🇷🇴";
            case "th_TH" -> "🇹🇭";
            case "vi_VN" -> "🇻🇳";
            case "id_ID" -> "🇮🇩";
            case "ms_MY" -> "🇲🇾";
            default -> "🏳️";
        };
    }

    private String getCountryName(String code) {
        return switch (code) {
            case "pt_BR" -> "Brasil";
            case "pt_PT" -> "Portugal";
            case "en_US" -> "United States";
            case "en_GB" -> "United Kingdom";
            case "es_ES" -> "España";
            case "es_MX" -> "México";
            case "es_AR" -> "Argentina";
            case "es_CL" -> "Chile";
            case "es_CO" -> "Colombia";
            case "fr_FR" -> "France";
            case "fr_CA" -> "Canada (FR)";
            case "de_DE" -> "Deutschland";
            case "de_AT" -> "Österreich";
            case "it_IT" -> "Italia";
            case "ru_RU" -> "Россия";
            case "ja_JP" -> "日本";
            case "zh_CN" -> "中国";
            case "zh_TW" -> "台灣";
            case "ko_KR" -> "대한민국";
            case "ar_SA" -> "السعودية";
            case "hi_IN" -> "भारत";
            case "tr_TR" -> "Türkiye";
            case "nl_NL" -> "Nederland";
            case "pl_PL" -> "Polska";
            case "uk_UA" -> "Україна";
            case "sv_SE" -> "Sverige";
            case "nb_NO" -> "Norge";
            case "da_DK" -> "Danmark";
            case "fi_FI" -> "Suomi";
            case "el_GR" -> "Ελλάδα";
            case "cs_CZ" -> "Česko";
            case "hu_HU" -> "Magyarország";
            case "ro_RO" -> "România";
            case "th_TH" -> "ไทย";
            case "vi_VN" -> "Việt Nam";
            case "id_ID" -> "Indonesia";
            case "ms_MY" -> "Malaysia";
            default -> code;
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
