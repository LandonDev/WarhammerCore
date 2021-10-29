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

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Teleport;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.massivecraft.factions.Conf;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class EssentialsFeatures {
    private static IEssentials essentials;

    public static void setup() {
        Plugin ess = Bukkit.getPluginManager().getPlugin("Essentials");
        essentials = (IEssentials) ess;
    }

    public static boolean isVanished(Player player) {
        if (essentials == null) {
            return false;
        }
        User user = essentials.getUser(player.getUniqueId());
        return user != null && user.isVanished();
    }

    public static boolean handleTeleport(Player player, Location loc) {
        if (!Conf.homesTeleportCommandEssentialsIntegration || essentials == null) {
            return false;
        }
        Teleport teleport = essentials.getUser(player).getTeleport();
        Trade trade = new Trade(Conf.econCostHome, essentials);
        try {
            teleport.teleport(loc, trade);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED.toString() + e.getMessage());
        }
        return true;
    }

    public static IEssentials getEssentials() {
        return essentials;
    }
}

