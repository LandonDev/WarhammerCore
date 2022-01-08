package landon.jurassiccore.listeners;

import com.massivecraft.factions.P;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.file.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;

public class Join implements Listener {
    private final JurassicCore instance;

    public Join(JurassicCore instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        FileManager fileManager = this.instance.getFileManager();
        FileConfiguration configLoadMain = fileManager.getConfig(new File(this.instance.getDataFolder(), "config.yml"))
                .getFileConfiguration();
        if (configLoadMain.getBoolean("Listeners.Join.Spawn.Enable"))
            if (!(new File(String.valueOf(this.instance.getDataFolder().toString()) + "/player-data", String.valueOf(player.getUniqueId().toString()) + ".yml")).exists() && !player.hasPlayedBefore()) {
                final FileConfiguration configLoadLocations = fileManager
                        .getConfig(new File(this.instance.getDataFolder(), "locations.yml")).getFileConfiguration();
                if (configLoadLocations.getString("Spawn") != null) {
                    player.teleport(this.instance.getLocationManager().getLocation(configLoadLocations, "Spawn"));
                    player.setFallDistance(0.0F);
                    Bukkit.getServer().getScheduler().runTaskLater(P.p, new Runnable() {
                        public void run() {
                            player.teleport(Join.this.instance.getLocationManager().getLocation(configLoadLocations, "Spawn"));
                            player.setFallDistance(0.0F);
                            player.setFireTicks(0);
                        }
                    }, 3L);
                }
            }
        PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
        playerDataManager.createPlayerData(player);
        playerDataManager.preparePlayerDataAsync(player);
        if (configLoadMain.getBoolean("Scoreboard.Enable"))
            this.instance.getScoreboardManager().sendScoreboard(player);
        if (configLoadMain.getBoolean("Nametag.Enable"))
            this.instance.getNametagManager().sendNametags(player);
        if (!configLoadMain.getBoolean("Listeners.Join.Message.Enable"))
            event.setJoinMessage(null);
    }
}
