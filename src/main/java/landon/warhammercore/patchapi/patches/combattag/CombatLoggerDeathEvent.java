package landon.warhammercore.patchapi.patches.combattag;

import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class CombatLoggerDeathEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;

    private LivingEntity combatLoggerEntity;

    private String playerName;

    private String playerUUIDString;

    private List<ItemStack> inventoryItems;

    private EntityDamageEvent rawEvent;

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public LivingEntity getCombatLoggerEntity() {
        return this.combatLoggerEntity;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public String getPlayerUUIDString() {
        return this.playerUUIDString;
    }

    public List<ItemStack> getInventoryItems() {
        return this.inventoryItems;
    }

    public EntityDamageEvent getRawEvent() {
        return this.rawEvent;
    }

    public CombatLoggerDeathEvent(LivingEntity combatLoggerEntity, String playerUUIDString, String playerName, EntityDamageEvent event, ItemStack[] itemsFromMetadata) {
        this.combatLoggerEntity = combatLoggerEntity;
        this.rawEvent = event;
        this.playerName = playerName;
        this.playerUUIDString = playerUUIDString;
        if (itemsFromMetadata != null)
            this.inventoryItems = Lists.newArrayList(itemsFromMetadata);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

