package landon.jurassiccore.commands;

import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.expiry.Expiry;
import landon.jurassiccore.expiry.ExpiryType;
import landon.jurassiccore.utils.EXPUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;

public class WildernessCommand implements CommandExecutor {
    private JurassicCore instance;

    public WildernessCommand(JurassicCore instance) {
        this.instance = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
            return true;
        }
        FileConfiguration configLoadLanguage = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
        Player player = (Player) sender;
        if (!player.hasPermission("jurassiccore.wilderness") && !player.hasPermission("jurassiccore.wilderness.*") &&
                !player.hasPermission("jurassiccore.*")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoadLanguage.getString("Commands.Wilderness.Permission.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            return true;
        }
        PlayerData playerData = this.instance.getPlayerDataManager().getPlayerData(player);
        if (playerData.hasPendingExpiry()) {
            Expiry expiry = playerData.getPendingExpiry();
            if (expiry.getType() == ExpiryType.Wilderness) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configLoadLanguage.getString("Commands.Wilderness.Teleporting.Message").replace("%seconds",
                                String.valueOf(Math.round((expiry.getTime() - System.currentTimeMillis()) * 0.001D)))));
                player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configLoadLanguage.getString("Commands.Wilderness.Pending.Message")));
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
            }
            return true;
        }
        if (this.instance.getCombatTagPlus() != null &&
                this.instance.getCombatTagPlus().getTagManager().isTagged(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoadLanguage.getString("Commands.Wilderness.Combat.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            return true;
        }
        if (player.hasPermission("jurassiccore.wilderness.bypass") || player.hasPermission("jurassiccore.wilderness.*") ||
                player.hasPermission("jurassiccore.*")) {
            player.teleport(this.instance.getLocationManager().getWildernessLocation());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoadLanguage.getString("Commands.Wilderness.Teleported.Message")));
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
            playerData.getExpiry(ExpiryType.Wilderness).setTime(System.currentTimeMillis() + expiryTime * 1000L);
            playerData.setTeleportLocation(player.getLocation().clone());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoadLanguage.getString("Commands.Wilderness.Teleporting.Message").replace("%seconds",
                            String.valueOf(Math.round((float) expiryTime)))));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Math.round((float) expiryTime) * 20 + 50,
                    Math.round((float) expiryTime)));
        }
        return true;
    }
}
