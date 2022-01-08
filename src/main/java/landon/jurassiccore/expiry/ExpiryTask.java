package landon.jurassiccore.expiry;

import com.massivecraft.factions.*;
import com.massivecraft.factions.iface.RelationParticipator;
import landon.jurassiccore.file.FileManager;
import landon.jurassiccore.location.LocationManager;
import landon.jurassiccore.playerdata.Home;
import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import net.minelink.ctplus.TagManager;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class ExpiryTask extends BukkitRunnable {
    private JurassicCore instance;

    public ExpiryTask(JurassicCore instance) {
        this.instance = instance;
    }

    public void run() {
        PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
        final LocationManager locationManager = this.instance.getLocationManager();
        FileManager fileManager = this.instance.getFileManager();
        final FileConfiguration configLoadLanguage = fileManager.getConfig(new File(this.instance.getDataFolder(), "language.yml"))
                .getFileConfiguration();
        final FileConfiguration configLoadLocation = fileManager
                .getConfig(new File(this.instance.getDataFolder(), "locations.yml")).getFileConfiguration();
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (!playerDataManager.hasPlayerData(all))
                continue;
            final PlayerData playerData = playerDataManager.getPlayerData(all);
            byte b;
            int i;
            ExpiryType[] arrayOfExpiryType;
            for (i = (arrayOfExpiryType = ExpiryType.values()).length, b = 0; b < i; ) {
                final ExpiryType expiryType = arrayOfExpiryType[b];
                final Expiry expiry = playerData.getExpiry(expiryType);
                if (expiry.getTime() != 0L)
                    Bukkit.getServer().getScheduler().runTask(P.p, new Runnable() {
                        public void run() {
                            long time = System.currentTimeMillis() - expiry.getTime();
                            if (expiry.getTime() == 0L)
                                return;
                            if (expiryType == ExpiryType.Death) {
                                if (time >= 0L) {
                                    playerData.setLastLocation(null);
                                    expiry.setTime(0L);
                                }
                            } else {
                                all.spigot().playEffect(all.getLocation().clone().add(0.0D, 0.9D, 0.0D),
                                        Effect.WITCH_MAGIC, 0, 0, 0.0F, 0.0F, 0.0F, 1.0F, 10, 0);
                                if (time >= 0L) {
                                    Location location = null;
                                    if (expiryType == ExpiryType.Spawn) {
                                        location = locationManager.getLocation(configLoadLocation, "Spawn");
                                    } else if (expiryType == ExpiryType.Back) {
                                        location = playerData.getLastLocation();
                                    } else if (expiryType == ExpiryType.Warp) {
                                        if (playerData.getPendingWarp() != null && configLoadLocation
                                                .getString("Warps." + playerData.getPendingWarp()) != null)
                                            location = locationManager.getLocation(configLoadLocation,
                                                    "Warps." + playerData.getPendingWarp() + ".Location");
                                    } else if (expiryType == ExpiryType.Home) {
                                        if (playerData.getPendingHome() != null &&
                                                playerData.hasHome(playerData.getPendingHome())) {
                                            Home home = playerData.getHome(playerData.getPendingHome());
                                            if (home != null) {
                                                location = home.getLocation();
                                                if (locationManager
                                                        .getRegionNames(ExpiryTask.this.instance.getWorldGuard(), location, false)
                                                        .size() != 0) {
                                                    all.sendMessage(

                                                            ChatColor.translateAlternateColorCodes('&',
                                                                    configLoadLanguage.getString(
                                                                            "Commands.Home.Region.Message"))
                                                                    .replace("%home", playerData.getPendingHome()));
                                                    all.playSound(all.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                                                    playerData.removeHome(home);
                                                    expiry.setTime(0L);
                                                    return;
                                                }
                                                Faction faction = Board.getInstance()
                                                        .getFactionAt(new FLocation(home.getLocation()));
                                                if (!faction.isWilderness()) {
                                                    FPlayer fPlayer = FPlayers.i.get(all);
                                                    if (fPlayer.getFaction() != faction &&
                                                            !faction.getRelationTo((RelationParticipator) fPlayer).isAlly()) {
                                                        all.sendMessage(
                                                                ChatColor.translateAlternateColorCodes('&',
                                                                        configLoadLanguage.getString(
                                                                                "Commands.Home.Faction.Message"))
                                                                        .replace("%home", playerData.getPendingHome()));
                                                        all.playSound(all.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                                                        playerData.removeHome(home);
                                                        expiry.setTime(0L);
                                                        return;
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        if (expiryType == ExpiryType.Teleport) {
                                            Player targetPlayer = Bukkit.getServer().getPlayer(playerData.getTeleport());
                                            if (targetPlayer != null) {
                                                if (ExpiryTask.this.instance.getCombatTagPlus() != null) {
                                                    TagManager tagManager = ExpiryTask.this.instance.getCombatTagPlus().getTagManager();
                                                    if (tagManager.isTagged(all.getUniqueId())) {
                                                        all.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                                configLoadLanguage.getString(
                                                                        "Tasks.Expiry.Teleport.Combat.Player.Message")));
                                                        all.playSound(all.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                                                        return;
                                                    }
                                                    if (tagManager.isTagged(targetPlayer.getUniqueId())) {
                                                        all.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                                configLoadLanguage.getString(
                                                                        "Tasks.Expiry.Teleport.Combat.Target.Message")));
                                                        all.playSound(all.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                                                        return;
                                                    }
                                                }
                                                FPlayers fPlayers = FPlayers.i;
                                                if (!Board.getInstance().getFactionAt(new FLocation(all.getLocation())).isWilderness()) {
                                                    FPlayer fPlayer = fPlayers.get(all);
                                                    if (!fPlayer.isInOwnTerritory() && !fPlayer.isInAllyTerritory() &&
                                                            !fPlayer.isInNeutralTerritory()) {
                                                        all.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                                configLoadLanguage.getString(
                                                                        "Tasks.Expiry.Teleport.Faction.Player.Message")));
                                                        all.playSound(all.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                                                        return;
                                                    }
                                                }
                                                if (!Board.getInstance().getFactionAt(new FLocation(targetPlayer.getLocation())).isWilderness()) {
                                                    FPlayer fPlayer = fPlayers.get(targetPlayer);
                                                    if (!fPlayer.isInOwnTerritory() && !fPlayer.isInAllyTerritory() &&
                                                            !fPlayer.isInNeutralTerritory()) {
                                                        all.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                                configLoadLanguage.getString(
                                                                        "Tasks.Expiry.Teleport.Faction.Target.Message")));
                                                        all.playSound(all.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                                                        return;
                                                    }
                                                }
                                                all.teleport(targetPlayer.getLocation());
                                                all.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                        configLoadLanguage
                                                                .getString(
                                                                        "Tasks.Expiry.Teleport.Teleported.Player.Message")
                                                                .replace("%player", targetPlayer.getName())));
                                                targetPlayer
                                                        .sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                                configLoadLanguage.getString(
                                                                        "Tasks.Expiry.Teleport.Teleported.Target.Message")
                                                                        .replace("%player", all.getName())));
                                            }
                                            all.removePotionEffect(PotionEffectType.CONFUSION);
                                            playerData.setTeleport(null);
                                            expiry.setTime(0L);
                                            return;
                                        }
                                        if (expiryType == ExpiryType.Wilderness)
                                            location = locationManager.getWildernessLocation();
                                    }
                                    all.removePotionEffect(PotionEffectType.CONFUSION);
                                    if (location == null || location.getWorld() == null) {
                                        all.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoadLanguage
                                                .getString("Commands." + expiryType.name() + ".Location.Message")));
                                        all.playSound(all.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
                                        expiry.setTime(0L);
                                        return;
                                    }
                                    all.teleport(location);
                                    String pendingHome = playerData.getPendingHome();
                                    if (pendingHome == null)
                                        pendingHome = "";
                                    String pendingWarp = playerData.getPendingWarp();
                                    if (pendingWarp == null)
                                        pendingWarp = "";
                                    all.sendMessage(

                                            ChatColor.translateAlternateColorCodes('&',
                                                    configLoadLanguage.getString("Commands." + expiryType.name() +
                                                            ".Teleported.Message"))
                                                    .replace("%home", pendingHome).replace("%warp", pendingWarp));
                                    all.playSound(all.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                                    expiry.setTime(0L);
                                }
                            }
                        }
                    });
                b++;
            }
        }
    }
}
