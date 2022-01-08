package net.minelink.ctplus.compat.api;

import org.bukkit.entity.Player;

public interface NpcPlayerHelper {
  Player spawn(Player paramPlayer);
  
  void despawn(Player paramPlayer);
  
  boolean isNpc(Player paramPlayer);
  
  NpcIdentity getIdentity(Player paramPlayer);
  
  void updateEquipment(Player paramPlayer);
  
  void syncOffline(Player paramPlayer);
  
  void createPlayerList(Player paramPlayer);
  
  void removePlayerList(Player paramPlayer);
}
