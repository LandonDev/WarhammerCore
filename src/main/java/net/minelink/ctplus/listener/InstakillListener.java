package net.minelink.ctplus.listener;

import com.google.common.base.Preconditions;
import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.event.CombatLogEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class InstakillListener implements Listener {
  private final CombatTagPlus plugin;
  
  public InstakillListener(CombatTagPlus plugin) {
    this.plugin = (CombatTagPlus)Preconditions.checkNotNull(plugin, "Null plugin");
  }
  
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onCombatLog(CombatLogEvent event) {
    if (!this.plugin.getSettings().instantlyKill())
      return; 
    if (event.getReason() == CombatLogEvent.Reason.TAGGED)
      event.getPlayer().setHealth(0.0D); 
  }
}
