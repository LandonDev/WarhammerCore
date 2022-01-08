/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.earth2me.essentials.IEssentials
 *  com.earth2me.essentials.Teleport
 *  com.earth2me.essentials.Trade
 *  com.earth2me.essentials.User
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.massivecraft.factions.integration;

import landon.jurassiccore.JurassicCore;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class EssentialsFeatures {
    private static JurassicCore core;

    public static boolean handleTeleport(Player player, Location loc) {
        player.teleport(loc);
        return true;
    }
}

