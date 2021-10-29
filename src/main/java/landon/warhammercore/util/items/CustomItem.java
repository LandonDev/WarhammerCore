package landon.warhammercore.util.items;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public abstract class CustomItem {
    private String name;
    private ItemStack item;

    public CustomItem(String name, ItemStack item) {
        this.name = name;
        this.item = item;
    }

    public abstract void onClick(PlayerInteractEvent event);

    public abstract void onInventoryClick(InventoryClickEvent event, Player player);

    public void removeItem(Player player, ItemStack is) {
        if(is.getAmount() > 1) {
            is.setAmount(is.getAmount() - 1);
        } else {
            player.setItemInHand(null);
        }
    }

    public void clear(InventoryClickEvent e) {
        if(e.getCursor().getAmount() > 1) {
            e.getCursor().setAmount(e.getCursor().getAmount() - 1);
        } else {
            e.getWhoClicked().setItemOnCursor(null);
        }
    }
}
