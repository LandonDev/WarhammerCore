/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.EntityType
 *  org.bukkit.material.MaterialData
 */
package com.massivecraft.factions.struct;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LogTimer extends ConcurrentHashMap<LogTimer.TimerType, Map<LogTimer.TimerSubType, LogTimer.Timer>> {
    private static Set<Material> loggableMaterials = new HashSet<Material>() {
        {
            this.add(Material.IRON_INGOT);
            this.add(Material.IRON_BLOCK);
            this.add(Material.EMERALD);
            this.add(Material.ENDER_PEARL);
            this.add(Material.BLAZE_ROD);
            this.add(Material.SULPHUR);
            this.add(Material.MOB_SPAWNER);
            this.add(Material.GOLD_INGOT);
            this.add(Material.GOLD_BLOCK);
            this.add(Material.DIAMOND);
            this.add(Material.DIAMOND_BLOCK);
        }
    };
    private String factionId;
    private String username;

    public LogTimer(String username, String factionId) {
        this.username = username;
        this.factionId = factionId;
    }

    public Map<LogTimer.TimerSubType, LogTimer.Timer> getCurrentTimersOrCreate(LogTimer.TimerType type) {
        return this.computeIfAbsent(type, (m) -> new ConcurrentHashMap<>());
    }

    public LogTimer.Timer attemptLog(LogTimer.TimerType type, LogTimer.TimerSubType subType, long increment) {
        return this.getCurrentTimersOrCreate(type).computeIfAbsent(subType, (e) -> new Timer(System.currentTimeMillis(), 0L, null)).increment(increment);
    }

    public void pushLogs(Faction faction, LogTimer.TimerType type) {
        StringBuilder soldString = new StringBuilder();
        this.forEach((timerType, map) -> {
            if (timerType == type) {
                if (timerType == LogTimer.TimerType.SELL) {
                    Map<MaterialData, AtomicInteger> totalCounts = new ConcurrentHashMap<>();
                    AtomicLong total = new AtomicLong(0L);
                    map.forEach((subTimer, timer) -> {
                        Map<MaterialData, AtomicInteger> currentCounts = (Map) timer.getExtraData();
                        if (currentCounts != null) {
                            currentCounts.forEach((data, amount) -> {
                                if (loggableMaterials.contains(data.getItemType())) {
                                    totalCounts.computeIfAbsent(data, (e) -> new AtomicInteger(0)).addAndGet(amount.get());
                                }

                            });
                            total.addAndGet(timer.getCount());
                        }
                    });
                    totalCounts.forEach((data, amount) -> {
                        Material dataType = data.getItemType();
                        String name = StringUtils.capitaliseAllWords(dataType.name().toLowerCase().replace("_", " "));
                        soldString.append(amount).append("x ").append(name).append(", ");
                    });
                    map.clear();
                    String done = soldString.toString();
                    if (done.endsWith(", ")) {
                        done = done.substring(0, done.length() - 2);
                    }

                    P.p.getFlogManager().log(faction, FLogType.SELLING, this.username, done, Econ.insertCommas((double) total.get()));
                } else if (timerType == LogTimer.TimerType.SPAWNER_EDIT) {
                    map.forEach((subTimer, timer) -> {
                        Map<EntityType, AtomicInteger> entityCounts = new HashMap<>();
                        Map<MaterialData, AtomicInteger> currentCounts = (Map) timer.getExtraData();
                        if (currentCounts != null) {
                            currentCounts.forEach((data, ints) -> {
                                EntityType types = EntityType.fromId(data.getData());
                                if (types == null) {
                                    Bukkit.getLogger().info("Unable to find EntityType for " + data.getData() + " for " + subTimer + " for fac " + this.factionId + "!");
                                } else {
                                    entityCounts.computeIfAbsent(types, (e) -> new AtomicInteger(0)).addAndGet(ints.get());
                                }
                            });
                            entityCounts.forEach((entityType, count) -> {
                                P.p.getFlogManager().log(faction, FLogType.SPAWNER_EDIT, this.username, subTimer == TimerSubType.SPAWNER_BREAK ? "broke" : "placed", count.get() + "x", StringUtils.capitaliseAllWords(entityType.name().toLowerCase().replace("_", " ")));
                            });
                        }
                    });
                }

            }
        });
        this.remove(type);
    }

    public String getFactionId() {
        return this.factionId;
    }

    public String getUsername() {
        return this.username;
    }

    public enum TimerSubType {
        SELL_ALL,
        SELL_HAND,
        SPAWNER_BREAK,
        SPAWNER_PLACE;

        TimerSubType() {
        }
    }

    public enum TimerType {
        SELL,
        SPAWNER_EDIT;

        TimerType() {
        }
    }

    public class Timer {
        private long startTime;
        private long count;
        private Object extraData;

        public Timer(long startTime, long count, Object extraData) {
            this.startTime = startTime;
            this.count = count;
            this.extraData = extraData;
        }

        LogTimer.Timer increment(long amount) {
            this.count += amount;
            return this;
        }

        public boolean isReadyToLog(long expiration) {
            return System.currentTimeMillis() - this.startTime >= expiration;
        }

        public long getStartTime() {
            return this.startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getCount() {
            return this.count;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public Object getExtraData() {
            return this.extraData;
        }

        public void setExtraData(Object extraData) {
            this.extraData = extraData;
        }
    }
}

