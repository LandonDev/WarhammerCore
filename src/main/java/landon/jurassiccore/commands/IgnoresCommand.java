package landon.jurassiccore.commands;

import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.JurassicCore;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class IgnoresCommand implements CommandExecutor {
    private JurassicCore instance;

    public IgnoresCommand(JurassicCore instance) {
        this.instance = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
            return true;
        }
        FileConfiguration configLoad = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
        Player player = (Player) sender;
        int pageNumber = 1;
        if (args.length > 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Commands.Ignores.Invalid.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            return true;
        }
        if (args.length == 1)
            if (args[0].matches("[0-9]+")) {
                if (args[0].length() >= 10) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Commands.Ignores.Pages.Message")));
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    return true;
                }
                pageNumber = Integer.valueOf(args[0]).intValue();
                if (pageNumber == 0)
                    pageNumber = 1;
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Commands.Ignores.Numerical.Message")));
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                return true;
            }
        PlayerData playerData = this.instance.getPlayerDataManager().getPlayerData(player);
        if (playerData.getIgnores().size() == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Commands.Ignores.Ignoring.Message")));
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
            return true;
        }
        int index = pageNumber * 8 - 8;
        int endIndex = (index >= playerData.getIgnores().size()) ? (playerData.getIgnores().size() - 1) : (index + 8);
        int nextEndIndex = (int) Math.round((playerData.getIgnores().size() / 8) + 0.5D);
        if (pageNumber > nextEndIndex) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Commands.Ignores.Pages.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            return true;
        }
        player.sendMessage(
                ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Ignores.Title.Message")
                        .replace("%current_page", String.valueOf(pageNumber)).replace("%max_page", String.valueOf(nextEndIndex))));
        for (; index < endIndex; index++) {
            if (playerData.getIgnores().size() > index)
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Commands.Ignores.Ignore.Message").replace("%position", String.valueOf(index + 1))
                                .replace("%player", playerData.getIgnores().get(index))));
        }
        player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
        return true;
    }
}
