package landon.jurassiccore.playerdata;

import com.massivecraft.factions.P;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.location.LocationManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private JurassicCore instance;

    private Map<UUID, PlayerData> playerDataStorage = new HashMap<>();

    public PlayerDataManager(JurassicCore instance) {
        this.instance = instance;
        for (Player all : Bukkit.getOnlinePlayers()) {
            createPlayerData(all);
            preparePlayerData(all);
        }
        (new PlayerDataTask(this)).runTaskTimerAsynchronously(P.p, 0L,
                instance.getFileManager().getConfig(new File(instance.getDataFolder(), "config.yml"))
                        .getFileConfiguration().getInt("PlayerData.Save.Time") * 20L);
    }

    public void onDisable() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (!this.playerDataStorage.containsKey(all.getUniqueId()))
                continue;
            savePlayerData(this.playerDataStorage.get(all.getUniqueId()));
        }
    }

    public void createPlayerData(Player player) {
        this.playerDataStorage.put(player.getUniqueId(), new PlayerData(player.getUniqueId(), player.isFlying()));
    }

    public void savePlayerData(PlayerData playerData) {
        LocationManager locationManager = this.instance.getLocationManager();
        File configFile = new File(String.valueOf(this.instance.getDataFolder().toString()) + "/player-data",
                String.valueOf(playerData.getUUID().toString()) + ".yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);
        yamlConfiguration.set("Homes", null);
        for (Home home : playerData.getHomes()) {
            locationManager.setLocation((FileConfiguration) yamlConfiguration, "Homes." + home.getName() + ".Location", home.getLocation());
            yamlConfiguration.set("Homes." + home.getName() + ".Icon.Material", home.getIcon().getType().name());
            yamlConfiguration.set("Homes." + home.getName() + ".Icon.Data", Short.valueOf(home.getIcon().getDurability()));
        }
        yamlConfiguration.set("Money", Double.valueOf(playerData.getBalance()));
        yamlConfiguration.set("God", Boolean.valueOf(playerData.hasGodMode()));
        yamlConfiguration.set("Pay.Enabled", Boolean.valueOf(playerData.isPayEnabled()));
        yamlConfiguration.set("MSG.Enabled", Boolean.valueOf(playerData.isMSGEnabled()));
        yamlConfiguration.set("MSG.Ignores", playerData.getIgnores());
        try {
            yamlConfiguration.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePlayerDataAsync(final PlayerData playerData) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(P.p, new Runnable() {
            public void run() {
                PlayerDataManager.this.savePlayerData(playerData);
            }
        });
    }

    public void preparePlayerData(Player player) {
        LocationManager locationManager = this.instance.getLocationManager();
        File configFile = new File(String.valueOf(this.instance.getDataFolder().toString()) + "/player-data",
                String.valueOf(player.getUniqueId().toString()) + ".yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);
        PlayerData playerData = this.playerDataStorage.get(player.getUniqueId());
        if (yamlConfiguration.getString("Homes") != null)
            for (String homeName : yamlConfiguration.getConfigurationSection("Homes").getKeys(false)) {
                ItemStack icon;
                if (yamlConfiguration.getString("Homes." + homeName + ".Icon.Data") == null) {
                    icon = new ItemStack(
                            Material.valueOf(yamlConfiguration.getString("Homes." + homeName + ".Icon.Material")));
                } else {
                    icon = new ItemStack(Material.valueOf(yamlConfiguration.getString("Homes." + homeName + ".Icon.Material")),
                            1, (short) yamlConfiguration.getInt("Homes." + homeName + ".Icon.Data"));
                }
                Location location = locationManager.getLocation((FileConfiguration) yamlConfiguration, "Homes." + homeName + ".Location");
                if (location == null || location.getWorld().getName() == null)
                    continue;
                playerData.addHome(new Home(homeName, location, icon));
            }
        if (yamlConfiguration.getString("Money") == null) {
            playerData.setBalance(this.instance.getFileManager().getConfig(new File(this.instance.getDataFolder(), "config.yml"))
                    .getFileConfiguration().getDouble("Balance.Default"));
        } else {
            playerData.setBalance(yamlConfiguration.getDouble("Money"));
        }
        if (yamlConfiguration.getString("God") != null && (
                player.hasPermission("jurassiccore.god") || player.hasPermission("jurassiccore.*")))
            playerData.setGodMode(yamlConfiguration.getBoolean("God"));
        if (yamlConfiguration.getString("Pay") != null)
            playerData.setPayEnabled(yamlConfiguration.getBoolean("Pay.Enabled"));
        if (yamlConfiguration.getString("MSG") != null) {
            playerData.setMSGEnabled(yamlConfiguration.getBoolean("MSG.Enabled"));
            playerData.setIgnores(yamlConfiguration.getStringList("MSG.Ignores"));
        }
        yamlConfiguration.set("Name", player.getName());
        try {
            yamlConfiguration.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void preparePlayerDataAsync(final Player player) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(P.p, new Runnable() {
            public void run() {
                PlayerDataManager.this.preparePlayerData(player);
            }
        });
    }

    public PlayerData getPlayerData(Player player) {
        return this.playerDataStorage.get(player.getUniqueId());
    }

    public void removePlayerData(Player player) {
        this.playerDataStorage.remove(player.getUniqueId());
    }

    public boolean hasPlayerData(Player player) {
        return this.playerDataStorage.containsKey(player.getUniqueId());
    }

    public int getMaxHomes(Player player) {
        for (int i = 1; i < 54; i++) {
            if (player.hasPermission("jurassiccore.sethome." + i))
                return i;
        }
        return 0;
    }

    public int getMaxNearRadius(Player player) {
        for (int i = 500; i > 0; i--) {
            if (player.hasPermission("jurassiccore.near." + i))
                return i;
        }
        return 0;
    }
}
