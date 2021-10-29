/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  me.ifamasssxd.cosmicbosses.utils.BossEggUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Creature
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.inventory.ItemStack
 */
package com.massivecraft.factions.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;

public class MiscUtil {
    public static HashSet<String> substanceChars = new HashSet<String>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));

    public static EntityType creatureTypeFromEntity(Entity entity) {
        if (!(entity instanceof Creature)) {
            return null;
        }
        String name = entity.getClass().getSimpleName();
        name = name.substring(5);
        return EntityType.fromName(name);
    }

    public static long[] range(long start, long end) {
        long[] values = new long[(int) Math.abs(end - start) + 1];
        if (end < start) {
            long oldstart = start;
            start = end;
            end = oldstart;
        }
        for (long i = start; i <= end; ++i) {
            values[(int) (i - start)] = i;
        }
        return values;
    }

    public static long getMoneyValue(String formatted_value) {
        String numb_val = ChatColor.stripColor(formatted_value.replaceAll(",", "").replace("$", "")).toLowerCase();
        numb_val = numb_val.replace("k", "000");
        numb_val = numb_val.replace("million", "000000");
        numb_val = numb_val.replace("billion", "000000000");
        numb_val = numb_val.replace("trillion", "000000000000");
        numb_val = numb_val.replace("mil", "000000");
        numb_val = numb_val.replace("bil", "000000000");
        numb_val = numb_val.replace("tril", "000000000000");
        numb_val = numb_val.replace("m", "000000");
        numb_val = numb_val.replace("b", "000000000");
        numb_val = numb_val.replace("t", "000000000000");
        return Long.parseLong(numb_val);
    }

    public static String getComparisonString(String str) {
        StringBuilder ret = new StringBuilder();
        str = ChatColor.stripColor(str);
        str = str.toLowerCase();
        for (char c : str.toCharArray()) {
            if (!substanceChars.contains(String.valueOf(c))) continue;
            ret.append(c);
        }
        return ret.toString().toLowerCase();
    }

    public static boolean isBossEgg(ItemStack item) {
        return false;
    }
}

