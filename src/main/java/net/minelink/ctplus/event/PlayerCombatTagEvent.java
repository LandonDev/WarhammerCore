package net.minelink.ctplus.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class PlayerCombatTagEvent extends PlayerEvent implements Cancellable {
  private static final HandlerList handlers = new HandlerList();
  
  private boolean cancelled;
  
  private final Player victim;
  
  private final Player attacker;
  
  private int tagDuration;
  
  public PlayerCombatTagEvent(Player victim, Player attacker, int tagDuration) {
    super((victim != null) ? victim : attacker);
    this.victim = victim;
    this.attacker = attacker;
    this.tagDuration = tagDuration;
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
  
  public Player getVictim() {
    return this.victim;
  }
  
  public Player getAttacker() {
    return this.attacker;
  }
  
  public int getTagDuration() {
    return this.tagDuration;
  }
  
  public void setTagDuration(int tagDuration) {
    this.tagDuration = tagDuration;
  }
}
