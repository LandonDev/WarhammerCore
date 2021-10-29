/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FactionRenameEvent;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.RolePerm;
import com.massivecraft.factions.util.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;

public class CmdTag
        extends FCommand {
    public CmdTag() {
        this.aliases.add("tag");
        this.requiredArgs.add("faction tag");
        this.requiredRolePermission = RolePerm.CHANGE_NAME;
        this.permission = Permission.TAG.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (!(this.fme == null || this.fme.hasFaction() && this.myFaction.isNormal())) {
            this.sender.sendMessage(CC.Red + "You must be in a faction to do that!");
            return;
        }
        P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
            String tag = CmdTag.this.argAsString(0);
            if (Factions.i.isTagTaken(tag) && !MiscUtil.getComparisonString(tag).equals(CmdTag.this.myFaction.getComparisonTag())) {
                CmdTag.this.msg("<b>That tag is already taken");
                return;
            }
            for (String s : CmdCreate.badTags) {
                if (!tag.toLowerCase().contains(s)) continue;
                CmdTag.this.msg("<b>That faction tag cannot be used.");
                return;
            }
            ArrayList<String> errors = new ArrayList<>(Factions.validateTag(tag));
            if (errors.size() > 0) {
                CmdTag.this.sendMessage(errors);
                return;
            }
            if (!CmdTag.this.canAffordCommand(Conf.econCostTag, "to change the faction tag")) {
                return;
            }
            FactionRenameEvent renameEvent = new FactionRenameEvent(CmdTag.this.fme, tag);
            Bukkit.getServer().getPluginManager().callEvent(renameEvent);
            if (renameEvent.isCancelled()) {
                return;
            }
            Player p = CmdTag.this.fme.getPlayer();
            if (!p.hasMetadata("fTagConfirm")) {
                p.sendMessage("");
                p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "(!) " + ChatColor.GOLD + "You are changing your FACTION TAG to \"" + ChatColor.WHITE + tag + ChatColor.GOLD + "\".");
                p.sendMessage(ChatColor.YELLOW + "To confirm this change, please execute the command again: ");
                p.sendMessage(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "/f tag " + tag);
                p.sendMessage("");
                p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "!!!" + ChatColor.RED + " Your previous tag will be open for any faction to claim.");
                p.sendMessage("");
                p.setMetadata("fTagConfirm", new FixedMetadataValue(P.getP(), tag));
                return;
            }
            String tagConfirm = p.getMetadata("fTagConfirm").get(0).asString();
            p.removeMetadata("fTagConfirm", P.getP());
            if (!tagConfirm.equals(tag)) {
                p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "** /F TAG CONFIRMATION FAILED, CMD CANCELLED **");
                return;
            }
            if (!CmdTag.this.payForCommand(Conf.econCostTag, "to change the faction tag", "for changing the faction tag")) {
                return;
            }
            String oldtag = CmdTag.this.myFaction.getTag();
            CmdTag.this.myFaction.setTag(tag);
            CmdTag.this.myFaction.msg("%s<i> changed your faction tag to %s", CmdTag.this.fme.describeToAsync(CmdTag.this.myFaction, true), CmdTag.this.myFaction.getTag(CmdTag.this.myFaction));
            P.p.logFactionEvent(CmdTag.this.myFaction, FLogType.FTAG_EDIT, CmdTag.this.fme.getNameAsync(), tag);
            if (Conf.broadcastTagChanges) {
                for (Faction faction : Factions.i.get()) {
                    if (faction == CmdTag.this.myFaction) continue;
                    faction.msg("<i>The faction %s<i> changed their name to %s.", CmdTag.this.fme.getColorTo(faction) + oldtag, CmdTag.this.myFaction.getTag(faction));
                }
            }
        });
    }

}

