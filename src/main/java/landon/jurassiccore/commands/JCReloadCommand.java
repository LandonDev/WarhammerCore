package landon.jurassiccore.commands;

import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.scoreboard.ScoreboardManager;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.file.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class JCReloadCommand implements CommandExecutor {
    private JurassicCore instance;

    public JCReloadCommand(JurassicCore instance) {
        this.instance = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
            return true;
        }
        PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
        ScoreboardManager scoreboardManager = this.instance.getScoreboardManager();
        FileManager fileManager = this.instance.getFileManager();
        FileManager.Config configMain = fileManager.getConfig(new File(this.instance.getDataFolder(), "config.yml"));
        FileManager.Config configLanguage = fileManager.getConfig(new File(this.instance.getDataFolder(), "language.yml"));
        FileManager.Config configLocations = fileManager.getConfig(new File(this.instance.getDataFolder(), "locations.yml"));
        FileConfiguration configLoadLanguage = configLanguage.getFileConfiguration();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("jurassiccore.jcreload") && !player.hasPermission("jurassiccore.*")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configLoadLanguage.getString("Commands.JCReload.Permission.Message")));
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                return true;
            }
            configMain.loadFile();
            configLanguage.loadFile();
            configLocations.loadFile();
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (!playerDataManager.hasPlayerData(all))
                    continue;
                PlayerData playerData = playerDataManager.getPlayerData(all);
                if (playerData.getScoreboard() != null)
                    playerData.getScoreboard().cancel();
                scoreboardManager.sendScoreboard(all);
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoadLanguage.getString("Commands.JCReload.Reloaded.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_USE, 1.0F, 1.0F);
        } else {
            configMain.loadFile();
            configLanguage.loadFile();
            configLocations.loadFile();
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (!playerDataManager.hasPlayerData(all))
                    continue;
                PlayerData playerData = playerDataManager.getPlayerData(all);
                if (playerData.getScoreboard() != null)
                    playerData.getScoreboard().cancel();
                scoreboardManager.sendScoreboard(all);
            }
            sender.sendMessage(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',
                    configLoadLanguage.getString("Commands.JCReload.Reloaded.Message"))));
        }
        return true;
    }
}
