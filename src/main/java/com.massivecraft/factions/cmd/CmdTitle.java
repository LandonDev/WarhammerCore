/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  org.bukkit.ChatColor
 *  org.bukkit.Server
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.RolePerm;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.ChatColor;

public class CmdTitle
        extends FCommand {
    public CmdTitle() {
        this.aliases.add("title");
        this.requiredArgs.add("player name");
        this.optionalArgs.put("title", "");
        this.permission = Permission.TITLE.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        final Faction myFaction = this.myFaction;
        P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
            FPlayer you = CmdTitle.this.argAsBestFPlayerMatch(0);
            if (you == null) {
                return;
            }
            if (you != CmdTitle.this.fme && !CmdTitle.this.checkHasPerm(CmdTitle.this.fme, you.getFaction(), RolePerm.CHANGE_MEMBER_TITLE) && !CmdTitle.this.fme.isAdminBypassing()) {
                return;
            }
            CmdTitle.this.args.remove(0);
            String title = TextUtil.implode(CmdTitle.this.args, " ");
            if (CmdTitle.this.fme.getRole().value <= you.getRole().value && !CmdTitle.this.fme.equals(you)) {
                CmdTitle.this.msg(CC.RedB + "(!) " + CC.Red + "You cannot modify " + you.getNameAsync() + "'s title as they have the same or higher faction rank!");
                return;
            }
            if (!myFaction.isNormal() && !CmdTitle.this.fme.isAdminBypassing()) {
                CmdTitle.this.msg(CC.RedB + "(!) " + CC.Red + "You must be in a faction to do that!");
                return;
            }
            if (!you.getFaction().equals(myFaction) && !CmdTitle.this.fme.isAdminBypassing()) {
                CmdTitle.this.msg(CC.RedB + "(!) " + CC.Red + "You cannot modify " + you.getNameAsync() + "'s title as they are not in your faction!");
                return;
            }
            if (!CmdTitle.this.payForCommand(Conf.econCostTitle, "to change a players title", "for changing a players title")) {
                return;
            }
            if (title.length() > 16) {
                you.msg("<b>Your title cannot exceed 16 characters.");
                return;
            }
            you.setTitle(ChatColor.translateAlternateColorCodes('&', title) + ChatColor.RESET);
            myFaction.msg("%s<i> changed a title: %s", CmdTitle.this.fme.describeToAsync(myFaction, true), you.describeToAsync(myFaction, true));
        });
    }

}

