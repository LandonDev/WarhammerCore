/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.World
 */
package com.massivecraft.factions.zcore.util;

import org.bukkit.Effect;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Random;

public class SmokeUtil {
    public static Random random = new Random();

    public static void spawnSingle(Location location, int direction) {
        if (location == null) {
            return;
        }
        location.getWorld().playEffect(location.clone(), Effect.SMOKE, direction);
    }

    public static void spawnSingle(Location location) {
        SmokeUtil.spawnSingle(location, 4);
    }

    public static void spawnSingleRandom(Location location) {
        SmokeUtil.spawnSingle(location, random.nextInt(9));
    }

    public static void spawnCloudSimple(Location location) {
        for (int i = 0; i <= 8; ++i) {
            SmokeUtil.spawnSingle(location, i);
        }
    }

    public static void spawnCloudSimple(Collection<Location> locations) {
        for (Location location : locations) {
            SmokeUtil.spawnCloudSimple(location);
        }
    }

    public static void spawnCloudRandom(Location location, float thickness) {
        int singles = (int) Math.floor(thickness * 9.0f);
        for (int i = 0; i < singles; ++i) {
            SmokeUtil.spawnSingleRandom(location.clone());
        }
    }

    public static void spawnCloudRandom(Collection<Location> locations, float thickness) {
        for (Location location : locations) {
            SmokeUtil.spawnCloudRandom(location, thickness);
        }
    }
}

