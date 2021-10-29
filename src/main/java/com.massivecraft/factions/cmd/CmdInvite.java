/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.RolePerm;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

import java.io.File;

public class CmdInvite
        extends FCommand {
    public CmdInvite() {
        this.aliases.add("invite");
        this.aliases.add("inv");
        this.requiredArgs.add("player name");
        this.permission = Permission.INVITE.node;
        this.requiredRolePermission = RolePerm.INVITE;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction faction = this.fme.getFaction();
        FPlayer fplayer = this.fme;
        FPlayer you = this.argAsBestFPlayerMatch(0);
        if (you == null) {
            return;
        }
        if (you.getFaction() == faction) {
            this.msg("<c>%s<p> is already a member of <c>%s", you.getNameAsync(), faction.getTag());
            this.msg("<i>You can kick them out with: <c>" + this.p.cmdBase.cmdKick.getUseageTemplate(false));
            return;
        }
        if (you.getFaction() != null && !you.getFaction().announceRelationshipRequests) {
            this.msg("<c>%s<p> is already a member of <c>%s", you.getNameAsync(), you.getFaction().getTag());
            return;
        }
        if (faction != null) {
            try {
                you.getPlayer();
                File banFile = new File("plugins/FactionsPlus/fbans", you.getPlayer().getName().toLowerCase());
                if (banFile.exists()) {
                    this.msg("<c>%s<p> is currently /f ban'd from joining <c>%s", you.getNameAsync(), faction.getTag());
                    this.msg("<i>You can use /f unban to allow them to join.");
                    return;
                }
            } catch (NullPointerException banFile) {
                // empty catch block
            }
        }
        if (!this.payForCommand(Conf.econCostInvite, "to invite someone", "for inviting someone")) {
            return;
        }
        faction.invite(you);
        if (you.getPlayer() != null) {
            FancyMessage message = new FancyMessage(fplayer.describeToAsync(you, true)).tooltip(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "/f join " + faction.getTag()).command("/f join " + faction.getTag()).then(" has invited you to join ").color(ChatColor.AQUA).tooltip(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "/f join " + faction.getTag()).command("/f join " + faction.getTag()).then(faction.describeToAsync(you)).tooltip(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "/f join " + faction.getTag()).command("/f join " + faction.getTag());
            message.send(you.getPlayer());
            you.getPlayer().sendMessage(ChatColor.GRAY + "Type " + ChatColor.AQUA + "/f join " + faction.getTag() + ChatColor.GRAY + " or click the invite to join!");
        }
        faction.msg("<c>%s<p> has invited <c>%s<p> to join your faction.", fplayer.describeToAsync(faction, true), you.describeToAsync(faction));
        P.p.logFactionEvent(faction, FLogType.INVITES, fplayer.getNameAsync(), CC.Green + "invited", you.getNameAsync());
    }
}

