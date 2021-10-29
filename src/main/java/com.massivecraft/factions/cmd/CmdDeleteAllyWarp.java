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

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionWrapper;
import com.massivecraft.factions.FactionWrappers;
import com.massivecraft.factions.Factions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CmdDeleteAllyWarp
        extends FCommand {
    String red = ChatColor.RED + ChatColor.BOLD.toString() + "(!) ";

    public CmdDeleteAllyWarp() {
        this.aliases.add("delallywarp");
        this.aliases.add("deleteallywarp");
        this.requiredArgs.add("faction/allywarpname");
        this.setHelpShort("Delete an ally warp.");
        this.senderMustBeAdmin = false;
        this.senderMustBeModerator = true;
        this.senderMustBePlayer = true;
    }

    @Override
    public void perform() {
        Faction f = this.myFaction;
        Player p = (Player) this.sender;
        String fName = this.argAsString(0);
        if (!f.isNormal()) {
            p.sendMessage(ChatColor.RED + "You need to be in a faction to do this.");
            return;
        }
        FactionWrapper wrapper = FactionWrappers.get(f);
        if (wrapper.getAllAllyWarps().isEmpty() && wrapper.getAllyWarps().isEmpty()) {
            p.sendMessage(this.red + ChatColor.RED + "Your faction has no ally warps set.");
            return;
        }
        if (wrapper.getAllAllyWarps().containsKey(fName)) {
            if (wrapper.getAllAllyWarps().isEmpty()) {
                p.sendMessage(this.red + ChatColor.RED + "Your faction has no open ally warps.");
                return;
            }
            Location l = wrapper.getAllAllyWarps().remove(fName);
            p.sendMessage(this.red + ChatColor.RED + "You have removed the open ally warp at x" + l.getBlockX() + " y" + l.getBlockY() + " z" + l.getBlockZ());
            return;
        }
        Faction otherFac = Factions.i.getByTag(fName);
        if (otherFac == null) {
            p.sendMessage(ChatColor.RED + "Please enter a valid warp or faction name.");
            return;
        }
        if (!otherFac.isNormal()) {
            p.sendMessage(ChatColor.RED + "Please enter a valid ally faction name.");
            return;
        }
        if (wrapper.getAllyWarp(otherFac) == null) {
            p.sendMessage(this.red + ChatColor.RED + "No ally warp set for " + otherFac.getTag());
            return;
        }
        if (!this.payForCommand(2500.0, "to remove an ally warp", "for removing an ally warp.")) {
            return;
        }
        wrapper.getAllyWarps().remove(otherFac.getId());
        p.sendMessage(this.red + ChatColor.RED + "Ally warp has been removed for " + otherFac.getTag() + ".");
    }
}

