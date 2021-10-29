/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.sk89q.worldedit.BlockVector
 *  com.sk89q.worldguard.bukkit.WorldGuardPlugin
 *  com.sk89q.worldguard.protection.managers.RegionManager
 *  com.sk89q.worldguard.protection.regions.ProtectedRegion
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 */
package com.massivecraft.factions.zcore.util;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.io.File;

public class WorldUtil {
    public static boolean isWorldLoaded(String name) {
        return Bukkit.getServer().getWorld(name) != null;
    }

    public static boolean doesWorldExist(String name) {
        return new File(name, "level.dat").exists();
    }

    public static void changeClaimsInRegionWithFaction(CommandSender sender, String regionName, World world, Faction toClaim, boolean claim) {
        WorldGuardPlugin worldGaurd = WorldGuardPlugin.inst();
        RegionManager manager = worldGaurd.getRegionManager(world);
        if (manager == null) {
            sender.sendMessage(ChatColor.RED + "No region manager found!");
            return;
        }
        ProtectedRegion region = manager.getRegion(regionName);
        if (region == null) {
            sender.sendMessage(ChatColor.RED + "No region found with that name!");
            return;
        }
        BlockVector topLeft = region.getMaximumPoint();
        BlockVector bottom = region.getMinimumPoint();
        int maxChunkX = topLeft.getBlockX() >> 4;
        int maxChunkZ = topLeft.getBlockZ() >> 4;
        int minChunkX = bottom.getBlockX() >> 4;
        int minChunkZ = bottom.getBlockZ() >> 4;
        int claimed = 0;
        for (int x = minChunkX; x <= maxChunkX; ++x) {
            for (int z = minChunkZ; z <= maxChunkZ; ++z) {
                FLocation flocation = new FLocation(world.getName(), x, z);
                Faction at = Board.getFactionAt(flocation);
                if (claim) {
                    if (at == toClaim) continue;
                    ++claimed;
                    Board.setFactionAt(toClaim, flocation);
                    continue;
                }
                if (at != toClaim) continue;
                ++claimed;
                Board.removeAt(flocation);
            }
        }
        sender.sendMessage(ChatColor.RED + (!claim ? "Removed " : "Claimed ") + claimed + " chunks for " + toClaim.getTag());
    }
}

