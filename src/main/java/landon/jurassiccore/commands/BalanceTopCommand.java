package landon.jurassiccore.commands;

import landon.jurassiccore.utils.NumberUtil;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.balance.Balance;
import landon.jurassiccore.balance.BalanceManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class BalanceTopCommand implements CommandExecutor {
    private JurassicCore instance;

    public BalanceTopCommand(JurassicCore instance) {
        this.instance = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
            return true;
        }
        BalanceManager balanceManager = this.instance.getBalanceManager();
        FileConfiguration configLoad = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
        Player player = (Player) sender;
        int pageNumber = 1;
        if (args.length > 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Commands.BalanceTop.Invalid.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            return true;
        }
        if (args.length == 1)
            if (args[0].matches("[0-9]+")) {
                if (args[0].length() >= 10) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Commands.BalanceTop.Pages.Message")));
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    return true;
                }
                pageNumber = Integer.valueOf(args[0]).intValue();
                if (pageNumber == 0)
                    pageNumber = 1;
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Commands.BalanceTop.Numerical.Message")));
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                return true;
            }
        int index = pageNumber * 8 - 8;
        int endIndex = (index >= balanceManager.getBalances().size()) ? (balanceManager.getBalances().size() - 1) : (
                index + 8);
        int nextEndIndex = (int) Math.round((balanceManager.getBalances().size() / 8) + 0.5D);
        if (pageNumber > nextEndIndex) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Commands.BalanceTop.Pages.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            return true;
        }
        for (String titleLine : configLoad.getStringList("Commands.BalanceTop.Title.Lines"))
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    titleLine.replace("%current_page", String.valueOf(pageNumber)).replace("%max_page", String.valueOf(nextEndIndex))
                            .replace("%server_balance", NumberUtil.formatNumberByCommas(
                                    String.valueOf(Double.valueOf(balanceManager.getServerBalance()).longValue())))));
        for (; index < endIndex; index++) {
            if (balanceManager.getBalances().size() > index) {
                Balance balance = balanceManager.getBalances().get(index);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Commands.BalanceTop.Balance.Message")
                                .replace("%position", String.valueOf(index + 1)).replace("%player", balance.getName())
                                .replace("%balance", NumberUtil.formatNumberByCommas(
                                        String.valueOf(Double.valueOf(balance.getBalance()).longValue())))));
            }
        }
        player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
        return true;
    }
}
