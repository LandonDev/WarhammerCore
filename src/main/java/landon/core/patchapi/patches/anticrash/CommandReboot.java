package landon.core.patchapi.patches.anticrash;

import com.massivecraft.factions.P;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReboot implements CommandExecutor {
    public CommandReboot() {
        Bukkit.getPluginManager().registerEvents(new RebootListener(), P.p);
    }

    public static boolean restart_task_exists = false;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player &&
                !((Player)sender).isOp())
            return true;
        if (restart_task_exists) {
            sender.sendMessage(ChatColor.YELLOW + "There is already a pending restart task running.");
            return true;
        }
        restart_task_exists = true;
        (new ServerRebootTask(false)).runTaskTimer(P.p, 20L, 20L);
        return true;
    }
}
