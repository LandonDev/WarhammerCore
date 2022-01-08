package landon.jurassiccore.listeners;

import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.expiry.Expiry;
import landon.jurassiccore.expiry.ExpiryType;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.Iterator;

public class Move implements Listener {
    private JurassicCore instance;

    public Move(JurassicCore instance) {
        this.instance = instance;
    }

    @EventHandler
    private void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE)
            return;
        Iterator<String> iterator = this.instance.getFileManager().getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration().getStringList("Listeners.Fly.Blacklist.Worlds").iterator();
        while (iterator.hasNext()) {
            String worldName = iterator.next();
            if (player.getWorld().getName().equalsIgnoreCase(worldName))
                return;
        }
        PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
        if (!playerDataManager.hasPlayerData(player))
            return;
        if (event.isFlying()) {
            player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1.0F, 0.1F);
        } else {
            player.playSound(player.getLocation(), Sound.FIREWORK_BLAST2, 1.0F, 0.7F);
        }
        playerDataManager.getPlayerData(player).setFlying(event.isFlying());
    }

    @EventHandler
    public void onPlayerMoveFlight(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE)
            return;
        if (player.getAllowFlight())
            return;
        if (this.instance.getCombatTagPlus() != null &&
                this.instance.getCombatTagPlus().getTagManager().isTagged(player.getUniqueId()))
            return;
        Iterator<String> iterator = this.instance.getFileManager().getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration().getStringList("Listeners.Fly.Blacklist.Worlds").iterator();
        while (iterator.hasNext()) {
            String worldName = iterator.next();
            if (player.getWorld().getName().equalsIgnoreCase(worldName))
                return;
        }
        player.setAllowFlight(true);
    }

    @EventHandler
    public void onPlayerMoveTeleport(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
        FileConfiguration configLoad = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
        if (playerDataManager.hasPlayerData(player)) {
            PlayerData playerData = playerDataManager.getPlayerData(player);
            if (playerData.hasPendingExpiry()) {
                if (playerData.getTeleportLocation() != null) {
                    Location teleportLocation = playerData.getTeleportLocation();
                    if (event.getTo().getWorld().getName().equals(teleportLocation.getWorld().getName()) &&
                            teleportLocation.getX() - event.getTo().getX() < 1.0D &&
                            teleportLocation.getX() - event.getTo().getX() > -1.0D &&
                            teleportLocation.getBlockY() == event.getTo().getBlockY() &&
                            teleportLocation.getZ() - event.getTo().getZ() < 1.0D &&
                            teleportLocation.getZ() - event.getTo().getZ() > -1.0D)
                        return;
                }
                Expiry pendingExpiry = playerData.getPendingExpiry();
                if (playerData.getTeleport() != null && pendingExpiry.getType() == ExpiryType.Teleport) {
                    Player targetPlayer = Bukkit.getServer().getPlayer(playerData.getTeleport());
                    if (targetPlayer != null) {
                        targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Listeners.Move.Teleport.Expiry.Message").replace("%player",
                                        player.getName())));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Listeners.Move.Teleport.Cancelled.Other.Message")
                                        .replace("%player", targetPlayer.getName())));
                    }
                    playerData.setTeleport(null);
                }
                if (pendingExpiry.getType() != ExpiryType.Teleport) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Listeners.Move.Teleport.Cancelled.Player.Message")));
                    player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1.0F, 1.0F);
                }
                player.removePotionEffect(PotionEffectType.CONFUSION);
                playerData.getPendingExpiry().setTime(0L);
            }
        }
    }
}
