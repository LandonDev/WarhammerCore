package landon.warhammercore.deathbans.lives;

import de.tr7zw.changeme.nbtapi.NBTItem;
import landon.warhammercore.WarhammerCore;
import landon.warhammercore.deathbans.Deathban;
import landon.warhammercore.deathbans.DeathbanManager;
import landon.warhammercore.util.c;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class LifeListeners implements Listener {
    @EventHandler
    public void deathEvent(PlayerDeathEvent e) {
        if(e.getEntity().getKiller() != null) {
            DeathbanManager.get().addKill(e.getEntity().getKiller());
        }
        String newDeathMessage;
            if(e.getEntity().getKiller() != null) {
                newDeathMessage = c.c(e.getDeathMessage().replace(e.getEntity().getName(), "&c" + e.getEntity().getName() + " [" + DeathbanManager.get().getTotalKills(e.getEntity().getUniqueId()) + "]&f").replace(e.getEntity().getKiller().getName(), "&a" + e.getEntity().getKiller().getName() + " [" + DeathbanManager.get().getTotalKills(e.getEntity().getKiller().getUniqueId()) + "]&f"));
            } else {
                newDeathMessage = c.c(e.getDeathMessage().replace(e.getEntity().getName(), "&c" + e.getEntity().getName() + " [" + DeathbanManager.get().getTotalKills(e.getEntity().getUniqueId()) + "]&f"));
            }
        Bukkit.broadcastMessage(newDeathMessage);
        e.setDeathMessage("");
        e.getEntity().getWorld().strikeLightningEffect(e.getEntity().getLocation());
        e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.AMBIENCE_THUNDER, 1.0F, 1.0F);
        WarhammerCore.get().getLifeManager().getLives(e.getEntity().getUniqueId(), lives -> {
            if(lives <= 1) {
                DeathbanManager.get().createDeathban(e.getEntity().getName(), e.getEntity().getUniqueId(), 1800L, newDeathMessage);
            } else {
                WarhammerCore.get().getLifeManager().removeLives(e.getEntity().getUniqueId(), 1);
            }
        });
    }

    @EventHandler
    public void redeemLifeItem(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(e.getItem() != null && new NBTItem(e.getItem()).hasKey("lifeCount")) {
                e.setCancelled(true);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                int amount = new NBTItem(e.getItem()).getInteger("lifeCount");
                e.getPlayer().sendMessage(c.c("&a&l+ " + amount + " LIVE(s)"));
                e.getPlayer().sendMessage(c.c("&7You can check your lives using /lives."));
                ItemStack newItem = e.getItem().clone();
                newItem.setAmount(e.getItem().getAmount() - 1);
                e.getPlayer().setItemInHand(newItem);
                WarhammerCore.get().getLifeManager().addLives(e.getPlayer().getUniqueId(), amount);
            }
        }
    }

    @EventHandler
    public void quitListener(PlayerQuitEvent e) {
        if(DeathbanManager.get().getTotalKills(e.getPlayer().getUniqueId()) > 0) {
            DeathbanManager.get().storeKills(e.getPlayer().getUniqueId(), false);
        }
    }

    @EventHandler
    public void joinEvent(PlayerJoinEvent e) {
        if(DeathbanManager.get().isDeathbanned(e.getPlayer())) {
            Deathban deathban = DeathbanManager.get().findDeathban(e.getPlayer());
            e.getPlayer().kickPlayer(deathban.generateKickMessage());
        }
    }
}
