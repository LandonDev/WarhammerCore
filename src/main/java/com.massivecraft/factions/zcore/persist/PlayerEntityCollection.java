/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.craftbukkit.libs.com.google.gson.Gson
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.zcore.persist;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class PlayerEntityCollection<E extends Entity>
        extends EntityCollection<E> {
    public PlayerEntityCollection(Class<E> entityClass, Collection<E> entities, Map<String, E> id2entity, File file, Gson gson) {
        super(entityClass, entities, id2entity, file, gson, true);
    }

    public E get(OfflinePlayer player) {
        return this.get(player.getUniqueId().toString());
    }

    public Set<E> getOnline() {
        HashSet entities = new HashSet<>();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            entities.add(this.get(player.getUniqueId().toString()));
        }
        return entities;
    }
}

