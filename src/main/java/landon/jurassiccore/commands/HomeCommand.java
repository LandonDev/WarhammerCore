package landon.jurassiccore.commands;

import com.massivecraft.factions.*;
import com.massivecraft.factions.iface.RelationParticipator;
import landon.jurassiccore.playerdata.Home;
import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.expiry.Expiry;
import landon.jurassiccore.expiry.ExpiryType;
import landon.jurassiccore.utils.EXPUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class HomeCommand implements CommandExecutor, TabCompleter {
    private JurassicCore instance;

    public HomeCommand(JurassicCore instance) {
        this.instance = instance;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    String.valueOf(this.instance.getDescription().getName()) + " | Error: You must be a player to perform that command.");
            return true;
        }
        final FileConfiguration configLoad = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
        final Player player = (Player) sender;
        if (!player.hasPermission("jurassiccore.home") && !player.hasPermission("jurassiccore.home.*") &&
                !player.hasPermission("jurassiccore.*")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Commands.Home.Permission.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            return true;
        }
        if (args.length == 0) {
            Bukkit.getServer().dispatchCommand((CommandSender) player, "homes");
        } else if (args.length == 1) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(P.p, new Runnable() {
                public void run() {
                    final PlayerData playerData = HomeCommand.this.instance.getPlayerDataManager().getPlayerData(player);
                    if (!playerData.hasHome(args[0])) {
                        player.sendMessage(
                                ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Home.Exist.Message"))
                                        .replace("%home", args[0]));
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                        return;
                    }
                    final Home home = playerData.getHome(args[0]);
                    if (home.getLocation() == null || home.getLocation().getWorld() == null) {
                        player.sendMessage(
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Commands.Home.Location.Message"))
                                        .replace("%home", home.getName()));
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                        return;
                    }
                    if (playerData.hasPendingExpiry()) {
                        Expiry expiry = playerData.getPendingExpiry();
                        if (expiry.getType() == ExpiryType.Home) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    configLoad.getString("Commands.Home.Teleporting.Message").replace("%seconds",
                                            String.valueOf(Math.round((expiry.getTime() - System.currentTimeMillis()) * 0.001D)))
                                            .replace("%home", playerData.getPendingHome())));
                            player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.0F, 1.0F);
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    configLoad.getString("Commands.Home.Pending.Message")));
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
                        }
                        return;
                    }
                    if (HomeCommand.this.instance.getCombatTagPlus() != null &&
                            HomeCommand.this.instance.getCombatTagPlus().getTagManager().isTagged(player.getUniqueId())) {
                        player.sendMessage(
                                ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Home.Combat.Message"))
                                        .replace("%home", home.getName()));
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                        return;
                    }
                    if (HomeCommand.this.instance.getLocationManager()
                            .getRegionNames(HomeCommand.this.instance.getWorldGuard(), home.getLocation(), false).size() != 0) {
                        player.sendMessage(
                                ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Home.Region.Message"))
                                        .replace("%home", home.getName()));
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                        playerData.removeHome(home);
                        return;
                    }
                    Faction faction = Board.getInstance().getFactionAt(new FLocation(home.getLocation()));
                    if (!faction.isWilderness()) {
                        FPlayer fPlayer = FPlayers.i.get(player);
                        if (fPlayer.getFaction() != faction && !faction.getRelationTo((RelationParticipator) fPlayer).isAlly()) {
                            player.sendMessage(
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Commands.Home.Faction.Message"))
                                            .replace("%home", home.getName()));
                            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                            playerData.removeHome(home);
                            return;
                        }
                    }
                    Bukkit.getServer().getScheduler().runTask(P.p, new Runnable() {
                        public void run() {
                            if (player.hasPermission("jurassiccore.home.bypass") ||
                                    player.hasPermission("jurassiccore.home.*") ||
                                    player.hasPermission("jurassiccore.*")) {
                                player.teleport(home.getLocation());
                                player.sendMessage(
                                        ChatColor.translateAlternateColorCodes('&',
                                                configLoad.getString("Commands.Home.Teleported.Message"))
                                                .replace("%home", home.getName()));
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
                                playerData.getExpiry(ExpiryType.Home)
                                        .setTime(System.currentTimeMillis() + expiryTime * 1000L);
                                playerData.setTeleportLocation(player.getLocation().clone());
                                playerData.setPendingHome(home.getName());
                                player.sendMessage(
                                        ChatColor.translateAlternateColorCodes('&',
                                                configLoad.getString("Commands.Home.Teleporting.Message")
                                                        .replace("%seconds", String.valueOf(Math.round((float) expiryTime))))
                                                .replace("%home", home.getName()));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,
                                        Math.round((float) expiryTime) * 20 + 50, Math.round((float) expiryTime)));
                            }
                        }
                    });
                }
            });
        } else {
            player.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', configLoad.getString("Commands.Home.Invalid.Message")));
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
        PlayerData playerData = this.instance.getPlayerDataManager().getPlayerData((Player) sender);
        for (Home home : playerData.getHomes()) {
            if (home.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                matches.add(home.getName());
        }
        return matches;
    }
}
