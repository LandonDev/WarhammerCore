package net.minelink.ctplus;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public final class PlayerCache {
  private final Map<UUID, Player> uuidCache = new HashMap<>();
  
  private final Map<String, Player> nameCache = new HashMap<>();
  
  public void addPlayer(Player player) {
    this.uuidCache.put(player.getUniqueId(), player);
    this.nameCache.put(player.getName().toLowerCase(), player);
  }
  
  public void removePlayer(Player player) {
    this.uuidCache.remove(player.getUniqueId());
    this.nameCache.remove(player.getName().toLowerCase());
  }
  
  public boolean isOnline(UUID id) {
    return this.uuidCache.containsKey(id);
  }
  
  public boolean isOnline(String name) {
    return this.nameCache.containsKey(name.toLowerCase());
  }
  
  public Player getPlayer(UUID id) {
    return this.uuidCache.get(id);
  }
  
  public Player getPlayer(String name) {
    return this.nameCache.get(name.toLowerCase());
  }
  
  public Collection<Player> getPlayers() {
    return Collections.unmodifiableCollection(this.uuidCache.values());
  }
}
