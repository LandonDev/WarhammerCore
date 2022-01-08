package landon.jurassiccore.commands;

import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.expiry.Expiry;
import landon.jurassiccore.expiry.ExpiryType;
import landon.jurassiccore.file.FileManager;
import landon.jurassiccore.location.LocationManager;
import landon.jurassiccore.utils.EXPUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WarpCommand implements CommandExecutor, TabCompleter {
    private JurassicCore instance;

    public WarpCommand(JurassicCore instance) {
        this.instance = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
            return true;
        }
        LocationManager locationManager = this.instance.getLocationManager();
        FileManager fileManager = this.instance.getFileManager();
        FileConfiguration configLoadLanguage = fileManager.getConfig(new File(this.instance.getDataFolder(), "language.yml"))
                .getFileConfiguration();
        FileConfiguration configLoadLocation = fileManager
                .getConfig(new File(this.instance.getDataFolder(), "locations.yml")).getFileConfiguration();
        Player player = (Player) sender;
        if (!player.hasPermission("jurassiccore.warp") && !player.hasPermission("jurassiccore.warp.*") &&
                !player.hasPermission("jurassiccore.*")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoadLanguage.getString("Commands.Warp.Permission.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            return true;
        }
        if (args.length == 0) {
            Bukkit.getServer().dispatchCommand((CommandSender) player, "warps");
        } else if (args.length == 1) {
            for (String warpName : configLoadLocation.getConfigurationSection("Warps").getKeys(false)) {
                if (warpName.equalsIgnoreCase(args[0])) {
                    Location location = locationManager.getLocation(configLoadLocation,
                            "Warps." + warpName + ".Location");
                    if (location == null || location.getWorld() == null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoadLanguage
                                .getString("Commands.Warp.Location.Message").replace("%warp", warpName)));
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                        return true;
                    }
                    PlayerData playerData = this.instance.getPlayerDataManager().getPlayerData(player);
                    if (playerData.hasPendingExpiry()) {
                        Expiry expiry = playerData.getPendingExpiry();
                        if (expiry.getType() == ExpiryType.Warp) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoadLanguage
                                    .getString("Commands.Warp.Teleporting.Message")
                                    .replace("%warp", playerData.getPendingWarp()).replace("%seconds",
                                            String.valueOf(Math.round((expiry.getTime() - System.currentTimeMillis()) * 0.001D)))));
                            player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    configLoadLanguage.getString("Commands.Warp.Pending.Message")));
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
                        }
                        return true;
                    }
                    if (this.instance.getCombatTagPlus() != null &&
                            this.instance.getCombatTagPlus().getTagManager().isTagged(player.getUniqueId())) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoadLanguage
                                .getString("Commands.Warp.Combat.Message").replace("%warp", warpName)));
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                        return true;
                    }
                    if (configLoadLocation.getBoolean("Warps." + warpName + ".Instant") ||
                            player.hasPermission("jurassiccore.warp.bypass") ||
                            player.hasPermission("jurassiccore.warp.*") || player.hasPermission("jurassiccore.*")) {
                        player.teleport(location);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoadLanguage
                                .getString("Commands.Warp.Teleported.Message").replace("%warp", warpName)));
                        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                    } else {
                        long expiryTime = 8L;
                        long removedExpiryTime = (EXPUtil.getTotalExperience(player) / 1000);
                        if (expiryTime - removedExpiryTime < 1L) {
                            expiryTime = 1L;
                        } else {
                            expiryTime -= removedExpiryTime;
                            if (expiryTime > 4L)
                                player.playSound(player.getLocation(), Sound.PORTAL_TRIGGER, 1.0F, 1.0F);
                        }
                        playerData.getExpiry(ExpiryType.Warp).setTime(System.currentTimeMillis() + expiryTime * 1000L);
                        playerData.setTeleportLocation(player.getLocation().clone());
                        playerData.setPendingWarp(warpName);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                configLoadLanguage.getString("Commands.Warp.Teleporting.Message")
                                        .replace("%warp", warpName).replace("%seconds", String.valueOf(Math.round((float) expiryTime)))));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,
                                Math.round((float) expiryTime) * 20 + 50, Math.round((float) expiryTime)));
                    }
                    return true;
                }
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoadLanguage.getString("Commands.Warp.Exist.Message").replace("%warp", args[0])));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoadLanguage.getString("Commands.Warp.Invalid.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> matches = new ArrayList<>();
        if (!(sender instanceof Player))
            return matches;
        if (args.length != 1)
            return matches;
        FileConfiguration configLoad = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "locations.yml")).getFileConfiguration();
        if (configLoad.getString("Warps") == null)
            return matches;
        for (String warpName : configLoad.getConfigurationSection("Warps").getKeys(false)) {
            if (warpName.toLowerCase().startsWith(args[0].toLowerCase()))
                matches.add(warpName);
        }
        return matches;
    }
}
