package net.minelink.ctplus.hook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minelink.ctplus.BlockPosition;
import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.util.LruCache;
import org.bukkit.Location;

public final class HookManager {
  private final List<Hook> hooks = new ArrayList<>();
  
  private final LruCache<BlockPosition, PvpBlock> pvpBlocks = new LruCache(100000);
  
  private final CombatTagPlus plugin;
  
  public HookManager(CombatTagPlus plugin) {
    this.plugin = plugin;
  }
  
  public boolean addHook(Hook hook) {
    return this.hooks.add(hook);
  }
  
  public boolean removeHook(Hook hook) {
    return this.hooks.remove(hook);
  }
  
  public List<Hook> getHooks() {
    return Collections.unmodifiableList(this.hooks);
  }
  
  public boolean isPvpEnabledAt(Location location) {
    PvpBlock pvpBlock;
    long currentTime = System.currentTimeMillis();
    BlockPosition position = new BlockPosition(location);
    synchronized (this.pvpBlocks) {
      pvpBlock = (PvpBlock)this.pvpBlocks.get(position);
      if (pvpBlock != null && pvpBlock.expiry > currentTime)
        return pvpBlock.enabled; 
      pvpBlock = new PvpBlock(currentTime + 60000L);
      this.pvpBlocks.put(position, pvpBlock);
    } 
    for (Hook hook : this.hooks) {
      if (!hook.isPvpEnabledAt(location)) {
        pvpBlock.enabled = false;
        break;
      } 
    } 
    return pvpBlock.enabled;
  }
  
  private static class PvpBlock {
    private final long expiry;
    
    private boolean enabled = true;
    
    PvpBlock(long expiry) {
      this.expiry = expiry;
    }
  }
}
