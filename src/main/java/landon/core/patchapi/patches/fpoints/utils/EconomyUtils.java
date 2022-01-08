package landon.core.patchapi.patches.fpoints.utils;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class EconomyUtils {
    private static Economy economy;

    public static Economy getEconomy() {
        if (economy == null) {
            economy = (Economy)Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
            if (economy == null) {
                Bukkit.getLogger().info("Unable to get economy!!");
                return null;
            }
        }
        return economy;
    }

    public static boolean hasBalance(OfflinePlayer player, double balance) {
        return getEconomy().has(player, balance);
    }

    public static double depositBalanceAndGet(OfflinePlayer player, double balance) {
        getEconomy().depositPlayer(player, balance);
        return getEconomy().getBalance(player);
    }

    public static void depositBalance(OfflinePlayer player, double balance) {
        getEconomy().depositPlayer(player, balance);
    }

    public static double withdrawBalanceAndGet(OfflinePlayer player, double balance) {
        getEconomy().withdrawPlayer(player, balance);
        return getEconomy().getBalance(player);
    }

    public static void withdrawBalance(OfflinePlayer player, double balance) {
        getEconomy().withdrawPlayer(player, balance);
    }

    public static double getBalance(OfflinePlayer player) {
        return getEconomy().getBalance(player);
    }
}
