/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.factionpoints.CosmicFactionPoints
 *  com.cosmicpvp.factionpoints.managers.CoreChunkManager
 *  com.cosmicpvp.factionpoints.struct.ExpiringChunkData
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Creeper
 *  org.bukkit.entity.Enderman
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Fireball
 *  org.bukkit.entity.Hanging
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.entity.TNTPrimed
 *  org.bukkit.entity.ThrownPotion
 *  org.bukkit.entity.Wither
 *  org.bukkit.entity.WitherSkull
 *  org.bukkit.entity.minecart.ExplosiveMinecart
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityChangeBlockEvent
 *  org.bukkit.event.entity.EntityCombustByEntityEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.entity.EntityExplodeEvent
 *  org.bukkit.event.entity.EntityTargetEvent
 *  org.bukkit.event.entity.PotionSplashEvent
 *  org.bukkit.event.hanging.HangingBreakByEntityEvent
 *  org.bukkit.event.hanging.HangingBreakEvent
 *  org.bukkit.event.hanging.HangingBreakEvent$RemoveCause
 *  org.bukkit.event.hanging.HangingPlaceEvent
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.projectiles.ProjectileSource
 */
package com.massivecraft.factions.listeners;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.PowerLossEvent;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class FactionsEntityListener
        implements Listener {
    private static final Set<PotionEffectType> badPotionEffects = new LinkedHashSet<PotionEffectType>(Arrays.asList(PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HARM, PotionEffectType.HUNGER, PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.WEAKNESS, PotionEffectType.WITHER));
    public P p;

    public FactionsEntityListener(P p) {
        this.p = p;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent event) {
        String msg;
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) entity;
        FPlayer fplayer = FPlayers.i.get(player.getUniqueId().toString());
        Faction faction = Board.getFactionAt(new FLocation(player.getLocation()));
        PowerLossEvent powerLossEvent = new PowerLossEvent(faction, fplayer);
        if (faction.isWarZone()) {
            if (!Conf.warZonePowerLoss) {
                powerLossEvent.setMessage("<i>You didn't lose any power since you were in a war zone.");
                powerLossEvent.setCancelled(true);
            }
            if (Conf.worldsNoPowerLoss.contains(player.getWorld().getName())) {
                powerLossEvent.setMessage("<b>The world you are in has power loss normally disabled, but you still lost power since you were in a war zone.\n<i>Your power is now <h>%d / %d");
            }
        } else if (faction.isNone() && !Conf.wildernessPowerLoss && !Conf.worldsNoWildernessProtection.contains(player.getWorld().getName())) {
            powerLossEvent.setMessage("<i>You didn't lose any power since you were in the wilderness.");
            powerLossEvent.setCancelled(true);
        } else if (Conf.worldsNoPowerLoss.contains(player.getWorld().getName())) {
            powerLossEvent.setMessage("<i>You didn't lose any power due to the world you died in.");
            powerLossEvent.setCancelled(true);
        } else if (Conf.peacefulMembersDisablePowerLoss && fplayer.hasFaction() && fplayer.getFaction().isPeaceful()) {
            powerLossEvent.setMessage("<i>You didn't lose any power since you are in a peaceful faction.");
            powerLossEvent.setCancelled(true);
        } else {
            powerLossEvent.setMessage("<i>Your power is now <h>%d / %d");
        }
        Bukkit.getPluginManager().callEvent(powerLossEvent);
        if (!powerLossEvent.isCancelled()) {
            fplayer.onDeath();
        }
        if ((msg = powerLossEvent.getMessage()) != null && !msg.isEmpty()) {
            fplayer.msg(msg, fplayer.getPowerRounded(), fplayer.getPowerMaxRounded());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent) event;
            if (!this.canDamagerHurtDamagee(sub, true)) {
                event.setCancelled(true);
            }
        } else if (Conf.safeZonePreventAllDamageToPlayers && this.isPlayerInSafeZone(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity boomer;
        Location loc;
        block16:
        {
            block17:
            {
                Faction faction;
                boolean online;
                block14:
                {
                    block15:
                    {
                        block12:
                        {
                            block13:
                            {
                                loc = event.getLocation();
                                boomer = event.getEntity();
                                faction = Board.getFactionAt(new FLocation(loc));
                                if (faction.noExplosionsInTerritory()) {
                                    event.setCancelled(true);
                                    return;
                                }
                                online = true;
                                if (!(boomer instanceof Creeper)) break block12;
                                if (faction.isNone() && Conf.wildernessBlockCreepers && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName()) || faction.isNormal() && (!online ? Conf.territoryBlockCreepersWhenOffline : Conf.territoryBlockCreepers))
                                    break block13;
                                if ((!faction.isWarZone() || !Conf.warZoneBlockCreepers) && !faction.isSafeZone())
                                    break block12;
                            }
                            event.setCancelled(true);
                            return;
                        }
                        if (!(boomer instanceof Fireball) && !(boomer instanceof WitherSkull) && !(boomer instanceof Wither))
                            break block14;
                        if (faction.isNone() && Conf.wildernessBlockFireballs && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName()) || faction.isNormal() && (!online ? Conf.territoryBlockFireballsWhenOffline : Conf.territoryBlockFireballs))
                            break block15;
                        if ((!faction.isWarZone() || !Conf.warZoneBlockFireballs) && !faction.isSafeZone())
                            break block14;
                    }
                    event.setCancelled(true);
                    return;
                }
                if (!(boomer instanceof TNTPrimed) && !(boomer instanceof ExplosiveMinecart)) break block16;
                if (faction.isNone() && Conf.wildernessBlockTNT && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName()) || faction.isNormal() && (!online ? Conf.territoryBlockTNTWhenOffline : Conf.territoryBlockTNT))
                    break block17;
                if ((!faction.isWarZone() || !Conf.warZoneBlockTNT) && (!faction.isSafeZone() || !Conf.safeZoneBlockTNT))
                    break block16;
            }
            event.setCancelled(true);
            return;
        }
        if (!(boomer instanceof TNTPrimed)) {
            if (!(boomer instanceof ExplosiveMinecart)) return;
        }
        if (!Conf.handleExploitTNTWaterlog) return;
        Block center = loc.getBlock();
        if (!center.isLiquid()) return;
        ArrayList<Block> targets = new ArrayList<Block>();
        targets.add(center.getRelative(0, 0, 1));
        targets.add(center.getRelative(0, 0, -1));
        targets.add(center.getRelative(0, 1, 0));
        targets.add(center.getRelative(0, -1, 0));
        targets.add(center.getRelative(1, 0, 0));
        targets.add(center.getRelative(-1, 0, 0));
        for (Block target : targets) {
            int id = target.getTypeId();
            if (id == 0 || id >= 7 && id <= 11 || id == 49 || id == 90 || id == 116 || id == 119 || id == 120 || id == 130)
                continue;
            target.breakNaturally();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityCombustByEntity(EntityCombustByEntityEvent event) {
        EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent(event.getCombuster(), event.getEntity(), EntityDamageEvent.DamageCause.FIRE, 0);
        if (!this.canDamagerHurtDamagee(sub, false)) {
            event.setCancelled(true);
        }
        sub = null;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPotionSplashEvent(PotionSplashEvent event) {
        boolean badjuju = false;
        for (PotionEffect effect : event.getPotion().getEffects()) {
            if (!badPotionEffects.contains(effect.getType())) continue;
            badjuju = true;
            break;
        }
        if (!badjuju) {
            return;
        }
        ProjectileSource thrower = event.getPotion().getShooter();
        if (!(thrower instanceof Entity)) {
            return;
        }
        if (thrower instanceof Player) {
            Player player = (Player) thrower;
            FPlayer fPlayer = FPlayers.i.get(player);
            if (badjuju && fPlayer.getFaction().isPeaceful()) {
                event.setCancelled(true);
                return;
            }
        }
        for (LivingEntity target : event.getAffectedEntities()) {
            EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent((Entity) thrower, target, EntityDamageEvent.DamageCause.CUSTOM, 0);
            if (!this.canDamagerHurtDamagee(sub, true)) {
                event.setIntensity(target, 0.0);
            }
            sub = null;
        }
    }

    public boolean isPlayerInSafeZone(Entity damagee) {
        if (!(damagee instanceof Player)) {
            return false;
        }
        return Board.getFactionAt(new FLocation(damagee.getLocation())).isSafeZone();
    }

    public boolean canDamagerHurtDamagee(EntityDamageByEntityEvent sub) {
        return this.canDamagerHurtDamagee(sub, true);
    }

    public boolean canDamagerHurtDamagee(EntityDamageByEntityEvent sub, boolean notify) {
        FPlayer attacker;
        Entity damager = sub.getDamager();
        Entity damagee = sub.getEntity();
        double damage = sub.getDamage();
        if (!(damagee instanceof Player)) {
            return true;
        }
        FPlayer defender = FPlayers.i.get(damagee.getUniqueId().toString());
        if (defender == null || defender.getPlayer() == null) {
            return true;
        }
        Location defenderLoc = defender.getPlayer().getLocation();
        FLocation defenderFLoc = new FLocation(defenderLoc);
        Faction defLocFaction = Board.getFactionAt(defenderFLoc);
        if (damager instanceof Projectile && damager instanceof Entity && ((Projectile) damager).getShooter() != null) {
            damager = (Entity) ((Projectile) damager).getShooter();
        }
        if (damager == damagee) {
            return true;
        }
        if (defLocFaction.noPvPInTerritory()) {
            if (damager instanceof Player) {
                if (notify) {
                    attacker = FPlayers.i.get(damager.getUniqueId().toString());
                    attacker.msg("<i>You can't hurt other players in " + (defLocFaction.isSafeZone() ? "a SafeZone." : "peaceful territory."));
                }
                return false;
            }
            return !defLocFaction.noMonstersInTerritory();
        }
        if (!(damager instanceof LivingEntity)) {
            return true;
        }
        attacker = FPlayers.i.get(damager.getUniqueId().toString());
        if (attacker == null || attacker.getPlayer() == null) {
            return true;
        }
        if (Conf.playersWhoBypassAllProtection.contains(attacker.getNameAsync())) {
            return true;
        }
        if (attacker.hasLoginPvpDisabled()) {
            if (notify) {
                attacker.msg("<i>You can't hurt other players for " + Conf.noPVPDamageToOthersForXSecondsAfterLogin + " seconds after logging in.");
            }
            return false;
        }
        Faction locFaction = Board.getFactionAt(new FLocation(attacker));
        if (locFaction.noPvPInTerritory()) {
            if (notify) {
                attacker.msg("<i>You can't hurt other players while you are in " + (locFaction.isSafeZone() ? "a SafeZone." : "peaceful territory."));
            }
            return false;
        }
        if (locFaction.isWarZone() && Conf.warZoneFriendlyFire) {
            return true;
        }
        if (Conf.worldsIgnorePvP.contains(defenderLoc.getWorld().getName())) {
            return true;
        }
        Faction defendFaction = defender.getFaction();
        Faction attackFaction = attacker.getFaction();
        if (attackFaction.isNone() && Conf.disablePVPForFactionlessPlayers) {
            if (notify) {
                attacker.msg("<i>You can't hurt other players until you join a faction.");
            }
            return false;
        }
        if (defendFaction.isNone()) {
            if (defLocFaction == attackFaction && Conf.enablePVPAgainstFactionlessInAttackersLand) {
                return true;
            }
            if (Conf.disablePVPForFactionlessPlayers) {
                if (notify) {
                    attacker.msg("<i>You can't hurt players who are not currently in a faction.");
                }
                return false;
            }
        }
        if (defendFaction.isPeaceful()) {
            if (notify) {
                attacker.msg("<i>You can't hurt players who are in a peaceful faction.");
            }
            return false;
        }
        if (attackFaction.isPeaceful()) {
            if (notify) {
                attacker.msg("<i>You can't hurt players while you are in a peaceful faction.");
            }
            return false;
        }
        Relation relation = defendFaction.getRelationTo(attackFaction);
        if (Conf.disablePVPBetweenNeutralFactions && relation.isNeutral()) {
            if (notify) {
                attacker.msg("<i>You can't hurt neutral factions. Declare them as an enemy.");
            }
            return false;
        }
        if (!defender.hasFaction()) {
            return true;
        }
        if (relation.isMember() || relation.isAlly()) {
            if (notify) {
                attacker.msg("<i>You can't hurt %s<i>.", defender.describeToAsync(attacker));
            }
            return false;
        }
        if (relation.isTruce() && !Conf.trucesCanHitEachOther) {
            if (notify) {
                attacker.msg("<i>You can't hurt %s<i>.", defender.describeToAsync(attacker));
            }
            return false;
        }
        if (relation.isTruce()) {
            return true;
        }
        boolean ownTerritory = defender.isInOwnTerritory();
        if (ownTerritory && relation.isNeutral()) {
            if (notify) {
                attacker.msg("<i>You can't hurt %s<i> in their own territory unless you declare them as an enemy.", defender.describeToAsync(attacker));
                defender.msg("%s<i> tried to hurt you.", attacker.describeToAsync(defender, true));
            }
            return false;
        }
        if (damage > 0.0 && ownTerritory && Conf.territoryShieldFactor > 0.0) {
            /*ExpiringChunkData data;
            if (P.p.factionPointsEnabled && ((data = CosmicFactionPoints.get().getChunkManager().getChunksToExpire().get(defenderFLoc)) == null || System.currentTimeMillis() - data.getTimeClaimed() < TimeUnit.HOURS.toMillis(24L))) {
                return true;
            }*/
            double new_damage = damage * (1.0 - Conf.territoryShieldFactor);
            sub.setDamage(new_damage);
            if (notify) {
                String perc = MessageFormat.format("{0,number,#%}", Conf.territoryShieldFactor);
                defender.msg("<i>Enemy damage reduced by <rose>%s<i>.", perc);
            }
        }
        return true;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Entity target = event.getTarget();
        if (target == null) {
            return;
        }
        EntityType entity = MiscUtil.creatureTypeFromEntity(event.getEntity());
        if (entity != null && !Conf.safeZoneNerfedCreatureTypes.contains(entity.name())) {
            return;
        }
        if (Board.getFactionAt(new FLocation(target.getLocation())).noMonstersInTerritory()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPaintingBreak(HangingBreakEvent event) {
        block8:
        {
            block9:
            {
                if (event.isCancelled()) {
                    return;
                }
                if (event.getCause() != HangingBreakEvent.RemoveCause.EXPLOSION) break block8;
                Location loc = event.getEntity().getLocation();
                Faction faction = Board.getFactionAt(new FLocation(loc));
                if (faction.noExplosionsInTerritory()) {
                    event.setCancelled(true);
                    return;
                }
                boolean online = faction.hasPlayersOnline();
                if (faction.isNone() && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName()) && (Conf.wildernessBlockCreepers || Conf.wildernessBlockFireballs || Conf.wildernessBlockTNT) || faction.isNormal() && (!online ? Conf.territoryBlockCreepersWhenOffline || Conf.territoryBlockFireballsWhenOffline || Conf.territoryBlockTNTWhenOffline : Conf.territoryBlockCreepers || Conf.territoryBlockFireballs || Conf.territoryBlockTNT))
                    break block9;
                if ((!faction.isWarZone() || !Conf.warZoneBlockCreepers && !Conf.warZoneBlockFireballs && !Conf.warZoneBlockTNT) && !faction.isSafeZone())
                    break block8;
            }
            event.setCancelled(true);
        }
        if (!(event instanceof HangingBreakByEntityEvent)) {
            return;
        }
        Entity breaker = ((HangingBreakByEntityEvent) event).getRemover();
        if (!(breaker instanceof Player)) {
            return;
        }
        if (!FactionsBlockListener.playerCanBuildDestroyBlock((Player) breaker, event.getEntity().getLocation(), null, "remove paintings", false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPaintingPlace(HangingPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), null, "place paintings", false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        block8:
        {
            block9:
            {
                Location loc;
                Entity entity;
                block7:
                {
                    if (event.isCancelled()) {
                        return;
                    }
                    entity = event.getEntity();
                    if (!(entity instanceof Enderman) && !(entity instanceof Wither)) {
                        return;
                    }
                    loc = event.getBlock().getLocation();
                    if (!(entity instanceof Enderman)) break block7;
                    if (this.stopEndermanBlockManipulation(loc)) {
                        event.setCancelled(true);
                    }
                    break block8;
                }
                if (!(entity instanceof Wither)) break block8;
                Faction faction = Board.getFactionAt(new FLocation(loc));
                if (faction.isNone() && Conf.wildernessBlockFireballs && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName()) || faction.isNormal() && (!faction.hasPlayersOnline() ? Conf.territoryBlockFireballsWhenOffline : Conf.territoryBlockFireballs))
                    break block9;
                if ((!faction.isWarZone() || !Conf.warZoneBlockFireballs) && !faction.isSafeZone()) break block8;
            }
            event.setCancelled(true);
        }
    }

    private boolean stopEndermanBlockManipulation(Location loc) {
        if (loc == null) {
            return false;
        }
        if (Conf.wildernessDenyEndermanBlocks && Conf.territoryDenyEndermanBlocks && Conf.territoryDenyEndermanBlocksWhenOffline && Conf.safeZoneDenyEndermanBlocks && Conf.warZoneDenyEndermanBlocks) {
            return true;
        }
        FLocation fLoc = new FLocation(loc);
        Faction claimFaction = Board.getFactionAt(fLoc);
        if (claimFaction.isNone()) {
            return Conf.wildernessDenyEndermanBlocks;
        }
        if (claimFaction.isNormal()) {
            return claimFaction.hasPlayersOnline() ? Conf.territoryDenyEndermanBlocks : Conf.territoryDenyEndermanBlocksWhenOffline;
        }
        if (claimFaction.isSafeZone()) {
            return Conf.safeZoneDenyEndermanBlocks;
        }
        if (claimFaction.isWarZone()) {
            return Conf.warZoneDenyEndermanBlocks;
        }
        return false;
    }
}

