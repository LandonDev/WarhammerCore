package landon.jurassiccore.commands;

import landon.jurassiccore.JurassicCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class ReplyCommand implements CommandExecutor {
    private JurassicCore instance;

    public ReplyCommand(JurassicCore instance) {
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
        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Commands.Reply.Invalid.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            return true;
        }
        String reply = this.instance.getPlayerDataManager().getPlayerData(player).getReply();
        if (reply == null) {
            player.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Reply.Reply.Message")));
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
            return true;
        }
        Bukkit.getServer().dispatchCommand((CommandSender) player, "msg " + reply + " " + String.join(" ", (CharSequence[]) args));
        return true;
    }
}
