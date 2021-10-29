/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.RolePerm;
import org.bukkit.Bukkit;

public class CmdSethome
        extends FCommand {
    public CmdSethome() {
        this.aliases.add("sethome");
        this.optionalArgs.put("faction tag", "mine");
        this.permission = Permission.SETHOME.node;
        this.requiredRolePermission = RolePerm.SETHOME;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
            if (!Conf.homesEnabled) {
                CmdSethome.this.fme.msg("<b>Sorry, Faction homes are disabled on this server.");
                return;
            }
            Faction faction = CmdSethome.this.argAsFaction(0, CmdSethome.this.myFaction);
            if (faction == null) {
                return;
            }
            if (faction != CmdSethome.this.myFaction && !Permission.SETHOME_ANY.has(CmdSethome.this.sender, true)) {
                return;
            }
            if (!Permission.BYPASS.has(CmdSethome.this.me) && Conf.homesMustBeInClaimedTerritory && Board.getFactionAt(new FLocation(CmdSethome.this.me)) != faction) {
                CmdSethome.this.fme.msg("<b>Sorry, your faction home can only be set inside your own claimed territory.");
                return;
            }
            if (!CmdSethome.this.payForCommand(Conf.econCostSethome, "to set the faction home", "for setting the faction home")) {
                return;
            }
            faction.setHome(CmdSethome.this.me.getLocation());
            faction.msg("%s<i> set the home for your faction. You can now use:", CmdSethome.this.fme.describeToAsync(CmdSethome.this.myFaction, true));
            faction.sendMessage(CmdSethome.this.p.cmdBase.cmdHome.getUseageTemplate());
            if (faction != CmdSethome.this.myFaction) {
                CmdSethome.this.fme.msg("<b>You have set the home for the " + faction.getTag(CmdSethome.this.fme) + "<i> faction.");
            }
            Bukkit.getLogger().info(CmdSethome.this.sender.getName() + " set faction home at " + faction.getHome());
        });
    }

}

