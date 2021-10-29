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

public class CmdMod
        extends FCommand {
    public CmdMod() {
        this.aliases.add("mod");
        this.requiredArgs.add("player name");
        this.permission = Permission.MOD.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
            FPlayer you = CmdMod.this.argAsBestFPlayerMatch(0);
            if (you == null) {
                return;
            }
            boolean permAny = Permission.MOD_ANY.has(CmdMod.this.sender, false);
            Faction targetFaction = you.getFaction();
            if (targetFaction != CmdMod.this.myFaction && !permAny) {
                CmdMod.this.msg("%s<b> is not a member in your faction.", you.describeToAsync(CmdMod.this.fme, true));
                return;
            }
            if (you == CmdMod.this.fme && !permAny) {
                CmdMod.this.msg("<b>The target player musn't be yourself.");
                return;
            }
            if (you.getRole() == Role.ADMIN || you.getRole() == Role.COLEADER) {
                CmdMod.this.msg("<b>The target player is a faction " + (you.getRole() == Role.ADMIN ? "admin" : "co-leader") + ". Demote them first.");
                return;
            }
            if (you.getRole().isAtLeast(Role.MODERATOR) && !CmdMod.this.checkHasPerm(CmdMod.this.fme, targetFaction, RolePerm.DEMOTE)) {
                return;
            }
            if (CmdMod.this.fme.getRole().value <= you.getRole().value && !CmdMod.this.fme.isAdminBypassing()) {
                CmdMod.this.msg(CC.RedB + "(!) " + CC.Red + "You cannot modify " + you.getNameAsync() + "'s higher faction rank!");
                return;
            }
            if (you.getRole() == Role.MODERATOR) {
                if (!CmdMod.this.checkHasPerm(CmdMod.this.fme, targetFaction, RolePerm.DEMOTE)) {
                    return;
                }
                you.setRole(Role.NORMAL);
                targetFaction.msg("%s<i> is no longer moderator in your faction.", you.describeToAsync(targetFaction, true));
                CmdMod.this.msg("<i>You have removed moderator status from %s<i>.", you.describeToAsync(CmdMod.this.fme, true));
                P.p.getFlogManager().log(targetFaction, FLogType.RANK_EDIT, CmdMod.this.sender.getName(), you.getNameAsync(), ChatColor.YELLOW + "Member");
            } else {
                if (!CmdMod.this.checkHasPerm(CmdMod.this.fme, targetFaction, RolePerm.PROMOTE)) {
                    return;
                }
                you.setRole(Role.MODERATOR);
                targetFaction.msg("%s<i> was promoted to moderator in your faction.", you.describeToAsync(targetFaction, true));
                CmdMod.this.msg("<i>You have promoted %s<i> to moderator.", you.describeToAsync(CmdMod.this.fme, true));
                P.p.getFlogManager().log(targetFaction, FLogType.RANK_EDIT, CmdMod.this.sender.getName(), you.getNameAsync(), ChatColor.LIGHT_PURPLE + "Mod");
            }
        });
    }

}

