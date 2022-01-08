package landon.jurassiccore.listeners;

import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.utils.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class Bucket implements Listener {
    private JurassicCore instance;

    public Bucket(JurassicCore instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        if (player.getItemInHand().getAmount() <= 1)
            return;
        ItemStack hand = player.getItemInHand().clone();
        hand.setAmount(hand.getAmount() - 1);
        event.setItemStack(hand);
        if (!this.instance.getFileManager().getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Listeners.Bucket.BucketStack.Bucket"))
            return;
        ItemStack bucket = new ItemStack(Material.BUCKET);
        if (InventoryUtil.isInventoryFull((Inventory) player.getInventory(), 0, 1, bucket)) {
            player.getWorld().dropItemNaturally(player.getLocation(), bucket);
        } else {
            player.getInventory().addItem(new ItemStack[]{bucket});
        }
    }
}
