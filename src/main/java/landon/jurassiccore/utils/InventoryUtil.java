package landon.jurassiccore.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryUtil {
    public static boolean isInventoryFull(final Inventory inv, final int subtract, final int amount, final ItemStack item) {
        for (int i = 0; i < inv.getSize() - subtract; ++i) {
            final ItemStack is = inv.getItem(i);
            if (is == null) {
                return false;
            }
            if (is.getType() == item.getType()) {
                final ItemMeta im = is.getItemMeta();
                if (im != null && im.hasDisplayName()) {
                    if (!item.getItemMeta().getDisplayName().equals(im.getDisplayName())) {
                        continue;
                    }
                } else if (im != null && item.getItemMeta().hasDisplayName()) {
                    continue;
                }
                if (im == null || !im.hasLore() || item.getItemMeta().getLore() == im.getLore()) {
                    if (is.getDurability() == 0) {
                        if (is.getAmount() < is.getMaxStackSize() && is.getAmount() + amount <= is.getMaxStackSize()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
