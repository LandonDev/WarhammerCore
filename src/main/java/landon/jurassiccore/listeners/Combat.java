package landon.jurassiccore.listeners;

import java.io.File;

import com.massivecraft.factions.P;
import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.expiry.Expiry;
import landon.jurassiccore.expiry.ExpiryType;
import landon.jurassiccore.timeout.Timeout;
import landon.jurassiccore.timeout.TimeoutType;
import net.minelink.ctplus.event.PlayerCombatTagEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

public class Combat implements Listener {
  private JurassicCore instance;
  
  public Combat(JurassicCore instance) {
    this.instance = instance;
  }
  
  @EventHandler
  public void onPlayerCombat(PlayerCombatTagEvent event) {
    final PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
    final FileConfiguration configLoad = this.instance.getFileManager()
      .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
    Player[] players = { event.getAttacker(), event.getVictim() };
    byte b;
    int i;
    Player[] arrayOfPlayer1;
    for (i = (arrayOfPlayer1 = players).length, b = 0; b < i; ) {
      final Player player = arrayOfPlayer1[b];
      if (player != null)
        if (playerDataManager.hasPlayerData(player)) {
          PlayerData playerData = playerDataManager.getPlayerData(player);
          if (playerData.getTeleport() != null) {
            Player targetPlayer = Bukkit.getServer().getPlayer(playerData.getTeleport());
            Timeout timeout = playerData.getTimeout(TimeoutType.Teleport);
            if (timeout.getTime() != 0L) {
              if (targetPlayer != null) {
                targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                      configLoad.getString("Listeners.Combat.Teleport.Request.Message").replace("%player", 
                        player.getName())));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                      configLoad.getString("Listeners.Combat.Teleport.Cancelled.Other.Message")
                      .replace("%player", targetPlayer.getName())));
              } 
              timeout.setTime(0L);
              playerData.setTeleport(null);
            } else if (playerData.hasPendingExpiry() && 
              playerData.getPendingExpiry().getType() == ExpiryType.Teleport) {
              if (targetPlayer != null) {
                targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                      configLoad.getString("Listeners.Combat.Teleport.Expiry.Message").replace("%player", 
                        player.getName())));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                      configLoad.getString("Listeners.Combat.Teleport.Time.Message").replace("%player", 
                        targetPlayer.getName())));
              } 
              playerData.setTeleport(null);
            } 
          } 
          Bukkit.getServer().getScheduler().runTaskAsynchronously(P.p, new Runnable() {
                public void run() {
                  for (Player all : Bukkit.getOnlinePlayers()) {
                    if (all.getUniqueId().equals(player.getUniqueId()))
                      continue; 
                    if (!playerDataManager.hasPlayerData(all))
                      continue; 
                    PlayerData playerData = playerDataManager.getPlayerData(all);
                    if (playerData.getTeleport() != null && playerData.getTeleport().equals(player.getUniqueId())) {
                      Timeout timeout = playerData.getTimeout(TimeoutType.Teleport);
                      if (timeout.getTime() != 0L) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                              configLoad.getString("Listeners.Combat.Teleport.Request.Message")
                              .replace("%player", all.getName())));
                        all.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                              configLoad.getString("Listeners.Combat.Teleport.Cancelled.Other.Message")
                              .replace("%player", player.getName())));
                        timeout.setTime(0L);
                      } else if (playerData.hasPendingExpiry() && 
                        playerData.getPendingExpiry().getType() == ExpiryType.Teleport) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                              configLoad.getString("Listeners.Combat.Teleport.Expiry.Message")
                              .replace("%player", all.getName())));
                        all.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                              configLoad.getString("Listeners.Combat.Teleport.Time.Message")
                              .replace("%player", player.getName())));
                      } 
                      playerData.setTeleport(null);
                      break;
                    } 
                  } 
                }
              });
          if (playerData.hasPendingExpiry()) {
            Expiry pendingExpiry = playerData.getPendingExpiry();
            if (pendingExpiry.getType() != ExpiryType.Teleport) {
              player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    configLoad.getString("Listeners.Combat.Teleport.Cancelled.Player.Message")));
              player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1.0F, 1.0F);
            } 
            player.removePotionEffect(PotionEffectType.CONFUSION);
            pendingExpiry.setTime(0L);
          } 
          if (player.getGameMode() != GameMode.CREATIVE) {
            playerData.setFlying(player.isFlying());
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setFallDistance(0.0F);
            player.playSound(player.getLocation(), Sound.FIREWORK_BLAST2, 1.0F, 0.7F);
          } 
        }  
      b++;
    } 
  }
}
