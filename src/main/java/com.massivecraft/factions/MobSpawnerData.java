/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.block.CreatureSpawner
 *  org.bukkit.entity.EntityType
 */
package com.massivecraft.factions;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

final class MobSpawnerData {
    private EntityType spawnerType;
    private int spawnerCount;

    public MobSpawnerData(CreatureSpawner creatureSpawner) {
        this.spawnerType = creatureSpawner.getSpawnedType();
        this.spawnerCount = 1;
    }

    public EntityType getSpawnerType() {
        return this.spawnerType;
    }

    public int getSpawnerCount() {
        return this.spawnerCount;
    }
}

