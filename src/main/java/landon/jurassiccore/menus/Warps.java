package landon.jurassiccore.menus;

import com.massivecraft.factions.P;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import landon.jurassiccore.location.LocationManager;
import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.utils.inventory.Placeholder;
import landon.jurassiccore.utils.inventory.nInventoryUtil;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.file.FileManager;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Warps {
    private static Warps instance;

    public static Warps getInstance() {
        if (Warps.instance == null) {
            Warps.instance = new Warps();
        }
        return Warps.instance;
    }

    public void open(final Player player) {
        final JurassicCore instance = JurassicCore.getInstance();
        final PlayerDataManager playerDataManager = instance.getPlayerDataManager();
        final LocationManager locationManager = instance.getLocationManager();
        final FileManager fileManager = instance.getFileManager();
        final FileConfiguration configLoadLanguage = fileManager.getConfig(new File(instance.getDataFolder(), "language.yml")).getFileConfiguration();
        final FileConfiguration configLoadLocation = fileManager.getConfig(new File(instance.getDataFolder(), "locations.yml")).getFileConfiguration();
        final PlayerData playerData = playerDataManager.getPlayerData(player);
        final nInventoryUtil inv = new nInventoryUtil(player, new nInventoryUtil.ClickEventHandler() {
            @Override
            public void onClick(final nInventoryUtil.ClickEvent event) {
                final ItemStack is = event.getItem();
                if (is.getType() == Material.ARROW) {
                    final PlayerData playerData = playerDataManager.getPlayerData(player);
                    if (is.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', configLoadLanguage.getString("Menus.Warps.Items.Previous.Displayname")))) {
                        playerData.setPage(playerData.getPage() - 1);
                    } else if (is.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', configLoadLanguage.getString("Menus.Warps.Items.Next.Displayname")))) {
                        playerData.setPage(playerData.getPage() + 1);
                    }
                    Bukkit.getServer().getScheduler().runTaskAsynchronously(P.p, (Runnable) new Runnable() {
                        @Override
                        public void run() {
                            Warps.this.open(player);
                        }
                    });
                    player.playSound(player.getLocation(), Sound.ARROW_HIT, 1.0f, 1.0f);
                    event.setWillClose(true);
                    event.setWillDestroy(true);
                } else {
                    final List<String> availableWarps = new ArrayList<String>(configLoadLocation.getConfigurationSection("Warps").getKeys(false));
                    int selectedWarp = event.getSlot();
                    if (playerData.getPage() != 1) {
                        selectedWarp = (playerData.getPage() - 1) * 45 + event.getSlot();
                    }
                    if (availableWarps.get(selectedWarp) != null) {
                        Bukkit.getServer().dispatchCommand((CommandSender) player, "warp " + availableWarps.get(selectedWarp));
                    }
                }
            }
        });
        inv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoadLanguage.getString("Menus.Warps.Title")));
        final List<String> availableWarps = new ArrayList<String>(configLoadLocation.getConfigurationSection("Warps").getKeys(false));
        int index = playerData.getPage() * 45 - 45;
        final int endIndex = (index >= availableWarps.size()) ? (availableWarps.size() - 1) : (index + 45);
        int invSlot = 0;
        while (index < endIndex) {
            if (availableWarps.size() > index) {
                final String warpName = availableWarps.get(index);
                final Location warpLocation = locationManager.getLocation(configLoadLocation, "Warps." + warpName + ".Location");
                final List<String> itemLore = new ArrayList<String>();
                for (final String itemLoreLine : configLoadLanguage.getStringList("Menus.Warps.Items.Warp.Lore")) {
                    if (itemLoreLine.contains("%description")) {
                        for (final String descriptionLine : configLoadLocation.getStringList("Warps." + warpName + ".Description")) {
                            itemLore.add(ChatColor.translateAlternateColorCodes('&', descriptionLine));
                        }
                    } else {
                        itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreLine.replace("%nearby_players", new StringBuilder().append(this.getPlayers(instance, warpName, false)).toString()).replace("%pvp_players", new StringBuilder().append(this.getPlayers(instance, warpName, true)).toString()).replace("%x", new StringBuilder().append(warpLocation.getBlockX()).toString()).replace("%z", new StringBuilder().append(warpLocation.getBlockZ()).toString())));
                    }
                }
                if (configLoadLocation.getString("Warps." + warpName + ".Item.Data") == null) {
                    inv.addItem(inv.createItem(new ItemStack(Material.valueOf(configLoadLocation.getString("Warps." + warpName + ".Item.Material"))), configLoadLanguage.getString("Menus.Warps.Items.Warp.Displayname").replace("%warp", warpName), itemLore, new Placeholder[]{new Placeholder("%warp", warpName)}, null, null), invSlot);
                } else {
                    inv.addItem(inv.createItem(new ItemStack(Material.valueOf(configLoadLocation.getString("Warps." + warpName + ".Item.Material")), 1, (short) configLoadLocation.getInt("Warps." + warpName + ".Item.Data")), configLoadLanguage.getString("Menus.Warps.Items.Warp.Displayname").replace("%warp", warpName), itemLore, new Placeholder[]{new Placeholder("%warp", warpName)}, null, null), invSlot);
                }
                ++invSlot;
            }
            ++index;
        }
        if (availableWarps.size() > 45) {
            final int playerMenuPage = playerData.getPage();
            final int nextEndIndex = availableWarps.size() - playerMenuPage * 45;
            if (playerMenuPage != 1) {
                inv.addItem(inv.createItem(new ItemStack(Material.ARROW), configLoadLanguage.getString("Menus.Warps.Items.Previous.Displayname"), null, null, null, null), 45);
            }
            if (nextEndIndex != 0 && nextEndIndex >= 0) {
                inv.addItem(inv.createItem(new ItemStack(Material.ARROW), configLoadLanguage.getString("Menus.Warps.Items.Next.Displayname"), null, null, null, null), 53);
            }
            inv.setRows(6);
        } else {
            inv.setRows((int) Math.round(availableWarps.size() / 9 + 0.5));
        }
        Bukkit.getServer().getScheduler().runTask(P.p, (Runnable) new Runnable() {
            @Override
            public void run() {
                inv.open();
            }
        });
    }

    private int getPlayers(final JurassicCore instance, final String warpName, final boolean pvp) {
        final WorldGuardPlugin worldGuard = instance.getWorldGuard();
        final LocationManager locationManager = instance.getLocationManager();
        final FileConfiguration configLoad = instance.getFileManager().getConfig(new File(instance.getDataFolder(), "locations.yml")).getFileConfiguration();
        final List<String> regionNames = locationManager.getRegionNames(worldGuard, locationManager.getLocation(configLoad, "Warps." + warpName + ".Location"), pvp);
        int players = 0;
        for (final Player all : Bukkit.getOnlinePlayers()) {
            for (final String regionName : locationManager.getRegionNames(worldGuard, all.getLocation(), false)) {
                if (regionNames.contains(regionName)) {
                    ++players;
                }
            }
        }
        return players;
    }
}

