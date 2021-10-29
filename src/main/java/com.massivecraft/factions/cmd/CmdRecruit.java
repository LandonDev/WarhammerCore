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
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.struct.RolePerm;
import org.bukkit.ChatColor;

public class CmdRecruit
        extends FCommand {
    public CmdRecruit() {
        this.aliases.add("recruit");
        this.requiredArgs.add("player name");
        this.disableOnLock = true;
        this.requiredRolePermission = RolePerm.DEMOTE;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeColeader = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
            FPlayer you = CmdRecruit.this.argAsBestFPlayerMatch(0);
            if (you == null) {
                return;
            }
            boolean permAny = CmdRecruit.this.sender.isOp();
            Faction targetFaction = you.getFaction();
            if (targetFaction != CmdRecruit.this.myFaction && !permAny) {
                CmdRecruit.this.msg("%s<b> is not a member in your faction.", you.describeToAsync(CmdRecruit.this.fme, true));
                return;
            }
            if (you == CmdRecruit.this.fme && !permAny) {
                CmdRecruit.this.msg("<b>The target player musn't be yourself.");
                return;
            }
            if (you.getRole() == Role.ADMIN || you.getRole() == Role.COLEADER) {
                CmdRecruit.this.msg("<b>The target player is a faction " + (you.getRole() == Role.ADMIN ? "admin" : "co-leader") + ". Demote them first.");
                return;
            }
            if (CmdRecruit.this.fme.getRole().value <= you.getRole().value) {
                CmdRecruit.this.msg(CC.RedB + "(!) " + CC.Red + "You cannot modify " + you.getNameAsync() + "'s higher faction rank!");
                return;
            }
            if (you.getRole() == Role.RECRUIT) {
                CmdRecruit.this.msg(ChatColor.RED + you.describeToAsync(CmdRecruit.this.fme, true) + " is already Recruit!");
                CmdRecruit.this.msg(ChatColor.GRAY + "Try /f member to promote them!");
            } else {
                you.setRole(Role.RECRUIT);
                targetFaction.msg("%s<i> was set to recruit in your faction.", you.describeToAsync(targetFaction, true));
                CmdRecruit.this.msg("<i>You have promoted %s<i> to recruit.", you.describeToAsync(CmdRecruit.this.fme, true));
                P.p.getFlogManager().log(targetFaction, FLogType.RANK_EDIT, CmdRecruit.this.sender.getName(), you.getNameAsync(), ChatColor.GRAY + "Recruit");
            }
        });
    }

}

