/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.util;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Role;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.ListIterator;

public class AutoLeaveProcessTask
        extends BukkitRunnable {
    private final transient ListIterator<FPlayer> iterator;
    private final transient double toleranceMillis;
    private transient boolean readyToGo = false;
    private transient boolean finished = false;

    public AutoLeaveProcessTask() {
        ArrayList fplayers = new ArrayList<>(FPlayers.i.get());
        this.iterator = fplayers.listIterator();
        this.toleranceMillis = Conf.autoLeaveAfterDaysOfInactivity * 24.0 * 60.0 * 60.0 * 1000.0;
        this.readyToGo = true;
        this.finished = false;
    }

    public void run() {
        if (Conf.autoLeaveAfterDaysOfInactivity <= 0.0 || (double) Conf.autoLeaveRoutineMaxMillisecondsPerTick <= 0.0) {
            this.stop();
            return;
        }
        if (!this.readyToGo) {
            return;
        }
        this.readyToGo = false;
        long loopStartTime = System.currentTimeMillis();
        while (this.iterator.hasNext()) {
            Faction faction;
            long now = System.currentTimeMillis();
            if (now > loopStartTime + (long) Conf.autoLeaveRoutineMaxMillisecondsPerTick) {
                this.readyToGo = true;
                return;
            }
            final FPlayer fplayer = this.iterator.next();
            if (!fplayer.isOffline() || !((double) (now - fplayer.getLastLoginTime()) > this.toleranceMillis)) continue;
            if (Conf.logFactionLeave || Conf.logFactionKick) {
                P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> P.p.log("Player " + fplayer.getNameAsync() + " was auto-removed due to inactivity."));
            }
            if (fplayer.getRole() == Role.ADMIN && fplayer.getFaction() != null) {
                fplayer.getFaction().promoteNewLeader(fplayer);
            }
            fplayer.leave(false);
            this.iterator.remove();
            fplayer.detach();
        }
        this.stop();
    }

    public void stop() {
        this.readyToGo = false;
        this.finished = true;
        this.cancel();
    }

    public boolean isFinished() {
        return this.finished;
    }

}

