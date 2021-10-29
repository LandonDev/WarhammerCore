/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.bukkit.Bukkit
 *  org.bukkit.craftbukkit.libs.com.google.gson.Gson
 *  org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder
 *  org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken
 */
package com.massivecraft.factions.struct;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionManager {
    Type token = new TypeToken<ConcurrentHashMap<String, FactionPermissions>>() {
    }.getType();
    private Map<String, FactionPermissions> permissionMap = new HashMap<String, FactionPermissions>();
    private Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
    private File permFile;

    public static PermissionManager get() {
        return P.getP().getPermissionManager();
    }

    public FactionPermissions getPermissions(Faction faction) {
        return this.permissionMap.computeIfAbsent(faction.getId(), e -> new FactionPermissions());
    }

    public boolean removePermissions(Faction faction, Faction toRemove) {
        FactionPermissions perms = this.permissionMap.get(faction.getId());
        if (perms != null) {
            String id = toRemove.getId();
            if (perms.getFactionDefaultPermissions().remove(id) != null) {
                Bukkit.getLogger().info("Removing " + toRemove.getTag() + " from " + faction.getTag() + " permission list.");
            }
            perms.getFactionChunkPermissionMap().values().forEach(map -> map.remove(toRemove.getId()));
            for (FPlayer pl : toRemove.getFPlayers()) {
                UUID uuid = pl.getCachedUUID();
                perms.getPlayerPermissionMap().values().forEach(map -> map.remove(uuid));
                perms.getDefaultPlayerPermissions().remove(uuid);
            }
            return true;
        }
        Bukkit.getLogger().info("Unable to find any permissions for " + faction.getTag() + "!");
        return false;
    }

    public boolean removePermissions(Faction toRemove, FLocation flocation, UUID uuid) {
        Map<UUID, Set<FactionPermission>> map;
        FactionPermissions perms = this.getPermissions(toRemove);
        if (perms != null && (map = perms.getPlayerPermissionMap().get(flocation)) != null) {
            return map.remove(uuid) != null;
        }
        return false;
    }

    public void loadPermissionMap() throws IOException {
        this.permFile = new File(P.getP().getDataFolder(), "permissions.json");
        if (!this.permFile.exists()) {
            this.permFile.createNewFile();
        }
        this.permissionMap = this.gson.fromJson(new FileReader(this.permFile), this.token);
        if (this.permissionMap == null) {
            this.permissionMap = new ConcurrentHashMap<>();
        }
    }

    public void savePermissions() throws IOException {
        FileWriter writer = new FileWriter(this.permFile);
        String str = this.gson.toJson(this.permissionMap, this.token);
        writer.write(str);
        writer.flush();
        writer.close();
    }

    public Map<String, FactionPermissions> getPermissionMap() {
        return this.permissionMap;
    }

}

