package landon.jurassiccore.menus;

import landon.jurassiccore.playerdata.Home;
import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.utils.inventory.Placeholder;
import landon.jurassiccore.utils.inventory.nInventoryUtil;
import landon.jurassiccore.JurassicCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class Homes {
    private static Homes instance;

    public static Homes getInstance() {
        if (instance == null)
            instance = new Homes();
        return instance;
    }

    public void open(final Player player) {
        JurassicCore instance = JurassicCore.getInstance();
        final PlayerDataManager playerDataManager = instance.getPlayerDataManager();
        final FileConfiguration configLoad = instance.getFileManager()
                .getConfig(new File(instance.getDataFolder(), "language.yml")).getFileConfiguration();
        PlayerData playerData = playerDataManager.getPlayerData(player);
        nInventoryUtil inv = new nInventoryUtil(player, new nInventoryUtil.ClickEventHandler() {
            public void onClick(nInventoryUtil.ClickEvent event) {
                PlayerData playerData = playerDataManager.getPlayerData(player);
                ItemStack is = event.getItem();
                if (is.getType() == Material.ARROW) {
                    if (is.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menus.Homes.Items.Previous.Displayname")))) {
                        playerData.setPage(playerData.getPage() - 1);
                    } else if (is.getItemMeta().getDisplayName()
                            .equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
                                    configLoad.getString("Menus.Homes.Items.Next.Displayname")))) {
                        playerData.setPage(playerData.getPage() + 1);
                    }
                    Homes.this.open(player);
                    player.playSound(player.getLocation(), Sound.ARROW_HIT, 1.0F, 1.0F);
                    event.setWillClose(false);
                } else {
                    int selectedHome = event.getSlot();
                    if (playerData.getPage() != 1)
                        selectedHome = (playerData.getPage() - 1) * 45 + event.getSlot();
                    if (playerData.getHomes().size() <= selectedHome)
                        return;
                    Home home = playerData.getHomes().get(selectedHome);
                    if (home != null)
                        if (event.getClick() == ClickType.LEFT) {
                            Bukkit.getServer().dispatchCommand((CommandSender) player, "home " + home.getName());
                        } else if (event.getClick() == ClickType.MIDDLE) {
                            Bukkit.getServer().dispatchCommand((CommandSender) player, "delhome " + home.getName());
                        } else if (event.getClick() == ClickType.RIGHT) {
                            Icons.getInstance().open(player, home);
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                            event.setWillClose(false);
                        }
                }
            }
        });
        int availableHomes = playerData.getHomes().size();
        inv.setTitle(ChatColor.translateAlternateColorCodes('&',
                configLoad.getString("Menus.Homes.Title").replace("%current_homes", String.valueOf(availableHomes))
                        .replace("%max_homes", String.valueOf(playerDataManager.getMaxHomes(player)))));
        int index = playerData.getPage() * 45 - 45;
        int endIndex = (index >= availableHomes) ? (availableHomes - 1) : (index + 45), invSlot = 0;
        for (; index < endIndex; index++) {
            if (availableHomes > index) {
                Home home = playerData.getHomes().get(index);
                inv.addItem(inv.createItem(home.getIcon(), configLoad.getString("Menus.Homes.Items.Home.Displayname").replace("%home", home.getName()), configLoad.getStringList("Menus.Homes.Items.Home.Lore"), new Placeholder[] { new Placeholder("%x", new StringBuilder().append(home.getLocation().getBlockX()).toString()), new Placeholder("%y", new StringBuilder().append(home.getLocation().getBlockY()).toString()), new Placeholder("%z", new StringBuilder().append(home.getLocation().getBlockZ()).toString()), new Placeholder("%world", new StringBuilder().append(home.getLocation().getWorld().getName()).toString()), new Placeholder("%home", home.getName()) }, null, null), invSlot);
                invSlot++;
            }
        }
        if (availableHomes > 45) {
            int playerMenuPage = playerData.getPage(), nextEndIndex = availableHomes - playerMenuPage * 45;
            if (playerMenuPage != 1)
                inv.addItem(
                        inv.createItem(new ItemStack(Material.ARROW),
                                configLoad.getString("Menus.Homes.Items.Previous.Displayname"), null, null, null, null), new int[]{45});
            if (nextEndIndex != 0 && nextEndIndex >= 0)
                inv.addItem(
                        inv.createItem(new ItemStack(Material.ARROW),
                                configLoad.getString("Menus.Homes.Items.Next.Displayname"), null, null, null, null), new int[]{53});
            inv.setRows(6);
        } else {
            inv.setRows((int) Math.round((availableHomes / 9) + 0.5D));
        }
        inv.open();
    }
}
