package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Relation;
import org.bukkit.ChatColor;
import com.massivecraft.factions.Faction;
import org.bukkit.OfflinePlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FPlayer;
import org.bukkit.entity.Player;
import com.massivecraft.factions.struct.Permission;

public class CmdFocus extends FCommand
{
    public CmdFocus() {
        this.aliases.add("focus");
        this.aliases.add("target");
        this.aliases.add("t");
        this.aliases.add("fo");
        this.requiredArgs.add("player name");
        this.permission = Permission.MOD.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = true;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (this.sender instanceof Player) {
            final FPlayer pSender = (FPlayer)FPlayers.i.get((OfflinePlayer)(Player)this.sender);
            if (this.myFaction != null) {
                final FPlayer playerToFocus = this.argAsBestFPlayerMatch(0);
                if (playerToFocus == null) {
                    return;
                }
                focusPlayer(pSender, this.myFaction, playerToFocus, true);
            }
        }
    }

    public static void focusPlayer(final FPlayer pSender, final Faction myFaction, final FPlayer playerToFocus, final boolean command) {
        if (myFaction != null) {
            if (playerToFocus == null) {
                return;
            }
            if (!playerToFocus.isOnline()) {
                if (command) {
                    pSender.sendMessage(ChatColor.RED + "The user \"" + playerToFocus.getNameAsync() + "\" is not online.");
                }
                return;
            }
            if (!pSender.getPlayer().canSee(playerToFocus.getPlayer())) {
                if (command) {
                    pSender.sendMessage(ChatColor.RED + "The user \"" + playerToFocus.getNameAsync() + "\" is not online.");
                }
                return;
            }
            if (playerToFocus.getRelationTo(pSender).isAtLeast(Relation.TRUCE)) {
                if (command) {
                    pSender.sendMessage(ChatColor.RED + "You cannot /f focus TRUCES, ALLIES, or MEMBERS.");
                }
                return;
            }
            if (command && myFaction.lastFocusEvent > 0L && System.currentTimeMillis() - myFaction.lastFocusEvent < 5000L) {
                pSender.sendMessage(ChatColor.RED + "Your faction " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " use /f focus for another " + (5000L - (System.currentTimeMillis() - myFaction.lastFocusEvent)) + "ms!");
                return;
            }
            myFaction.lastFocusEvent = System.currentTimeMillis();
            FPlayer removeFocus = null;
            if (myFaction.focusedPlayer == null || myFaction.focusedPlayer.getPlayer() == null || !myFaction.focusedPlayer.getPlayer().getUniqueId().equals(playerToFocus.getPlayer().getUniqueId())) {
                if (myFaction.focusedPlayer != null) {
                    removeFocus = myFaction.focusedPlayer;
                }
            }
            myFaction.focusedPlayer = playerToFocus;
            if (removeFocus != null && removeFocus.isOnline()) {

            }
            if (command) {
                myFaction.msg("", new Object[0]);
            }
            myFaction.msg(ChatColor.RED + "" + ChatColor.BOLD + "/f focus: " + ChatColor.WHITE + ChatColor.BOLD + ChatColor.UNDERLINE + " " + playerToFocus.getTag() + " " + playerToFocus.getPlayer().getName(), new Object[0]);
            if (command) {
                pSender.sendMessage(ChatColor.GRAY + "Use " + ChatColor.UNDERLINE + "/f unfocus" + ChatColor.GRAY + " to clear your faction target.");
            }
            if (command) {
                myFaction.msg("", new Object[0]);
            }
            for (final FPlayer fplayer : FPlayers.i.getOnline()) {
                if (myFaction.getRelationTo(fplayer) == Relation.ALLY) {
                    fplayer.sendMessage(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + pSender.getNameAndTagAsync() + ChatColor.LIGHT_PURPLE + " has focused " + ChatColor.DARK_PURPLE + ChatColor.BOLD + playerToFocus.getTag() + " " + playerToFocus.getPlayer().getName());
                }
                else {
                    if (!fplayer.isSpyingChat()) {
                        continue;
                    }
                    fplayer.sendMessage("[ACspy]: " + ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + pSender.getNameAndTagAsync() + ChatColor.LIGHT_PURPLE + " has focused " + ChatColor.DARK_PURPLE + ChatColor.BOLD + playerToFocus.getNameAndTagAsync());
                }
            }
        }
    }
}
