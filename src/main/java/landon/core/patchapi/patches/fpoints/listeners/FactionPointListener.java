package landon.core.patchapi.patches.fpoints.listeners;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.FLogType;
import java.util.concurrent.TimeUnit;

import com.massivecraft.factions.util.TimeUtils;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import landon.core.patchapi.patches.fpoints.FactionPoints;
import landon.core.patchapi.patches.fpoints.FactionsPointsAPI;
import landon.core.patchapi.patches.fpoints.utils.FactionUtils;
import landon.core.patchapi.patches.fpoints.utils.NumberUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FactionPointListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String lower = event.getMessage().toLowerCase();
        if (lower.startsWith("/f point")) {
            event.setMessage(lower.replace("/f points", "/factionpoints")
                    .replace("/f point", "/factionpoints"));
        } else if (lower.startsWith("/faction point")) {
            event.setMessage(lower.replace("/faction point", "/factionpoints")
                    .replace("/faction points", "/factionpoints"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            Player player = (Player)event.getRightClicked();
            if (player.hasMetadata("NPC") && player.getName().contains("Point Merchant")) {
                event.setCancelled(true);
                player.performCommand("/factionpoints buy");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractPointItem(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemStack item = event.getItem();
            if (item == null || item.getType() == Material.AIR)
                return;
            NBTItem wrapper = new NBTItem(item);
            Player player = event.getPlayer();
            if (FactionsPointsAPI.isPointItem(wrapper)) {
                event.setCancelled(true);
                if (player.getWorld().getName().startsWith("world_duels")) {
                    player.sendMessage(CC.RedB + "(!) " + CC.Red + "You cannot claim Faction Points inside the duel world!");
                    return;
                }
                NBTCompound builder = wrapper.getCompound("cosmicData");
                int points = builder.getInteger("points");
                if (points >= 0) {
                    Long timeCreated = builder.getLong("created");
                    if (timeCreated != null) {
                        Long manualExpiration = builder.getLong("expiration");
                        long expireTime = TimeUnit.HOURS.toMillis(24L);
                        long timeSinceCreated = System.currentTimeMillis() - timeCreated.longValue();
                        if ((manualExpiration != null && manualExpiration.longValue() != 0L && manualExpiration.longValue() < System.currentTimeMillis()) || timeSinceCreated >= expireTime) {
                            long timeSinceExpired = (manualExpiration != null && manualExpiration.longValue() != 0L) ? (System.currentTimeMillis() - manualExpiration.longValue()) : (timeSinceCreated - expireTime);
                            if (timeSinceExpired > 0L) {
                                player.sendMessage(CC.RedB + "(!) " + CC.Red + "Your Factions Point Note expired " + CC.RedU +
                                        TimeUtils.formatSeconds(timeSinceExpired / 1000L) + CC.Red + " ago, it has been removed!");
                                player.setItemInHand(null);
                                player.updateInventory();
                                player.playSound(player.getLocation(), Sound.ZOMBIE_WOODBREAK, 1.0F, 1.1F);
                                FactionPoints.log("Removing" + item + " from " + player.getName() + " due to it expiring " + timeSinceExpired + "ms ago!");
                                return;
                            }
                        }
                    }
                    Faction facIn = FactionUtils.getFaction(player, false);
                    if (facIn == null || !facIn.isNormal()) {
                        player.sendMessage(CC.RedB + "(!) " + CC.Red + "You must be in a Faction to claim Faction Points!");
                        return;
                    }
                    int pointsToApply = points;
                    Long newPoints = FactionsPointsAPI.modifyPoints(facIn, pointsToApply, true);
                    if (newPoints == null) {
                        player.playSound(player.getLocation(), Sound.FIZZ, 1.1F, 1.0F);
                        return;
                    }
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        player.setItemInHand(null);
                    }
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.4F);
                    player.updateInventory();
                    facIn.sendMessage(CC.GreenB + "+ " + CC.Green + NumberUtils.formatSeconds(pointsToApply) + " Faction Points (" + player.getName() + ")");
                    try {
                        FactionPoints.get().getPointManager().logPointChange(facIn, player, "redeemed", pointsToApply);
                        P.p.getFlogManager().log(facIn, FLogType.F_POINTS, new String[] { CC.GreenB + "+ " + CC.Green + pointsToApply + " Faction Points (" + newPoints + ") ", player.getName(), "- " + CC.GreenB + "REDEEMED" });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

