package landon.jurassiccore.listeners;

import com.massivecraft.factions.P;
import landon.jurassiccore.playerdata.Item;
import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.expiry.ExpiryType;
import landon.jurassiccore.file.FileManager;
import landon.jurassiccore.timeout.Timeout;
import landon.jurassiccore.timeout.TimeoutType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;

public class Quit implements Listener {
    private final JurassicCore instance;

    public Quit(JurassicCore instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
        FileManager fileManager = this.instance.getFileManager();
        FileConfiguration configLoad = fileManager.getConfig(new File(this.instance.getDataFolder(), "language.yml"))
                .getFileConfiguration();
        if (!fileManager.getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Listeners.Quit.Message.Enable"))
            event.setQuitMessage(null);
        if (!playerDataManager.hasPlayerData(player))
            return;
        PlayerData playerData = playerDataManager.getPlayerData(player);
        if (playerData.getTeleport() != null) {
            Player targetPlayer = Bukkit.getServer().getPlayer(playerData.getTeleport());
            if (playerData.getTimeout(TimeoutType.Teleport).getTime() != 0L) {
                if (targetPlayer != null)
                    targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Listeners.Quit.Teleport.Request.Message").replace("%player",
                                    player.getName())));
            } else if (playerData.hasPendingExpiry() &&
                    playerData.getPendingExpiry().getType() == ExpiryType.Teleport &&
                    targetPlayer != null) {
                targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad
                        .getString("Listeners.Quit.Teleport.Expiry.Message").replace("%player", player.getName())));
            }
        }
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (all.getUniqueId().equals(player.getUniqueId()))
                continue;
            if (!playerDataManager.hasPlayerData(all))
                continue;
            PlayerData targetPlayerData = playerDataManager.getPlayerData(all);
            if (targetPlayerData.getReply() != null && targetPlayerData.getReply().equals(player.getName()))
                targetPlayerData.setReply(null);
            if (targetPlayerData.getTeleport() != null && targetPlayerData.getTeleport().equals(player.getUniqueId())) {
                Timeout timeout = targetPlayerData.getTimeout(TimeoutType.Teleport);
                if (timeout.getTime() != 0L) {
                    all.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Listeners.Quit.Teleport.Request.Message").replace("%player",
                                    player.getName())));
                    timeout.setTime(0L);
                } else if (targetPlayerData.hasPendingExpiry() &&
                        targetPlayerData.getPendingExpiry().getType() == ExpiryType.Teleport) {
                    all.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad
                            .getString("Listeners.Quit.Teleport.Time.Message").replace("%player", player.getName())));
                    targetPlayerData.getPendingExpiry().setTime(0L);
                }
                targetPlayerData.setTeleport(null);
            }
        }
        playerDataManager.savePlayerDataAsync(playerData);
        playerDataManager.removePlayerData(player);
        for (Item item : playerData.getItems())
            item.getItem().removeMetadata("ItemProtect", P.p);
    }
}
