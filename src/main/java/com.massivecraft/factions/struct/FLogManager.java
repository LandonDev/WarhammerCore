/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.JSONUtils
 *  net.minecraft.util.com.google.common.reflect.TypeToken
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package com.massivecraft.factions.struct;

import com.google.common.reflect.TypeToken;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.FactionLogs;
import com.massivecraft.factions.struct.LogTimer;
import java.io.File;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import com.massivecraft.factions.util.JSONUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class FLogManager {
    private Map<String, FactionLogs> factionLogMap = new ConcurrentHashMap<String, FactionLogs>();
    private File logFile;
    private Type logToken = new TypeToken<ConcurrentHashMap<String, FactionLogs>>() {
    }.getType();
    private Map<UUID, LogTimer> logTimers = new ConcurrentHashMap<UUID, LogTimer>();
    private boolean saving = false;

    public void log(Faction faction, FLogType type, String... arguments) {
        FactionLogs logs = this.factionLogMap.computeIfAbsent(faction.getId(), n -> new FactionLogs());
        logs.log(type, arguments);
    }

    public void loadLogs(P plugin) {
        try {
            this.logFile = new File(plugin.getDataFolder(), "factionLogs.json");
            if (!this.logFile.exists()) {
                this.logFile.createNewFile();
            }
            this.factionLogMap = (Map) JSONUtils.fromJson(this.logFile, this.logToken);
            if (this.factionLogMap == null) {
                this.factionLogMap = new ConcurrentHashMap<String, FactionLogs>();
            }
            this.factionLogMap.forEach((factionId, factionLogs) -> {
                Faction faction = Factions.i.get(factionId);
                if (faction != null && faction.isNormal()) {
                    factionLogs.checkExpired();
                    if (!factionLogs.isEmpty()) return;
                    this.factionLogMap.remove(factionId);
                    return;
                }
                Bukkit.getLogger().info("Removing dead faction logs for " + factionId + "!");
                this.factionLogMap.remove(factionId);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        long delay = TimeUnit.SECONDS.toMillis(15L);
        long sellDelay = TimeUnit.SECONDS.toMillis(30L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(P.p, () -> {
            if (this.saving) {
                Bukkit.getLogger().info("Ignoring saveLogs scheduler due to saving==true!");
                return;
            }
            this.logTimers.forEach((uuid, logTimer) -> {
                if (logTimer == null || logTimer.getFactionId() == null) {
                    this.logTimers.remove(uuid);
                    return;
                }
                Faction faction = Factions.i.get(logTimer.getFactionId());
                if (faction == null) {
                    this.logTimers.remove(uuid);
                    Bukkit.getLogger().info("Null faction for logs " + logTimer.getFactionId());
                    return;
                }
                logTimer.forEach((type, subSet) -> subSet.forEach((subType, timer) -> {
                    if (!timer.isReadyToLog(type == LogTimer.TimerType.SELL ? sellDelay : delay)) return;
                    logTimer.pushLogs(faction, type);
                }));
                if (!logTimer.isEmpty()) return;
                this.logTimers.remove(uuid);
            });
        }, 20L, 400L);
    }

    public void pushPendingLogs(LogTimer.TimerType type) {
        Faction faction = null;
        Iterator<Map.Entry<UUID, LogTimer>> iterator = this.getLogTimers().entrySet().iterator();
        do {
            if (!iterator.hasNext()) {
                if (type != null) return;
                this.getLogTimers().clear();
                return;
            }
            Map.Entry<UUID, LogTimer> timer = iterator.next();
            LogTimer logTimer = timer.getValue();
            if (faction == null) {
                faction = Factions.i.get(logTimer.getFactionId());
            }
            if (type != null) {
                Map timers = logTimer.get(type);
                if (timers == null || faction == null) continue;
                logTimer.pushLogs(faction, type);
                continue;
            }
            if (faction == null) continue;
            Faction finalFaction = faction;
            logTimer.keySet().forEach(timerType -> logTimer.pushLogs(finalFaction, timerType));
            logTimer.clear();
        } while (true);
    }

    public void saveLogs() {
        if (this.saving) {
            Bukkit.getLogger().info("Ignoring saveLogs due to saving==true!");
            return;
        }
        this.saving = true;
        try {
            this.pushPendingLogs(null);
        } catch (Exception e) {
            Bukkit.getLogger().info("error pushing pending logs: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            JSONUtils.saveJSONToFile(this.logFile, this.factionLogMap, this.logToken);
        } catch (Exception e) {
            Bukkit.getLogger().info("ERRRO SAVING JSON LOGS: " + e.getMessage());
            e.printStackTrace();
        }
        this.saving = false;
    }

    public Map<String, FactionLogs> getFactionLogMap() {
        return this.factionLogMap;
    }

    public Map<UUID, LogTimer> getLogTimers() {
        return this.logTimers;
    }
}
