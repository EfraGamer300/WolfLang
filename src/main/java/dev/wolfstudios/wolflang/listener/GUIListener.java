package dev.wolfstudios.wolflang.listener;

import dev.wolfstudios.wolflang.WolfLangPlugin;
import dev.wolfstudios.wolflang.gui.LanguageGUI;
import dev.wolfstudios.wolflang.manager.LanguageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GUIListener implements Listener {

    private final WolfLangPlugin plugin;
    private final LanguageManager languageManager;
    private final LanguageGUI gui;
    private final Map<UUID, Boolean> inGUI = new ConcurrentHashMap<>();

    public GUIListener(WolfLangPlugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.languageManager = languageManager;
        this.gui = new LanguageGUI(plugin, languageManager);
    }

    /**
     * Abre a GUI para o jogador
     */
    public void openGUI(Player player) {
        inGUI.put(player.getUniqueId(), true);
        gui.open(player);
    }

    /**
     * Verifica se o jogador está na GUI
     */
    public boolean isInGUI(Player player) {
        return inGUI.containsKey(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Verifica se é a nossa GUI
        if (!isInGUI(player)) return;

        // Cancela o evento (não deixa mover itens)
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String title = event.getView().getTitle();
        if (!title.contains("Selecione")) return;

        int slot = event.getRawSlot();

        // Botão de fechar (slot 53)
        if (slot == 53) {
            player.closeInventory();
            return;
        }

        // Botão auto-detectar (slot 49)
        if (slot == 49) {
            player.closeInventory();
            player.sendMessage(Component.text("Detectando idioma do seu IP...", NamedTextColor.YELLOW));

            // Auto-detecta em async
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    String ip = player.getAddress().getAddress().getHostAddress();
                    String detectedLang = dev.wolfstudios.wolflang.geoip.GeoIPService.detectLanguage(ip);

                    if (detectedLang != null) {
                        Map<String, String> supported = languageManager.getSupportedLanguages();
                        String finalLang = supported.containsKey(detectedLang) ? detectedLang : languageManager.getDefaultLanguage();

                        languageManager.setLanguage(player, finalLang);

                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            String langName = supported.getOrDefault(finalLang, finalLang);
                            player.sendMessage(Component.text("Idioma detectado e definido para: " + langName, NamedTextColor.GREEN));
                        });
                    } else {
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            player.sendMessage(Component.text("Nao foi possivel detectar seu idioma.", NamedTextColor.RED));
                        });
                    }
                } catch (Exception e) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        player.sendMessage(Component.text("Erro ao detectar: " + e.getMessage(), NamedTextColor.RED));
                    });
                }
            });
            return;
        }

        // Clique em idioma (slots 10-44)
        if (slot >= 10 && slot <= 44) {
            // Calcula qual idioma foi clicado baseado no slot
            String[] langCodes = languageManager.getSupportedLanguages().keySet().toArray(new String[0]);
            int index = calculateLanguageIndex(slot);

            if (index >= 0 && index < langCodes.length) {
                String selectedLang = langCodes[index];
                String oldLang = languageManager.getLanguage(player);

                if (selectedLang.equals(oldLang)) {
                    player.sendMessage(Component.text("Este ja e seu idioma atual!", NamedTextColor.YELLOW));
                    return;
                }

                languageManager.setLanguage(player, selectedLang);
                String langName = languageManager.getSupportedLanguages().get(selectedLang);

                player.sendMessage(Component.text("Idioma alterado para: " + langName, NamedTextColor.GREEN));
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        inGUI.remove(player.getUniqueId());
    }

    /**
     * Calcula o índice do idioma baseado no slot clicado
     */
    private int calculateLanguageIndex(int slot) {
        // Slots válidos: 10-16, 19-25, 28-34, 37-43
        int row = (slot - 10) / 9;
        int col = (slot - 10) % 9;

        if (row < 0 || row > 3) return -1;
        if (col < 0 || col > 6) return -1;

        return (row * 7) + col;
    }
}
