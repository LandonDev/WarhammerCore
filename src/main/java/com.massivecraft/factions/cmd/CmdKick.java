/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.PluginManager
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.struct.RolePerm;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class CmdKick
        extends FCommand {
    public CmdKick() {
        this.aliases.add("kick");
        this.requiredArgs.add("player name");
        this.permission = Permission.KICK.node;
        this.requiredRolePermission = RolePerm.KICK;
        this.disableOnLock = false;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        FPlayer toKick = this.argAsBestFPlayerMatch(0);
        if (toKick == null) {
            return;
        }
        if (this.fme == toKick) {
            this.msg("<b>You cannot kick yourself.");
            this.msg("<i>You might want to: %s", this.p.cmdBase.cmdLeave.getUseageTemplate(false));
            return;
        }
        Faction toKickFaction = toKick.getFaction();
        if (ChatColor.stripColor(toKickFaction.getTag()).toLowerCase().equalsIgnoreCase("wilderness")) {
            this.sender.sendMessage("This player is not in a faction!");
            return;
        }
        if (!Permission.KICK_ANY.has(this.sender)) {
            if (toKickFaction != this.myFaction) {
                this.msg("%s<b> is not a member of %s", toKick.describeToAsync(this.fme, true), this.myFaction.describeToAsync(this.fme));
                return;
            }
            if (toKick.getRole().value >= this.fme.getRole().value && this.fme.getRole() != Role.ADMIN) {
                this.msg("<b>Your rank is too low to kick this player.");
                return;
            }
            if (!Conf.canLeaveWithNegativePower && toKick.getPower() < 0.0) {
                this.msg("<b>You cannot kick that member until their power is positive.");
                return;
            }
        }
        if (!this.canAffordCommand(Conf.econCostKick, "to kick someone from the faction")) {
            return;
        }
        FPlayerLeaveEvent event = new FPlayerLeaveEvent(toKick, toKick.getFaction(), FPlayerLeaveEvent.PlayerLeaveReason.KICKED);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        if (!this.payForCommand(Conf.econCostKick, "to kick someone from the faction", "for kicking someone from the faction")) {
            return;
        }
        toKickFaction.msg("%s<i> kicked %s<i> from the faction! :O", this.fme.describeToAsync(toKickFaction, true), toKick.describeToAsync(toKickFaction, true));
        toKick.msg("%s<i> kicked you from %s<i>! :O", this.fme.describeToAsync(toKick, true), toKickFaction.describeToAsync(toKick));
        if (toKickFaction != this.myFaction) {
            this.fme.msg("<i>You kicked %s<i> from the faction %s<i>!", toKick.describeToAsync(this.fme), toKickFaction.describeToAsync(this.fme));
        }
        P.p.logFactionEvent(toKickFaction, FLogType.INVITES, this.fme.getNameAsync(), CC.Red + "kicked", toKick.getNameAsync());
        if (Conf.logFactionKick) {
            P.p.log((this.senderIsConsole ? "A console command" : this.fme.getNameAsync()) + " kicked " + toKick.getNameAsync() + " from the faction: " + toKickFaction.getTag());
        }
        if (toKick.getRole() == Role.ADMIN) {
            toKickFaction.promoteNewLeader(toKick);
        }
        toKickFaction.deinvite(toKick);
        toKick.resetFactionData();
    }
}

