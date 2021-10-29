/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  me.ifamasssxd.factionupgrades.FactionUpgradeAPI
 *  me.ifamasssxd.factionupgrades.struct.FactionUpgrade
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CmdJoin
        extends FCommand {
    private static volatile Map<UUID, Long> proccessingJoins = new HashMap<UUID, Long>();

    public CmdJoin() {
        this.aliases.add("join");
        this.requiredArgs.add("faction name");
        this.optionalArgs.put("player", "you");
        this.permission = Permission.JOIN.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Long timeLastExecuted = proccessingJoins.get(this.me.getUniqueId());
        if (timeLastExecuted != null && System.currentTimeMillis() - timeLastExecuted < 5000L) {
            Bukkit.getLogger().info("(Factions Concurrency Protection) Not allowing " + this.me.getName() + " to use /f join due to already being processed!");
            return;
        }
        proccessingJoins.put(this.me.getUniqueId(), System.currentTimeMillis());
        P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
            boolean samePlayer;
            Faction faction = CmdJoin.this.argAsFaction(0);
            if (faction == null) {
                proccessingJoins.remove(CmdJoin.this.me.getUniqueId());
                return;
            }
            if (CmdJoin.this.fme == null || !CmdJoin.this.fme.getPlayer().isOnline()) {
                proccessingJoins.remove(CmdJoin.this.me.getUniqueId());
                return;
            }
            FPlayer fplayer = CmdJoin.this.argAsBestFPlayerMatch(1, CmdJoin.this.fme, false);
            boolean bl = samePlayer = fplayer == CmdJoin.this.fme;
            if (!samePlayer && !Permission.JOIN_OTHERS.has(CmdJoin.this.sender, false)) {
                CmdJoin.this.msg("<b>You do not have permission to move other players into a faction.");
                proccessingJoins.remove(CmdJoin.this.me.getUniqueId());
                return;
            }
            if (!faction.isNormal()) {
                CmdJoin.this.msg("<b>Players may only join normal factions. This is a system faction.");
                proccessingJoins.remove(CmdJoin.this.me.getUniqueId());
                return;
            }
            if (faction == fplayer.getFaction()) {
                CmdJoin.this.msg("<b>%s %s already a member of %s", fplayer.describeToAsync(CmdJoin.this.fme, true), samePlayer ? "are" : "is", faction.getTag(CmdJoin.this.fme));
                proccessingJoins.remove(CmdJoin.this.me.getUniqueId());
                return;
            }
            if (Conf.factionMemberLimit > 0 && faction.getFPlayers().size() >= Conf.factionMemberLimit && !fplayer.isAdminBypassing()) {
                if (Bukkit.getPluginManager().isPluginEnabled("FactionUpgrades")) {
                    int extraMemebers = 0;
                    if (faction.getFPlayers().size() >= Conf.factionMemberLimit + extraMemebers) {
                        CmdJoin.this.msg(" <b>!<white> The faction %s is at the limit of %d members, so %s cannot currently join.", faction.getTag(CmdJoin.this.fme), Conf.factionMemberLimit, fplayer.describeToAsync(CmdJoin.this.fme, false));
                        proccessingJoins.remove(CmdJoin.this.me.getUniqueId());
                        return;
                    }
                } else {
                    CmdJoin.this.msg(" <b>!<white> The faction %s is at the limit of %d members, so %s cannot currently join.", faction.getTag(CmdJoin.this.fme), Conf.factionMemberLimit, fplayer.describeToAsync(CmdJoin.this.fme, false));
                    proccessingJoins.remove(CmdJoin.this.me.getUniqueId());
                    return;
                }
            }
            if (fplayer.hasFaction()) {
                CmdJoin.this.msg("<b>%s must leave %s current faction first.", fplayer.describeToAsync(CmdJoin.this.fme, true), samePlayer ? "your" : "their");
                proccessingJoins.remove(CmdJoin.this.me.getUniqueId());
                return;
            }
            if (!Conf.canLeaveWithNegativePower && fplayer.getPower() < 0.0) {
                CmdJoin.this.msg("<b>%s cannot join a faction with a negative power level.", fplayer.describeToAsync(CmdJoin.this.fme, true));
                proccessingJoins.remove(CmdJoin.this.me.getUniqueId());
                return;
            }
            File banFile = new File("plugins/FactionsPlus/fbans", faction.getId() + "." + CmdJoin.this.fme.getPlayer().getName().toLowerCase());
            if (banFile.exists()) {
                CmdJoin.this.msg("<c>You are /f ban'd from joining %s", faction.getTag());
                proccessingJoins.remove(CmdJoin.this.me.getUniqueId());
                return;
            }
            if (!(faction.getOpen() || faction.isInvited(fplayer) || CmdJoin.this.fme.isAdminBypassing() || Permission.JOIN_ANY.has(CmdJoin.this.sender, false))) {
                if (!faction.announceRelationshipRequests) {
                    CmdJoin.this.msg("<b>The faction '" + faction.getTag() + "' is not accepting join requests.");
                    CmdJoin.this.msg("<b>Their owner may use /f tr to re-enable join requests.");
                    proccessingJoins.remove(CmdJoin.this.me.getUniqueId());
                    return;
                }
                CmdJoin.this.msg("<i>This faction requires invitation.");
                if (samePlayer) {
                    faction.msg("<b>%s<b> tried to join your faction.", fplayer.describeToAsync(faction, true));
                }
                proccessingJoins.remove(CmdJoin.this.me.getUniqueId());
                return;
            }
            if (samePlayer && !CmdJoin.this.canAffordCommand(Conf.econCostJoin, "to join a faction")) {
                proccessingJoins.remove(CmdJoin.this.me.getUniqueId());
                return;
            }
            FPlayerJoinEvent joinEvent = new FPlayerJoinEvent(FPlayers.i.get(CmdJoin.this.me), faction, FPlayerJoinEvent.PlayerJoinReason.COMMAND);
            Bukkit.getServer().getPluginManager().callEvent(joinEvent);
            if (joinEvent.isCancelled()) {
                proccessingJoins.remove(CmdJoin.this.me.getUniqueId());
                return;
            }
            if (samePlayer && !CmdJoin.this.payForCommand(Conf.econCostJoin, "to join a faction", "for joining a faction")) {
                proccessingJoins.remove(CmdJoin.this.me.getUniqueId());
                return;
            }
            CmdJoin.this.fme.msg(ChatColor.YELLOW + "%s" + ChatColor.YELLOW + " successfully joined %s.", fplayer.describeToAsync(CmdJoin.this.fme, true), faction.getTag(CmdJoin.this.fme));
            if (!samePlayer) {
                fplayer.msg(ChatColor.YELLOW + "%s" + ChatColor.YELLOW + " moved you into the faction %s.", CmdJoin.this.fme.describeToAsync(fplayer, true), faction.getTag(fplayer));
            }
            faction.msg(ChatColor.YELLOW + "%s" + ChatColor.YELLOW + " joined your faction.", fplayer.describeToAsync(faction, true));
            fplayer.resetFactionData();
            fplayer.setFaction(faction);
            faction.deinvite(fplayer);
            if (faction.focusedPlayer != null && faction.focusedPlayer.getId().equals(fplayer.getId())) {
                CmdUnfocus.unfocusPlayer(faction, null);
            }
            if (Conf.logFactionJoin) {
                if (samePlayer) {
                    P.p.log("%s joined the faction %s.", fplayer.getNameAsync(), faction.getTag());
                } else {
                    P.p.log("%s moved the player %s into the faction %s.", CmdJoin.this.fme.getNameAsync(), fplayer.getNameAsync(), faction.getTag());
                }
            }
            proccessingJoins.remove(CmdJoin.this.me.getUniqueId());
            P.p.logFactionEvent(faction, FLogType.INVITES, CmdJoin.this.me.getName(), CC.Green + "joined", "the faction");
        });
    }

}

