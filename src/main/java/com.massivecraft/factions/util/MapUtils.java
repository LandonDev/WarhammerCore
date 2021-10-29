/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class MapUtils {
    public static LinkedHashMap<FPlayer, Long> sortByComparator(Map<FPlayer, Long> unsortMap) {
        LinkedList<Map.Entry<FPlayer, Long>> list = new LinkedList<Map.Entry<FPlayer, Long>>(unsortMap.entrySet());
        list.sort((o1, o2) -> o1.getValue() < o2.getValue() ? 1 : (o1.getValue().longValue() == o2.getValue().longValue() ? 0 : -1));
        LinkedHashMap<FPlayer, Long> sortedMap = new LinkedHashMap<FPlayer, Long>();
        for (Map.Entry entry : list) {
            sortedMap.put((FPlayer) entry.getKey(), (Long) entry.getValue());
        }
        return sortedMap;
    }

    public static void printMap(Map<?, ?> map) {
        if (map == null) {
            Bukkit.getLogger().info("Null map!");
            return;
        }
        map.forEach((ent, ent2) -> {
            Bukkit.getLogger().info("Key: " + ent + " -> Values: ");
            if (ent2 instanceof Map) {
                MapUtils.printMap((Map) ent2);
            } else {
                Bukkit.getLogger().info(ent2.toString());
            }
        });
    }

}

