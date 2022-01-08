package landon.jurassiccore.commands;

import java.io.File;
import landon.jurassiccore.JurassicCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class GameModeCommand implements CommandExecutor {
  private JurassicCore instance;
  
  public GameModeCommand(JurassicCore instance) {
    this.instance = instance;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
    String gamemodeInput;
    GameMode gamemode;
    String[] commandArgs = new String[0];
    if (s.equalsIgnoreCase("gamemode") || s.equalsIgnoreCase("gm")) {
      commandArgs = args;
    } else if (s.equalsIgnoreCase("adventure") || s.equalsIgnoreCase("adventuremode") || 
      s.equalsIgnoreCase("gma")) {
      if (args.length == 0) {
        commandArgs = new String[] { "adventure" };
      } else if (args.length == 1) {
        commandArgs = new String[] { "adventure", args[0] };
      } 
    } else if (s.equalsIgnoreCase("creative") || s.equalsIgnoreCase("creativemode") || s.equalsIgnoreCase("gmc")) {
      if (args.length == 0) {
        commandArgs = new String[] { "creative" };
      } else if (args.length == 1) {
        commandArgs = new String[] { "creative", args[0] };
      } 
    } else if (s.equalsIgnoreCase("survival") || s.equalsIgnoreCase("survivalmode") || s.equalsIgnoreCase("gms")) {
      if (args.length == 0) {
        commandArgs = new String[] { "survival" };
      } else if (args.length == 1) {
        commandArgs = new String[] { "survival", args[0] };
      } 
    } else if (s.equalsIgnoreCase("spectator") || s.equalsIgnoreCase("spectatormode") || s.equalsIgnoreCase("spec") || 
      s.equalsIgnoreCase("sp") || s.equalsIgnoreCase("gmsp")) {
      if (args.length == 0) {
        commandArgs = new String[] { "spectator" };
      } else if (args.length == 1) {
        commandArgs = new String[] { "spectator", args[0] };
      } 
    } 
    FileConfiguration configLoad = this.instance.getFileManager()
      .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
    if (sender instanceof Player) {
      Player player = (Player)sender;
      if (!player.hasPermission("jurassiccore.gamemode") && !player.hasPermission("jurassiccore.gamemode.*") && 
        !player.hasPermission("jurassiccore.*")) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.GameMode.Permission.Command.Message")));
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        return true;
      } 
      if (commandArgs.length == 0 || commandArgs.length > 2) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.GameMode.Invalid.Message")));
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        return true;
      } 
      if (commandArgs.length == 1) {
        gamemodeInput = commandArgs[0];
      } else {
        gamemodeInput = commandArgs[0];
        if (!player.hasPermission("jurassiccore.gamemode.other") && 
          !player.hasPermission("jurassiccore.gamemode.*") && !player.hasPermission("jurassiccore.*")) {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configLoad.getString("Commands.GameMode.Permission.Other.Message")));
          player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
          return true;
        } 
        if (commandArgs.length != 2) {
          player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                configLoad.getString("Commands.GameMode.Invalid.Message")));
          player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
          return true;
        } 
      } 
    } else {
      if (commandArgs.length != 2) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
              configLoad.getString("Commands.GameMode.Invalid.Message")));
        return true;
      } 
      gamemodeInput = commandArgs[0];
    } 
    if (isGameMode(gamemodeInput)) {
      gamemode = getGameMode(gamemodeInput);
    } else {
      sender.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.GameMode.Exist.Message"))
          .replace("%gamemode", gamemodeInput));
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      } 
      return true;
    } 
    Player targetPlayer = null;
    if (commandArgs.length == 2) {
      targetPlayer = Bukkit.getServer().getPlayer(commandArgs[1]);
    } else if (sender instanceof Player) {
      targetPlayer = (Player)sender;
    } 
    if (targetPlayer == null) {
      sender.sendMessage(
          ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.GameMode.Connected.Message"))
          .replace("%player", commandArgs[1]));
      if (sender instanceof Player) {
        Player player = (Player)sender;
        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
      } 
      return true;
    } 
    targetPlayer.setGameMode(gamemode);
    if (sender instanceof Player) {
      Player player = (Player)sender;
      player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
      if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad
              .getString("Commands.GameMode.Set.Yourself.Message").replace("%gamemode", gamemode.name())));
        return true;
      } 
    } 
    sender.sendMessage(
        ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.GameMode.Set.Target.Message")
          .replace("%player", targetPlayer.getName()).replace("%gamemode", gamemode.name())));
    return true;
  }
  
  public GameMode getGameMode(String input) {
    byte b;
    int i;
    GameMode[] arrayOfGameMode;
    for (i = (arrayOfGameMode = GameMode.values()).length, b = 0; b < i; ) {
      GameMode gamemode = arrayOfGameMode[b];
      if (gamemode.name().equalsIgnoreCase(input) || (
        input.matches("[0-9]+") && gamemode.getValue() == Integer.valueOf(input).intValue()))
        return gamemode; 
      b++;
    } 
    return null;
  }
  
  public boolean isGameMode(String input) {
    byte b;
    int i;
    GameMode[] arrayOfGameMode;
    for (i = (arrayOfGameMode = GameMode.values()).length, b = 0; b < i; ) {
      GameMode gamemode = arrayOfGameMode[b];
      if (gamemode.name().equalsIgnoreCase(input) || (
        input.matches("[0-9]+") && gamemode.getValue() == Integer.valueOf(input).intValue()))
        return true; 
      b++;
    } 
    return false;
  }
}
