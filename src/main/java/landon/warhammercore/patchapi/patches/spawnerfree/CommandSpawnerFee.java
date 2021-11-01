package landon.warhammercore.patchapi.patches.spawnerfree;

import com.massivecraft.factions.P;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

final class CommandSpawnerFee implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp())
            return true;
        if (args.length != 2) {
            for (EntityType et : EntityType.values()) {
                if (SpawnerFeeListener.getSpawnerFeeBase(et) != 0)
                    sender.sendMessage(et.toString() + " => " + SpawnerFeeListener.getSpawnerFeeBase(et));
            }
            sender.sendMessage("/spawnerfee <entityType> <spawnerFee>");
            return true;
        }
        if (args[0].equals("disable") && args[1].equals("fees")) {
            SpawnerFee.spawnerFee = false;
            P.p.getConfig().set("patches.spawner_fee.spawnerFee", Boolean.valueOf(false));
            P.p.saveConfig();
            sender.sendMessage("Spawner Fees DISABLED");
            return true;
        }
        if (args[0].equals("enable") && args[1].equals("fees")) {
            SpawnerFee.spawnerFee = true;
            P.p.getConfig().set("patches.spawner_fee.spawnerFee", Boolean.valueOf(true));
            P.p.saveConfig();
            sender.sendMessage("Spawner Fees ENABLED");
            return true;
        }
        String entityType = args[0];
        int price = Integer.parseInt(args[1]);
        if (EntityType.valueOf(entityType.toUpperCase()) == null) {
            sender.sendMessage("Invalid entityType.");
            return true;
        }
        P.p.getConfig().set("patches.spawner_fee." + entityType.toLowerCase(), Integer.valueOf(price));
        P.p.saveConfig();
        sender.sendMessage(ChatColor.YELLOW + "Set spawner fee for " + entityType + " to $" + price);
        return true;
    }
}
