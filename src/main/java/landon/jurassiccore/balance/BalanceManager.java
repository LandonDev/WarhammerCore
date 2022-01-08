package landon.jurassiccore.balance;

import com.massivecraft.factions.P;
import landon.jurassiccore.file.FileManager;
import landon.jurassiccore.JurassicCore;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class BalanceManager {
    private final JurassicCore instance;

    private List<Balance> balances = new ArrayList<>();

    public BalanceManager(JurassicCore instance) {
        this.instance = instance;
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null)
            return;
        prepareBalances();
        (new BalanceTask(this)).runTaskTimerAsynchronously(P.p, 0L,
                instance.getFileManager().getConfig(new File(instance.getDataFolder(), "config.yml"))
                        .getFileConfiguration().getInt("Balance.Top.Time") * 20L);
    }

    public void prepareBalances() {
        FileManager fileManager = this.instance.getFileManager();
        Economy economy = this.instance.getVaultManager().getEconomy();
        byte b;
        int i;
        File[] arrayOfFile;
        for (i = (arrayOfFile = (new File(this.instance.getDataFolder() + "/player-data")).listFiles()).length, b = 0; b < i; ) {
            File configFile = arrayOfFile[b];
            if (!fileManager.getFileExtension(configFile).equals(".yml"))
                return;
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);
            if (yamlConfiguration.getString("Name") != null) {
                UUID uuid = UUID.fromString(configFile.getName().replace(".yml", ""));
                try {
                    this.balances.add(new Balance(uuid, yamlConfiguration.getString("Name"),
                            economy.getBalance(Bukkit.getServer().getOfflinePlayer(uuid))));
                } catch (Exception e) {
                    this.balances.add(new Balance(uuid, yamlConfiguration.getString("Name"),
                            economy.getBalance(yamlConfiguration.getString("Name"))));
                }
            }
            b++;
        }
        this.balances.sort(new Comparator<Balance>() {
            public int compare(Balance balance1, Balance balance2) {
                return Double.valueOf(balance2.getBalance()).compareTo(Double.valueOf(balance1.getBalance()));
            }
        });
    }

    public void clearBalances() {
        this.balances.clear();
    }

    public double getServerBalance() {
        double serverBalance = 0.0D;
        for (Balance balance : this.balances)
            serverBalance += balance.getBalance();
        return serverBalance;
    }

    public Balance getBalance(String name) {
        for (Balance balance : this.balances) {
            if (balance.getName().equalsIgnoreCase(name))
                return balance;
        }
        return null;
    }

    public List<Balance> getBalances() {
        return this.balances;
    }
}
