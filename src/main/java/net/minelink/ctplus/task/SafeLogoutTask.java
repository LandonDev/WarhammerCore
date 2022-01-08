package net.minelink.ctplus.task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.massivecraft.factions.P;
import net.minelink.ctplus.CombatTagPlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.NumberConversions;

public final class SafeLogoutTask extends BukkitRunnable {
  private static final Map<UUID, SafeLogoutTask> tasks = new HashMap<>();
  
  private final CombatTagPlus plugin;
  
  private final UUID playerId;
  
  private final Location loc;
  
  private final long logoutTime;
  
  private int remainingSeconds = Integer.MAX_VALUE;
  
  private boolean finished;
  
  SafeLogoutTask(CombatTagPlus plugin, Player player, long logoutTime) {
    this.plugin = plugin;
    this.playerId = player.getUniqueId();
    this.loc = player.getLocation();
    this.logoutTime = logoutTime;
  }
  
  private int getRemainingSeconds() {
    long currentTime = System.currentTimeMillis();
    return (this.logoutTime > currentTime) ? NumberConversions.ceil((this.logoutTime - currentTime) / 1000.0D) : 0;
  }
  
  public void run() {
    Player player = this.plugin.getPlayerCache().getPlayer(this.playerId);
    if (player == null) {
      cancel();
      return;
    } 
    if (hasMoved(player)) {
      if (!this.plugin.getSettings().getLogoutCancelledMessage().isEmpty())
        player.sendMessage(this.plugin.getSettings().getLogoutCancelledMessage()); 
      cancel();
      return;
    } 
    int remainingSeconds = getRemainingSeconds();
    if (remainingSeconds <= 0) {
      this.finished = true;
      this.plugin.getTagManager().untag(this.playerId);
      if (!this.plugin.getSettings().getLogoutSuccessMessage().isEmpty())
        player.kickPlayer(this.plugin.getSettings().getLogoutSuccessMessage()); 
      cancel();
      return;
    } 
    if (remainingSeconds < this.remainingSeconds) {
      String remaining = this.plugin.getSettings().formatDuration(remainingSeconds);
      if (!this.plugin.getSettings().getLogoutPendingMessage().isEmpty())
        player.sendMessage(this.plugin.getSettings().getLogoutPendingMessage().replace("{remaining}", remaining)); 
      this.remainingSeconds = remainingSeconds;
    } 
  }
  
  private boolean hasMoved(Player player) {
    Location l = player.getLocation();
    return (this.loc.getWorld() != l.getWorld() || this.loc.getBlockX() != l.getBlockX() || this.loc
      .getBlockY() != l.getBlockY() || this.loc.getBlockZ() != l.getBlockZ());
  }
  
  public static void run(CombatTagPlus plugin, Player player) {
    if (hasTask(player))
      return; 
    long logoutTime = System.currentTimeMillis() + (plugin.getSettings().getLogoutWaitTime() * 1000);
    SafeLogoutTask task = new SafeLogoutTask(plugin, player, logoutTime);
    task.runTaskTimer(P.p, 0L, 5L);
    tasks.put(player.getUniqueId(), task);
  }
  
  public static boolean hasTask(Player player) {
    SafeLogoutTask task = tasks.get(player.getUniqueId());
    if (task == null)
      return false; 
    BukkitScheduler s = Bukkit.getScheduler();
    if (s.isQueued(task.getTaskId()) || s.isCurrentlyRunning(task.getTaskId()))
      return true; 
    tasks.remove(player.getUniqueId());
    return false;
  }
  
  public static boolean isFinished(Player player) {
    return (hasTask(player) && ((SafeLogoutTask)tasks.get(player.getUniqueId())).finished);
  }
  
  public static boolean cancel(Player player) {
    if (!hasTask(player))
      return false; 
    Bukkit.getScheduler().cancelTask(((SafeLogoutTask)tasks.get(player.getUniqueId())).getTaskId());
    tasks.remove(player.getUniqueId());
    return true;
  }
  
  public static void purgeFinished() {
    Iterator<SafeLogoutTask> iterator = tasks.values().iterator();
    BukkitScheduler s = Bukkit.getScheduler();
    while (iterator.hasNext()) {
      int taskId = ((SafeLogoutTask)iterator.next()).getTaskId();
      if (!s.isQueued(taskId) && !s.isCurrentlyRunning(taskId))
        iterator.remove(); 
    } 
  }
}
