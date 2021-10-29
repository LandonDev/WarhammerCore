package landon.warhammercore.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveUtil {
    public static void giveOrDropItem(Player player, ItemStack item) {
        int amountOfItems = 0;
        for(int i = 0; i < 36; i++) {
            if(player.getInventory().getItem(i) != null) {
                amountOfItems++;
            }
        }
        if(amountOfItems >= 36) {
            player.getWorld().dropItem(player.getLocation(), item);
            if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                player.sendMessage(c.c("&c&l(!) &cYour inventory was full so your item was dropped in front of you! (" + item.getItemMeta().getDisplayName() + "&c)"));
            } else {
                player.sendMessage(c.c("&c&l(!) &cYour inventory was full so your item was dropped in front of you!"));
            }
        } else {
            player.getInventory().addItem(item);
            if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                player.sendMessage(c.c("&a&l+ " + item.getAmount() + "x " + item.getItemMeta().getDisplayName()));
            }
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
        }
    }
}
