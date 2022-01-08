package landon.jurassiccore.vault;

import java.io.File;
import java.io.IOException;
import java.util.List;

import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.balance.Balance;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class EconomyImplementer implements Economy {
  private final JurassicCore instance;
  
  public EconomyImplementer(JurassicCore instance) {
    this.instance = instance;
  }
  
  public EconomyResponse bankBalance(String arg0) {
    return null;
  }
  
  public EconomyResponse bankDeposit(String arg0, double arg1) {
    return null;
  }
  
  public EconomyResponse bankHas(String arg0, double arg1) {
    return null;
  }
  
  public EconomyResponse bankWithdraw(String arg0, double arg1) {
    return null;
  }
  
  public EconomyResponse createBank(String arg0, String arg1) {
    return null;
  }
  
  public EconomyResponse createBank(String arg0, OfflinePlayer offlinePlayer) {
    return null;
  }
  
  public boolean createPlayerAccount(String arg0) {
    return false;
  }
  
  public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
    return false;
  }
  
  public boolean createPlayerAccount(String arg0, String arg1) {
    return false;
  }
  
  public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String arg1) {
    return false;
  }
  
  public String currencyNamePlural() {
    return null;
  }
  
  public String currencyNameSingular() {
    return null;
  }
  
  public EconomyResponse deleteBank(String s) {
    return null;
  }
  
  public EconomyResponse depositPlayer(String s, double v) {
    PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
    Player player = Bukkit.getServer().getPlayer(s);
    if (player == null || !playerDataManager.hasPlayerData(player))
      return null; 
    PlayerData playerData = playerDataManager.getPlayerData(player);
    playerData.setBalance(playerData.getBalance() + v);
    return null;
  }
  
  public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
    if (offlinePlayer == null || offlinePlayer.getUniqueId() == null)
      return null; 
    if (offlinePlayer.isOnline()) {
      depositPlayer(offlinePlayer.getName(), v);
    } else {
      File configFile = new File(String.valueOf(this.instance.getDataFolder().toString()) + "/player-data", 
          String.valueOf(offlinePlayer.getUniqueId().toString()) + ".yml");
      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);
      yamlConfiguration.set("Money", Double.valueOf(yamlConfiguration.getDouble("Money") + v));
      try {
        yamlConfiguration.save(configFile);
      } catch (IOException e) {
        e.printStackTrace();
      } 
    } 
    return null;
  }
  
  public EconomyResponse depositPlayer(String s, String s1, double v) {
    PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
    Player player = Bukkit.getServer().getPlayer(s);
    if (player == null || !playerDataManager.hasPlayerData(player))
      return null; 
    PlayerData playerData = playerDataManager.getPlayerData(player);
    playerData.setBalance(playerData.getBalance() + v);
    return null;
  }
  
  public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
    if (offlinePlayer == null || offlinePlayer.getUniqueId() == null)
      return null; 
    if (offlinePlayer.isOnline()) {
      depositPlayer(offlinePlayer.getName(), v);
    } else {
      File configFile = new File(String.valueOf(this.instance.getDataFolder().toString()) + "/player-data", 
          String.valueOf(offlinePlayer.getUniqueId().toString()) + ".yml");
      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);
      yamlConfiguration.set("Money", Double.valueOf(yamlConfiguration.getDouble("Money") + v));
      try {
        yamlConfiguration.save(configFile);
      } catch (IOException e) {
        e.printStackTrace();
      } 
    } 
    return null;
  }
  
  public String format(double arg0) {
    return null;
  }
  
  public int fractionalDigits() {
    return 0;
  }
  
  public double getBalance(String s) {
    PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
    Player player = Bukkit.getServer().getPlayer(s);
    if (player == null || !playerDataManager.hasPlayerData(player)) {
      Balance balance = this.instance.getBalanceManager().getBalance(s);
      if (balance != null)
        return balance.getBalance(); 
      return 0.0D;
    } 
    return playerDataManager.getPlayerData(player).getBalance();
  }
  
  public double getBalance(OfflinePlayer offlinePlayer) {
    if (offlinePlayer == null || offlinePlayer.getUniqueId() == null)
      return 0.0D; 
    if (offlinePlayer.isOnline())
      return getBalance(offlinePlayer.getName()); 
    return YamlConfiguration.loadConfiguration(new File(String.valueOf(this.instance.getDataFolder().toString()) + "/player-data", 
          String.valueOf(offlinePlayer.getUniqueId().toString()) + ".yml")).getDouble("Money");
  }
  
  public double getBalance(String s, String s1) {
    PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
    Player player = Bukkit.getServer().getPlayer(s);
    if (player == null || !playerDataManager.hasPlayerData(player)) {
      Balance balance = this.instance.getBalanceManager().getBalance(s);
      if (balance != null)
        return balance.getBalance(); 
      return 0.0D;
    } 
    return playerDataManager.getPlayerData(player).getBalance();
  }
  
  public double getBalance(OfflinePlayer offlinePlayer, String s) {
    if (offlinePlayer == null || offlinePlayer.getUniqueId() == null)
      return 0.0D; 
    if (offlinePlayer.isOnline())
      return getBalance(offlinePlayer.getName()); 
    return YamlConfiguration.loadConfiguration(new File(String.valueOf(this.instance.getDataFolder().toString()) + "/player-data", 
          String.valueOf(offlinePlayer.getUniqueId().toString()) + ".yml")).getDouble("Money");
  }
  
  public List<String> getBanks() {
    return null;
  }
  
  public String getName() {
    return null;
  }
  
  public boolean has(String arg0, double arg1) {
    return true;
  }
  
  public boolean has(OfflinePlayer offlinePlayer, double arg1) {
    return true;
  }
  
  public boolean has(String arg0, String arg1, double arg2) {
    return true;
  }
  
  public boolean has(OfflinePlayer offlinePlayer, String arg1, double arg2) {
    return true;
  }
  
  public boolean hasAccount(String arg0) {
    return true;
  }
  
  public boolean hasAccount(OfflinePlayer offlinePlayer) {
    return false;
  }
  
  public boolean hasAccount(String arg0, String arg1) {
    return true;
  }
  
  public boolean hasAccount(OfflinePlayer offlinePlayer, String arg1) {
    return true;
  }
  
  public boolean hasBankSupport() {
    return true;
  }
  
  public EconomyResponse isBankMember(String arg0, String arg1) {
    return null;
  }
  
  public EconomyResponse isBankMember(String arg0, OfflinePlayer offlinePlayer) {
    return null;
  }
  
  public EconomyResponse isBankOwner(String arg0, String arg1) {
    return null;
  }
  
  public EconomyResponse isBankOwner(String arg0, OfflinePlayer offlinePlayer) {
    return null;
  }
  
  public boolean isEnabled() {
    return false;
  }
  
  public EconomyResponse withdrawPlayer(String s, double v) {
    PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
    Player player = Bukkit.getServer().getPlayer(s);
    if (player == null || !playerDataManager.hasPlayerData(player))
      return null; 
    PlayerData playerData = playerDataManager.getPlayerData(player);
    playerData.setBalance(playerData.getBalance() - v);
    return null;
  }
  
  public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
    if (offlinePlayer == null || offlinePlayer.getUniqueId() == null)
      return null; 
    if (offlinePlayer.isOnline()) {
      withdrawPlayer(offlinePlayer.getName(), v);
    } else {
      File configFile = new File(String.valueOf(this.instance.getDataFolder().toString()) + "/player-data", 
          String.valueOf(offlinePlayer.getUniqueId().toString()) + ".yml");
      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);
      yamlConfiguration.set("Money", Double.valueOf(yamlConfiguration.getDouble("Money") - v));
      try {
        yamlConfiguration.save(configFile);
      } catch (IOException e) {
        e.printStackTrace();
      } 
    } 
    return null;
  }
  
  public EconomyResponse withdrawPlayer(String s, String s1, double v) {
    PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
    Player player = Bukkit.getServer().getPlayer(s);
    if (player == null || !playerDataManager.hasPlayerData(player))
      return null; 
    PlayerData playerData = playerDataManager.getPlayerData(player);
    playerData.setBalance(playerData.getBalance() - v);
    return null;
  }
  
  public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
    if (offlinePlayer == null || offlinePlayer.getUniqueId() == null)
      return null; 
    if (offlinePlayer.isOnline()) {
      withdrawPlayer(offlinePlayer.getName(), v);
    } else {
      File configFile = new File(String.valueOf(this.instance.getDataFolder().toString()) + "/player-data", 
          String.valueOf(offlinePlayer.getUniqueId().toString()) + ".yml");
      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);
      yamlConfiguration.set("Money", Double.valueOf(yamlConfiguration.getDouble("Money") - v));
      try {
        yamlConfiguration.save(configFile);
      } catch (IOException e) {
        e.printStackTrace();
      } 
    } 
    return null;
  }
}
