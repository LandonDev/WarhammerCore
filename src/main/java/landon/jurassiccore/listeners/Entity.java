package landon.jurassiccore.listeners;

import com.massivecraft.factions.P;
import landon.jurassiccore.playerdata.Item;
import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Entity implements Listener {
    private final JurassicCore instance;

    public Entity(JurassicCore instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
        Player player = (Player) event.getEntity();
        if (!playerDataManager.hasPlayerData(player))
            return;
        if (playerDataManager.getPlayerData(player).hasGodMode())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        if (event.isCancelled())
            return;
        FileConfiguration configLoad = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration();
        if (!configLoad.getBoolean("Listeners.Entity.Damage.Blood.Enable"))
            return;
        Player player = (Player) event.getEntity();
        player.getLocation().getWorld().playEffect(player.getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND,
                configLoad.getInt("Listeners.Entity.Damage.Blood.BlockId"));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
        FileConfiguration configLoad = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration();
        for (String entityType : configLoad.getConfigurationSection("Listeners.Entity.Death.Entities").getKeys(false)) {
            if (event.getEntityType() != EntityType.valueOf(entityType))
                continue;
            if (configLoad.getBoolean("Listeners.Entity.Death.Entities." + entityType + ".Clear"))
                event.getDrops().clear();
            Iterator<String> iterator = configLoad.getConfigurationSection("Listeners.Entity.Death.Entities." + entityType + ".Items").getKeys(false).iterator();
            while (iterator.hasNext()) {
                ItemStack is;
                String itemName = iterator.next();
                if (configLoad.getString("Listeners.Entity.Death.Entities." + entityType + ".Items." + itemName +
                        ".Item.Data") == null) {
                    is = new ItemStack(Material.valueOf(configLoad.getString("Listeners.Entity.Death.Entities." +
                            entityType + ".Items." + itemName + ".Item.Material")));
                } else {
                    is = new ItemStack(
                            Material.valueOf(configLoad.getString("Listeners.Entity.Death.Entities." + entityType +
                                    ".Items." + itemName + ".Item.Material")),
                            1, (short) configLoad.getInt("Listeners.Entity.Death.Entities." + entityType + ".Items." +
                            itemName + ".Item.Data"));
                }
                if (configLoad.getBoolean(
                        "Listeners.Entity.Death.Entities." + entityType + ".Items." + itemName + ".Random.Enable")) {
                    is.setAmount((new Random()).nextInt(configLoad.getInt(
                            "Listeners.Entity.Death.Entities." + entityType + ".Items." + itemName + ".Random.Max") -
                            configLoad.getInt("Listeners.Entity.Death.Entities." + entityType + ".Items." + itemName +
                                    ".Random.Min") +
                            1) +
                            configLoad.getInt("Listeners.Entity.Death.Entities." + entityType + ".Items." + itemName +
                                    ".Random.Min"));
                } else {
                    is.setAmount(configLoad.getInt(
                            "Listeners.Entity.Death.Entities." + entityType + ".Items." + itemName + ".Amount"));
                }
                event.getDrops().add(is);
            }
            break;
        }
        if (!(event.getEntity() instanceof Player) || event.getEntity().getKiller() == null ||
                !(event.getEntity().getKiller() instanceof Player))
            return;
        if (!configLoad.getBoolean("ItemProtect.Enable"))
            return;
        List<ItemStack> entityDrops = event.getDrops();
        if (entityDrops == null)
            return;
        Player killed = (Player) event.getEntity();
        Player killer = event.getEntity().getKiller();
        if (!playerDataManager.hasPlayerData(killer))
            return;
        PlayerData playerData = playerDataManager.getPlayerData(killer);
        for (ItemStack is : entityDrops) {
            if (is == null || is.getType() == Material.AIR || is.getAmount() == 0)
                continue;
            org.bukkit.entity.Item item = killed.getWorld().dropItemNaturally(killed.getLocation(), is);
            item.setMetadata("ItemProtect", (MetadataValue) new FixedMetadataValue(P.p, killer.getName()));
            playerData.addItem(new Item(item,
                    System.currentTimeMillis() + (configLoad.getInt("ItemProtect.Time") * 1000)));
        }
        event.getDrops().clear();
    }
}
