/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 */
package com.massivecraft.factions.util;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.P;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.logging.Level;

public abstract class SpiralTask
        implements Runnable {
    private transient World world = null;
    private transient boolean readyToGo = false;
    private transient int taskID = -1;
    private transient int limit = 0;
    private transient int x = 0;
    private transient int z = 0;
    private transient boolean isZLeg = false;
    private transient boolean isNeg = false;
    private transient int length = -1;
    private transient int current = 0;

    public SpiralTask(FLocation fLocation, int radius) {
        this.limit = (radius - 1) * 2;
        this.world = Bukkit.getWorld(fLocation.getWorldName());
        if (this.world == null) {
            P.p.log(Level.WARNING, "[SpiralTask] A valid world must be specified!");
            this.stop();
            return;
        }
        this.x = (int) fLocation.getX();
        this.z = (int) fLocation.getZ();
        this.readyToGo = true;
        this.setTaskID(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(P.p, this, 2L, 2L));
    }

    private static long now() {
        return System.currentTimeMillis();
    }

    public abstract boolean work();

    public final FLocation currentFLocation() {
        return new FLocation(this.world.getName(), this.x, this.z);
    }

    public final Location currentLocation() {
        return new Location(this.world, (double) FLocation.chunkToBlock(this.x), 65.0, (double) FLocation.chunkToBlock(this.z));
    }

    public final int getX() {
        return this.x;
    }

    public final int getZ() {
        return this.z;
    }

    public final void setTaskID(int ID) {
        if (ID == -1) {
            this.stop();
        }
        this.taskID = ID;
    }

    @Override
    public final void run() {
        if (!this.valid() || !this.readyToGo) {
            return;
        }
        this.readyToGo = false;
        if (!this.insideRadius()) {
            return;
        }
        long loopStartTime = SpiralTask.now();
        while (SpiralTask.now() < loopStartTime + 20L) {
            if (!this.work()) {
                this.finish();
                return;
            }
            if (this.moveToNext()) continue;
            return;
        }
        this.readyToGo = true;
    }

    public final boolean moveToNext() {
        if (!this.valid()) {
            return false;
        }
        if (this.current < this.length) {
            ++this.current;
            if (!this.insideRadius()) {
                return false;
            }
        } else {
            this.current = 0;
            this.isZLeg ^= true;
            if (this.isZLeg) {
                this.isNeg ^= true;
                ++this.length;
            }
        }
        if (this.isZLeg) {
            this.z += this.isNeg ? -1 : 1;
        } else {
            this.x += this.isNeg ? -1 : 1;
        }
        return true;
    }

    public final boolean insideRadius() {
        boolean inside;
        boolean bl = inside = this.current < this.limit;
        if (!inside) {
            this.finish();
        }
        return inside;
    }

    public void finish() {
        this.stop();
    }

    public final void stop() {
        if (!this.valid()) {
            return;
        }
        this.readyToGo = false;
        Bukkit.getServer().getScheduler().cancelTask(this.taskID);
        this.taskID = -1;
    }

    public final boolean valid() {
        return this.taskID != -1;
    }
}

