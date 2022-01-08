package net.minelink.ctplus.task;

import com.massivecraft.factions.P;
import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.Npc;
import org.bukkit.plugin.Plugin;

public class NpcDespawnTask implements Runnable {
  private final CombatTagPlus plugin;
  
  private final Npc npc;
  
  private long time;
  
  private int taskId;
  
  public NpcDespawnTask(CombatTagPlus plugin, Npc npc, long time) {
    this.plugin = plugin;
    this.npc = npc;
    this.time = time;
  }
  
  public long getTime() {
    return this.time;
  }
  
  public void setTime(long time) {
    this.time = time;
  }
  
  public Npc getNpc() {
    return this.npc;
  }
  
  public void start() {
    this.taskId = P.p.getServer().getScheduler().runTaskTimer(P.p, this, 1L, 1L).getTaskId();
  }
  
  public void stop() {
    P.p.getServer().getScheduler().cancelTask(this.taskId);
  }
  
  public void run() {
    if (this.time > System.currentTimeMillis())
      return; 
    this.plugin.getNpcManager().despawn(this.npc);
  }
}
