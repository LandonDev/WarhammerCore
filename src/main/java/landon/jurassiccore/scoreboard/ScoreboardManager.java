package landon.jurassiccore.scoreboard;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import landon.core.util.c;
import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.file.FileManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScoreboardManager {
    private final JurassicCore instance;
    private List<UUID> disabled = new ArrayList<>();

    public ScoreboardManager(JurassicCore instance) {
        this.instance = instance;
    }

    public void sendScoreboard(Player player) {
        if(player.getWorld().getName().startsWith("dungeon_")) {
            return;
        }
        if(this.disabled.contains(player.getUniqueId())) {
            return;
        }
        Faction faction = FPlayers.i.get(player).getFaction();
        if (faction == null || faction.isNone()) {
            sendDefaultScoreboard(player);
        } else {
            sendFactionScoreboard(player);
        }
    }

    public String toggleScoreboard(Player player) {
        if(this.disabled.contains(player.getUniqueId())) {
            this.disabled.remove(player.getUniqueId());
            return c.c("&a&lJurassic&2&lPvP &8| &aInfo: &eYou have &a&lENABLED &ethe player sidebar.");
        }
        this.disabled.add(player.getUniqueId());
        return c.c("&a&lJurassic&2&lPvP &8| &aInfo: &eYou have &c&lDISABLED &ethe player sidebar.");
    }

    public void sendScoreboard(Player player, String scoreboardType) {
        PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
        FileManager fileManager = this.instance.getFileManager();
        if (!playerDataManager.hasPlayerData(player))
            return;
        FileConfiguration configLoad = fileManager.getConfig(new File(this.instance.getDataFolder(), "language.yml"))
                .getFileConfiguration();
        PlayerData playerData = playerDataManager.getPlayerData(player);
        Scoreboard scoreboard = new Scoreboard(this.instance, player);
        if (playerData.getScoreboard() != null)
            playerData.getScoreboard().cancel();
        scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                configLoad.getString("Scoreboard." + scoreboardType + ".Displayname")));
        scoreboard.setDisplayList(configLoad.getStringList("Scoreboard." + scoreboardType + ".Lines"));
        scoreboard.setHealth(fileManager.getConfig(new File(this.instance.getDataFolder(), "config.yml"))
                .getFileConfiguration().getBoolean("Scoreboard.Health"));
        scoreboard.run();
        playerDataManager.getPlayerData(player).setScoreboard(scoreboard);
    }

    public void sendDefaultScoreboard(Player player) {
        sendScoreboard(player, "Default");
    }

    public void sendFactionScoreboard(Player player) {
        sendScoreboard(player, "Faction");
    }
}
