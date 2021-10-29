/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Relation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CmdSendCoords
        extends FCommand {
    private Map<UUID, Long> coordCooldown = new HashMap<UUID, Long>();
    private DecimalFormat format = new DecimalFormat("#,###");

    public CmdSendCoords() {
        this.aliases.add("sendcoords");
        this.aliases.add("coords");
        this.disableOnLock = false;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        boolean ally;
        Faction faction = this.fme.getFaction();
        if (faction == null || !faction.isNormal()) {
            this.sender.sendMessage(ChatColor.RED + "You must be in a Faction to use this!");
            return;
        }
        Player player = (Player) this.sender;
        Long nextAllowed = this.coordCooldown.get(player.getUniqueId());
        if (nextAllowed != null && nextAllowed > System.currentTimeMillis()) {
            this.sender.sendMessage(ChatColor.RED + "Please wait " + (nextAllowed - System.currentTimeMillis()) / 1000L + "s before sending your location again!");
            return;
        }
        if (!player.isOp()) {
            this.coordCooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(60L));
        }
        if ((ally = this.fme.getChatMode() == ChatMode.ALLIANCE) || this.fme.getChatMode() == ChatMode.TRUCE) {
            Location location = player.getLocation();
            for (FPlayer fplayer : FPlayers.i.getOnline()) {
                Player pl;
                if (fplayer.getRelationTo(faction) != (ally ? Relation.ALLY : Relation.TRUCE) || (pl = fplayer.getPlayer()) == null)
                    continue;
                double distance = pl.getWorld().equals(player.getWorld()) ? pl.getLocation().distance(player.getLocation()) : -1.0;
                fplayer.sendMessage(this.getMessage(location, fplayer.getFaction(), this.fme, distance));
            }
            player.sendMessage(this.getMessage(player.getLocation(), faction, this.fme, 0.0));
            return;
        }
        this.sendCoords(player, faction);
    }

    private void sendCoords(Player player, Faction faction) {
        Location location = player.getLocation();
        for (FPlayer fPlayer : faction.getFPlayersWhereOnline(true)) {
            Player pl = fPlayer.getPlayer();
            if (pl == null) continue;
            double distance = pl.getWorld().equals(player.getWorld()) ? pl.getLocation().distance(player.getLocation()) : -1.0;
            fPlayer.sendMessage(this.getMessage(location, fPlayer.getFaction(), this.fme, distance));
        }
    }

    private String getMessage(Location location, Faction faction, FPlayer fme, double distance) {
        ChatColor colorTo = faction.getColorTo(fme);
        return ChatColor.YELLOW + ChatColor.BOLD.toString() + "(!) " + ChatColor.GREEN + ChatColor.UNDERLINE + colorTo + fme.getNameAsync() + "'s" + ChatColor.GREEN + " coordinates: " + ChatColor.WHITE + ChatColor.BOLD.toString() + location.getBlockX() + "x " + location.getBlockY() + "y " + location.getBlockZ() + "z" + ChatColor.GREEN + ChatColor.BOLD + (distance > 0.0 ? " (" + ChatColor.GREEN + this.format.format(distance) + "m" + ChatColor.GREEN + ChatColor.BOLD + ")" : "") + ChatColor.YELLOW + ChatColor.BOLD.toString() + " (!)";
    }
}

