/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.factionpoints.FactionsCoreChunkAPI
 *  com.cosmicpvp.factionpoints.FactionsPointsAPI
 *  com.earth2me.essentials.User
 *  com.google.common.collect.Lists
 *  me.ifamasssxd.factionstop.FactionsTop
 *  me.ifamasssxd.factionstop.manager.TopManager
 *  me.ifamasssxd.factionstop.struct.StoredFaction
 *  me.ifamasssxd.factionstop.struct.TopFaction
 *  net.milkbowl.vault.economy.Economy
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.cmd;

import com.google.common.collect.Lists;
import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.EssentialsFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import mkremins.fanciful.FancyMessage;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

public class CmdShow extends FCommand {
    private DecimalFormat df = new DecimalFormat("#,###");

    public CmdShow() {
        this.aliases.add("show");
        this.aliases.add("who");
        this.aliases.add("f");
        this.optionalArgs.put("faction tag", "yours");
        this.optionalArgs.put("showEnemies", "");
        this.permission = Permission.SHOW.node;
        this.disableOnLock = false;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    public void perform() {
        Faction myFaction = this.fme.getFaction();
        Faction finalFaction = myFaction;
        if (this.argIsSet(0)) {
            finalFaction = this.argAsFaction(0);
            if (finalFaction == null) {
                return;
            }
        }

        double econ = Econ.getFactionBalance(finalFaction);
        if (this.payForCommand(Conf.econCostShow, "to show faction information", "for showing faction information")) {
            Collection<FPlayer> admins = finalFaction.getFPlayersWhereRole(Role.ADMIN);
            Collection<FPlayer> mods = Lists.newArrayList();
            mods.addAll(finalFaction.getFPlayersWhereRole(Role.COLEADER));
            mods.addAll(finalFaction.getFPlayersWhereRole(Role.MODERATOR));
            Collection<FPlayer> normals = finalFaction.getFPlayersWhereRole(Role.NORMAL);
            Collection<FPlayer> recruits = finalFaction.getFPlayersWhereRole(Role.RECRUIT);
            if (finalFaction == Factions.i.getNone()) {
                this.msg(this.p.txt.titleize(ChatColor.AQUA + "No Faction"));
                this.msg(ChatColor.translateAlternateColorCodes('&', "<c>&4Description: <p>&f%s") , this.argAsString(0) + " is not currently in a faction.");
            } else {
                this.msg(this.p.txt.titleize(finalFaction.getTag(this.fme)));
                this.msg(ChatColor.translateAlternateColorCodes('&',"<c>&4Description: <p>&f%s"), finalFaction.getDescription());
            }

            if (finalFaction.isNormal()) {
                String peaceStatus = "";
                if (finalFaction.isPeaceful()) {
                    peaceStatus = "     " + Conf.colorNeutral + "This faction is Peaceful";
                }

                this.msg(ChatColor.translateAlternateColorCodes('&',"<c>&4Joining: <p>&f") + (finalFaction.getOpen() ? "Open" : "Invite Only") + peaceStatus);
                double powerBoost = finalFaction.getPowerBoost();
                final String boost = powerBoost == 0.0D ? "" : (powerBoost > 0.0D ? " (bonus: " : " (penalty: ") + powerBoost + ")";
                final int coordCount = finalFaction.getLandRounded();
                Faction finalFaction1 = finalFaction;
                /*(new BukkitRunnable() {
                    public void run() {
                        CmdShow.this.msg(ChatColor.translateAlternateColorCodes('&',"<c>&4Land/Power/Maxpower: <p>&f%d/%d/%d %s"), coordCount, finalFaction1.getPowerRounded(), finalFaction1.getPowerMaxRounded(), boost);
                        TopFaction tf = FactionsTop.get().getTopManager().getTopFaction(finalFaction1.getId());
                        if (tf != null) {
                            StoredFaction sf = tf.getStoredFaction();
                            CmdShow.this.msg(ChatColor.translateAlternateColorCodes('&',"<c>&4Faction Wealth: <p>&f$%s"), CmdShow.this.df.format((long) sf.getTotalWorth()));
                            CmdShow.this.msg(ChatColor.translateAlternateColorCodes('&',"<c>&4Spawner Value: <p>&f$%s"), CmdShow.this.df.format((long) sf.getTotalSpawnerWorth()));
                            CmdShow.this.msg(ChatColor.translateAlternateColorCodes('&',"<c>&4Block Value: <p>&f$%s"), CmdShow.this.df.format((long) sf.getContainerWorth()));
                            CmdShow.this.msg(ChatColor.translateAlternateColorCodes('&',"<c>&4Faction Rank: <p>&f#%s"), tf.getRank());
                        }

                    }
                }).runTask(P.getP());*/
                String allyList;
                /*if (P.p.factionPointsEnabled) {
                    Long points = FactionsPointsAPI.getPoints(finalFaction);
                    if (points == null) {
                        points = 0L;
                    }

                    this.msg(ChatColor.translateAlternateColorCodes('&',"<c>&4Faction Points: <p>&f%s"), this.df.format(points));
                    allyList = FactionsCoreChunkAPI.getWhenRaidableString(finalFaction);
                    if (allyList != null) {
                        this.msg(ChatColor.translateAlternateColorCodes('&',"<c>&4Raid Cooldown: <p>&f%s"), allyList);
                    }

                    if (myFaction != null && myFaction.equals(finalFaction)) {
                        FLocation coreChunk = FactionsCoreChunkAPI.getCoreChunk(myFaction);
                        this.msg(ChatColor.translateAlternateColorCodes('&',"<c>&4Core Chunk: <p>&f%s"), coreChunk == null ? "N/A" : FLocation.chunkToBlock((int) coreChunk.getX()) + "x, " + FLocation.chunkToBlock((int) coreChunk.getZ()) + "z");
                    }
                }*/

                if (Conf.bankEnabled) {
                    this.msg(ChatColor.translateAlternateColorCodes('&',"<c>&4Bank Balance: <p>&f$%s"), this.df.format(econ));
                }

                if (finalFaction.isPermanent()) {
                    this.msg(ChatColor.translateAlternateColorCodes('&',"<c>&4This faction is permanent, remaining even with no members."));
                }

                allyList = this.p.txt.parse(ChatColor.translateAlternateColorCodes('&',"<c>&4Allies: &f"));
                String truceList = this.p.txt.parse(ChatColor.translateAlternateColorCodes('&',"<c>&4Truces:&f "));
                String enemyList = this.p.txt.parse(ChatColor.translateAlternateColorCodes('&',"<c>&4Enemies:&f "));
                Iterator var19 = Factions.i.get().iterator();

                while (true) {
                    Faction otherFaction;
                    Relation rel;
                    do {
                        do {
                            if (!var19.hasNext()) {
                                if (allyList.endsWith(", ")) {
                                    allyList = allyList.substring(0, allyList.length() - 2);
                                }

                                if (truceList.endsWith(", ")) {
                                    truceList = truceList.substring(0, truceList.length() - 2);
                                }

                                if (enemyList.endsWith(", ")) {
                                    enemyList = enemyList.substring(0, enemyList.length() - 2);
                                }

                                if (allyList.length() > 5000) {
                                    this.sender.sendMessage(ChatColor.RED + "Could not fetch ally list for faction, length exceeds 16k");
                                } else {
                                    this.sendMessage(allyList);
                                }

                                if (truceList.length() > 5000) {
                                    this.sender.sendMessage(ChatColor.RED + "Could not fetch truce list for faction, length exceeds 16k");
                                } else {
                                    this.sendMessage(truceList);
                                }

                                Bukkit.getLogger().info("[Factions] " + finalFaction.getTag() + " enemyList size => " + enemyList.length());
                                String s;
                                if (enemyList.length() > 5000) {
                                    if (this.sender.getName().equals("Vaquxine")) {
                                        while (enemyList.length() > 16000) {
                                            s = enemyList.substring(0, 15999);
                                            enemyList = enemyList.substring(16000);
                                            this.sendMessage(s);
                                        }
                                    }

                                    this.sender.sendMessage(ChatColor.RED + "Could not fetch enemy list for faction, length exceeds 16k");
                                } else if (this.argIsSet(1) && ((s = this.argAsString(1, "")).equalsIgnoreCase("true") || s.equalsIgnoreCase("enemies") || s.equalsIgnoreCase("yes"))) {
                                    this.sendMessage(enemyList);
                                }

                                FancyMessage onlineList = new FancyMessage(this.p.txt.parse("<c>") + ChatColor.translateAlternateColorCodes('&',"&4Members online:&f "));
                                FancyMessage offlineList = new FancyMessage(this.p.txt.parse("<c>") + ChatColor.translateAlternateColorCodes('&',"&4Members offline:&f "));
                                Iterator var32 = admins.iterator();

                                while (true) {
                                    FPlayer follower;
                                    double balance;
                                    OfflinePlayer plyer;
                                    while (var32.hasNext()) {
                                        follower = (FPlayer) var32.next();
                                        plyer = Bukkit.getOfflinePlayer(UUID.fromString(follower.getId()));
                                        if (plyer == null || plyer.getName() == null) {
                                            Bukkit.getLogger().info("Null offline player name for " + follower.getId());
                                            continue;
                                        }
                                        balance = Econ.econ.getBalance(plyer);

                                        if (follower.isOnlineAndVisibleTo(this.me)) {
                                            if (!onlineList.toOldMessageFormat().endsWith(" ")) {
                                                onlineList.then(", ");
                                            }

                                            onlineList = follower.appendNameAndTitleWithPowerAsync(balance, this.fme, onlineList, follower.getPlayer()).then(this.p.txt.parse("<p>"));
                                        } else {
                                            if (!offlineList.toOldMessageFormat().endsWith(" ")) {
                                                offlineList.then(", ");
                                            }

                                            offlineList = follower.appendNameAndTitleWithPowerAsync(balance, this.fme, offlineList, null).then(this.p.txt.parse("<p>"));
                                        }
                                    }

                                    var32 = mods.iterator();

                                    while (true) {
                                        while (var32.hasNext()) {
                                            follower = (FPlayer) var32.next();
                                            plyer = Bukkit.getOfflinePlayer(UUID.fromString(follower.getId()));
                                            if (plyer == null || plyer.getName() == null) {
                                                Bukkit.getLogger().info("Null offline player name for " + follower.getId());
                                                continue;
                                            }
                                            balance = Econ.econ.getBalance(plyer);

                                            if (follower.isOnlineAndVisibleTo(this.me)) {
                                                if (!onlineList.toOldMessageFormat().endsWith(" ")) {
                                                    onlineList.then(", ");
                                                }

                                                onlineList = follower.appendNameAndTitleWithPowerAsync(balance, this.fme, onlineList, follower.getPlayer()).then(this.p.txt.parse("<p>"));
                                            } else {
                                                if (!offlineList.toOldMessageFormat().endsWith(" ")) {
                                                    offlineList.then(", ");
                                                }

                                                offlineList = follower.appendNameAndTitleWithPowerAsync(balance, this.fme, offlineList, null).then(this.p.txt.parse("<p>"));
                                            }
                                        }

                                        var32 = normals.iterator();

                                        while (true) {
                                            while (var32.hasNext()) {
                                                follower = (FPlayer) var32.next();
                                                plyer = Bukkit.getOfflinePlayer(UUID.fromString(follower.getId()));
                                                if (plyer == null || plyer.getName() == null) {
                                                    Bukkit.getLogger().info("Null offline player name for " + follower.getId());
                                                    continue;
                                                }
                                                balance = Econ.econ.getBalance(plyer);

                                                if (follower.isOnlineAndVisibleTo(this.me)) {
                                                    if (!onlineList.toOldMessageFormat().endsWith(" ")) {
                                                        onlineList.then(", ");
                                                    }

                                                    onlineList = follower.appendNameAndTitleWithPowerAsync(balance, this.fme, onlineList, follower.getPlayer()).then(this.p.txt.parse("<p>"));
                                                } else {
                                                    if (!offlineList.toOldMessageFormat().endsWith(" ")) {
                                                        offlineList.then(", ");
                                                    }

                                                    offlineList = follower.appendNameAndTitleWithPowerAsync(balance, this.fme, offlineList, null).then(this.p.txt.parse("<p>"));
                                                }
                                            }

                                            var32 = recruits.iterator();

                                            while (true) {
                                                while (var32.hasNext()) {
                                                    follower = (FPlayer) var32.next();
                                                    plyer = Bukkit.getOfflinePlayer(UUID.fromString(follower.getId()));
                                                    if (plyer == null || plyer.getName() == null) {
                                                        Bukkit.getLogger().info("Null offline player name for " + follower.getId());
                                                        continue;
                                                    }
                                                    balance = Econ.econ.getBalance(plyer);

                                                    if (follower.isOnlineAndVisibleTo(this.me)) {
                                                        if (!onlineList.toOldMessageFormat().endsWith(" ")) {
                                                            onlineList.then(", ");
                                                        }

                                                        onlineList = follower.appendNameAndTitleWithPowerAsync(balance, this.fme, onlineList, follower.getPlayer()).then(this.p.txt.parse("<p>"));
                                                    } else {
                                                        if (!offlineList.toOldMessageFormat().endsWith(" ")) {
                                                            offlineList.then(", ");
                                                        }

                                                        offlineList = follower.appendNameAndTitleWithPowerAsync(balance, this.fme, offlineList, null).then(this.p.txt.parse("<p>"));
                                                    }
                                                }

                                                if (onlineList.toJSONString().length() >= 32000) {
                                                    this.sender.sendMessage(ChatColor.RED + "Could not fetch online player list for faction, length exceeds 16k");
                                                } else {
                                                    onlineList.send(this.sender);
                                                }

                                                if (offlineList.toJSONString().length() >= 32000) {
                                                    this.sender.sendMessage(ChatColor.RED + "Could not fetch offline player list for faction, length exceeds 16k");
                                                } else {
                                                    offlineList.send(this.sender);
                                                }

                                                return;
                                            }
                                        }
                                    }
                                }
                            }

                            otherFaction = (Faction) var19.next();
                        } while (otherFaction == finalFaction);

                        rel = otherFaction.getRelationTo(finalFaction);
                    } while (!rel.isAlly() && !rel.isEnemy() && !rel.isTruce());

                    String listpart = otherFaction.getTag(this.fme) + this.p.txt.parse("<p>&f") + ", ";
                    if (rel.isAlly()) {
                        allyList = allyList + listpart;
                    } else if (rel.isTruce()) {
                        truceList = truceList + listpart;
                    } else if (rel.isEnemy()) {
                        enemyList = enemyList + listpart;
                    }
                }
            }
        }
    }

    public static boolean isUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException var2) {
            return false;
        }
    }
}

