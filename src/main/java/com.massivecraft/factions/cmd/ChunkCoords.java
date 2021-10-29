/*
 * Decompiled with CFR 0.145.
 */
package com.massivecraft.factions.cmd;

class ChunkCoords {
    public int x;
    public int z;

    ChunkCoords(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public boolean equals(Object o) {
        if (o instanceof ChunkCoords) {
            return ((ChunkCoords) o).x == this.x && ((ChunkCoords) o).z == this.z;
        }
        return false;
    }
}

