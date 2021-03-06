package net.minelink.ctplus.worldguard.v6;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import net.minelink.ctplus.hook.Hook;
import org.bukkit.Location;

public final class WorldGuardHook implements Hook {
  public boolean isPvpEnabledAt(Location loc) {
    StateFlag.State s = (StateFlag.State)WGBukkit.getRegionManager(loc.getWorld()).getApplicableRegions(loc).getFlag((Flag)DefaultFlag.PVP);
    return (s == null || s != StateFlag.State.DENY);
  }
}
