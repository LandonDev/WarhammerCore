/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  org.bukkit.ChatColor
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.struct.RolePerm;
import org.bukkit.ChatColor;

public class CmdColeader
        extends FCommand {
    public CmdColeader() {
        this.aliases.add("coleader");
        this.requiredArgs.add("player name");
        this.permission = Permission.ADMIN.node;
        this.requiredRolePermission = RolePerm.PROMOTE;
        this.disableOnLock = true;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeColeader = false;
        this.senderMustBeAdmin = true;
    }

    @Override
    public void perform() {
        P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
            FPlayer you = CmdColeader.this.argAsBestFPlayerMatch(0);
            if (you == null) {
                return;
            }
            boolean permAny = Permission.ADMIN_ANY.has(CmdColeader.this.sender, false);
            Faction targetFaction = you.getFaction();
            if (targetFaction != CmdColeader.this.myFaction && !permAny) {
                CmdColeader.this.msg("%s<b> is not a member in your faction.", you.describeToAsync(CmdColeader.this.fme, true));
                return;
            }
            if (you == CmdColeader.this.fme && !permAny) {
                CmdColeader.this.msg("<b>The target player musn't be yourself.");
                return;
            }
            if (you.getRole() == Role.ADMIN) {
                CmdColeader.this.msg("<b>The target player is a faction admin. Demote them first.");
                return;
            }
            if (CmdColeader.this.fme.getRole().value <= you.getRole().value && !CmdColeader.this.fme.isAdminBypassing()) {
                CmdColeader.this.msg(CC.RedB + "(!) " + CC.Red + "You cannot modify " + you.getNameAsync() + "'s higher faction rank!");
                return;
            }
            if (you.getRole() == Role.COLEADER) {
                you.setRole(Role.NORMAL);
                targetFaction.msg("%s<i> is no longer Co-Leader in your faction.", you.describeToAsync(targetFaction, true));
                CmdColeader.this.msg("<i>You have removed Co-Leader status from %s<i>.", you.describeToAsync(CmdColeader.this.fme, true));
                P.p.getFlogManager().log(targetFaction, FLogType.RANK_EDIT, CmdColeader.this.sender.getName(), you.getNameAsync(), ChatColor.YELLOW + "Member");
            } else {
                you.setRole(Role.COLEADER);
                targetFaction.msg("%s<i> was promoted to Co-Leader in your faction.", you.describeToAsync(targetFaction, true));
                CmdColeader.this.msg("<i>You have promoted %s<i> to Co-Leader.", you.describeToAsync(CmdColeader.this.fme, true));
                P.p.getFlogManager().log(targetFaction, FLogType.RANK_EDIT, CmdColeader.this.sender.getName(), you.getNameAsync(), ChatColor.RED + "Co-Leader");
            }
        });
    }

}

