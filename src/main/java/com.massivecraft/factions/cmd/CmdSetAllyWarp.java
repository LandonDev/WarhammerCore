/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.RolePerm;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CmdSetAllyWarp
        extends FCommand {
    public CmdSetAllyWarp() {
        this.aliases.add("setallywarp");
        this.requiredArgs.add("allyname/open");
        this.optionalArgs.put("name", "");
        this.setHelpShort(ChatColor.GRAY + "Set an ally warp so all allies can teleport to it.");
        this.disableOnLock = true;
        this.requiredRolePermission = RolePerm.SETWARP;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (this.sender instanceof Player) {
            Player p = (Player) this.sender;
            Faction f = this.myFaction;
            if (!this.myFaction.isNormal()) {
                p.sendMessage(ChatColor.RED + "You must be a part of a faction to set an ally warp.");
                return;
            }
            FLocation loc = new FLocation(p.getLocation());
            String other = this.argAsString(0);
            String warpName = this.argAsString(1).toLowerCase();
            if (Board.getFactionAt(loc).equals(f)) {
                Location l = p.getLocation();
                FactionWrapper wrapper = FactionWrappers.get(f);
                if (other.equalsIgnoreCase("open")) {
                    if (this.args.size() == 1) {
                        p.sendMessage(ChatColor.RED + "You must enter a valid ally warp name.");
                        return;
                    }
                    if (wrapper.allAllyWarps.size() >= 7) {
                        p.sendMessage(ChatColor.RED + "You can only have 7 open ally warps set.");
                        p.sendMessage(ChatColor.GRAY + "Please use /f delallywarp to delete one.");
                        return;
                    }
                    if (wrapper.getAllAllyWarps().containsKey(warpName)) {
                        p.sendMessage(ChatColor.RED + "That warp already exists.");
                        p.sendMessage(ChatColor.GRAY + "Please use /f delallywarp " + warpName + " to delete this warp.");
                        return;
                    }
                    if (!this.payForCommand(5000.0, "to set an open ally warp", "for setting an open ally warp.")) {
                        return;
                    }
                    wrapper.addAllAllyWarp(warpName, l);
                    p.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "(!) " + ChatColor.GREEN + "All allies can now teleport to the " + warpName + " warp.");
                } else {
                    Faction otherFaction = Factions.i.getByTag(other);
                    if (otherFaction == null || !otherFaction.isNormal()) {
                        p.sendMessage(ChatColor.RED + other + " is not a valid faction.");
                        return;
                    }
                    if (otherFaction == f) {
                        p.sendMessage(ChatColor.RED + "You cannot set ally warps for your own faction.");
                        return;
                    }
                    if (otherFaction.getRelationTo(f).isAlly()) {
                        if (!this.payForCommand(5000.0, "to set an ally warp", "for setting an ally warp.")) {
                            return;
                        }
                        wrapper.getAllyWarps().put(otherFaction.getId(), l);
                        p.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "(!) " + ChatColor.GREEN + "Ally warp set for " + otherFaction.getTag() + "!");
                    } else {
                        p.sendMessage(ChatColor.RED + "That faction must be an ally to set a warp for them.");
                    }
                }
            } else {
                p.sendMessage(ChatColor.RED + "You must claim the chunk you are trying to set a warp to.");
            }
        }
    }
}

