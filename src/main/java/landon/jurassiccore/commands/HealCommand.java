package landon.jurassiccore.commands;

import java.io.File;
import java.util.concurrent.TimeUnit;

import landon.jurassiccore.cooldown.CooldownType;
import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.JurassicCore;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class HealCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public HealCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(
          String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
      return true;
    } 
    FileConfiguration configLoad = this.instance.getFileManager()
      .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
    Player player = (Player)sender;
    if (!player.hasPermission("jurassiccore.heal") && !player.hasPermission("jurassiccore.heal.*")) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            configLoad.getString("Commands.Heal.Permission.Message")));
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    PlayerData playerData = this.instance.getPlayerDataManager().getPlayerData(player);
    int cooldownTime = playerData.getCooldown(CooldownType.Heal).getTime();
    if (cooldownTime != 0) {
      long minute = TimeUnit.SECONDS.toMinutes(cooldownTime) - TimeUnit.SECONDS.toHours(cooldownTime) * 60L;
      long second = TimeUnit.SECONDS.toSeconds(cooldownTime) - TimeUnit.SECONDS.toMinutes(cooldownTime) * 60L;
      if (minute == 0L) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.Heal.Cooldown.Message").replace("%time", 
                String.valueOf(second) + " " + configLoad.getString("Commands.Heal.Cooldown.Word.Second"))));
      } else {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.Heal.Cooldown.Message").replace("%time", 
                String.valueOf(minute) + " " + configLoad.getString("Commands.Heal.Cooldown.Word.Minute") + " " + second + 
                " " + configLoad.getString("Commands.Heal.Cooldown.Word.Second"))));
      } 
      player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      return true;
    } 
    int[] cooldownTimes = { 600, 300, 120 };
    for (int i = 3; i > 0; i--) {
      if (player.hasPermission("jurassiccore.heal." + i)) {
        cooldownTime = cooldownTimes[i - 1];
        if (configLoad.getString("Commands.Heal.Healed.Message") != null) {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configLoad.getString("Commands.Heal.Healed.Message")));
          player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 0.1F);
        } 
        if (i == 1) {
          if (player.getHealth() + 6.0D > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
          } else {
            player.setHealth(player.getHealth() + 6.0D);
          } 
        } else {
          player.setHealth(player.getMaxHealth());
        } 
        PotionEffectType[] illegalPotionEffectTypes = { PotionEffectType.BLINDNESS, 
            PotionEffectType.CONFUSION, PotionEffectType.HARM, PotionEffectType.HUNGER, 
            PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, 
            PotionEffectType.WEAKNESS, PotionEffectType.WITHER };
        byte b;
        int j;
        PotionEffectType[] arrayOfPotionEffectType1;
        for (j = (arrayOfPotionEffectType1 = illegalPotionEffectTypes).length, b = 0; b < j; ) {
          PotionEffectType potionEffectType = arrayOfPotionEffectType1[b];
          player.removePotionEffect(potionEffectType);
          b++;
        } 
        if (!player.hasPermission("jurassiccore.heal.bypass") && !player.hasPermission("jurassiccore.heal.*") && 
          !player.hasPermission("jurassiccore.*"))
          playerData.getCooldown(CooldownType.Heal).setTime(cooldownTime); 
        return true;
      } 
    } 
    player.sendMessage(
        ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Heal.Permission.Message")));
    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
    return true;
  }
}
