package net.minelink.ctplus.event;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class CombatLogEvent extends PlayerEvent implements Cancellable {
  private static final HandlerList handlers = new HandlerList();
  
  private final Reason reason;
  
  private boolean cancelled;
  
  public CombatLogEvent(Player player, Reason reason) {
    super((Player)Preconditions.checkNotNull(player, "Null player"));
    this.reason = (Reason)Preconditions.checkNotNull(reason, "Null reason");
  }
  
  public Reason getReason() {
    return this.reason;
  }
  
  public static HandlerList getHandlerList() {
    return handlers;
  }
  
  public HandlerList getHandlers() {
    return handlers;
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
  
  public enum Reason {
    TAGGED, UNSAFE_LOGOUT;
  }
}
