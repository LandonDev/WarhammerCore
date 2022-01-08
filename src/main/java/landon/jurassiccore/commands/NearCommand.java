package landon.jurassiccore.commands;

import com.massivecraft.factions.P;
import landon.jurassiccore.JurassicCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NearCommand implements CommandExecutor {
    private JurassicCore instance;

    public NearCommand(JurassicCore instance) {
        this.instance = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
            return true;
        }
        final FileConfiguration configLoad = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
        final Player player = (Player) sender;
        if (!player.hasPermission("jurassiccore.near") && !player.hasPermission("jurassiccore.near.*") &&
                !player.hasPermission("jurassiccore.*")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Commands.Near.Permission.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            return true;
        }
        Bukkit.getServer().getScheduler().runTaskAsynchronously(P.p, new Runnable() {
            public void run() {
                int nearRadius = NearCommand.this.instance.getPlayerDataManager().getMaxNearRadius(player);
                if (nearRadius == 0) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Commands.Near.Permission.Message")));
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                } else {
                    List<Entity> nearbyEntities = player.getNearbyEntities(nearRadius, nearRadius, nearRadius);
                    List<NearCommand.NearbyPlayer> nearbyPlayers = new ArrayList<>();
                    for (int i = 0; i < nearbyEntities.size(); i++) {
                        Entity nearbyEntity = nearbyEntities.get(i);
                        if (nearbyEntity instanceof Player) {
                            Player nearbyPlayer = (Player) nearbyEntity;
                            if (!nearbyPlayer.hasPotionEffect(PotionEffectType.INVISIBILITY) &&
                                    nearbyPlayer.getGameMode() != GameMode.SPECTATOR)
                                nearbyPlayers.add(new NearCommand.NearbyPlayer(nearbyEntity.getName(),
                                        (int) player.getLocation().distance(nearbyEntity.getLocation())));
                        }
                    }
                    if (nearbyPlayers.size() == 0) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Commands.Near.Players.None.Message")));
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                    } else {
                        nearbyPlayers.sort(new Comparator<NearCommand.NearbyPlayer>() {
                            public int compare(NearCommand.NearbyPlayer nearbyPlayer1, NearCommand.NearbyPlayer nearbyPlayer2) {
                                return Integer.valueOf(nearbyPlayer2.getDistance())
                                        .compareTo(Integer.valueOf(nearbyPlayer1.getDistance()));
                            }
                        });
                        for (String nearLine : configLoad.getStringList("Commands.Near.Players.Near.Lines")) {
                            if (nearLine.contains("%players_list")) {
                                for (NearCommand.NearbyPlayer nearbyPlayer : nearbyPlayers)
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Commands.Near.Players.Near.Format")
                                                    .replaceAll("%player", nearbyPlayer.getName())
                                                    .replace("%distance", String.valueOf(nearbyPlayer.getDistance()))));
                                continue;
                            }
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    nearLine.replace("%players", String.valueOf(nearbyPlayers.size()))));
                        }
                        player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
                    }
                }
            }
        });
        return true;
    }

    public class NearbyPlayer {
        private String name;

        private int distance;

        public NearbyPlayer(String name, int distance) {
            this.name = name;
            this.distance = distance;
        }

        public String getName() {
            return this.name;
        }

        public int getDistance() {
            return this.distance;
        }
    }
}
