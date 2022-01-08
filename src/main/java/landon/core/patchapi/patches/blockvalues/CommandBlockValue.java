package landon.core.patchapi.patches.blockvalues;

import com.massivecraft.factions.P;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

final class CommandBlockValue implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp())
            return true;
        if (args.length != 2) {
            for (Material m : Material.values()) {
                if (BlockValue.getBlockValueBase(m) != 0)
                    sender.sendMessage(m.name() + " => " + BlockValue.getBlockValueBase(m));
            }
            sender.sendMessage("/blockvalue <material> <value>");
            return true;
        }
        String blockType = args[0];
        int price = Integer.parseInt(args[1]);
        if (Material.valueOf(blockType.toUpperCase()) == null) {
            sender.sendMessage("Invalid bukkit material. ref: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html");
            return true;
        }
        P.p.getConfig().set("patches.blockValue." + blockType.toLowerCase(), Integer.valueOf(price));
        P.p.saveConfig();
        sender.sendMessage(ChatColor.YELLOW + "Set blockValue of " + blockType + " to $" + price);
        return true;
    }
}
