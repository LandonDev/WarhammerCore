package landon.warhammercore.titles.utils;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.*;
import landon.warhammercore.titles.mongo.TitleManager;
import landon.warhammercore.util.items.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TitleGUI implements InventoryProvider {
    public static SmartInventory open(Player player) {
        int unlockedTitles = TitleManager.get().getUnlockedTitles(player).size();
        int rows = (unlockedTitles <= 9 ? 1 : (unlockedTitles <= 18 ? 2 : (unlockedTitles <= 27 ? 3 : (unlockedTitles <= 36 ? 4 : (unlockedTitles <= 45 ? 5 : 6)))));
        return SmartInventory.builder()
                .title("Unlocked Titles (" + unlockedTitles + ")")
                .id("titleMenu")
                .size(rows, 9)
                .provider(new TitleGUI())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        List<String> unlockedTitles = TitleManager.get().getUnlockedTitles(player);
        if(unlockedTitles.size() > 52) {
            ClickableItem[] items = new ClickableItem[unlockedTitles.size()];
            for(int i = 0; i < unlockedTitles.size(); i++) {
                if(TitleManager.get().matches(TitleManager.get().getEquippedTitle(player.getUniqueId()), unlockedTitles.get(i))) {
                    String title = unlockedTitles.get(i);
                    items[i] = ClickableItem.of(ItemBuilder.createItem(Material.STAINED_GLASS_PANE, "&a" + ChatColor.stripColor(title), 1, 5, "&8[&r" + title + "&8]", "&a&lEQUIPPED", "&7Click to uneqip."), e -> {
                        e.setCancelled(true);
                        TitleManager.get().setEquippedTitle(player, null);
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                        init(player, contents);
                    });
                } else {
                    String title = unlockedTitles.get(i);
                    items[i] = ClickableItem.of(ItemBuilder.createItem(Material.STAINED_GLASS_PANE, "&f" + ChatColor.stripColor(title), 1, 0, "&8[&r" + title + "&8]", "&f&lUNLOCKED", "&7Click to equip."), e -> {
                        e.setCancelled(true);
                        TitleManager.get().setEquippedTitle(player, title);
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                        init(player, contents);
                    });
                }
            }
            Pagination pagination = contents.pagination();
            pagination.setItems(items);
            pagination.setItemsPerPage(52);
            pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));
            contents.set(5, 7, ClickableItem.of(ItemBuilder.createItem(Material.ARROW, "&e&lBack", "&7Click to go back."), e -> {
                TitleGUI.open(player).open(player, pagination.previous().getPage());
                player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
            }));
            contents.set(5, 8, ClickableItem.of(ItemBuilder.createItem(Material.ARROW, "&e&lNext Page", "&7Click to go forward."), e -> {
                TitleGUI.open(player).open(player, pagination.next().getPage());
                player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
            }));
        } else {
            SlotIterator iterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0);
            for (String title : unlockedTitles) {
                if(TitleManager.get().matches(TitleManager.get().getEquippedTitle(player.getUniqueId()), title)) {
                    contents.add(ClickableItem.of(ItemBuilder.createItem(Material.STAINED_GLASS_PANE, "&a" + ChatColor.stripColor(title), 1, 5, "&8[&r" + title + "&8]", "&a&lEQUIPPED", "&7Click to uneqip."), e -> {
                        e.setCancelled(true);
                        TitleManager.get().setEquippedTitle(player, null);
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                        init(player, contents);
                    }));
                } else {
                    contents.add(ClickableItem.of(ItemBuilder.createItem(Material.STAINED_GLASS_PANE, "&f" + ChatColor.stripColor(title), 1, 0, "&8[&r" + title + "&8]", "&f&lUNLOCKED", "&7Click to equip."), e -> {
                        e.setCancelled(true);
                        TitleManager.get().setEquippedTitle(player, title);
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                        init(player, contents);
                    }));
                }
            }
        }
    }
}
