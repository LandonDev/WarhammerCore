/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.craftbukkit.libs.com.google.gson.Gson
 *  org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions;

import com.google.common.reflect.TypeToken;
import com.massivecraft.factions.zcore.persist.PlayerEntityCollection;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class FPlayers
        extends PlayerEntityCollection<FPlayer> {
    public static FPlayers i = new FPlayers();
    P p = P.p;

    private FPlayers() {
        super(FPlayer.class, new CopyOnWriteArrayList<>(), new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER), new File(P.p.getDataFolder(), "players.json"), P.p.gson);
        this.setCreative(true);
    }

    @Override
    public Type getMapType() {
        return new TypeToken<Map<String, FPlayer>>() {
        }.getType();
    }

    public void clean() {
        P.getP().getServer().getScheduler().runTaskAsynchronously(this.p, () -> {
            for (FPlayer fplayer : FPlayers.this.get()) {
                if (Factions.i.exists(fplayer.getFactionId())) continue;
                FPlayers.this.p.log("Reset faction data (invalid faction) for player " + fplayer.getNameAsync());
                fplayer.resetFactionData();
            }
        });
    }

    public void cleanSync() {
        for (FPlayer fplayer : this.get()) {
            if (Factions.i.exists(fplayer.getFactionId())) continue;
            this.p.log("Reset faction data (invalid faction) for player " + fplayer.getNameAsync());
            fplayer.resetFactionData();
        }
    }

    @Override
    public FPlayer get(String id) {
        FPlayer fp = super.get(id);
        if (fp == null) {
            fp = this.create(id);
        }
        return fp;
    }

}

