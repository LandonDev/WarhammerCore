package landon.jurassiccore.commands;

import com.massivecraft.factions.P;
import landon.jurassiccore.utils.NumberUtil;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.balance.Balance;
import landon.jurassiccore.balance.BalanceManager;
import landon.jurassiccore.vault.VaultManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class BalanceCommand implements CommandExecutor {
    private JurassicCore instance;

    public BalanceCommand(JurassicCore instance) {
        this.instance = instance;
    }

    public boolean onCommand(final CommandSender sender, Command command, String s, final String[] args) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(P.p, new Runnable() {
            public void run() {
                String targetPlayerName;
                double targetPlayerBalance;
                if (!(sender instanceof Player)) {
                    sender.sendMessage(String.valueOf(BalanceCommand.this.instance.getDescription().getName()) +
                            " | Error: You must be a player to perform that command.");
                    return;
                }
                BalanceManager balanceManager = BalanceCommand.this.instance.getBalanceManager();
                VaultManager vaultManager = BalanceCommand.this.instance.getVaultManager();
                FileConfiguration configLoad = BalanceCommand.this.instance.getFileManager()
                        .getConfig(new File(BalanceCommand.this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
                Player player = (Player) sender;
                if (args.length > 1) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Commands.Balance.Invalid.Message")));
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    return;
                }
                if (args.length == 1) {
                    Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
                    if (targetPlayer == null) {
                        Balance balance = balanceManager.getBalance(args[0]);
                        if (balance == null) {
                            player.sendMessage(
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Commands.Balance.Fetch.Message"))
                                            .replace("%player", args[0]));
                            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                            return;
                        }
                        targetPlayerName = balance.getName();
                        targetPlayerBalance = balance.getBalance();
                    } else {
                        targetPlayerName = targetPlayer.getName();
                        targetPlayerBalance = vaultManager.getEconomy().getBalance((OfflinePlayer) targetPlayer);
                    }
                } else {
                    targetPlayerName = player.getName();
                    targetPlayerBalance = vaultManager.getEconomy().getBalance((OfflinePlayer) player);
                }
                if (targetPlayerName.equalsIgnoreCase(player.getName())) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Commands.Balance.Balance.Yourself.Message")
                                    .replace("%player", player.getName())
                                    .replace("%balance", NumberUtil.formatNumberByCommas(
                                            String.valueOf(Double.valueOf(targetPlayerBalance).longValue())))));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Commands.Balance.Balance.Target.Message")
                                    .replace("%player", targetPlayerName)
                                    .replace("%balance", NumberUtil.formatNumberByCommas(
                                            String.valueOf(Double.valueOf(targetPlayerBalance).longValue())))));
                }
                player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
            }
        });
        return true;
    }
}
