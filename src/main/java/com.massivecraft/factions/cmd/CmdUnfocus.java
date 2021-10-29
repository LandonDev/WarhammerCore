/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.scoreboard.TeamAPI
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

//import com.cosmicpvp.scoreboard.TeamAPI;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CmdUnfocus
        extends FCommand {
    public CmdUnfocus() {
        this.aliases.add("unfocus");
        this.aliases.add("untarget");
        this.permission = Permission.MOD.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = true;
        this.senderMustBeAdmin = false;
    }

    public static void unfocusPlayer(Faction faction, Player commandSender) {
        if (faction != null) {
            if (faction.focusedPlayer == null) {
                if (commandSender != null) {
                    commandSender.sendMessage(ChatColor.RED + "Your faction does not have an active /f focus.");
                }
                return;
            }
            Player pUnfocus = faction.focusedPlayer.getPlayer();
            faction.focusedPlayer = null;
            if (pUnfocus.isOnline()) {

            }
            faction.msg(ChatColor.GREEN.toString() + ChatColor.BOLD + "*** " + ChatColor.GREEN + "Your faction no longer has an /f focus target.");
        }
    }

    @Override
    public void perform() {
        if (this.sender instanceof Player) {
            FPlayer pSender = FPlayers.i.get((OfflinePlayer) this.sender);
            CmdUnfocus.unfocusPlayer(this.myFaction, pSender.getPlayer());
        }
    }
}

