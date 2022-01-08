package landon.core.patchapi.patches.pluginviewer;

import com.massivecraft.factions.P;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import landon.core.WarhammerCore;
import landon.core.inventories.CustomItemInventory;
import landon.core.inventories.CustomItemSearchedInventory;
import landon.core.util.GiveUtil;
import landon.core.util.LoreUtil;
import landon.core.util.anvilsearch.AnvilGUI;
import landon.core.util.items.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PluginListGUI implements InventoryProvider {
    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .closeable(true)
            .id("pluginListGUI")
            .size(6, 9)
            .title("Plugins (" + PluginViewer.inst.getPlugins().size() + ")")
            .provider(new PluginListGUI())
            .manager(WarhammerCore.get().getInventoryManager())
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();
        ClickableItem[] items = new ClickableItem[PluginViewer.inst.getPlugins().size()];
        for(int i = 0; i < PluginViewer.inst.getJurassicPlugins().size(); i++) {
            Plugin plugin = PluginViewer.inst.getJurassicPlugins().get(i);
            ItemStack stack;
            if(plugin.isEnabled()) {
                stack = ItemBuilder.createItem(plugin.getDescription().getName().replace("Factions", "JurassicCore").contains("Jurassic") ? Material.STAINED_GLASS_PANE : Material.STAINED_GLASS, "&a&l" + plugin.getDescription().getName().replace("Factions", "JurassicCore"), 1, 5, plugin.getDescription().getName().contains("Jurassic") ? "&7Author: &aApolloDev" : "&7Author: &a" + this.toReadableString(plugin.getDescription().getAuthors(), ChatColor.GREEN), "&7Version: &a" + plugin.getDescription().getVersion(), "&a&lENABLED");
            } else {
                stack = ItemBuilder.createItem(plugin.getDescription().getName().replace("Factions", "JurassicCore").contains("Jurassic") ? Material.STAINED_GLASS_PANE : Material.STAINED_GLASS, "&c&l" + plugin.getDescription().getName().replace("Factions", "JurassicCore"), 1, 14, plugin.getDescription().getName().contains("Jurassic") ? "&7Author: &aApolloDev" : "&7Author: &a" + this.toReadableString(plugin.getDescription().getAuthors(), ChatColor.GREEN), "&7Version: &a" + plugin.getDescription().getVersion(), "&c&lDISABLED");
            }
            if(plugin.getDescription().getName().replace("Factions", "JurassicCore").contains("Jurassic")) {
                stack = ItemBuilder.modifyItem(stack, stack.getItemMeta().getDisplayName(), LoreUtil.getAndModifyLore(stack, "", "&7This plugin was specially developed by", "&7the JurassicPvP team to enhance your experience."));
            }
            items[i] = ClickableItem.empty(stack);
        }
        for(int i = 0; i < PluginViewer.inst.getRestPlugins().size(); i++) {
            Plugin plugin = PluginViewer.inst.getRestPlugins().get(i);
            ItemStack stack;
            if(plugin.isEnabled()) {
                stack = ItemBuilder.createItem(plugin.getDescription().getName().contains("Jurassic") ? Material.STAINED_GLASS_PANE : Material.STAINED_GLASS, "&a&l" + plugin.getDescription().getName().replace("Factions", "JurassicCore"), 1, 5, plugin.getDescription().getName().contains("Jurassic") ? "&7Author: &aApolloDev" : "&7Author: &a" + this.toReadableString(plugin.getDescription().getAuthors(), ChatColor.GREEN), "&7Version: &a" + plugin.getDescription().getVersion(), "&a&lENABLED");
            } else {
                stack = ItemBuilder.createItem(plugin.getDescription().getName().contains("Jurassic") ? Material.STAINED_GLASS_PANE : Material.STAINED_GLASS, "&c&l" + plugin.getDescription().getName().replace("Factions", "JurassicCore"), 1, 14, plugin.getDescription().getName().contains("Jurassic") ? "&7Author: &aApolloDev" : "&7Author: &a" + this.toReadableString(plugin.getDescription().getAuthors(), ChatColor.GREEN), "&7Version: &a" + plugin.getDescription().getVersion(), "&c&lDISABLED");

            }
            if(plugin.getDescription().getName().replace("Factions", "JurassicCore").contains("Jurassic")) {
                stack = ItemBuilder.modifyItem(stack, stack.getItemMeta().getDisplayName(), LoreUtil.getAndModifyLore(stack, "", "&7This plugin was specially developed by", "&7the JurassicPvP team to enhance your experience."));
            }
            items[i + PluginViewer.inst.getJurassicPlugins().size()] = ClickableItem.empty(stack);
        }
        for(int i = 0; i < PluginViewer.inst.getDisabledPlugins().size(); i++) {
            Plugin plugin = PluginViewer.inst.getDisabledPlugins().get(i);
            ItemStack stack;
            if(plugin.isEnabled()) {
                stack = ItemBuilder.createItem(plugin.getDescription().getName().contains("Jurassic") ? Material.STAINED_GLASS_PANE : Material.STAINED_GLASS, "&a&l" + plugin.getDescription().getName().replace("Factions", "JurassicCore"), 1, 5, plugin.getDescription().getName().contains("Jurassic") ? "&7Author: &aApolloDev" : "&7Author: &a" + this.toReadableString(plugin.getDescription().getAuthors(), ChatColor.GREEN), "&7Version: &a" + plugin.getDescription().getVersion(), "&a&lENABLED");
            } else {
                stack = ItemBuilder.createItem(plugin.getDescription().getName().contains("Jurassic") ? Material.STAINED_GLASS_PANE : Material.STAINED_GLASS, "&c&l" + plugin.getDescription().getName().replace("Factions", "JurassicCore"), 1, 14, plugin.getDescription().getName().contains("Jurassic") ? "&7Author: &aApolloDev" : "&7Author: &a" + this.toReadableString(plugin.getDescription().getAuthors(), ChatColor.GREEN), "&7Version: &a" + plugin.getDescription().getVersion(), "&c&lDISABLED");

            }
            if(plugin.getDescription().getName().replace("Factions", "JurassicCore").contains("Jurassic")) {
                stack = ItemBuilder.modifyItem(stack, stack.getItemMeta().getDisplayName(), LoreUtil.getAndModifyLore(stack, "", "&7This plugin was specially developed by", "&7the JurassicPvP team to enhance your experience."));
            }
            items[i + (PluginViewer.inst.getJurassicPlugins().size() + PluginViewer.inst.getRestPlugins().size())] = ClickableItem.empty(stack);
        }
        pagination.setItems(items);
        pagination.setItemsPerPage(51);
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));
        contents.set(5, 7, ClickableItem.of(ItemBuilder.createItem(Material.ARROW, "&e&lBack", "&7Click to go back."), e -> {
            INVENTORY.open(player, pagination.previous().getPage());
            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
        }));
        contents.set(5, 8, ClickableItem.of(ItemBuilder.createItem(Material.ARROW, "&e&lNext Page", "&7Click to go forward."), e -> {
            INVENTORY.open(player, pagination.next().getPage());
            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }

    private String toReadableString(List<String> list, ChatColor color) {
        if(list == null || list.isEmpty()) {
            return "Undefined";
        }
        String s = "";
        for (String player : list) {
            s = s + color + player + ChatColor.WHITE + ", ";
        }
        return s.substring(0, s.lastIndexOf(","));
    }
}
