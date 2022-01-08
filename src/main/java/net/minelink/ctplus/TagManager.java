package net.minelink.ctplus;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minelink.ctplus.compat.api.NpcPlayerHelper;
import net.minelink.ctplus.event.PlayerCombatTagEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public final class TagManager {
  private final CombatTagPlus plugin;
  
  public enum Flag {
    TAG_VICTIM, TAG_ATTACKER;
  }
  
  private final Map<UUID, Tag> tags = new HashMap<>();
  
  TagManager(CombatTagPlus plugin) {
    this.plugin = plugin;
  }
  
  void purgeExpired() {
    Iterator<Tag> iterator = this.tags.values().iterator();
    while (iterator.hasNext()) {
      Tag tag = iterator.next();
      if (tag.isExpired())
        iterator.remove(); 
    } 
  }
  
  public void tag(Player victim, Player attacker) {
    tag(victim, attacker, EnumSet.of(Flag.TAG_VICTIM, Flag.TAG_ATTACKER));
  }
  
  public void tag(Player victim, Player attacker, Set<Flag> flags) {
    NpcPlayerHelper helper = this.plugin.getNpcPlayerHelper();
    UUID victimId = null;
    if (victim != null)
      if (victim.getHealth() <= 0.0D || victim.isDead()) {
        victim = null;
      } else if (helper.isNpc(victim)) {
        victimId = helper.getIdentity(victim).getId();
      } else if (!victim.hasPermission("ctplus.bypass.tag")) {
        victimId = victim.getUniqueId();
      } else {
        victim = null;
      }  
    UUID attackerId = null;
    if (attacker != null)
      if (attacker.getHealth() <= 0.0D || attacker.isDead() || attacker == victim) {
        attacker = null;
      } else if (helper.isNpc(attacker)) {
        attackerId = helper.getIdentity(attacker).getId();
      } else if (!attacker.hasPermission("ctplus.bypass.tag")) {
        attackerId = attacker.getUniqueId();
      } else {
        attacker = null;
      }  
    if (victim == null && attacker == null)
      return; 
    int tagDuration = this.plugin.getSettings().getTagDuration();
    PlayerCombatTagEvent event = new PlayerCombatTagEvent(victim, attacker, tagDuration);
    Bukkit.getPluginManager().callEvent((Event)event);
    if (event.isCancelled())
      return; 
    long expireTime = System.currentTimeMillis() + (event.getTagDuration() * 1000);
    Tag tag = new Tag(helper, expireTime, victim, attacker);
    if (victim != null && flags.contains(Flag.TAG_VICTIM))
      this.tags.put(victimId, tag); 
    if (attacker != null && flags.contains(Flag.TAG_ATTACKER))
      this.tags.put(attackerId, tag); 
  }
  
  public boolean untag(UUID playerId) {
    Tag tag = this.tags.remove(playerId);
    return (tag != null && !tag.isExpired());
  }
  
  public Tag getTag(UUID playerId) {
    return getTag(playerId, false);
  }
  
  public Tag getTag(UUID playerId, boolean includeHidden) {
    Tag tag = this.tags.get(playerId);
    if (tag == null || tag.isExpired() || (!includeHidden && this.plugin
      .getSettings().onlyTagAttacker() && tag
      .getVictimId().equals(playerId)))
      return null; 
    return tag;
  }
  
  public boolean isTagged(UUID playerId) {
    Tag tag = this.tags.get(playerId);
    boolean tagged = (tag != null && !tag.isExpired());
    if (tagged && this.plugin.getSettings().onlyTagAttacker())
      return !tag.getVictimId().equals(playerId); 
    return tagged;
  }
}
