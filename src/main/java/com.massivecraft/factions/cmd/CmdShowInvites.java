/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

public class CmdShowInvites
        extends FCommand {
    public CmdShowInvites() {
        this.aliases.add("showinvites");
        this.permission = Permission.SHOWINVITES.node;
    }

    @Override
    public void perform() {
        if (this.senderIsConsole) {
            this.msg("This is not a console command.");
            return;
        }
        final Faction faction = FPlayers.i.get((OfflinePlayer) this.sender).getFaction();
        P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
            StringBuilder name_builder = new StringBuilder();
            for (String playerUUID : faction.getPendingInvites()) {
                if (name_builder.length() > 1) {
                    name_builder.append(", ");
                } else {
                    name_builder.append(ChatColor.DARK_GREEN);
                }
                name_builder.append(FPlayers.i.get(playerUUID).getNameAsync());
            }
            CmdShowInvites.this.sender.sendMessage(ChatColor.YELLOW + "Players with pending invites into your faction: " + name_builder.toString());
        });
    }

}

