package landon.core.util;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LoreUtil {
    public static List<String> getAndModifyLore(ItemStack item, String... toAdd) {
        List<String> lore;
        if(item.hasItemMeta() && item.getItemMeta().hasLore()) {
            lore = new ArrayList<>(item.getItemMeta().getLore());
        } else {
            lore = new ArrayList<>();
        }
        for (String s : toAdd) {
            lore.add(c.c(s));
        }
        return lore;
    }
}
