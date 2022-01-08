/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.cosmicpvp.cosmicutils.utils.com.massivecraft.factions.util.WorldGuardUtils
 *  com.google.common.collect.Lists
 *  net.minecraft.server.v1_7_R4.AsyncPacketSenderThread
 *  net.minecraft.server.v1_7_R4.EntityPlayer
 *  net.minecraft.server.v1_7_R4.Packet
 *  net.minecraft.server.v1_7_R4.PacketPlayOutBlockChange
 *  net.minecraft.server.v1_7_R4.World
 *  net.minecraft.server.v1_7_R4.WorldServer
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.command.CommandSender
 *  org.bukkit.craftbukkit.v1_7_R4.CraftWorld
 *  org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerBucketEmptyEvent
 *  org.bukkit.event.player.PlayerBucketFillEvent
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerKickEvent
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.material.MaterialData
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.listeners;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.CmdClaim;
import com.massivecraft.factions.cmd.killtracker.KillHandler;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.PowerLossEvent;
import com.massivecraft.factions.struct.*;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.TimeUtil;
import com.massivecraft.factions.util.WorldGuardUtils;
import com.massivecraft.factions.zcore.util.MojangUUIDFetcher;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.*;

public class FactionsPlayerListener
        implements Listener {
    public P p;
    List<Material> containerBlocks = new ArrayList<Material>(Arrays.asList(Material.CHEST, Material.TRAPPED_CHEST, Material.HOPPER, Material.HOPPER_MINECART, Material.DISPENSER, Material.DROPPER, Material.BREWING_STAND, Material.FURNACE, Material.BURNING_FURNACE, Material.BEACON, Material.STORAGE_MINECART));
    private HashMap<UUID, Long> showTimes = new HashMap<>();
    private Map<String, InteractAttemptSpam> interactSpammers = new HashMap<String, InteractAttemptSpam>();

    public FactionsPlayerListener(P p) {
        this.p = p;
    }

    public static boolean preventCommand(String fullCmd, Player player) {
        String shortCmd;
        if (Conf.territoryNeutralDenyCommands.isEmpty() && Conf.territoryEnemyDenyCommands.isEmpty() && Conf.territoryAllyDenyCommands.isEmpty() && Conf.territoryTruceDenyCommands.isEmpty() && Conf.permanentFactionMemberDenyCommands.isEmpty()) {
            return false;
        }
        fullCmd = fullCmd.toLowerCase();

        // Bukkit.broadcastMessage("p: "+player.getName());
        FPlayer me = FPlayers.i.get(player.getUniqueId().toString());
        //Bukkit.broadcastMessage("fplayer: "+me.getAccountId()+","+me.getFaction().getTag());
        //Bukkit.broadcastMessage("f: "+me.getPlayer().getName());

        if (me == null) return false;
        if (fullCmd.startsWith("/")) {
            shortCmd = fullCmd.substring(1);
        } else {
            shortCmd = fullCmd;
            fullCmd = "/" + fullCmd;
        }
        if (me.hasFaction() && !me.isAdminBypassing() && !Conf.permanentFactionMemberDenyCommands.isEmpty() && me.getFaction().isPermanent() && FactionsPlayerListener.isCommandInList(fullCmd, shortCmd, Conf.permanentFactionMemberDenyCommands.iterator())) {
            me.msg("<b>You can't use the command \"" + fullCmd + "\" because you are in a permanent faction.");
            return true;
        }
        if (!me.isInOthersTerritory()) {
            return false;
        }
        Relation rel = me.getRelationToLocation();
        if (rel == null) return false;
        if (rel.isAtLeast(Relation.ALLY)) {
            return false;
        }
        if (rel.isNeutral() && !Conf.territoryNeutralDenyCommands.isEmpty() && !me.isAdminBypassing() && FactionsPlayerListener.isCommandInList(fullCmd, shortCmd, Conf.territoryNeutralDenyCommands.iterator())) {
            me.msg("<b>You can't use the command \"" + fullCmd + "\" in neutral territory.");
            return true;
        }
        if (rel.isEnemy() && !Conf.territoryEnemyDenyCommands.isEmpty() && !me.isAdminBypassing() && FactionsPlayerListener.isCommandInList(fullCmd, shortCmd, Conf.territoryEnemyDenyCommands.iterator())) {
            me.msg("<b>You can't use the command \"" + fullCmd + "\" in enemy territory.");
            return true;
        }
        if (rel.isTruce() && !Conf.territoryTruceDenyCommands.isEmpty() && !me.isAdminBypassing() && FactionsPlayerListener.isCommandInList(fullCmd, shortCmd, Conf.territoryTruceDenyCommands.iterator())) {
            me.msg("<b>You can't use the command \"" + fullCmd + "\" in truce territory.");
            return true;
        }
        if (rel.isAlly() && !Conf.territoryAllyDenyCommands.isEmpty() && !me.isAdminBypassing() && FactionsPlayerListener.isCommandInList(fullCmd, shortCmd, Conf.territoryAllyDenyCommands.iterator())) {
            me.msg("<b>You can't use the command \"" + fullCmd + "\" in ally territory.");
            return true;
        }
        return false;
    }

    private static boolean isCommandInList(String fullCmd, String shortCmd, Iterator<String> iter) {
        while (iter.hasNext()) {
            String cmdCheck = iter.next();
            if (cmdCheck == null) {
                iter.remove();
                continue;
            }
            if (!fullCmd.startsWith(cmdCheck = cmdCheck.toLowerCase()) && !shortCmd.startsWith(cmdCheck)) continue;
            return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLeaveFaction(final FPlayerLeaveEvent event) {
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(this.p, () -> {
            if (event.getFPlayer() == null || event.getFPlayer().isOffline()) {
                return;
            }
            if (Conf.factionScoreboardEnable) {
                if (!event.getFPlayer().hasFaction() || event.getFPlayer().getFaction() == null) {
                    return;
                }
            }
        }, 10L);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLoss(final PowerLossEvent event) {
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(this.p, () -> {
            if (event.getFPlayer() == null || event.getFPlayer().isOffline()) {
                return;
            }
            if (Conf.factionScoreboardEnable) {
            }
        }, 10L);
    }

    @EventHandler
    public void killDeath(PlayerDeathEvent e){
        Player victim = e.getEntity();
        if (victim.getKiller() instanceof Player){
            Player killer = victim.getKiller();
            P.p.getFileManager().getMessages().getConfig().set(killer.getUniqueId()+".kills", Math.addExact(KillHandler.getKills(killer), 1));
            P.p.getFileManager().getMessages().getConfig().set(victim.getUniqueId()+".deaths", Math.addExact(KillHandler.getDeaths(victim), 1));
            P.p.getFileManager().getMessages().saveFile();
        }
    }



    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FPlayer fplayer = FPlayers.i.get(player);
   //     player.setMetadata("cosmicClientVersion", (MetadataValue)new FixedMetadataValue((Plugin) ClientComms.getInstance(), "2.1.10.b1adf"));
        if (fplayer == null) {
            return;
        }
        fplayer.setLastLoginTime(System.currentTimeMillis());
        fplayer.updateLastKnownName(event.getPlayer().getName());
        Faction fac = fplayer.getFaction();
        if (fac == null || !fac.isNormal()) {
            return;
        }
        String message = ChatColor.YELLOW + ChatColor.BOLD.toString() + "(!) " + ChatColor.YELLOW.toString() + ChatColor.BOLD + player.getName() + ChatColor.YELLOW + " has logged in.";
        for (FPlayer member : fac.getFPlayers()) {
            if (member.equals(fplayer)) continue;
            member.sendMessage(message);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(P.p, () -> fac.sendMOTD(fplayer, false), 80L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (!event.getPlayer().isOnline()) {
            return;
        }
        Player player = event.getPlayer();
        FPlayer fplayer = FPlayers.i.get(player);
        if (fplayer == null) {
            return;
        }
        Faction fac = fplayer.getFaction();
        if (fac == null || !fac.isNormal()) {
            return;
        }
        String message = ChatColor.YELLOW + ChatColor.BOLD.toString() + "(!) " + ChatColor.YELLOW.toString() + ChatColor.BOLD + player.getName() + ChatColor.YELLOW + " has logged out.";
        for (FPlayer member : fac.getFPlayers()) {
            if (member.equals(fplayer)) continue;
            member.sendMessage(message);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent e) {
        FPlayer me = FPlayers.i.get(e.getPlayer().getUniqueId().toString());
        me.clearCachedPlayer();
        MojangUUIDFetcher.updateCachedUUID(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        FLocation to;
        boolean changedFaction;
        if (event.getFrom().getBlockX() >> 4 == event.getTo().getBlockX() >> 4 && event.getFrom().getBlockZ() >> 4 == event.getTo().getBlockZ() >> 4 && event.getFrom().getWorld() == event.getTo().getWorld()) {
            return;
        }
        Player player = event.getPlayer();
        FPlayer me = FPlayers.i.get(player);
        FLocation from = me.getLastStoodAt();
        if (from.equals(to = new FLocation(event.getTo()))) {
            return;
        }
        me.setLastStoodAt(to);
        Faction factionFrom = Board.getFactionAt(from);
        Faction factionTo = Board.getFactionAt(to);
        boolean bl = changedFaction = !me.lastFactionMessage.equals(me.getFactionHereMessage());
        if (me.isMapAutoUpdating()) {
            if (!this.showTimes.containsKey(player.getUniqueId()) || this.showTimes.get(player.getUniqueId()) <= System.currentTimeMillis()) {
                me.sendFancyMessage(Board.getMap(me.getFaction(), to, player));
                this.showTimes.put(player.getUniqueId(), System.currentTimeMillis() + 4000L);
            }
        } else {
            Faction myFaction = me.getFaction();
            String ownersTo = myFaction.getOwnerListStringAsync(to);
            if (changedFaction) {
                me.sendFactionHereMessage();
                if (Conf.ownedAreasEnabled && Conf.ownedMessageOnBorder && myFaction == factionTo && !ownersTo.isEmpty()) {
                    me.sendMessage(Conf.ownedLandMessage + ownersTo);
                }
            } else if (Conf.ownedAreasEnabled && Conf.ownedMessageInsideTerritory && factionFrom == factionTo && myFaction == factionTo) {
                String ownersFrom = myFaction.getOwnerListStringAsync(from);
                if (Conf.ownedMessageByChunk || !ownersFrom.equals(ownersTo)) {
                    if (!ownersTo.isEmpty()) {
                        me.sendMessage(Conf.ownedLandMessage + ownersTo);
                    } else if (!Conf.publicLandMessage.isEmpty()) {
                        me.sendMessage(Conf.publicLandMessage);
                    }
                }
            }
        }
        if (me.getAutoClaimFor() != null) {
            if (!CmdClaim.chunkCoordsPendingClaim.contains(event.getTo().getChunk().getX() + "," + event.getTo().getChunk().getZ())) {
                me.attemptClaimAsync(me.getAutoClaimFor(), event.getTo(), true);
                P.p.logFactionEvent(me.getAutoClaimFor(), FLogType.CHUNK_CLAIMS, me.getNameAsync(), CC.GreenB + "CLAIMED", String.valueOf(1), new FLocation(player.getLocation()).formatXAndZ(","));
            } else {
                Bukkit.getLogger().info("[Factions] Skipping auto-claim for chunk at " + event.getTo() + " due to chunkCoordsPendingClaim.");
            }
        } else if (me.isAutoSafeClaimEnabled()) {
            if (!Permission.MANAGE_SAFE_ZONE.has(player)) {
                me.setIsAutoSafeClaimEnabled(false);
            } else if (!Board.getFactionAt(to).isSafeZone()) {
                Board.setFactionAt(Factions.i.getSafeZone(), to);
                me.msg("<i>This land is now a safe zone.");
            }
        } else if (me.isAutoWarClaimEnabled()) {
            if (!Permission.MANAGE_WAR_ZONE.has(player)) {
                me.setIsAutoWarClaimEnabled(false);
            } else if (!Board.getFactionAt(to).isWarZone()) {
                Board.setFactionAt(Factions.i.getWarZone(), to);
                me.msg("<i>This land is now a war zone.");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractMon(PlayerInteractEvent event) {
        if (FactionPermissions.debug) {
            Bukkit.getLogger().info("Final result: " + event.useInteractedBlock() + " Hand: " + event.useItemInHand());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteractNew(PlayerInteractEvent event) {
        Set<FactionPermission> permissions;
        FLocation waterLocation;
        Faction faction;
        Faction in;
        Block block;
        FactionPermissions otherFaction;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        Player player = event.getPlayer();
        if (clickedBlock == null) {
            return;
        }
        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.MONSTER_EGG && item.getDurability() != EntityType.CREEPER.getTypeId() && (in = Board.getFactionAt(new FLocation(clickedBlock))) != null && in.isWarZone() && MiscUtil.isBossEgg(item)) {
            return;
        }
        ItemStack hand = event.getPlayer().getItemInHand();
        Material blockClickedType = clickedBlock.getType();
        FLocation location = new FLocation(clickedBlock);
        Faction interactedFaction = Board.getFactionAt(location);
        if (Conf.playersWhoBypassAllProtection.contains(player.getName())) {
            return;
        }
        FPlayer me = FPlayers.i.get(player);
        if (me.isAdminBypassing()) {
            return;
        }
        FactionWrapper wrapper = FactionWrappers.get(interactedFaction);
        MaterialData handData = hand != null ? hand.getData() : null;
        FactionPermission desiredBlockPermission = FactionPermission.getFromMaterial(new MaterialData(blockClickedType));
        FactionPermission desiredHandPermissions = FactionPermission.getFromMaterial(handData);
        if (!(handData == null || handData.getItemType() != Material.LAVA_BUCKET && handData.getItemType() != Material.WATER_BUCKET && handData.getItemType() != Material.BUCKET || (faction = Board.getFactionAt(waterLocation = new FLocation((block = clickedBlock.getRelative(event.getBlockFace())).getLocation()))) == null || faction == interactedFaction || (permissions = (otherFaction = PermissionManager.get().getPermissions(faction)).getPermissions(faction, waterLocation, me.getFaction(), me, "interact")).contains(FactionPermission.OTHER) || permissions.contains(FactionPermission.FULL))) {
            event.setUseItemInHand(Event.Result.DENY);
            event.setCancelled(true);
            Bukkit.getLogger().info("Cancelling bucket placement from " + me.getNameAsync() + " due to placing on the edge of 2 factions!");
            return;
        }
        boolean isOverridableitem = handData != null && (FactionPermission.isConsumable(handData.getItemType()) || handData.getItemType() == Material.BOW);
        Relation rel = interactedFaction.getRelationTo(me);
        FactionPermissions perms = PermissionManager.get().getPermissions(interactedFaction);
        Set<FactionPermission> playerOrFactionPermissions = perms.getPermissions(interactedFaction, location, me.getFaction(), me, "interact");
        boolean doAnyEntitiesHaveChunkAccess = perms.hasChunkSpecificPerms(location);
        boolean allowedToOpen = FactionPermission.isAlwaysOpennableContainer(blockClickedType);
        if (!(allowedToOpen || blockClickedType != Material.CHEST && blockClickedType != Material.TRAPPED_CHEST || !playerOrFactionPermissions.contains(FactionPermission.CHEST) && rel != Relation.ENEMY)) {
            allowedToOpen = true;
        }
        if (doAnyEntitiesHaveChunkAccess) {
            boolean hasBlockPermission;
            boolean hasHandPermission;
            boolean bl = hasHandPermission = playerOrFactionPermissions.contains(desiredHandPermissions) || playerOrFactionPermissions.contains(FactionPermission.FULL);
            if (!hasHandPermission) {
                if (isOverridableitem) {
                    event.setUseItemInHand(Event.Result.ALLOW);
                } else {
                    event.setUseItemInHand(Event.Result.DENY);
                    if (desiredHandPermissions == FactionPermission.CREEPER) {
                        event.setUseInteractedBlock(Event.Result.DENY);
                        if (FactionPermissions.debug) {
                            Bukkit.getLogger().info("Denying block interact as well due to creeper click with no permissions!");
                        }
                    }
                }
                if (FactionPermissions.debug) {
                    Bukkit.getLogger().info("Denying hand item: " + event.useItemInHand() + " for " + desiredHandPermissions);
                }
            } else if (desiredHandPermissions == FactionPermission.CREEPER) {
                event.setUseItemInHand(Event.Result.ALLOW);
                if (FactionPermissions.debug) {
                    Bukkit.getLogger().info("Allowing creeper egg since we have explicit access");
                }
            }
            boolean bl2 = hasBlockPermission = playerOrFactionPermissions.contains(desiredBlockPermission) || playerOrFactionPermissions.contains(FactionPermission.FULL);
            if (!hasBlockPermission && !allowedToOpen) {
                event.setUseInteractedBlock(Event.Result.DENY);
                if (isOverridableitem) {
                    event.setUseItemInHand(Event.Result.ALLOW);
                    if (FactionPermissions.debug) {
                        Bukkit.getLogger().info("Allowing override item: " + handData + " " + event.useItemInHand() + " : " + desiredBlockPermission);
                    }
                }
                if (FactionPermissions.debug) {
                    Bukkit.getLogger().info("Denying block permission item: " + event.useItemInHand() + " for " + handData + " : " + desiredBlockPermission + " KeyS: " + Arrays.toString(playerOrFactionPermissions.toArray(new Object[0])));
                }
            }
            if (event.useItemInHand() == Event.Result.DENY && !allowedToOpen && desiredBlockPermission != FactionPermission.OTHER) {
                if (TimeUtil.isOnCooldown(player, "cooldownMessage")) {
                    return;
                }
                TimeUtil.setCooldown(player, "cooldownMessage", 1);
                player.sendMessage(ChatColor.RED + "You do not have permission to do that in this territory! A Coleader+ of " + interactedFaction.getTag() + " needs to grant you /f perm to perform this action.");
            }
            return;
        }
        if (interactedFaction.getRelationTo(me) == Relation.MEMBER && me.getRole() == Role.RECRUIT) {
            if (isOverridableitem) {
                event.setUseItemInHand(Event.Result.ALLOW);
                event.setUseInteractedBlock(Event.Result.DENY);
                return;
            }
            player.sendMessage(ChatColor.RED + "You are a Recruit, you must be promoted to Member or given /f access to this chunk.");
            event.setCancelled(true);
            return;
        }
        boolean bl = allowedToOpen = allowedToOpen || desiredBlockPermission == FactionPermission.HOPPER || desiredBlockPermission == FactionPermission.DROPPER || desiredBlockPermission == FactionPermission.DISPENSE || desiredBlockPermission == FactionPermission.CHEST;
        if (allowedToOpen) {
            event.setUseInteractedBlock(Event.Result.ALLOW);
        }
        if (!isOverridableitem && (desiredHandPermissions == FactionPermission.CREEPER || handData != null && handData.getItemType() == Material.MONSTER_EGG && handData.getData() == EntityType.CREEPER.getTypeId())) {
            isOverridableitem = true;
        }
        if (isOverridableitem) {
            event.setUseItemInHand(Event.Result.ALLOW);
        }
        if (wrapper.isLocationTracked(location)) {
            if (wrapper.doesPlayerHaveChunkAccess(location, me, true)) {
                return;
            }
            if (!allowedToOpen && !isOverridableitem) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "This chunk is privately owned by " + interactedFaction.getTag() + " and you do not have access.");
            } else if (!isOverridableitem) {
                event.setUseItemInHand(Event.Result.DENY);
            } else if (!allowedToOpen) {
                event.setUseInteractedBlock(Event.Result.DENY);
            }
            if (FactionPermissions.debug) {
                Bukkit.getLogger().info("No access to tracked location for " + player.getName() + " b=" + event.useInteractedBlock() + " item=" + event.useItemInHand());
            }
            return;
        }
        if (interactedFaction.isNone()) {
            if (!Conf.wildernessDenyUseage || Conf.worldsNoWildernessProtection.contains(location.getWorld().getName())) {
                return;
            }
            me.msg("<b>You can't use <h>%s<b> in the wilderness.", TextUtil.getMaterialName(blockClickedType));
            if (!allowedToOpen) {
                event.setUseInteractedBlock(Event.Result.DENY);
            }
            if (!isOverridableitem) {
                event.setUseItemInHand(Event.Result.DENY);
            }
            return;
        }
        if (interactedFaction.isSafeZone()) {
            if (!Conf.safeZoneDenyUseage || Permission.MANAGE_SAFE_ZONE.has(player)) {
                return;
            }
            me.msg("<b>You can't use <h>%s<b> in a safe zone.", TextUtil.getMaterialName(blockClickedType));
            event.setCancelled(true);
            return;
        }
        if (interactedFaction.isWarZone()) {
            Material type;
            if (!Conf.warZoneDenyUseage || Permission.MANAGE_WAR_ZONE.has(player)) {
                return;
            }
            if (handData != null && ((type = handData.getItemType()) == Material.WATER_BUCKET || type == Material.LAVA_BUCKET || type == Material.BUCKET || type == Material.MONSTER_EGG && handData.getData() == EntityType.CREEPER.getTypeId()) && !event.getPlayer().isOp()) {
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setUseItemInHand(Event.Result.DENY);
                if (FactionPermissions.debug) {
                    Bukkit.getLogger().info("Cancelling water bucket placement for " + player.getName() + " in WarZone!");
                }
            }
            if (clickedBlock.getLocation().getWorld().getName().startsWith("dungeon_")) {
                return;
            }
            if (WorldGuardUtils.isInRegionsContainsFast(clickedBlock.getLocation().clone().add(0.0, 1.0, 0.0), (List) Lists.newArrayList((Object[]) new String[]{"_blocks"}))) {
                return;
            }
            if (!allowedToOpen) {
                event.setUseInteractedBlock(Event.Result.DENY);
            }
            if (!isOverridableitem) {
                event.setUseItemInHand(Event.Result.DENY);
            }
            return;
        }
        if (rel.isNeutral() || rel.isEnemy() && Conf.territoryEnemyProtectMaterials || rel.isTruce() && Conf.territoryTruceProtectMaterials || rel.isAlly() && Conf.territoryAllyProtectMaterials) {
            if (!allowedToOpen) {
                event.setUseInteractedBlock(Event.Result.DENY);
            }
            if (!isOverridableitem) {
                if (desiredHandPermissions != FactionPermission.CREEPER) {
                    event.setUseItemInHand(Event.Result.DENY);
                } else {
                    event.setUseItemInHand(Event.Result.ALLOW);
                }
            }
            if (FactionPermissions.debug) {
                Bukkit.getLogger().info("Denying access due to default relation status: " + rel + " Action: " + event.getAction() + " Use Item: " + event.useItemInHand() + " block: " + event.useInteractedBlock());
            }
            if (event.getAction() != Action.PHYSICAL && Conf.territoryProtectedMaterials.contains(blockClickedType.name())) {
                me.msg("<b>You can't %s <h>%s<b> in the territory of <h>%s<b>.", blockClickedType == Material.SOIL ? "trample" : "use", TextUtil.getMaterialName(blockClickedType), interactedFaction.getTag(me));
            }
            if (FactionPermissions.debug) {
                Bukkit.getLogger().info("Clicked: " + blockClickedType);
            }
            return;
        }
        if (Conf.ownedAreasEnabled && Conf.ownedAreaProtectMaterials && !interactedFaction.playerHasOwnershipRights(me, location)) {
            if (event.getAction() != Action.PHYSICAL) {
                me.msg("<b>You can't use <h>%s<b> in this territory, it is owned by: %s<b>.", TextUtil.getMaterialName(blockClickedType), interactedFaction.getOwnerListStringAsync(location));
            }
            if (!allowedToOpen) {
                event.setUseInteractedBlock(Event.Result.DENY);
            }
            if (!isOverridableitem) {
                event.setUseItemInHand(Event.Result.DENY);
            }
            if (FactionPermissions.debug) {
                Bukkit.getLogger().info("action: " + event.getAction() + " Use Item: " + event.useItemInHand() + " block: " + event.useInteractedBlock());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        FPlayer me = FPlayers.i.get(event.getPlayer());
        me.getPower();
        Location home = me.getFaction().getHome();
        if (Conf.homesEnabled && Conf.homesTeleportToOnDeath && home != null && (Conf.homesRespawnFromNoPowerLossWorlds || !Conf.worldsNoPowerLoss.contains(event.getPlayer().getWorld().getName()))) {
            event.setRespawnLocation(home);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPRe(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equals("/f scoreboard")) {
            event.setMessage("/togglesb");
        } else if (event.getMessage().equalsIgnoreCase("/tl")) {
            event.setMessage("/f sendcoords");
        } else if (event.getMessage().equals("/audit") || event.getMessage().equals("/logs")) {
            event.setMessage("/f audit");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Block block = event.getBlockClicked();
        Player player = event.getPlayer();
        PlayerInteractEvent spoofedEvent = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, player.getItemInHand(), block, event.getBlockFace());
        this.onPlayerInteractNew(spoofedEvent);
        if (spoofedEvent.useItemInHand() == Event.Result.DENY || spoofedEvent.useInteractedBlock() == Event.Result.DENY) {
            event.setCancelled(true);
            if (FactionPermissions.debug) {
                Bukkit.getLogger().info("Cancelling " + event.getPlayer().getName() + " Bucket empty event: " + spoofedEvent.useItemInHand() + " block: " + spoofedEvent.useInteractedBlock());
            }
        }
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (FactionPermissions.debug) {
            Bukkit.getLogger().info("Bucket fill: " + event.getBucket());
        }
        Block block = event.getBlockClicked();
        Player player = event.getPlayer();
        PlayerInteractEvent spoofedEvent = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, player.getItemInHand(), block, event.getBlockFace());
        this.onPlayerInteractNew(spoofedEvent);
        if (spoofedEvent.useItemInHand() == Event.Result.DENY || spoofedEvent.useInteractedBlock() == Event.Result.DENY) {
            event.setCancelled(true);
            if (FactionPermissions.debug) {
                Bukkit.getLogger().info("Cancelling " + event.getPlayer().getName() + " Bucket fill event: " + spoofedEvent.useItemInHand() + " block: " + spoofedEvent.useInteractedBlock());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerKick(PlayerKickEvent event) {
        FPlayer badGuy = FPlayers.i.get(event.getPlayer().getUniqueId().toString());
        if (badGuy == null) {
            return;
        }
        if (Conf.removePlayerDataWhenBanned && event.getReason().equals("Banned by admin.")) {
            if (badGuy.getRole() == Role.ADMIN) {
                badGuy.getFaction().promoteNewLeader(badGuy);
            }
            badGuy.leave(false);
            badGuy.detach();
        }
    }


    //inspect
    /*@EventHandler
    public void onInspect(PlayerInteractEvent e) {
        if (e.getAction().name().contains("BLOCK")) {
            FPlayer fplayer = FPlayers.i.get(e.getPlayer());
            if (!fplayer.isInspectMode()) {
                return;
            }
            e.setCancelled(true);
            if (!fplayer.isAdminBypassing()) {
                if (!fplayer.hasFaction()) {
                    fplayer.setInspectMode(false);
                    fplayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[!]&7 Inspect mode is now &cdisabled,&7 because you &cdo not have a faction!"));
                    return;
                }
                if (fplayer.getFaction() != Board.getFactionAt(new FLocation(e.getPlayer().getLocation()))) {
                    fplayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[!]&7 &7You can &conly&7 inspect in &cyour &7claims!"));
                    return;
                }
            } else {
                fplayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[!]&7 Inspecting in &cbypass&7 mode"));
            }
            List<String[]> info = CoreProtect.getInstance().getAPI().blockLookup(e.getClickedBlock(), 0);
            if (info.size() == 0) {
                e.getPlayer().sendMessage(P.color("&c&l[!]&7 &7No Data was found!"));
                return;
            }
            Player player = e.getPlayer();
            CoreProtectAPI coAPI = CoreProtect.getInstance().getAPI();
            player.sendMessage(P.color("&c&m---&7Inspect Data&c&m---&c//&7x:{x},y:{y},z:{z}").replace("{x}", e.getClickedBlock().getX() + "")
                    .replace("{y}", e.getClickedBlock().getY() + "")
                    .replace("{z}", e.getClickedBlock().getZ() + ""));
            String rowFormat = P.color("&c{time} &7// &c{action} &7// &c{player} &7// &c{block-type}");
            for (String[] strings : info) {
                CoreProtectAPI.ParseResult row = coAPI.parseResult(strings);
                player.sendMessage(rowFormat
                        .replace("{time}", convertTime(row.getTime()))
                        .replace("{action}", row.getActionString())
                        .replace("{player}", row.getPlayer())
                        .replace("{block-type}", row.getType().toString().toLowerCase()));
            }
        }
    }*/

    private String convertTime(int time) {
        String result = String.valueOf(Math.round((System.currentTimeMillis() / 1000L - time) / 36.0D) / 100.0D);
        return (result.length() == 3 ? result + "0" : result) + "/hrs ago";
    }

    private static class InteractAttemptSpam {
        private int attempts = 0;
        private long lastAttempt = System.currentTimeMillis();

        private InteractAttemptSpam() {
        }

        public int increment() {
            long Now = System.currentTimeMillis();
            this.attempts = Now > this.lastAttempt + 2000L ? 1 : ++this.attempts;
            this.lastAttempt = Now;
            return this.attempts;
        }
    }

}

