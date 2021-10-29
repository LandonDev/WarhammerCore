/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.command.CommandSender
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.util.List;

public class CmdLookup
        extends FCommand {
    private final DecimalFormat format = new DecimalFormat("#.#");

    public CmdLookup() {
        this.aliases.add("lookup");
        this.requiredArgs.add("faction name");
        this.permission = Permission.KICK.node;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (!this.sender.isOp()) {
            return;
        }
        Faction faction = this.argAsFaction(0);
        if (faction == null) {
            this.sender.sendMessage(ChatColor.RED + "Invalid faction found.");
            return;
        }
        if (faction.isNormal()) {
            if (faction.getHome() != null) {
                this.sender.sendMessage(ChatColor.RED + "Faction Home: " + ChatColor.WHITE + this.format.format(faction.getHome().getX()) + "x " + this.format.format(faction.getHome().getY()) + "y " + this.format.format(faction.getHome().getZ()) + "z");
            }
            List<FLocation> locations = Board.getAllClaimedLand(faction);
            this.sender.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "Found " + ChatColor.RED + ChatColor.UNDERLINE.toString() + locations.size() + ChatColor.RED + " Claimed Chunk(s) for " + ChatColor.WHITE + faction.getTag());
            for (FLocation flocation : locations) {
                this.sender.sendMessage(ChatColor.WHITE + flocation.getWorldName() + " " + ChatColor.GRAY + " (" + flocation.getX() * 16L + "x, " + flocation.getZ() * 16L + "z)");
            }
        } else {
            this.sender.sendMessage(ChatColor.RED + "You can only enter normal factions.");
        }
    }
}

