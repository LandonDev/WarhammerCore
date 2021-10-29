/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions;

import com.massivecraft.factions.util.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class FLocation {
    public long lastReferenced = 0L;
    private String worldName = "world";
    private int x = 0;
    private int z = 0;

    public FLocation() {
    }

    public FLocation(String worldName, int x, int z) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
    }

    public FLocation(Location location) {
        this(location.getWorld().getName(), FLocation.blockToChunk(location.getBlockX()), FLocation.blockToChunk(location.getBlockZ()));
    }

    public Chunk getChunk() {
        return Bukkit.getWorld(worldName).getChunkAt(x, z);
    }

    public FLocation(Player player) {
        this(player.getLocation());
    }

    public FLocation(FPlayer fplayer) {
        this(fplayer.getPlayer());
    }

    public FLocation(Block block) {
        this(block.getLocation());
    }

    public static int blockToChunk(int blockVal) {
        return blockVal >> 4;
    }

    public static int blockToRegion(int blockVal) {
        return blockVal >> 9;
    }

    public static int chunkToRegion(int chunkVal) {
        return chunkVal >> 5;
    }

    public static int chunkToBlock(int chunkVal) {
        return chunkVal << 4;
    }

    public static int regionToBlock(int regionVal) {
        return regionVal << 9;
    }

    public static int regionToChunk(int regionVal) {
        return regionVal << 5;
    }

    public static HashSet<FLocation> getArea(FLocation from, FLocation to) {
        HashSet<FLocation> ret = new HashSet<FLocation>();
        for (long x : MiscUtil.range(from.getX(), to.getX())) {
            for (long z : MiscUtil.range(from.getZ(), to.getZ())) {
                ret.add(new FLocation(from.getWorldName(), (int) x, (int) z));
            }
        }
        return ret;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public World getWorld() {
        return Bukkit.getWorld(this.worldName);
    }

    public long getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public long getZ() {
        return this.z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getCoordString() {
        return "" + this.x + "," + this.z;
    }

    public String getRawCoordString() {
        return "" + this.x * 16 + "," + this.z * 16;
    }

    public String toString() {
        return "[" + this.getWorldName() + "," + this.getCoordString() + "]";
    }

    public String toRawString() {
        return "[" + this.getWorldName() + "," + this.getRawCoordString() + "]";
    }

    public Chunk toChunk() {
        return this.getWorld().getChunkAt(this.x, this.z);
    }

    public FLocation getRelative(int dx, int dz) {
        return new FLocation(this.worldName, this.x + dx, this.z + dz);
    }

    public double getDistanceTo(FLocation that) {
        double dx = that.x - this.x;
        double dz = that.z - this.z;
        return Math.sqrt(dx * dx + dz * dz);
    }

    public double getDistanceSquaredTo(FLocation that) {
        double dx = that.x - this.x;
        double dz = that.z - this.z;
        return dx * dx + dz * dz;
    }

    public Set<FLocation> getCircle(double radius) {
        double radiusSquared = radius * radius;
        LinkedHashSet<FLocation> ret = new LinkedHashSet<FLocation>();
        if (radius <= 0.0) {
            return ret;
        }
        int xfrom = (int) Math.floor((double) this.x - radius);
        int xto = (int) Math.ceil((double) this.x + radius);
        int zfrom = (int) Math.floor((double) this.z - radius);
        int zto = (int) Math.ceil((double) this.z + radius);
        for (int x = xfrom; x <= xto; ++x) {
            for (int z = zfrom; z <= zto; ++z) {
                FLocation potential = new FLocation(this.worldName, x, z);
                if (!(this.getDistanceSquaredTo(potential) <= radiusSquared)) continue;
                ret.add(potential);
            }
        }
        return ret;
    }

    public int hashCode() {
        return (this.x << 9) + this.z + (this.worldName != null ? this.worldName.hashCode() : 0);
    }

    public String formatXAndZ(String splitter) {
        return FLocation.chunkToBlock(this.x) + "x" + splitter + " " + FLocation.chunkToBlock(this.z) + "z";
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FLocation)) {
            return false;
        }
        FLocation that = (FLocation) obj;
        return this.x == that.x && this.z == that.z && (Objects.equals(this.worldName, that.worldName));
    }
}

