/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  me.ifamasssxd.cosmicplaytime.CosmicPlaytime
 *  net.milkbowl.vault.economy.Economy
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.MapUtils;
import com.massivecraft.factions.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

public class CmdStatus
        extends FCommand {
    private DecimalFormat format = new DecimalFormat("#,###");

    public CmdStatus() {
        this.aliases.add("status");
        this.aliases.add("online");
        this.disableOnLock = false;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction faction = this.fme.getFaction();
        if (faction != null && faction.isNormal()) {
            try {
                this.sender.sendMessage("");
                /*CosmicPlaytime playtime = (CosmicPlaytime) Bukkit.getPluginManager().getPlugin("CosmicPlaytime");
                if (playtime != null) {
                    long start = System.currentTimeMillis();
                    LinkedHashMap<FPlayer, Long> mostRecentPlayers = new LinkedHashMap<FPlayer, Long>();
                    int online = 0;
                    for (FPlayer fplayer : faction.getFPlayers()) {
                        mostRecentPlayers.put(fplayer, fplayer.isCachedAsOnline() ? System.currentTimeMillis() : fplayer.getLastLoginTime());
                        if (fplayer.bukkitPlayer == null) continue;
                        ++online;
                    }
                    LinkedHashMap<FPlayer, Long> sorted = MapUtils.sortByComparator(mostRecentPlayers);
                    this.sender.sendMessage(ChatColor.AQUA.toString() + ChatColor.UNDERLINE + ChatColor.BOLD + "/f status" + ChatColor.RESET + ChatColor.GRAY + " [" + ChatColor.GREEN + online + ChatColor.GRAY + "/" + ChatColor.RED + faction.getFPlayers().size() + ChatColor.GRAY + "]");
                    for (Map.Entry<FPlayer, Long> data : sorted.entrySet()) {
                        FPlayer fplayer = data.getKey();
                        long lastLogin = data.getValue();
                        StringBuilder builder = new StringBuilder();
                        boolean onlineStatus = fplayer.getPlayer() != null;
                        builder.append((onlineStatus ? ChatColor.GREEN : ChatColor.RED)).append(ChatColor.BOLD).append(fplayer.getRole().getPrefix()).append(fplayer.getNameAsync()).append(" ");
                        if (!onlineStatus) {
                            builder.append(ChatColor.GRAY).append("[").append(TimeUtil.formatDifference((System.currentTimeMillis() - lastLogin) / 1000L).trim()).append("] ");
                        }
                        builder.append(ChatColor.GRAY).append("has ");
                        builder.append(ChatColor.YELLOW).append("$").append(this.format.format(Econ.econ.getBalance(Bukkit.getOfflinePlayer(String.valueOf(fplayer.getCachedUUID())))));
                        if (AuthenticationUtils.isEnabled()) {
                            boolean enabled = AuthenticationUtils.isEnabled(fplayer.getCachedUUID());
                            builder.append(ChatColor.GRAY).append(" with");
                            builder.append((enabled ? ChatColor.GREEN : ChatColor.RED)).append(" /2fa ").append(enabled ? "ON" : "OFF");
                        }
                        this.sender.sendMessage(builder.toString());
                    }
                    this.sender.sendMessage("");
                    Bukkit.getLogger().info("Sent " + sorted.size() + " fplayers to " + this.sender.getName() + " in " + (System.currentTimeMillis() - start) + "ms");
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.sender.sendMessage(ChatColor.RED + "You must be in a faction to use this command!");
        }
    }
}

