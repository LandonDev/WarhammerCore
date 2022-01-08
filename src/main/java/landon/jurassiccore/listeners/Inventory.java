package landon.jurassiccore.listeners;

import com.massivecraft.factions.P;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.menus.Invsee;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class Inventory implements Listener {
    private final JurassicCore instance;

    public Inventory(JurassicCore instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onInventoryClickBoots(InventoryClickEvent event) {
        if (event.getAction() != InventoryAction.PLACE_ALL && event.getAction() != InventoryAction.SWAP_WITH_CURSOR)
            return;
        if (event.getCurrentItem() == null || event.getCursor() == null ||
                event.getCurrentItem().getType() == Material.AIR || event.getCursor().getType() == Material.AIR ||
                !event.getCurrentItem().getType().name().contains("BOOTS"))
            return;
        ItemStack clicked = event.getCurrentItem();
        ItemStack cursor = event.getCursor(), fakeCursor = event.getCursor().clone();
        FileConfiguration configLoad = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration();
        if (!configLoad.getBoolean("Listeners.Inventory.DepthStriderCrystal.Enable"))
            return;
        if (clicked.containsEnchantment(Enchantment.DEPTH_STRIDER))
            return;
        fakeCursor.setAmount(1);
        if (fakeCursor.getType() != Material.getMaterial(configLoad.getString("DepthStriderCrystal.Item.Material")) ||
                !fakeCursor.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("DepthStriderCrystal.Displayname"))))
            return;
        clicked.addEnchantment(Enchantment.DEPTH_STRIDER, 3);
        event.setCancelled(true);
        int amount = cursor.getAmount() - 1;
        if (amount > 0) {
            cursor.setAmount(amount);
        } else {
            event.getWhoClicked().setItemOnCursor(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClickBuckets(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getAction() != InventoryAction.PLACE_ALL && event.getAction() != InventoryAction.PLACE_ONE &&
                event.getAction() != InventoryAction.PLACE_SOME)
            return;
        if (event.getCurrentItem() == null || (event.getCurrentItem().getType() != Material.WATER_BUCKET &&
                event.getCurrentItem().getType() != Material.LAVA_BUCKET))
            return;
        ItemStack clicked = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        if (cursor.getType() != clicked.getType())
            return;
        if (clicked.getAmount() >= clicked.getMaxStackSize())
            return;
        if (!this.instance.getFileManager().getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Listeners.Inventory.BucketStack.Enable"))
            return;
        if (cursor.getAmount() > clicked.getMaxStackSize() - clicked.getAmount()) {
            clicked.setAmount(clicked.getMaxStackSize());
            cursor.setAmount(cursor.getAmount() - clicked.getMaxStackSize() - clicked.getAmount());
        } else {
            clicked.setAmount(clicked.getAmount() + cursor.getAmount());
            player.setItemOnCursor(null);
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClickInvsee(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if (event.isCancelled())
            return;
        if (event.getRawSlot() < event.getInventory().getSize())
            return;
        final FileConfiguration configLoad = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration();
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(P.p, new Runnable() {
            public void run() {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (!all.getOpenInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menus.Invsee.Title").replace("%player", player.getName()))))
                        continue;
                    Invsee.getInstance().open(all, player);
                }
            }
        }, 1L);
    }
}
