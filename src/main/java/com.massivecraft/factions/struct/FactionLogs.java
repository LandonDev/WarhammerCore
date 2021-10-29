/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.struct;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class FactionLogs {
    private static final transient int MAX_LOG_SIZE = 60;
    public static transient SimpleDateFormat format = new SimpleDateFormat("MM/dd hh:mmaa");
    private Map<FLogType, LinkedList<FactionLog>> mostRecentLogs = new ConcurrentHashMap<FLogType, LinkedList<FactionLog>>();

    public void log(FLogType type, String... arguments) {
        int maxLog;
        if (type.getRequiredArgs() > arguments.length) {
            Bukkit.getLogger().info("INVALID ARGUMENT COUNT MET: " + type.getRequiredArgs() + " REQUIRED: ");
            Thread.dumpStack();
            return;
        }
        LinkedList logs = this.mostRecentLogs.computeIfAbsent(type, lists -> new LinkedList<>());
        logs.add(new FactionLog(System.currentTimeMillis(), Lists.newArrayList((String[]) arguments)));
        int n = maxLog = type == FLogType.F_POINTS ? 200 : 60;
        if (logs.size() > maxLog) {
            logs.pop();
        }
    }

    public boolean isEmpty() {
        return this.mostRecentLogs.isEmpty();
    }

    public void checkExpired() {
        long duration = TimeUnit.DAYS.toMillis(7L);
        ArrayList toRemove = Lists.newArrayList();
        this.mostRecentLogs.forEach((logType, logs) -> {
            if (logs == null) {
                toRemove.add(logType);
                return;
            }
            if (logType == FLogType.F_POINTS) {
                return;
            }
            Iterator iter = logs.iterator();
            while (iter.hasNext()) {
                try {
                    FactionLog log = (FactionLog) iter.next();
                    if (log != null && !log.isExpired(duration)) continue;
                    iter.remove();
                } catch (Exception e) {
                    Bukkit.getLogger().info("ERROR TRYING TO GET next FACTION LOG: " + e.getMessage());
                    try {
                        iter.remove();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            if (logs.size() <= 0) {
                toRemove.add(logType);
            }
        });
        toRemove.forEach(rem -> this.mostRecentLogs.remove(rem));
    }

    public Map<FLogType, LinkedList<FactionLog>> getMostRecentLogs() {
        return this.mostRecentLogs;
    }

    class FactionLog {
        private long t;
        private List<String> a;

        public FactionLog(long t, List<String> a) {
            this.t = t;
            this.a = a;
        }

        public boolean isExpired(long duration) {
            return System.currentTimeMillis() - this.t >= duration;
        }

        public String getLogLine(FLogType type, boolean timestamp) {
            Object[] args = this.a.toArray(new String[0]);
            String timeFormat = "";
            if (timestamp && (timeFormat = format.format(this.t)).startsWith("0")) {
                timeFormat = timeFormat.substring(1);
            }
            return String.format(ChatColor.translateAlternateColorCodes('&', type.getMsg()), args) + (timestamp ? ChatColor.GRAY + " - " + timeFormat : "");
        }
    }

}

