package landon.jurassiccore.commands;

import com.massivecraft.factions.*;
import landon.jurassiccore.playerdata.Home;
import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.location.LocationManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class SetHomeCommand implements CommandExecutor {
    private JurassicCore instance;

    public SetHomeCommand(JurassicCore instance) {
        this.instance = instance;
    }

    public boolean onCommand(final CommandSender sender, Command command, String s, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
            return true;
        }
        Bukkit.getServer().getScheduler().runTaskAsynchronously(P.p, new Runnable() {
            public void run() {
                String homeName;
                PlayerDataManager playerDataManager = SetHomeCommand.this.instance.getPlayerDataManager();
                LocationManager locationManager = SetHomeCommand.this.instance.getLocationManager();
                FileConfiguration configLoad = SetHomeCommand.this.instance.getFileManager()
                        .getConfig(new File(SetHomeCommand.this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
                Player player = (Player) sender;
                if (!player.hasPermission("jurassiccore.sethome") &&
                        !player.hasPermission("jurassiccore.sethome.*") &&
                        !player.hasPermission("jurassiccore.*")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Commands.SetHome.Permission.Command.Message")));
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    return;
                }
                if (args.length > 1) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Commands.SetHome.Invalid.Message")));
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    return;
                }
                if (args.length == 1) {
                    homeName = args[0];
                } else {
                    homeName = "Default";
                }
                PlayerData playerData = playerDataManager.getPlayerData(player);
                if (playerData.hasHome(homeName)) {
                    if (homeName.equalsIgnoreCase("Default")) {
                        player.sendMessage(
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Commands.SetHome.Exist.Default.Message"))
                                        .replace("%home", homeName));
                    } else {
                        player.sendMessage(
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Commands.SetHome.Exist.Other.Message"))
                                        .replace("%home", homeName));
                    }
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    return;
                }
                if (!player.hasPermission("jurassiccore.sethome.*") &&
                        !player.hasPermission("jurassiccore.*")) {
                    int maxHomes = playerDataManager.getMaxHomes(player);
                    if (maxHomes == 0) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Commands.SetHome.Permission.Command.Message")));
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                        return;
                    }
                    if (playerData.getHomes().size() >= maxHomes) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Commands.SetHome.Permission.Home.Message")));
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                        return;
                    }
                }
                if (locationManager.getRegionNames(SetHomeCommand.this.instance.getWorldGuard(), player.getLocation(), false).size() != 0) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Commands.SetHome.Region.Message")));
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    return;
                }
                if (!Board.getInstance().getFactionAt(new FLocation(player.getLocation())).isWilderness()) {
                    FPlayer fPlayer = FPlayers.i.get(player);
                    if (!fPlayer.isInOwnTerritory() && !fPlayer.isInAllyTerritory()) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Commands.SetHome.Faction.Message")));
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                        return;
                    }
                }
                playerData.addHome(new Home(homeName, player.getLocation(), new ItemStack(Material.GRASS)));
                player.sendMessage(
                        ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.SetHome.Set.Message"))
                                .replace("%home", homeName));
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
            }
        });
        return true;
    }
}
