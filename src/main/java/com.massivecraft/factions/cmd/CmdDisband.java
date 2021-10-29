/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  net.minecraft.util.com.google.common.collect.Lists
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Sound
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.cmd;

import com.google.common.collect.Lists;
import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.listeners.menu.FDisbandConfirm;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.RolePerm;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.concurrent.TimeUnit;

public class CmdDisband
        extends FCommand {
    public CmdDisband() {
        this.aliases.add("disband");
        this.optionalArgs.put("faction tag", "yours");
        this.permission = Permission.DISBAND.node;
        this.requiredRolePermission = RolePerm.DISBAND;
        this.disableOnLock = true;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeColeader = false;
    }

    @Override
    public void perform() {
        boolean isMyFaction = false;
        long time;
        final Faction faction = this.argAsFaction(0, this.fme == null ? null : this.myFaction);
        if (faction == null) {
            return;
        }
        boolean bl = this.fme != null && (isMyFaction = faction == this.myFaction);
        if (!isMyFaction && !Permission.DISBAND_ANY.has(this.sender, true)) {
            return;
        }
        if (!faction.isNormal()) {
            this.msg("<i>You cannot disband the Wilderness, SafeZone, or WarZone.");
            return;
        }
        if (faction.isPermanent()) {
            this.msg("<i>This faction is designated as permanent, so you cannot disband it.");
            return;
        }
        boolean access = false;
        if (this.me.hasMetadata("disband_confirm") && (time = this.me.getMetadata("disband_confirm").get(0).asLong()) != 0L && System.currentTimeMillis() - time <= TimeUnit.SECONDS.toMillis(3L)) {
            access = true;
        }
        if (!access) {
            this.me.playSound(this.me.getLocation(), Sound.ANVIL_LAND, 1.0f, 1.4f);
            new FDisbandConfirm(this.me, faction).openGUI(P.p);
            return;
        }
        FactionDisbandEvent disbandEvent = new FactionDisbandEvent(this.me, faction.getId());
        Bukkit.getServer().getPluginManager().callEvent(disbandEvent);
        if (disbandEvent.isCancelled()) {
            return;
        }
        final String fId = faction.getId();
        Board.disbandingFactions.add(fId);
        Bukkit.getLogger().info("[Factions] Adding " + fId + " to disbandingFactions list.");
        for (FPlayer fplayer : faction.getFPlayers()) {
            Bukkit.getServer().getPluginManager().callEvent(new FPlayerLeaveEvent(fplayer, faction, FPlayerLeaveEvent.PlayerLeaveReason.DISBAND));
        }
        for (FPlayer fplayer : FPlayers.i.getOnline()) {
            String who;
            String string = who = this.senderIsConsole ? "a server admin" : this.fme.describeToAsync(fplayer);
            if (fplayer.getFaction() == faction) {
                fplayer.msg("<b>Your faction has been disbanded by %s", who);
            }
        }
        if (Conf.logFactionDisband) {
            P.p.log("The faction " + faction.getTag() + " (" + faction.getId() + ") was disbanded by " + (this.senderIsConsole ? "console command" : this.fme.getNameAsync()) + ".");
        }
        if (Conf.bankEnabled && !this.senderIsConsole) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(P.p, () -> {
                double amount = Econ.transferAllBankMoney(faction, CmdDisband.this.fme);
                if (amount > 0.0) {
                    String amountString = Econ.moneyString(amount);
                    CmdDisband.this.msg("<i>You have been given the disbanded faction's bank, totaling %s.", amountString);
                    P.p.log(CmdDisband.this.fme.getNameAsync() + " has been given bank holdings of " + amountString + " from disbanding " + CmdDisband.this.myFaction.getTag() + ".");
                }
            });
        }
        for (String relations : Lists.newArrayList(faction.relationWish.keySet())) {
            Faction f = Factions.i.get(relations);
            if (f == null) continue;
            if (f.getRelationWish(faction) != Relation.NEUTRAL) {
                f.setRelationWish(faction, Relation.NEUTRAL);
            }
            if (faction.getRelationWish(f) == Relation.NEUTRAL) continue;
            faction.setRelationWish(f, Relation.NEUTRAL);
        }
        faction.detach();
        Bukkit.getScheduler().runTaskLater(P.getP(), () -> {
            Bukkit.getLogger().info("[Factions] Removing " + fId + " from disbandingFactions list.");
            Board.disbandingFactions.remove(fId);
        }, 20L);
    }

}

