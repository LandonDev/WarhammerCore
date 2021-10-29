/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.google.common.collect.Lists
 *  com.google.common.collect.MapMaker
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.SpiralTask;
import com.massivecraft.factions.zcore.util.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CmdClaim extends FCommand {
    public static Set<String> chunkCoordsPendingClaim = Collections.newSetFromMap((Map)CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.MINUTES).build().asMap());
    Set<Material> seeThrough;

    public CmdClaim() {
        this.seeThrough = new HashSet<>(Lists.newArrayList(Material.values()));
        this.aliases.add("claim");
        this.optionalArgs.put("faction", "your");
        this.optionalArgs.put("radius", "1");
        this.optionalArgs.put("r", "");
        this.permission = Permission.CLAIM.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (this.args.size() == 3 && this.argAsString(0).equalsIgnoreCase("region") && this.sender.isOp()) {
            Faction faction = this.argAsFaction(2);
            if (faction == null) {
                this.sender.sendMessage(ChatColor.RED + "Invalid faction entered!");
                return;
            }
            WorldUtil.changeClaimsInRegionWithFaction(this.sender, this.argAsString(1), (this.sender instanceof Player) ? ((Player) this.sender).getWorld() : Bukkit.getWorlds().get(0), faction, true);
        } else {
            Faction forFaction = this.argAsFaction(0, this.myFaction);
            int radius = this.argAsInt(1, 1);
            if (radius < 1) {
                this.msg("<b>If you specify a radius, it must be at least 1.");
                return;
            }
            String chunkCoords = this.me.getLocation().getChunk().getX() + "," + this.me.getLocation().getChunk().getZ();
            if (radius < 2) {
                if (CmdClaim.chunkCoordsPendingClaim.contains(chunkCoords)) {
                    this.msg("<b>There is already a pending /f claim on this chunk.");
                    return;
                }
                CmdClaim.chunkCoordsPendingClaim.add(chunkCoords);
                try {
                    if (this.fme.attemptClaimAsync(forFaction, this.me.getLocation(), true)) {
                        P.p.logFactionEvent(forFaction, FLogType.CHUNK_CLAIMS, this.fme.getNameAsync(), CC.GreenB + "CLAIMED", "1", new FLocation(this.me.getLocation()).formatXAndZ(","));
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }
                CmdClaim.chunkCoordsPendingClaim.remove(chunkCoords);
            } else {
                if (!Permission.CLAIM_RADIUS.has(this.sender, false)) {
                    this.msg("<b>You do not have permission to claim in a radius.");
                    return;
                }
                Chunk centerChunk = this.me.getLocation().getChunk();
                HashSet<String> chunkCoordsToAttemptRadiusClaim = new HashSet<String>();
                radius = Math.min(20, radius);
                for (int xMod = -radius; xMod < radius; ++xMod) {
                    for (int zMod = -radius; zMod < radius; ++zMod) {
                        String ichunkCoords = centerChunk.getX() + xMod + "," + (centerChunk.getZ() + zMod);
                        if (CmdClaim.chunkCoordsPendingClaim.contains(ichunkCoords)) {
                            this.msg("<b>A chunk within your radius already has a pending /f claim.");
                            return;
                        }
                        chunkCoordsToAttemptRadiusClaim.add(ichunkCoords);
                    }
                }
                CmdClaim.chunkCoordsPendingClaim.addAll(chunkCoordsToAttemptRadiusClaim);
                FPlayer fPlayer = this.fme;
                Player player = this.me;
                new SpiralTask(new FLocation(player), radius) {
                    private int limit = Conf.radiusClaimFailureLimit - 1;
                    int claimed = 0;
                    private int failCount = 0;

                    @Override
                    public boolean work() {
                        boolean success = fPlayer.attemptClaimAsync(forFaction, this.currentLocation(), true);
                        if (!success) {
                            ++this.failCount;
                        }
                        if (success) {
                            this.failCount = 0;
                            ++this.claimed;
                        } else if (this.failCount++ >= this.limit) {
                            for (String s : chunkCoordsToAttemptRadiusClaim) {
                                CmdClaim.chunkCoordsPendingClaim.remove(s);
                            }
                            P.p.logFactionEvent(forFaction, FLogType.CHUNK_CLAIMS, fPlayer.getNameAsync(), CC.GreenB + "CLAIMED", String.valueOf(this.claimed), new FLocation(player.getLocation()).formatXAndZ(","));
                            this.stop();
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public void finish() {
                        this.stop();
                        for (String s : chunkCoordsToAttemptRadiusClaim) {
                            CmdClaim.chunkCoordsPendingClaim.remove(s);
                        }
                        P.p.logFactionEvent(forFaction, FLogType.CHUNK_CLAIMS, fPlayer.getNameAsync(), CC.GreenB + "CLAIMED", String.valueOf(this.claimed), new FLocation(player.getLocation()).formatXAndZ(","));
                    }
                };
            }
        }
    }
}

