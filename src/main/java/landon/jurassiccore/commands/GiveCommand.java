package landon.jurassiccore.commands;

import landon.core.util.GiveUtil;
import landon.core.util.c;
import lombok.SneakyThrows;
import landon.jurassiccore.JurassicCore;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommand implements CommandExecutor {
    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;
        if (player.hasPermission("jurassiccore.give")) {
            if (args.length < 1) {
                player.sendMessage(c.c("&cCorrect usage: /i <item> [amount]"));
            }
            ItemStack stack;
            if(args.length >= 2) {
                if(NumberUtils.isNumber(args[0])) {
                    stack = JurassicCore.getInstance().getItemDB().get(args[0], Integer.parseInt(args[1]));
                } else {
                    stack = JurassicCore.getInstance().getItemDB().getByName(args[0], Integer.parseInt(args[1]));
                }
            } else {
                if(NumberUtils.isNumber(args[0])) {
                    stack = JurassicCore.getInstance().getItemDB().get(args[0]);
                } else {
                    stack = JurassicCore.getInstance().getItemDB().getByName(args[0]);
                }
            }
            if (stack.getType() == Material.AIR) {
                player.sendMessage(c.c("&cThat's air, bud. We can't spawn that."));
                player.sendMessage(c.c("&7(It also could be a plugin error, who knows!)"));
                return false;
            }
            GiveUtil.giveOrDropItem(player, stack);
            player.updateInventory();
            return false;
        }
        player.sendMessage(c.c("&8[&a&lJurassic&2&lPvP&8] &fUnknown command. Type '&7/help&f' for help."));
        return false;
    }
}
