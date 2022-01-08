package net.minelink.ctplus;

import java.util.UUID;
import net.minelink.ctplus.compat.api.NpcPlayerHelper;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

public final class Tag {
  private long tagTime = System.currentTimeMillis();
  
  private long expireTime;
  
  private UUID victimId;
  
  private String victimName;
  
  private UUID attackerId;
  
  private String attackerName;
  
  Tag(NpcPlayerHelper helper, long expireTime, Player victim, Player attacker) {
    this.expireTime = expireTime;
    if (victim != null)
      if (helper.isNpc(victim)) {
        this.victimId = helper.getIdentity(victim).getId();
        this.victimName = helper.getIdentity(victim).getName();
      } else {
        this.victimId = victim.getUniqueId();
        this.victimName = victim.getName();
      }  
    if (attacker != null)
      if (helper.isNpc(attacker)) {
        this.attackerId = helper.getIdentity(attacker).getId();
        this.attackerName = helper.getIdentity(attacker).getName();
      } else {
        this.attackerId = attacker.getUniqueId();
        this.attackerName = attacker.getName();
      }  
  }
  
  public long getTagTime() {
    return this.tagTime;
  }
  
  public long getExpireTime() {
    return this.expireTime;
  }
  
  public void setExpireTime(long expireTime) {
    this.expireTime = expireTime;
  }
  
  public UUID getVictimId() {
    return this.victimId;
  }
  
  public String getVictimName() {
    return this.victimName;
  }
  
  public UUID getAttackerId() {
    return this.attackerId;
  }
  
  public String getAttackerName() {
    return this.attackerName;
  }
  
  public int getTagDuration() {
    long currentTime = System.currentTimeMillis();
    return (this.expireTime > currentTime) ? NumberConversions.ceil((this.expireTime - currentTime) / 1000.0D) : 0;
  }
  
  public boolean isExpired() {
    return (getTagDuration() < 1);
  }
}
