package landon.core.patchapi.patches.ftop.commands;

import com.massivecraft.factions.P;
import landon.core.patchapi.patches.ftop.FactionsTop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandEditFactionsTop implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.isOp())
            return true;
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("includeBank")) {
                boolean current = FactionsTop.get().isIncludeBank();
                current = !current;
                if (args.length == 2)
                    try {
                        current = Boolean.parseBoolean(args[1]);
                    } catch (Exception exception) {}
                FactionsTop.get().setIncludeBank(current);
                P.p.getConfig().set("includeBank", Boolean.valueOf(current));
                P.p.saveConfig();
                sender.sendMessage(ChatColor.RED + "Include Bank: " + current);
                return true;
            }
            if (args[0].equalsIgnoreCase("gradual") || args[0].equalsIgnoreCase("gradualSpawnerValue")) {
                boolean current = FactionsTop.get().isGradualSpawnerValues();
                current = !current;
                if (args.length == 2)
                    try {
                        current = Boolean.parseBoolean(args[1]);
                    } catch (Exception exception) {}
                FactionsTop.get().setGradualSpawnerValues(current);
                P.p.getConfig().set("gradualSpawnerValue", Boolean.valueOf(current));
                P.p.saveConfig();
                sender.sendMessage(ChatColor.RED + "Gradual Spawner Values: " + current);
                return true;
            }
            if (args[0].equalsIgnoreCase("includeHoppers")) {
                boolean current = FactionsTop.get().isIncludeHoppers();
                current = !current;
                if (args.length == 2)
                    try {
                        current = Boolean.parseBoolean(args[1]);
                    } catch (Exception exception) {}
                FactionsTop.get().setIncludeHoppers(current);
                sender.sendMessage(ChatColor.RED + "Hopper Top Calculations: " + current);
                P.p.getConfig().set("includeHoppers", Boolean.valueOf(current));
                P.p.saveConfig();
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "Invalid! Valid: gradualSpawnerValue, includeBank, includeHoppers");
        return false;
    }
}

