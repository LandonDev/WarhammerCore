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
import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.util.List;

public class CmdShowClaims
        extends FCommand {
    private final DecimalFormat format = new DecimalFormat("#.#");

    public CmdShowClaims() {
        this.aliases.add("showclaims");
        this.aliases.add("listclaims");
        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = true;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (this.myFaction.isNormal()) {
            List<FLocation> locations = Board.getAllClaimedLand(this.myFaction);
            this.sender.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "LAND CLAIMS OF: " + ChatColor.WHITE + ChatColor.UNDERLINE + this.myFaction.getTag());
            for (FLocation flocation : locations) {
                this.sender.sendMessage(ChatColor.WHITE + flocation.getWorldName() + " " + ChatColor.GRAY + "(" + flocation.getX() * 16L + "x, " + flocation.getZ() * 16L + "z)");
            }
            if (this.myFaction.getHome() != null) {
                this.sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Faction Home: " + ChatColor.WHITE + this.format.format(this.myFaction.getHome().getX()) + "x, " + this.format.format(this.myFaction.getHome().getY()) + "y, " + this.format.format(this.myFaction.getHome().getZ()) + "z");
            }
            this.sender.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "(!) " + ChatColor.YELLOW + "Found " + ChatColor.YELLOW + ChatColor.UNDERLINE.toString() + locations.size() + ChatColor.YELLOW + " Claimed Chunk(s) for " + ChatColor.WHITE + this.myFaction.getTag());
        } else {
            this.sender.sendMessage(ChatColor.RED + "You are not in a player-owned faction.");
        }
    }
}

