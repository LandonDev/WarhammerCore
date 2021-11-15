package landon.warhammercore.patchapi.patches.combattag;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CombatTagEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;

    private Player combatPlayer;

    private boolean alreadyInCombat;

    public CombatTagEvent(Player p, boolean alreadyInCombat) {
        this.combatPlayer = p;
        this.alreadyInCombat = alreadyInCombat;
    }

    public Player getPlayer() {
        return this.combatPlayer;
    }

    public boolean wasAlreadyInCombat() {
        return this.alreadyInCombat;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
