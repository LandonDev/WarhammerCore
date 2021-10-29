/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.util;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;

public class AutoLeaveTask
        implements Runnable {
    private static AutoLeaveProcessTask task;
    double rate = Conf.autoLeaveRoutineRunsEveryXMinutes;

    public static void setTask(AutoLeaveProcessTask task) {
        AutoLeaveTask.task = task;
    }

    @Override
    public synchronized void run() {
        if (task != null && !task.isFinished()) {
            return;
        }
        assert task != null;
        task.runTaskTimer(P.p, 1L, 1L);
        if (this.rate != Conf.autoLeaveRoutineRunsEveryXMinutes) {
            P.p.startAutoLeaveTask(true);
        }
    }
}

