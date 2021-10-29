package landon.warhammercore.titles.listeners;

import de.tr7zw.changeme.nbtapi.NBTItem;
import landon.warhammercore.titles.mongo.TitleManager;
import landon.warhammercore.util.c;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class VoucherListener implements Listener {
    @EventHandler
    public void titleRedeem(PlayerInteractEvent e) {
        if(e.getItem() != null && e.getItem().getType() != Material.AIR && new NBTItem(e.getItem()).hasKey("titleVoucher")) {
            e.setCancelled(true);
            UUID title = UUID.fromString(new NBTItem(e.getItem()).getString("titleVoucher"));
            if(!TitleManager.get().hasTitle(e.getPlayer(), title)) {
                if(e.getItem().getAmount() > 1) {
                    e.getItem().setAmount(e.getItem().getAmount() - 1);
                } else {
                    e.getItem().setAmount(0);
                }
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
                TitleManager.get().unlockTitle(e.getPlayer(), title);
                e.getPlayer().sendMessage(c.c("&b&l(!) &bYou have successfully unlocked the &n" + ChatColor.stripColor(TitleManager.get().getTitle(title.toString())) + "&b!"));
                e.getPlayer().sendMessage(c.c("&7View your unlocked titles and equip new ones using /title."));
            } else {
                e.getPlayer().sendMessage(c.c("&c&l(!) &cYou already have the &n" + ChatColor.stripColor(TitleManager.get().getTitle(title.toString())) + "&c title unlocked!"));
            }
        }
    }
}
