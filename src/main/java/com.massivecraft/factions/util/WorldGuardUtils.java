package com.massivecraft.factions.util;

import com.massivecraft.factions.P;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.lang.reflect.Method;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public class WorldGuardUtils {
    private static WorldGuardPlugin wg = null;

    private static Class<?> bukkitUtil = null;

    private static Method toVector = null;

    public static void hook() {
        wg = getWorldGuard();
        try {
            bukkitUtil = wg.getClass().getClassLoader().loadClass("com.sk89q.worldguard.bukkit.BukkitUtil");
            toVector = bukkitUtil.getMethod("toVector", new Class[] { Block.class });
        } catch (ClassNotFoundException|NoSuchMethodException|SecurityException e) {
            e.printStackTrace();
        }
    }

    public static boolean isInRegionsContainsFast(Location location, List<String> regionNames) {
        RegionManager regionMan = wg.getGlobalRegionManager().get(location.getWorld());
        if (regionMan == null)
            return false;
        ApplicableRegionSet regionManager = regionMan.getApplicableRegions(location);
        if (regionManager == null)
            return false;
        for (ProtectedRegion region : regionManager) {
            for (String str : regionNames) {
                if (region.getId().contains(str))
                    return true;
            }
        }
        return false;
    }

    public static String getRegionName(Location l) {
        try {
            Vector blockVector = (Vector)toVector.invoke(null, new Object[] { l.getBlock() });
            List<String> regionSet = wg.getGlobalRegionManager().get(l.getWorld()).getApplicableRegionsIDs(blockVector);
            if (regionSet.size() < 1)
                return "";
            String return_region = "";
            int return_priority = -1;
            for (String region : regionSet) {
                int region_priority = wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getPriority();
                if (return_region.equalsIgnoreCase("")) {
                    return_region = wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getId();
                    return_priority = region_priority;
                }
                if (region_priority > return_priority) {
                    return_region = wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getId();
                    return_priority = region_priority;
                }
            }
            if (return_region.contains("global"))
                return_region = "";
            return return_region;
        } catch (Exception exception) {
            return "";
        }
    }

    public static boolean isInvincible(Location l) {
        try {
            BlockVector blockVector = BukkitUtil.toVector(l.getBlock());
            List<String> regionSet = wg.getGlobalRegionManager().get(l.getWorld()).getApplicableRegionsIDs((Vector)blockVector);
            if (regionSet.size() < 1)
                try {
                    return ((StateFlag.State)wg.getGlobalRegionManager().get(l.getWorld()).getRegion("__global__").getFlags().get(DefaultFlag.INVINCIBILITY) == StateFlag.State.ALLOW);
                } catch (Exception err) {
                    if (err instanceof NullPointerException)
                        return false;
                    err.printStackTrace();
                    return false;
                }
            boolean return_flag = false;
            int return_priority = -1;
            for (String region : regionSet) {
                if (wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getFlags().containsKey(DefaultFlag.INVINCIBILITY)) {
                    StateFlag.State pvp_flag = (StateFlag.State)wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getFlags().get(DefaultFlag.INVINCIBILITY);
                    int region_priority = wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getPriority();
                    if (return_priority == -1) {
                        return_flag = (pvp_flag == StateFlag.State.ALLOW);
                        return_priority = region_priority;
                        continue;
                    }
                    if (region_priority > return_priority) {
                        return_flag = (pvp_flag == StateFlag.State.ALLOW);
                        return_priority = region_priority;
                    }
                }
            }
            return return_flag;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean isPvPDisabled(Location l) {
        try {
            ApplicableRegionSet regionSet = wg.getGlobalRegionManager().get(l.getWorld()).getApplicableRegions(l);
            if (regionSet.size() < 1)
                try {
                    return ((StateFlag.State)wg.getGlobalRegionManager().get(l.getWorld()).getRegion("__global__").getFlags().get(DefaultFlag.PVP) == StateFlag.State.DENY);
                } catch (Exception err) {
                    if (err instanceof NullPointerException)
                        return false;
                    err.printStackTrace();
                    return false;
                }
            boolean return_flag = false;
            int return_priority = -1;
            for (ProtectedRegion region : regionSet) {
                if (region.getFlags().containsKey(DefaultFlag.PVP)) {
                    StateFlag.State pvp_flag = (StateFlag.State)region.getFlags().get(DefaultFlag.PVP);
                    int region_priority = region.getPriority();
                    if (return_priority == -1) {
                        return_flag = (pvp_flag == StateFlag.State.DENY);
                        return_priority = region_priority;
                        continue;
                    }
                    if (region_priority > return_priority) {
                        return_flag = (pvp_flag == StateFlag.State.DENY);
                        return_priority = region_priority;
                    }
                }
            }
            return return_flag;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean isLavaFlowDisabled(Location l) {
        try {
            BlockVector blockVector = BukkitUtil.toVector(l.getBlock());
            List<String> regionSet = wg.getGlobalRegionManager().get(l.getWorld()).getApplicableRegionsIDs((Vector)blockVector);
            if (regionSet.size() < 1)
                try {
                    return ((StateFlag.State)wg.getGlobalRegionManager().get(l.getWorld()).getRegion("__global__").getFlags().get(DefaultFlag.LAVA_FLOW) == StateFlag.State.DENY);
                } catch (Exception err) {
                    if (err instanceof NullPointerException)
                        return false;
                    return false;
                }
            boolean return_flag = false;
            int return_priority = -1;
            for (String region : regionSet) {
                if (wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getFlags().containsKey(DefaultFlag.LAVA_FLOW)) {
                    StateFlag.State lava_flag = (StateFlag.State)wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getFlags().get(DefaultFlag.LAVA_FLOW);
                    int region_priority = wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getPriority();
                    if (return_priority == -1) {
                        return_flag = (lava_flag == StateFlag.State.DENY);
                        return_priority = region_priority;
                        continue;
                    }
                    if (region_priority > return_priority) {
                        return_flag = (lava_flag == StateFlag.State.DENY);
                        return_priority = region_priority;
                    }
                }
            }
            return return_flag;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean getFlagState(Location l, Flag flag) {
        try {
            Vector blockVector = (Vector)toVector.invoke(null, new Object[] { l.getBlock() });
            List<String> regionSet = wg.getGlobalRegionManager().get(l.getWorld()).getApplicableRegionsIDs(blockVector);
            if (regionSet.size() < 1)
                return false;
            boolean return_flag = false;
            int return_priority = -1;
            for (String region : regionSet) {
                if (wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getFlags().containsKey(flag)) {
                    StateFlag.State invincible_flag = (StateFlag.State)wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getFlags().get(flag);
                    int region_priority = wg.getGlobalRegionManager().get(l.getWorld()).getRegion(region).getPriority();
                    if (return_priority == -1) {
                        return_flag = (invincible_flag == StateFlag.State.ALLOW);
                        return_priority = region_priority;
                        continue;
                    }
                    if (region_priority > return_priority) {
                        return_flag = (invincible_flag == StateFlag.State.ALLOW);
                        return_priority = region_priority;
                    }
                }
            }
            return return_flag;
        } catch (Exception exception) {
            return false;
        }
    }

    public static WorldGuardPlugin getWorldGuard() {
        if (wg != null)
            return wg;
        Plugin plugin = P.p.getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof WorldGuardPlugin))
            return null;
        return (WorldGuardPlugin)plugin;
    }
}
