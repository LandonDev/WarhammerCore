package landon.jurassiccore.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.massivecraft.factions.P;
import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.expiry.Expiry;
import landon.jurassiccore.expiry.ExpiryType;
import landon.jurassiccore.teleport.PendingTeleportPlayer;
import landon.jurassiccore.timeout.Timeout;
import landon.jurassiccore.timeout.TimeoutType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TPDenyCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public TPDenyCommand(JurassicCore instance) {
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
    final Player player = (Player)sender;
    Bukkit.getServer().getScheduler().runTaskAsynchronously(P.p, new Runnable() {
          public void run() {
            Player targetPlayer = null;
            if (args.length == 0) {
              List<PendingTeleportPlayer> pendingTeleportPlayers = new ArrayList<>();
              for (Player all : Bukkit.getOnlinePlayers()) {
                if (all.getName().equals(player.getName()))
                  continue; 
                if (!playerDataManager.hasPlayerData(all))
                  continue; 
                PlayerData playerData = playerDataManager.getPlayerData(all);
                Timeout timeout = playerData.getTimeout(TimeoutType.Teleport);
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
                targetPlayer = ((PendingTeleportPlayer)pendingTeleportPlayers.get(0)).getPlayer();
              } 
              if (targetPlayer == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                      configLoad.getString("Commands.TPDeny.Pending.Recent.Message")));
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                return;
              } 
            } else if (args.length == 1) {
              targetPlayer = Bukkit.getServer().getPlayer(args[0]);
            } else {
              player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    configLoad.getString("Commands.TPDeny.Invalid.Message")));
              player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
              return;
            } 
            if (targetPlayer == null) {
              player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    configLoad.getString("Commands.TPDeny.Offline.Message")));
              player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
              return;
            } 
            if (targetPlayer.getName().equals(player.getName())) {
              player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    configLoad.getString("Commands.TPDeny.Yourself.Message")));
              player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
              return;
            } 
            PlayerData targetPlayerData = playerDataManager.getPlayerData(targetPlayer);
            if (targetPlayerData.getTeleport() == null || 
              !targetPlayerData.getTeleport().equals(player.getUniqueId())) {
              player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    configLoad.getString("Commands.TPDeny.Pending.Player.Message")));
              player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
              return;
            } 
            if (targetPlayerData.getTeleport() != null && 
              targetPlayerData.getTeleport().equals(player.getUniqueId())) {
              Timeout timeout = targetPlayerData.getTimeout(TimeoutType.Teleport);
              if (timeout.getTime() != 0L) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                      configLoad.getString("Commands.TPDeny.Denied.Player.Message").replace("%player", 
                        targetPlayer.getName())));
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
                targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                      configLoad.getString("Commands.TPDeny.Denied.Target.Message").replace("%player", 
                        player.getName())));
                targetPlayerData.setTeleport(null);
                timeout.setTime(0L);
                return;
              } 
              if (targetPlayerData.hasPendingExpiry()) {
                Expiry pendingExpiry = targetPlayerData.getPendingExpiry();
                if (pendingExpiry.getType() == ExpiryType.Teleport) {
                  player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                        configLoad.getString("Commands.TPDeny.Denied.Player.Message").replace("%player", 
                          targetPlayer.getName())));
                  player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
                  targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                        configLoad.getString("Commands.TPDeny.Denied.Target.Message").replace("%player", 
                          player.getName())));
                  pendingExpiry.setTime(0L);
                  targetPlayerData.setTeleport(null);
                  return;
                } 
              } 
            } 
          }
        });
    return true;
  }
}
