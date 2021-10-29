/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CmdToggleTp
        extends FCommand {
    public CmdToggleTp() {
        this.aliases.add("toggletp");
        this.aliases.add("toggleteleport");
        this.aliases.add("tptoggle");
        this.aliases.add("teleporttoggle");
        this.permission = Permission.RELATION.node;
        this.disableOnLock = false;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (this.sender instanceof Player) {
            FPlayer fp = FPlayers.i.get((OfflinePlayer) this.sender);
            if (fp.onlyAllowTpFromFactionMembers()) {
                fp.setAllowTpFromAll(true);
                fp.sendMessage(ChatColor.GREEN + "** " + ChatColor.UNDERLINE + "ALL PLAYERS" + ChatColor.GREEN + " can now send you teleport requests.");
            } else {
                fp.setAllowTpFromAll(false);
                fp.sendMessage(ChatColor.YELLOW + "** Only " + ChatColor.UNDERLINE + "FACTION MEMBERS" + ChatColor.YELLOW + " can send you teleport requests.");
            }
        }
    }
}

