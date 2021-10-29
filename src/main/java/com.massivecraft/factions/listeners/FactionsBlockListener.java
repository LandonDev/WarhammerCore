/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.CreatureSpawner
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockPistonExtendEvent
 *  org.bukkit.event.block.BlockPistonRetractEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.material.MaterialData
 */
package com.massivecraft.factions.listeners;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.*;
import com.massivecraft.factions.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FactionsBlockListener
        implements Listener {
    public P p;
    private long placeTimer = TimeUnit.SECONDS.toMillis(15L);

    public FactionsBlockListener(P p) {
        this.p = p;
    }

    public static boolean playerCanBuildDestroyBlock(Player player, Location location, MaterialData material, String action, boolean justCheck) {
        FactionPermissions perms;
        boolean anyAccess;
        String name = player.getName();
        if (Conf.playersWhoBypassAllProtection.contains(name)) {
            return true;
        }
        FPlayer me = FPlayers.i.get(player.getUniqueId().toString());
        if (me.isAdminBypassing()) {
            return true;
        }
        FLocation loc = new FLocation(location);
        Faction otherFaction = Board.getFactionAt(loc);
        if (otherFaction.isNone()) {
            if (!Conf.wildernessDenyBuild || Conf.worldsNoWildernessProtection.contains(location.getWorld().getName())) {
                return true;
            }
            if (!justCheck) {
                me.msg("<b>You can't " + action + " in the wilderness.");
            }
            return false;
        }
        if (otherFaction.isSafeZone()) {
            if (!Conf.safeZoneDenyBuild || Permission.MANAGE_SAFE_ZONE.has(player)) {
                return true;
            }
            if (!justCheck) {
                me.msg("<b>You can't " + action + " in a safe zone.");
            }
            return false;
        }
        if (otherFaction.isWarZone()) {
            if (!Conf.warZoneDenyBuild || Permission.MANAGE_WAR_ZONE.has(player)) {
                return true;
            }
            if (!justCheck) {
                me.msg("<b>You can't " + action + " in a war zone.");
            }
            return false;
        }
        if (FactionPermissions.debug) {
            System.out.println("Block build check: " + action);
        }
        Faction myFaction = me.getFaction();
        FactionWrapper fWrapper = FactionWrappers.get(otherFaction.getId());
        if (fWrapper.isLocationTracked(loc)) {
            boolean access = true;
            if (action.equals("build") || action.equals("destroy")) {
                if (!FactionsBlockListener.hasAccessToBuild(me, otherFaction, loc, action, material)) {
                    access = false;
                    if (FactionPermissions.debug) {
                        Bukkit.getLogger().info("Denying hasAccessToBuild for " + me.getNameAsync());
                    }
                }
            } else if (action.equals("interact") && FactionsBlockListener.hasAccessToInteract(me, otherFaction, loc, action, material) != null) {
                access = false;
                if (FactionPermissions.debug) {
                    Bukkit.getLogger().info("Denying hasAccessToInteract for " + me.getNameAsync());
                }
            }
            if (fWrapper.doesPlayerHaveChunkAccess(loc, me, true)) {
                if (!access) {
                    if (!TimeUtil.isOnCooldown(player, "deny_msg")) {
                        me.msg("<b>You can't " + action + " in this territory, it is /f owner'd by: " + otherFaction.getTag());
                        TimeUtil.setCooldown(player, "deny_msg", 1);
                    }
                    return false;
                }
                return true;
            }
            if (!access) {
                if (!TimeUtil.isOnCooldown(player, "deny_msg")) {
                    player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "This chunk is privately owned by " + otherFaction.getTag() + " and you do not have access.");
                    TimeUtil.setCooldown(player, "deny_msg", 1);
                }
                return false;
            }
        }
        Relation rel = myFaction.getRelationTo(otherFaction);
        boolean online = false;
        boolean pain = !justCheck && rel.confPainBuild(online);
        boolean deny = rel.confDenyBuild(online);
        if (pain) {
            player.damage((double) Conf.actionDeniedPainAmount);
            if (!deny) {
                me.msg("<b>It is painful to try to " + action + " in the territory of " + otherFaction.getTag(myFaction));
            }
        }
        boolean bl = anyAccess = (perms = PermissionManager.get().getPermissions(otherFaction)) != null && perms.hasChunkSpecificPerms(loc);
        if (anyAccess) {
            if (rel != Relation.ENEMY) {
                if (action.equals("build") || action.equals("destroy")) {
                    if (!FactionsBlockListener.hasAccessToBuild(me, otherFaction, loc, action, material)) {
                        me.msg("<b>You can't " + action + " in this territory, it is owned by: " + otherFaction.getTag());
                        return false;
                    }
                    if (FactionPermissions.debug) {
                        Bukkit.getLogger().info("Allowing hasAccessToBuild for " + me.getNameAsync() + " in " + otherFaction.getTag());
                    }
                    deny = false;
                } else if (action.equals("interact")) {
                    if (FactionPermissions.debug) {
                        System.out.println("Interact!");
                    }
                    if (FactionsBlockListener.hasAccessToInteract(me, otherFaction, loc, action, material) != null) {
                        if (!TimeUtil.isOnCooldown(player, "lastInteractMsg")) {
                            me.msg("<b>You can't " + action + " in this territory, it is owned by: " + otherFaction.getTag());
                            TimeUtil.setCooldown(player, "lastInteractMsg", 1);
                        }
                        return false;
                    }
                    deny = false;
                }
            }
        } else {
            if (otherFaction.getRelationTo(me) == Relation.MEMBER && me.getRole() == Role.RECRUIT) {
                player.sendMessage(ChatColor.RED + "You are a Recruit, you must be promoted to Member or given /f access to this chunk.");
                return false;
            }
            if (FactionPermissions.debug) {
                Bukkit.getLogger().info("No access found at " + loc.formatXAndZ("") + " for " + otherFaction.getTag());
            }
        }
        if (deny) {
            if (!justCheck && !TimeUtil.isOnCooldown(player, "lastInteractMsg")) {
                me.msg("<b>You can't " + action + " in the territory of " + otherFaction.getTag(myFaction));
                TimeUtil.setCooldown(player, "lastInteractMsg", 1);
            }
            return false;
        }
        if (Conf.ownedAreasEnabled && (Conf.ownedAreaDenyBuild || Conf.ownedAreaPainBuild) && !otherFaction.playerHasOwnershipRights(me, loc)) {
            if (!pain && Conf.ownedAreaPainBuild && !justCheck) {
                player.damage((double) Conf.actionDeniedPainAmount);
                if (!Conf.ownedAreaDenyBuild) {
                    me.msg("<b>It is painful to try to " + action + " in this territory, it is owned by: " + otherFaction.getOwnerListStringAsync(loc));
                }
            }
            if (Conf.ownedAreaDenyBuild) {
                if (!justCheck) {
                    me.msg("<b>You can't " + action + " in this territory, it is owned by: " + otherFaction.getOwnerListStringAsync(loc));
                }
                return false;
            }
        }
        return true;
    }

    public static boolean hasAccessToBuild(FPlayer player, Faction factionIn, FLocation location, String action, MaterialData type) {
        if (action.equals("build") || action.equals("destroy")) {
            FactionPermission permission = FactionPermission.getFromBlock(type);
            if (permission == null) {
                return false;
            }
            return factionIn.hasPermission(player, location, permission, action);
        }
        return false;
    }

    public static FactionPermission hasAccessToInteract(FPlayer player, Faction factionIn, FLocation location, String action, MaterialData item) {
        return FactionsBlockListener.hasAccessToInteract(player, factionIn, location, action, item, null);
    }

    public static FactionPermission hasAccessToInteract(FPlayer player, Faction factionIn, FLocation location, String action, MaterialData item, MaterialData hand) {
        FactionPermission permission = FactionsBlockListener.getPermission(item);
        if (action.equals("interact")) {
            FactionPermission handPerm;
            Relation relation;
            if (hand != null && permission == FactionPermission.OTHER && (handPerm = FactionsBlockListener.getPermission(hand)) != null) {
                if (FactionPermissions.debug) {
                    Bukkit.getLogger().info("Converting " + permission + " to " + handPerm + " from " + hand + " due to not finding anything from " + item);
                }
                permission = handPerm;
            }
            if ((relation = factionIn.getRelationTo(player)) == Relation.ENEMY) {
                if (permission == FactionPermission.CREEPER || permission == FactionPermission.HOPPER || permission == FactionPermission.DROPPER || permission == FactionPermission.DISPENSE || permission == FactionPermission.CHEST) {
                    return null;
                }
                return permission;
            }
            if (relation.isAtMost(Relation.NEUTRAL) && permission == FactionPermission.CHEST) {
                return null;
            }
            if (FactionPermissions.debug) {
                System.out.println("Found perm: " + permission + " for -> " + item.getItemType() + " on interact");
            }
            if (!factionIn.hasPermission(player, location, permission, action)) {
                return permission;
            }
        }
        return null;
    }

    private static FactionPermission getPermission(MaterialData item) {
        FactionPermission permission = FactionPermission.getFromItem(item);
        if (permission == null && (permission = FactionPermission.getFromBlock(item)) == null) {
            return null;
        }
        return permission;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.FIRE) {
            return;
        }
        if (!FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), new MaterialData(event.getBlockPlaced().getType()), "build", false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), new MaterialData(event.getBlock().getType()), "destroy", false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerPlace(BlockPlaceEvent event) {
        FPlayer fplayer;
        Faction at;
        ItemStack item = event.getItemInHand();
        if (item != null && item.getType() == Material.MOB_SPAWNER && (at = Board.getFactionAt(new FLocation(event.getBlockPlaced()))) != null && at.isNormal() && (fplayer = FPlayers.i.get(event.getPlayer())) != null && at.getRelationTo(fplayer.getFaction()).isAtLeast(Relation.TRUCE)) {
            this.handleSpawnerUpdate(at, event.getPlayer(), item, LogTimer.TimerSubType.SPAWNER_PLACE);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block != null && block.getType() == Material.MOB_SPAWNER) {
            FPlayer fplayer;
            ItemStack item = new ItemStack(block.getType(), 1, (short) block.getData());
            Faction at = Board.getFactionAt(new FLocation(block));
            if (at != null && at.isNormal() && (fplayer = FPlayers.i.get(event.getPlayer())) != null && at.getRelationTo(fplayer.getFaction()).isAtLeast(Relation.TRUCE)) {
                BlockState state = block.getState();
                if (state instanceof CreatureSpawner) {
                    CreatureSpawner spawner = (CreatureSpawner) state;
                    item.setDurability(spawner.getSpawnedType().getTypeId());
                }
                this.handleSpawnerUpdate(at, event.getPlayer(), item, LogTimer.TimerSubType.SPAWNER_BREAK);
            }
        }
    }

    public void handleSpawnerUpdate(Faction at, Player player, ItemStack spawnerItem, LogTimer.TimerSubType subType) {
        FLogManager manager = P.p.getFlogManager();
        LogTimer logTimer = manager.getLogTimers().computeIfAbsent(player.getUniqueId(), e -> new LogTimer(player.getName(), at.getId()));
        LogTimer.Timer timer = logTimer.attemptLog(LogTimer.TimerType.SPAWNER_EDIT, subType, 0L);
        Map<MaterialData, AtomicInteger> currentCounts = timer.getExtraData() == null ? new HashMap<>() : (Map) timer.getExtraData();
        currentCounts.computeIfAbsent(spawnerItem.getData(), e -> new AtomicInteger(0)).addAndGet(1);
        timer.setExtraData(currentCounts);
        if (timer.isReadyToLog(this.placeTimer)) {
            logTimer.pushLogs(at, LogTimer.TimerType.SPAWNER_EDIT);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (!Conf.pistonProtectionThroughDenyBuild) {
            return;
        }
        Faction pistonFaction = Board.getFactionAt(new FLocation(event.getBlock()));
        Block targetBlock = event.getBlock().getRelative(event.getDirection(), event.getLength() + 1);
        if ((targetBlock.isEmpty() || targetBlock.isLiquid()) && !this.canPistonMoveBlock(pistonFaction, targetBlock.getLocation())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (event.isCancelled() || !event.isSticky() || !Conf.pistonProtectionThroughDenyBuild) {
            return;
        }
        Location targetLoc = event.getRetractLocation();
        if (targetLoc.getBlock().isEmpty() || targetLoc.getBlock().isLiquid()) {
            return;
        }
        Faction pistonFaction = Board.getFactionAt(new FLocation(event.getBlock()));
        if (!this.canPistonMoveBlock(pistonFaction, targetLoc)) {
            event.setCancelled(true);
            return;
        }
    }

    //GRACE
    @EventHandler
    public void onBreak(EntityExplodeEvent e) {
        if (!Conf.gracePeriod) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void entityDamage(EntityDamageEvent e) {
        if (!Conf.gracePeriod) return;

        if (e.getEntity() instanceof Player) {
            if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTNTPlace(BlockPlaceEvent e1) {
        FPlayer fp = FPlayers.i.get(e1.getPlayer());
        if (!Conf.gracePeriod) return;

        if (!fp.isAdminBypassing()) {
            if (e1.getBlock().getType().equals(Material.TNT)) {
                e1.setCancelled(true);

                fp.sendMessage(P.color("&c&l[!] &7You may not place explosives while grace is active!"));
            }
        }
    }

    private boolean canPistonMoveBlock(Faction pistonFaction, Location target) {
        Faction otherFaction = Board.getFactionAt(new FLocation(target));
        if (pistonFaction == otherFaction) {
            return true;
        }
        if (otherFaction.isNone()) {
            return !Conf.wildernessDenyBuild || Conf.worldsNoWildernessProtection.contains(target.getWorld().getName());
        }
        if (otherFaction.isSafeZone()) {
            return !Conf.safeZoneDenyBuild;
        }
        if (otherFaction.isWarZone()) {
            return !Conf.warZoneDenyBuild;
        }
        Relation rel = pistonFaction.getRelationTo(otherFaction);
        return !rel.confDenyBuild(otherFaction.hasPlayersOnline());
    }
}

