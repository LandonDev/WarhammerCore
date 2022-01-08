package landon.core.patchapi.patches.ftop.commands;

import com.massivecraft.factions.P;
import landon.core.patchapi.patches.ftop.FactionsTop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRecalcFactionsTop
        implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.isOp()) {
            return true;
        }
        sender.sendMessage((Object)ChatColor.YELLOW + ChatColor.BOLD.toString() + "(!) " + (Object)ChatColor.YELLOW + "Please wait while we calculate top Factions..");
        FactionsTop.get().getTopManager().loadTopFactions(() -> Bukkit.getScheduler().scheduleSyncDelayedTask(P.p, () -> {
            if (sender instanceof Player) {
                Player pl = (Player)sender;
                pl.performCommand("ftop");
            }
        }), -1, args.length == 1 && args[0].equalsIgnoreCase("wealth"));
        return true;
    }
}
