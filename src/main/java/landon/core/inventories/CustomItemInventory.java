package landon.core.inventories;

import com.massivecraft.factions.P;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import landon.core.WarhammerCore;
import landon.core.util.GiveUtil;
import landon.core.util.anvilsearch.AnvilGUI;
import landon.core.util.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomItemInventory implements InventoryProvider {
    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .closeable(true)
            .id("oasisItemInventory")
            .size(6, 9)
            .title("Oasis Items")
            .provider(new CustomItemInventory())
            .manager(WarhammerCore.get().getInventoryManager())
            .build();
    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();
        ClickableItem[] items = new ClickableItem[WarhammerCore.get().getItemManager().getOasisItems().size()];
        for(int i = 0; i < items.length; i++) {
            items[i] = ClickableItem.of(WarhammerCore.get().getItemManager().getOasisItems().get(i).getItem(), e -> {
                player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
                new BukkitRunnable() {
                    public void run() {
                        GiveUtil.giveOrDropItem(player, e.getCurrentItem().clone());
                    }
                }.runTaskLater(P.p, 5L);
            });
        }
        pagination.setItems(items);
        pagination.setItemsPerPage(51);
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));
        contents.set(5, 6, ClickableItem.of(ItemBuilder.createItem(Material.ARROW, "&e&lBack", "&7Click to go back."), e -> {
            INVENTORY.open(player, pagination.previous().getPage());
            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
        }));
        contents.set(5, 7, ClickableItem.of(ItemBuilder.createItem(Material.SIGN, "&e&lSearch Items", "&7Click to search all items."), e -> {
            player.closeInventory();
            new AnvilGUI.Builder()
                    .onComplete((players, toSearch) -> {
                        new BukkitRunnable() {
                            public void run() {
                                players.setMetadata("lastOasisItemSearch", new FixedMetadataValue(P.p, toSearch));
                                CustomItemSearchedInventory.INVENTORY.open(players);
                            }
                        }.runTaskLater(P.p, 1L);
                        return AnvilGUI.Response.close();
                    })
                    .preventClose()
                    .text("Search")
                    .plugin(P.p)
                    .itemLeft(ItemBuilder.createItem(Material.BARRIER, ""))
                    .onLeftInputClick(player1 -> {
                        e.setCancelled(true);
                    })
                    .onRightInputClick(player1 -> {
                        e.setCancelled(true);
                    })
                    .open(player);
        }));
        contents.set(5, 8, ClickableItem.of(ItemBuilder.createItem(Material.ARROW, "&e&lNext Page", "&7Click to go forward."), e -> {
            INVENTORY.open(player, pagination.next().getPage());
            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        init(player, contents);
    }
}
