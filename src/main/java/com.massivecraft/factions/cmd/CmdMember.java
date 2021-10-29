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

public class CmdMember
        extends FCommand {
    public CmdMember() {
        this.aliases.add("member");
        this.requiredArgs.add("player name");
        this.disableOnLock = true;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeColeader = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
            FPlayer you = CmdMember.this.argAsBestFPlayerMatch(0);
            if (you == null) {
                return;
            }
            boolean permAny = CmdMember.this.sender.isOp();
            Faction targetFaction = you.getFaction();
            if (targetFaction != CmdMember.this.myFaction && !permAny) {
                CmdMember.this.msg("%s<b> is not a member in your faction.", you.describeToAsync(CmdMember.this.fme, true));
                return;
            }
            if (you == CmdMember.this.fme && !permAny) {
                CmdMember.this.msg("<b>The target player musn't be yourself.");
                return;
            }
            if (you.getRole() == Role.ADMIN || you.getRole() == Role.COLEADER) {
                CmdMember.this.msg("<b>The target player is a faction " + (you.getRole() == Role.ADMIN ? "admin" : "co-leader") + ". Demote them first.");
                return;
            }
            if (you.getRole().isAtLeast(Role.NORMAL) && !CmdMember.this.checkHasPerm(CmdMember.this.fme, targetFaction, RolePerm.DEMOTE)) {
                return;
            }
            if (CmdMember.this.fme.getRole().value <= you.getRole().value) {
                CmdMember.this.msg(CC.RedB + "(!) " + CC.Red + "You cannot modify " + you.getNameAsync() + "'s higher faction rank!");
                return;
            }
            if (you.getRole() == Role.NORMAL) {
                you.setRole(Role.RECRUIT);
                targetFaction.msg("%s<i> is no longer member in your faction.", you.describeToAsync(targetFaction, true));
                CmdMember.this.msg("<i>You have removed member status from %s<i>.", you.describeToAsync(CmdMember.this.fme, true));
                P.p.getFlogManager().log(targetFaction, FLogType.RANK_EDIT, CmdMember.this.sender.getName(), you.getNameAsync(), ChatColor.GRAY + "Recruit");
            } else {
                if (!CmdMember.this.checkHasPerm(CmdMember.this.fme, targetFaction, RolePerm.PROMOTE)) {
                    return;
                }
                you.setRole(Role.NORMAL);
                targetFaction.msg("%s<i> was set to normal member in your faction.", you.describeToAsync(targetFaction, true));
                CmdMember.this.msg("<i>You have promoted %s<i> to member.", you.describeToAsync(CmdMember.this.fme, true));
                P.p.getFlogManager().log(targetFaction, FLogType.RANK_EDIT, CmdMember.this.sender.getName(), you.getNameAsync(), ChatColor.YELLOW + "Member");
            }
        });
    }

}

