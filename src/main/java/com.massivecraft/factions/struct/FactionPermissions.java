/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package com.massivecraft.factions.struct;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FactionPermissions {
    public static boolean debug = false;
    private ConcurrentHashMap<FLocation, Map<UUID, Set<FactionPermission>>> playerPermissionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<FLocation, Map<String, Set<FactionPermission>>> factionChunkPermissionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<FLocation, Set<String>> factionEdittedChunkPermissions = new ConcurrentHashMap<>();
    private ConcurrentHashMap<FLocation, Set<UUID>> playerEdittedChunkPermissions = new ConcurrentHashMap<>();
    private Map<String, Set<FactionPermission>> factionDefaultPermissions = new HashMap<String, Set<FactionPermission>>();
    private Map<UUID, Set<FactionPermission>> defaultPlayerPermissions = new HashMap<UUID, Set<FactionPermission>>();

    public void storeChunkEditted(FLocation location, Faction fac) {
        Set str = this.factionEdittedChunkPermissions.computeIfAbsent(location, map -> new HashSet<>());
        str.add(fac.getId());
    }

    public void storeChunkEditted(FLocation location, FPlayer player) {
        Set str = this.playerEdittedChunkPermissions.computeIfAbsent(location, map -> new HashSet<>());
        str.add(player.getCachedUUID());
    }

    public Set<FactionPermission> getPermissions(Faction owner, FLocation location, Faction backup, FPlayer fplayer, String interactType) {
        boolean enemied;
        UUID uuid = fplayer.getCachedUUID();
        Map<UUID, Set<FactionPermission>> perms = this.playerPermissionMap.get(location);
        boolean bl = enemied = owner.getRelationTo(fplayer) == Relation.ENEMY;
        if (enemied) {
            return this.getAllowedDefaultPermissions();
        }
        if (perms != null && perms.size() > 0) {
            Set<FactionPermission> found = perms.get(uuid);
            if (debug) {
                //System.out.println("Returning playerMap: " + (found != null ? Arrays.toString((Object[])found.toArray((T[])new FactionPermission[found.size()])) : null) + " Size: " + perms.size() + " Key: " + Arrays.toString(perms.keySet().toArray(new UUID[perms.keySet().size()])));
            }
            if (found != null) {
                return found;
            }
            if (owner.getRelationTo(fplayer) == Relation.MEMBER && fplayer.getRole().isAtLeast(Role.NORMAL)) {
                return this.getFullAccessPermissions();
            }
            if (debug) {
                Bukkit.getLogger().info("Managed to find no permissions for " + fplayer.getNameAsync() + ", checking faction.");
            }
        } else if ((perms == null || perms.size() == 0) && owner.getRelationTo(fplayer) == Relation.MEMBER && fplayer.getRole().isAtLeast(Role.NORMAL)) {
            if (debug) {
                Bukkit.getLogger().info("Returning full list of permissions!");
            }
            return this.getFullAccessPermissions();
        }
        Map<String, Set<FactionPermission>> factionPermissions = this.factionChunkPermissionMap.get(location);
        if (factionPermissions != null && factionPermissions.size() > 0) {
            Set<FactionPermission> factionPerms = factionPermissions.get(backup.getId());
            if (fplayer.getRole() == Role.RECRUIT && fplayer.getFaction().isNormal()) {
                return new HashSet<>();
            }
            if (debug) {
                //System.out.println("Returning fac chunk perm: " + (factionPerms != null ? Arrays.toString((Object[])factionPerms.toArray((T[])new FactionPermission[factionPerms.size()])) : null) + " Size: " + factionPermissions.size());
            }
            if (factionPerms != null) {
                return factionPerms;
            }
        }
        if (owner.getRelationTo(fplayer) == Relation.MEMBER && fplayer.getRole() == Role.RECRUIT) {
            return new HashSet<>();
        }
        if (debug) {
            Bukkit.getLogger().info("Returning blank perms + CREEPER for " + fplayer.getNameAsync());
        }
        HashSet<FactionPermission> allowed = new HashSet<FactionPermission>();
        if (interactType != null && interactType.equals("interact")) {
            return this.getAllowedDefaultPermissions();
        }
        return allowed;
    }

    private Set<FactionPermission> getAllowedDefaultPermissions() {
        HashSet<FactionPermission> allowed = new HashSet<FactionPermission>();
        allowed.add(FactionPermission.CREEPER);
        allowed.add(FactionPermission.DISPENSE);
        allowed.add(FactionPermission.DROPPER);
        allowed.add(FactionPermission.HOPPER);
        return allowed;
    }

    public void updateClaimsWithDefaultPermissions(Faction faction) {
        Set<FactionPermission> newPermissions = this.factionDefaultPermissions.get(faction.getId());
        this.factionChunkPermissionMap.forEach((loc, map) -> {
            if (map.containsKey(faction.getId())) {
                Set<String> editted = this.factionEdittedChunkPermissions.get(loc);
                if (editted != null && editted.contains(faction.getId())) {
                    return;
                }
                map.put(faction.getId(), new HashSet<>(newPermissions));
            }
        });
    }

    public void updateClaimsWithDefaultPermissions(FPlayer fplayer) {
        UUID uuid = fplayer.getCachedUUID();
        Set<FactionPermission> newPermissions = this.defaultPlayerPermissions.get(uuid);
        this.playerPermissionMap.forEach((loc, map) -> {
            if (map.containsKey(uuid)) {
                Set<UUID> editted = this.playerEdittedChunkPermissions.get(loc);
                if (editted != null && editted.contains(uuid)) {
                    return;
                }
                map.put(uuid, new HashSet<>(newPermissions));
            }
        });
    }

    public boolean hasChunkSpecificPerms(FLocation location) {
        return this.playerPermissionMap.containsKey(location) || this.factionChunkPermissionMap.containsKey(location);
    }

    public boolean isDefaultPermission(FLocation location, Faction fac) {
        Set<String> editted = this.factionEdittedChunkPermissions.get(location);
        return editted == null || !editted.contains(fac.getId());
    }

    public boolean isDefaultPermission(FLocation location, FPlayer fPlayer) {
        Set<UUID> editted = this.playerEdittedChunkPermissions.get(location);
        return editted == null || !editted.contains(fPlayer.getCachedUUID());
    }

    private Set<FactionPermission> getFactionPermissions() {
        HashSet<FactionPermission> set = new HashSet<FactionPermission>();
        set.add(FactionPermission.FULL);
        return set;
    }

    public void cleanupPermissions(FLocation location) {
        this.factionChunkPermissionMap.remove(location);
        this.playerPermissionMap.remove(location);
        this.factionEdittedChunkPermissions.remove(location);
        this.playerEdittedChunkPermissions.remove(location);
    }

    public void removePermissions(FLocation location, FPlayer player) {
        Set<UUID> modified;
        Map<UUID, Set<FactionPermission>> permissions = this.playerPermissionMap.get(location);
        if (permissions != null) {
            permissions.remove(player.getCachedUUID());
        }
        if ((modified = this.playerEdittedChunkPermissions.get(location)) != null) {
            modified.remove(player.getCachedUUID());
        }
    }

    public void removePermissions(FLocation location, Faction fac) {
        Set<String> modified;
        Map<String, Set<FactionPermission>> permissions = this.factionChunkPermissionMap.get(location);
        if (permissions != null) {
            permissions.remove(fac.getId());
        }
        if ((modified = this.factionEdittedChunkPermissions.get(location)) != null) {
            modified.remove(fac.getId());
        }
    }

    public void createDefaultPermissions(FLocation location, FPlayer player) {
        Map permissions = this.playerPermissionMap.computeIfAbsent(location, e -> new HashMap<>());
        Set<UUID> editted = this.playerEdittedChunkPermissions.get(location);
        Set currentPerms = (Set) permissions.get(player.getCachedUUID());
        if (currentPerms != null && editted != null && editted.contains(player.getCachedUUID())) {
            Bukkit.getLogger().info("Current perms set for " + player.getNameAsync() + ", not overwritting");
            return;
        }
        Set<FactionPermission> perms = this.defaultPlayerPermissions.get(player.getCachedUUID());
        if (perms == null) {
            perms = this.getDefaultPermissions();
        }
        permissions.put(player.getCachedUUID(), new HashSet<FactionPermission>(perms));
    }

    public void createDefaultPermissions(FLocation location, Faction fac) {
        Map permissions = this.factionChunkPermissionMap.computeIfAbsent(location, e -> new HashMap<>());
        Set<String> edittedPermissions = this.factionEdittedChunkPermissions.get(location);
        Set currentPerms = (Set) permissions.get(fac.getId());
        if (currentPerms != null && edittedPermissions != null && edittedPermissions.contains(fac.getId())) {
            return;
        }
        Set<FactionPermission> perms = this.factionDefaultPermissions.get(fac.getId());
        if (perms == null) {
            perms = this.getDefaultPermissions();
            this.factionDefaultPermissions.put(fac.getId(), perms);
        }
        permissions.put(fac.getId(), new HashSet<FactionPermission>(perms));
    }

    public Set<FactionPermission> getFullAccessPermissions() {
        HashSet<FactionPermission> perms = new HashSet<FactionPermission>();
        perms.add(FactionPermission.FULL);
        return perms;
    }

    public Set<FactionPermission> getDefaultPermissions() {
        HashSet<FactionPermission> perms = new HashSet<FactionPermission>();
        perms.add(FactionPermission.OTHER);
        perms.add(FactionPermission.CHEST);
        return perms;
    }

    public void cleanupPermissions(UUID uuid) {
        this.playerPermissionMap.forEach((loc, map) -> map.remove(uuid));
        this.defaultPlayerPermissions.remove(uuid);
        this.playerEdittedChunkPermissions.values().forEach(str -> str.remove(uuid));
    }

    public void cleanupPermissions(Faction faction) {
        this.factionChunkPermissionMap.forEach((loc, map) -> map.remove(faction.getId()));
        this.factionDefaultPermissions.remove(faction.getId());
        this.factionEdittedChunkPermissions.values().forEach(str -> str.remove(faction.getId()));
    }

    public boolean hasPermission(Faction owning, FLocation location, Faction backup, FPlayer uuid, FactionPermission permission, String interactType) {
        Set<FactionPermission> perms = this.getPermissions(owning, location, backup, uuid, interactType);
        if (debug) {
            Bukkit.getLogger().info("Returning: " + perms.toString() + " for " + uuid.getNameAsync() + " inside " + owning.getTag() + " Needed: " + permission + " for " + interactType);
        }
        return perms.contains(permission) || perms.contains(FactionPermission.FULL);
    }

    public ConcurrentHashMap<FLocation, Map<UUID, Set<FactionPermission>>> getPlayerPermissionMap() {
        return this.playerPermissionMap;
    }

    public ConcurrentHashMap<FLocation, Map<String, Set<FactionPermission>>> getFactionChunkPermissionMap() {
        return this.factionChunkPermissionMap;
    }

    public ConcurrentHashMap<FLocation, Set<String>> getFactionEdittedChunkPermissions() {
        return this.factionEdittedChunkPermissions;
    }

    public ConcurrentHashMap<FLocation, Set<UUID>> getPlayerEdittedChunkPermissions() {
        return this.playerEdittedChunkPermissions;
    }

    public Map<String, Set<FactionPermission>> getFactionDefaultPermissions() {
        return this.factionDefaultPermissions;
    }

    public Map<UUID, Set<FactionPermission>> getDefaultPlayerPermissions() {
        return this.defaultPlayerPermissions;
    }
}

