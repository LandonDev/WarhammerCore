/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 */
package com.massivecraft.factions.util;

import com.massivecraft.factions.P;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.concurrent.TimeUnit;

public class TimeUtil {
    public static String formatFutureTime(long timeInFuture) {
        long timeDifference = timeInFuture - System.currentTimeMillis();
        long seconds = timeDifference / 1000L;
        return TimeUtil.formatSeconds(seconds);
    }

    public static String formatSeconds(long seconds) {
        String diff;
        if (seconds == 0L) {
            return "0s";
        }
        long day = TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - day * 24L;
        long minutes = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.SECONDS.toHours(seconds) * 60L;
        long secs = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.SECONDS.toMinutes(seconds) * 60L;
        StringBuilder sb = new StringBuilder();
        if (day > 0L) {
            sb.append(day).append(day == 1L ? "d" : "d").append(" ");
        }
        if (hours > 0L) {
            sb.append(hours).append(hours == 1L ? "h" : "h").append(" ");
        }
        if (minutes > 0L) {
            sb.append(minutes).append(minutes == 1L ? "m" : "m").append(" ");
        }
        if (secs > 0L && minutes <= 0L && hours <= 0L) {
            sb.append(secs).append(secs == 1L ? "s" : "s");
        }
        return (diff = sb.toString()).isEmpty() ? "Now" : diff.trim();
    }

    public static String formatDifference(long time) {
        String diff;
        if (time == 0L) {
            return "Never";
        }
        long day = TimeUnit.SECONDS.toDays(time);
        long hours = TimeUnit.SECONDS.toHours(time) - day * 24L;
        long minutes = TimeUnit.SECONDS.toMinutes(time) - TimeUnit.SECONDS.toHours(time) * 60L;
        long seconds = TimeUnit.SECONDS.toSeconds(time) - TimeUnit.SECONDS.toMinutes(time) * 60L;
        StringBuilder sb = new StringBuilder();
        if (day > 0L) {
            sb.append(day).append(day == 1L ? "day" : "days").append(" ");
        }
        if (hours > 0L) {
            sb.append(hours).append(hours == 1L ? "h" : "h").append(" ");
        }
        if (minutes > 0L) {
            sb.append(minutes).append(minutes == 1L ? "m" : "m").append(" ");
        }
        return (diff = sb.toString()).isEmpty() ? "Now" : diff;
    }

    public static void setCooldown(Player player, String name, int seconds) {
        player.setMetadata(name, new FixedMetadataValue(P.getP(), (System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds))));
    }

    public static boolean isOnCooldown(Player player, String name) {
        if (!player.hasMetadata(name) || player.getMetadata(name).size() <= 0) {
            return false;
        }
        long time = player.getMetadata(name).get(0).asLong();
        return time > System.currentTimeMillis();
    }
}

