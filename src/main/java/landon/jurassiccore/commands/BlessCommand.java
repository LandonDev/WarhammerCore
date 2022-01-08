package landon.jurassiccore.commands;

import landon.jurassiccore.JurassicCore;
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

public class BlessCommand implements CommandExecutor {
    private JurassicCore instance;

    public BlessCommand(JurassicCore instance) {
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
        Player player = (Player) sender;
        if (!player.hasPermission("jurassiccore.bless") && !player.hasPermission("jurassiccore.*")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Commands.Bless.Permission.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            return true;
        }
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (potionEffect.getType().equals(PotionEffectType.BLINDNESS) ||
                    potionEffect.getType().equals(PotionEffectType.CONFUSION) ||
                    potionEffect.getType().equals(PotionEffectType.HARM) ||
                    potionEffect.getType().equals(PotionEffectType.HUNGER) ||
                    potionEffect.getType().equals(PotionEffectType.POISON) ||
                    potionEffect.getType().equals(PotionEffectType.SLOW) ||
                    potionEffect.getType().equals(PotionEffectType.SLOW_DIGGING) ||
                    potionEffect.getType().equals(PotionEffectType.WEAKNESS) ||
                    potionEffect.getType().equals(PotionEffectType.WITHER))
                player.removePotionEffect(potionEffect.getType());
        }
        player.sendMessage(
                ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Bless.Blessed.Message")));
        player.playSound(player.getLocation(), Sound.FIZZ, 1.0F, 5.0F);
        return true;
    }
}
