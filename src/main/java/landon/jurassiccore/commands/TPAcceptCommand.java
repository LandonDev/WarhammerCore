package landon.jurassiccore.commands;

import com.massivecraft.factions.*;
import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.expiry.Expiry;
import landon.jurassiccore.expiry.ExpiryType;
import landon.jurassiccore.teleport.PendingTeleportPlayer;
import landon.jurassiccore.timeout.Timeout;
import landon.jurassiccore.timeout.TimeoutType;
import landon.jurassiccore.utils.EXPUtil;
import net.minelink.ctplus.TagManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TPAcceptCommand implements CommandExecutor {
    private JurassicCore instance;

    public TPAcceptCommand(JurassicCore instance) {
        this.instance = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
            return true;
        }
        final PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
        final FileConfiguration configLoad = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
        final Player player = (Player) sender;
        Bukkit.getServer().getScheduler().runTaskAsynchronously(P.p, new Runnable() {
            public void run() {
                PlayerData teleportPlayerData;
                Player teleportPlayer, targetPlayer = null;
                if (args.length == 0) {
                    List<PendingTeleportPlayer> pendingTeleportPlayers = new ArrayList<>();
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (all.getName().equals(player.getName()))
                            continue;
                        if (!playerDataManager.hasPlayerData(all))
                            continue;
                        PlayerData playerData1 = playerDataManager.getPlayerData(all);
                        Timeout timeout = playerData1.getTimeout(TimeoutType.Teleport);
                        if (timeout.getTime() != 0L)
                            pendingTeleportPlayers.add(new PendingTeleportPlayer(all, timeout.getTime()));
                    }
                    if (pendingTeleportPlayers.size() != 0) {
                        pendingTeleportPlayers.sort(new Comparator<PendingTeleportPlayer>() {
                            public int compare(PendingTeleportPlayer pendingTeleportPlayer1, PendingTeleportPlayer pendingTeleportPlayer2) {
                                return Long.valueOf(pendingTeleportPlayer2.getTime())
                                        .compareTo(Long.valueOf(pendingTeleportPlayer1.getTime()));
                            }
                        });
                        targetPlayer = ((PendingTeleportPlayer) pendingTeleportPlayers.get(0)).getPlayer();
                    }
                    if (targetPlayer == null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Commands.TPAccept.Pending.Recent.Message")));
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                        return;
                    }
                } else if (args.length == 1) {
                    targetPlayer = Bukkit.getServer().getPlayer(args[0]);
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Commands.TPAccept.Invalid.Message")));
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    return;
                }
                if (targetPlayer == null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Commands.TPAccept.Offline.Message")));
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    return;
                }
                if (targetPlayer.getName().equals(player.getName())) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Commands.TPAccept.Yourself.Message")));
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    return;
                }
                PlayerData targetPlayerData = playerDataManager.getPlayerData(targetPlayer);
                if (targetPlayerData.getTeleport() == null ||
                        !targetPlayerData.getTeleport().equals(player.getUniqueId())) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Commands.TPAccept.Pending.Player.Message")));
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    return;
                }
                PlayerData playerData = playerDataManager.getPlayerData(player);
                if (playerData.hasPendingExpiry()) {
                    Expiry pendingExpiry = playerData.getPendingExpiry();
                    if (playerData.getTeleport() != null && playerData.getTeleport().equals(targetPlayer.getUniqueId()) &&
                            pendingExpiry.getType() == ExpiryType.Teleport) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Commands.TPAccept.Teleporting.Player.Target.Message").replace(
                                        "%seconds",
                                        String.valueOf((int) ((System.currentTimeMillis() - pendingExpiry.getTime()) / 1000L)))));
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Commands.TPAccept.Teleporting.Player.Other.Message")));
                    }
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    return;
                }
                if (targetPlayerData.hasPendingExpiry()) {
                    Expiry pendingExpiry = targetPlayerData.getPendingExpiry();
                    if (targetPlayerData.getTeleport() != null &&
                            targetPlayerData.getTeleport().equals(player.getUniqueId()) &&
                            pendingExpiry.getType() == ExpiryType.Teleport) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Commands.TPAccept.Teleporting.Target.Target.Message").replace(
                                        "%seconds",
                                        String.valueOf((int) ((System.currentTimeMillis() - pendingExpiry.getTime()) / 1000L)))));
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Commands.TPAccept.Teleporting.Target.Other.Message")));
                    }
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    return;
                }
                if (TPAcceptCommand.this.instance.getCombatTagPlus() != null) {
                    TagManager tagManager = TPAcceptCommand.this.instance.getCombatTagPlus().getTagManager();
                    if (tagManager.isTagged(player.getUniqueId())) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Commands.TPAccept.Combat.Player.Message")));
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                        return;
                    }
                    if (tagManager.isTagged(targetPlayer.getUniqueId())) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Commands.TPAccept.Combat.Target.Message")));
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                        return;
                    }
                }
                FPlayers fPlayers = FPlayers.i;
                if (!Board.getInstance().getFactionAt(new FLocation(player.getLocation())).isWilderness()) {
                    FPlayer fPlayer = fPlayers.get(player);
                    if (!fPlayer.isInOwnTerritory() && !fPlayer.isInAllyTerritory() &&
                            !fPlayer.isInNeutralTerritory()) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Commands.TPAccept.Faction.Player.Message")));
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                        return;
                    }
                }
                if (!Board.getInstance().getFactionAt(new FLocation(targetPlayer.getLocation())).isWilderness()) {
                    FPlayer fPlayer = fPlayers.get(targetPlayer);
                    if (!fPlayer.isInOwnTerritory() && !fPlayer.isInAllyTerritory() &&
                            !fPlayer.isInNeutralTerritory()) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Commands.TPAccept.Faction.Target.Message")));
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                        return;
                    }
                }
                targetPlayerData.getTimeout(TimeoutType.Teleport).setTime(0L);
                if (targetPlayerData.isTeleportToPlayer()) {
                    teleportPlayerData = targetPlayerData;
                    teleportPlayer = targetPlayer;
                } else {
                    playerData.setTeleport(targetPlayer.getUniqueId());
                    targetPlayerData.setTeleport(null);
                    teleportPlayerData = playerData;
                    teleportPlayer = player;
                }
                long expiryTime = 8L;
                long removedExpiryTime = (EXPUtil.getTotalExperience(teleportPlayer) / 1000);
                if (expiryTime - removedExpiryTime < 1L) {
                    expiryTime = 1L;
                } else {
                    expiryTime -= removedExpiryTime;
                    if (expiryTime > 4L)
                        teleportPlayer.playSound(teleportPlayer.getLocation(), Sound.PORTAL_TRIGGER, 1.0F, 1.0F);
                }
                teleportPlayerData.getExpiry(ExpiryType.Teleport)
                        .setTime(System.currentTimeMillis() + expiryTime * 1000L);
                teleportPlayerData.setTeleportLocation(teleportPlayer.getLocation().clone());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Commands.TPAccept.Accepted.Player.Message")
                                .replace("%player", targetPlayer.getName()).replace("%seconds", String.valueOf(expiryTime))));
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Commands.TPAccept.Accepted.Receiver.Message").replace("%player",
                                player.getName())));
            }
        });
        return true;
    }
}
