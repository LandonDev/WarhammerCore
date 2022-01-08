package landon.jurassiccore.commands;

import landon.core.util.c;
import landon.jurassiccore.utils.Enchantments;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class EnchantCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player) {
            Player user = (Player) commandSender;
            ItemStack stack = user.getItemInHand();
            if (stack == null || stack.getType() == Material.AIR) {
                user.sendMessage(c.c("&cNothing in your hand!"));
                return false;
            }
            if (args.length == 0) {
                Set<String> enchantmentslist = new TreeSet<String>();
                for (Map.Entry<String, Enchantment> entry : (Iterable<Map.Entry<String, Enchantment>>) Enchantments.entrySet()) {
                    String str = ((Enchantment)entry.getValue()).getName().toLowerCase(Locale.ENGLISH);
                    if (enchantmentslist.contains(str) && ((Enchantment)entry.getValue()).canEnchantItem(stack)) {
                        enchantmentslist.add(entry.getKey());
                    }
                }
                user.sendMessage(c.c("&cNot enough args!"));
            }
            int level = -1;
            if (args.length > 1)
                try {
                    level = Integer.parseInt(args[1]);
                } catch (NumberFormatException ex) {
                    level = -1;
                }
            Enchantment enchantment = Enchantments.getByName(args[0]);
            stack.addUnsafeEnchantment(enchantment, level);
            user.getInventory().setItemInHand(stack);
            user.updateInventory();
            String enchantmentName = enchantment.getName().toLowerCase(Locale.ENGLISH);
            if (level == 0) {
                user.sendMessage(c.c("&aEnchantment " + enchantmentName + " removed!"));
            } else {
                user.sendMessage(c.c("&aEnchantment " + enchantmentName + " applied!"));
            }
            return false;
        }
        return false;
    }
}
