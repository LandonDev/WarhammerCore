package landon.core.commands;

import landon.core.WarhammerCore;
import landon.core.inventories.CustomItemInventory;
import landon.core.util.GiveUtil;
import landon.core.util.c;
import landon.core.util.items.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdCustomItem implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender.isOp()) {
            if(args.length > 1 && Bukkit.getPlayer(args[0]) != null) {
                for(CustomItem item : WarhammerCore.get().getItemManager().getOasisItems()) {
                    if(item.getName().equalsIgnoreCase(args[1])) {
                        GiveUtil.giveOrDropItem(Bukkit.getPlayer(args[0]), item.getItem());
                        return true;
                    }
                }
                List<String> oasisItemList = new ArrayList<>();
                for(CustomItem item : WarhammerCore.get().getItemManager().getOasisItems()) {
                    oasisItemList.add(item.getName());
                }
                sender.sendMessage(c.c("&cNo item found for query '&7" + args[1] + "&c'"));
                sender.sendMessage(c.c("&7Available items: &f" + oasisItemList.toString()));
                return true;
            } else {
                if(Bukkit.getPlayer(args[0]) != null && args.length == 1) {
                    CustomItemInventory.INVENTORY.open((Player)sender);
                }
                sender.sendMessage("/giveitem <player> <itemName>");
            }
        }
        return true;
    }
}
