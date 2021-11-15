package landon.warhammercore.patchapi.patches.combattag;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.massivecraft.factions.P;
import com.wimbli.WorldBorder.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class CombatLogCombatListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCombatTag(EntityDamageEvent e) {
        if (e.getDamage() <= 0.0D)
            return;
        if (e.getCause() == EntityDamageEvent.DamageCause.CONTACT || e
                .getCause() == EntityDamageEvent.DamageCause.STARVATION || e
                .getCause() == EntityDamageEvent.DamageCause.FALL || e
                .getCause() == EntityDamageEvent.DamageCause.SUFFOCATION || e
                .getCause() == EntityDamageEvent.DamageCause.FALLING_BLOCK || e
                .getCause() == EntityDamageEvent.DamageCause.LAVA || e
                .getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e
                .getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
            return;
        if (e.getEntity() instanceof Player || (e instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent)e)
                .getDamager() instanceof Player) || (e instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent)e)
                .getDamager() instanceof Projectile && ((Projectile)((EntityDamageByEntityEvent)e).getDamager()).getShooter() instanceof Player)) {
            if (e.getEntity() instanceof Player) {
                if (e instanceof EntityDamageByEntityEvent) {
                    Entity damager = ((EntityDamageByEntityEvent)e).getDamager();
                    if (damager instanceof LivingEntity) {
                        LivingEntity leDamager = (LivingEntity)damager;
                        if (leDamager.getType() == EntityType.MAGMA_CUBE || (((CraftLivingEntity)leDamager).getHandle()).fromMobSpawner)
                            return;
                    }
                }
                disableFlight((Player)e.getEntity());
                CombatLog.flagCombat((Player)e.getEntity());
            }
            if (e instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent)e).getDamager() instanceof Player && (
                    e.getEntity() instanceof Player || e.getEntity().hasMetadata("boss") || e.getEntity().hasMetadata("minion") || e.getEntity().hasMetadata("endMonsterKills"))) {
                Player pDamager = (Player)((EntityDamageByEntityEvent)e).getDamager();
                if (e.getEntity() instanceof Player) {
                    Player pHurt = (Player)e.getEntity();
                    pHurt.setMetadata("lastPlayerDamagerUUID", (MetadataValue)new FixedMetadataValue((Plugin)P.p, pDamager.getUniqueId()));
                    if (!pHurt.canSee(pDamager)) {
                        boolean hasFeignDeath = pDamager.hasMetadata("feign_vanish");
                        boolean allowedToOverride = ((hasFeignDeath && pDamager.hasMetadata("feign_hits") && pDamager.hasMetadata("feign_hits_max") && ((MetadataValue)pDamager.getMetadata("feign_hits").get(0)).asInt() < ((MetadataValue)pDamager.getMetadata("feign_hits_max").get(0)).asInt()) || hasFeignDeath);
                        if (!allowedToOverride) {
                            User uDamager = ((Essentials)Bukkit.getPluginManager().getPlugin("Essentials")).getUser(pDamager);
                            User uHurt = ((Essentials)Bukkit.getPluginManager().getPlugin("Essentials")).getUser(pHurt);
                            if (!uDamager.isVanished()) {
                                pHurt.showPlayer(pDamager);
                                Bukkit.getLogger().info("[Arkkit (CombatLog)] Player " + pHurt.getName() + " could not see " + pDamager.getName() + ", executed .showPlayer()[1]!");
                            } else {
                                Bukkit.getLogger().info("[Arkkit (CombatLog)] Skipping .showPlayer()[1] on " + pHurt.getName() + ".show(" + pDamager.getName() + ") because " + pDamager.getName() + " is vanished!");
                            }
                            if (!uHurt.isVanished()) {
                                pDamager.showPlayer(pHurt);
                                Bukkit.getLogger().info("[Arkkit (CombatLog)] Player " + pHurt.getName() + " could not see " + pDamager.getName() + ", executed .showPlayer()[2]!");
                            } else {
                                Bukkit.getLogger().info("[Arkkit (CombatLog)] Skipping .showPlayer()[2] on " + pDamager.getName() + ".show(" + pHurt.getName() + ") because " + pHurt.getName() + " is vanished!");
                            }
                        }
                    }
                }
                disableFlight(pDamager);
                CombatLog.flagCombat(pDamager);
            }
            if (e instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent)e).getDamager() instanceof Projectile && ((Projectile)((EntityDamageByEntityEvent)e).getDamager()).getShooter() instanceof Player &&
                    e.getEntity() instanceof Player) {
                disableFlight((Player)((Projectile)((EntityDamageByEntityEvent)e).getDamager()).getShooter());
                CombatLog.flagCombat((Player)((Projectile)((EntityDamageByEntityEvent)e).getDamager()).getShooter());
            }
        }
    }

    private void disableFlight(Player pl) {
        pl.setFlying(false);
        pl.setAllowFlight(false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player)
            CombatLog.flagCombat((Player)e.getEntity());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent e) {
        if (CombatLog.inCombat(e.getPlayer())) {
            if (e.getPlayer().isOp() || e.getPlayer().hasPermission("core.combatlog.use_commands")) {
                String pName = e.getPlayer().getName();
                if (!pName.equals("TBNRfrags"))
                    return;
            }
            String rootCommand = e.getMessage().toLowerCase();
            if (rootCommand.contains(" "))
                rootCommand = rootCommand.split(" ")[0];
            if (rootCommand.equals("/ci") || rootCommand
                    .equals("/clearinventory") || rootCommand
                    .equals("/eci") || rootCommand
                    .equals("/clean") || rootCommand
                    .equals("/eclean") || rootCommand
                    .equals("/clear") || rootCommand
                    .equals("/eclear") || rootCommand
                    .equals("/clearinvent") || rootCommand
                    .equals("/eclearinvent") || rootCommand
                    .equals("/eclearinventory")) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.RED + "You cannot use that command while in combat!");
            }
            if (e.getMessage().contains(":") || rootCommand
                    .equals("/tpahere") || rootCommand
                    .equals("/etpahere") || rootCommand
                    .equals("/tpa") || rootCommand
                    .equals("/etpa") || rootCommand
                    .equals("/tpaccept") || rootCommand
                    .equals("/tpyes") || rootCommand
                    .equals("/etpyes") || rootCommand
                    .equals("/etpaccept") || rootCommand
                    .equals("/spawn") || rootCommand
                    .equals("/home") || rootCommand
                    .equals("/espawn") || rootCommand
                    .equals("/ehome") || rootCommand
                    .equals("/vault") || rootCommand
                    .equals("/pvault") || rootCommand
                    .equals("/pv") || rootCommand
                    .equals("/echest") || rootCommand
                    .equals("/enderchest") || rootCommand
                    .equals("/ec") || rootCommand
                    .equals("/bec") || rootCommand
                    .equals("/betterenderchest") || rootCommand
                    .equals("/hub") || rootCommand
                    .equals("/lobby") || rootCommand
                    .equals("/chest") || rootCommand
                    .equals("/vault") || rootCommand
                    .equals("/playervaults") || rootCommand
                    .equals("/hub") || rootCommand
                    .equals("/quit") || rootCommand
                    .equals("/lobby") || rootCommand
                    .equals("/server") || rootCommand
                    .equals("/ah") || rootCommand
                    .equals("/auction") || rootCommand
                    .equals("/auc") || rootCommand
                    .equals("/fly") || rootCommand
                    .equals("/efly") || rootCommand
                    .equals("/tinkerer") || rootCommand
                    .equals("/enchanter") || rootCommand
                    .equals("/bid") || rootCommand
                    .equals("/gift") || rootCommand
                    .equals("/merchant") || rootCommand
                    .equals("/armormerchant") || rootCommand
                    .equals("/showcase") || rootCommand
                    .equals("/tnt") || rootCommand
                    .equals("/sell") || rootCommand
                    .equals("/esell") || rootCommand
                    .equals("/coinflip") || rootCommand
                    .equals("/cf") || rootCommand
                    .equals("/shop") || rootCommand
                    .equals("/store") || rootCommand
                    .equals("/shops") || rootCommand
                    .equals("/cosmicshop") || rootCommand
                    .equals("/cshop") || rootCommand
                    .equals("/fund") || rootCommand
                    .equals("/cosmicfund") || rootCommand
                    .equals("/trade") || rootCommand
                    .equals("/kit") || rootCommand
                    .equals("/kits") || rootCommand
                    .equals("/ekit") || rootCommand
                    .equals("/ekits") || rootCommand
                    .equals("/tnt")) {
                e.setCancelled(true);
                e.setMessage("/null");
                e.getPlayer().sendMessage(ChatColor.RED + "You cannot use that command while in combat!");
            }
            if ((rootCommand.equals("/heal") || rootCommand.equals("/eheal")) && P.p.getConfig().getBoolean("patches.combat_log.block_heal")) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.RED + "You cannot use that command while in combat!");
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getPlayer().hasMetadata("ignore_combat_tag"))
            return;
        if (CombatLog.inCombat(e.getPlayer())) {
            if (e.getPlayer().isOp() || e.getPlayer().hasPermission("core.combatlog.use_enderpearls"))
                return;
            if (e.getPlayer().hasMetadata("bypassCombatTeleport") && (
                    (Location)((MetadataValue)e.getPlayer().getMetadata("bypassCombatTeleport").get(0)).value()).equals(e.getTo())) {
                e.getPlayer().removeMetadata("bypassCombatTeleport", P.p);
                return;
            }
            if ((CombatLog.block_enderpearls || e.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) && (e.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN || !CombatLog.duel_plugin)) {
                if (!e.getTo().getWorld().equals(e.getFrom().getWorld())) {
                    e.setCancelled(true);
                    if (!e.getPlayer().hasMetadata("core.combatlog.tp_deny") || System.currentTimeMillis() - ((MetadataValue)e.getPlayer().getMetadata("core.combatlog.tp_deny").get(0)).asLong() > 5000L) {
                        e.getPlayer().setMetadata("core.combatlog.tp_deny", (MetadataValue)new FixedMetadataValue((Plugin)P.p, Long.valueOf(System.currentTimeMillis())));
                        e.getPlayer().sendMessage(ChatColor.RED + "You cannot teleport while in combat!");
                    }
                    return;
                }
                if (WorldBorder.plugin.getWorldBorder(e.getFrom().getWorld().getName()) != null &&
                        !WorldBorder.plugin.GetWorldBorder(e.getFrom().getWorld().getName()).insideBorder(e.getFrom()))
                    return;
                double dist_sqrd = e.getTo().distanceSquared(e.getFrom());
                if (dist_sqrd >= 25.0D) {
                    e.setCancelled(true);
                    if (!e.getPlayer().hasMetadata("core.combatlog.tp_deny") || System.currentTimeMillis() - ((MetadataValue)e.getPlayer().getMetadata("core.combatlog.tp_deny").get(0)).asLong() > 5000L) {
                        e.getPlayer().setMetadata("core.combatlog.tp_deny", (MetadataValue)new FixedMetadataValue((Plugin)P.p, Long.valueOf(System.currentTimeMillis())));
                        e.getPlayer().sendMessage(ChatColor.RED + "You cannot teleport while in combat!");
                    }
                }
            }
        }
    }

    public static int getTimeLeftMSInCombat(Player pl) {
        if (pl.hasMetadata("cl_combat") && pl.getMetadata("cl_combat").get(0) != null &&
                System.currentTimeMillis() - ((MetadataValue)pl.getMetadata("cl_combat").get(0)).asLong() <= 10000L)
            return (int)(10000L - System.currentTimeMillis() - ((MetadataValue)pl.getMetadata("cl_combat").get(0)).asLong());
        return 0;
    }
}
