/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  net.minecraft.server.v1_7_R4.EntityPlayer
 *  net.minecraft.server.v1_7_R4.PlayerConnection
 *  org.bukkit.Bukkit
 *  org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.zcore.persist;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PlayerEntity
        extends Entity {
    public transient Player bukkitPlayer = null;
    protected transient UUID cachedUUID = null;

    public boolean isCachedAsOnline() {
        return this.bukkitPlayer != null;
    }

    public UUID getCachedUUID() {
        return this.cachedUUID;
    }

    public void setCachedUUID(UUID uuid) {
        this.cachedUUID = uuid;
    }

    public Player getPlayer() {

        if (this.bukkitPlayer != null) {
            //good
            return this.bukkitPlayer;
        }

        if (getCachedUUID() != null) {
            //get player from uuid
            this.bukkitPlayer = Bukkit.getPlayer(getCachedUUID());
        } else if (this.getId().length() > 16 && this.getId().contains("-")) {
            try {
                this.bukkitPlayer = Bukkit.getPlayer(String.valueOf(UUID.fromString(this.getId())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return this.bukkitPlayer;



/*
        if (this.bukkitPlayer != null) {
            if (!((CraftPlayer) this.bukkitPlayer).getHandle().playerConnection.isDisconnected())
            this.bukkitPlayer = null;
        }
        if (this.getCachedUUID() != null) {
            this.bukkitPlayer = Bukkit.getPlayer(String.valueOf(this.getCachedUUID()));
        } else if (this.getId().length() > 16 && this.getId().contains("-")) {
            try {
                this.bukkitPlayer = Bukkit.getPlayer(String.valueOf(UUID.fromString(this.getId())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.bukkitPlayer;

 */
    }

    public void clearCachedPlayer() {
        this.bukkitPlayer = null;
    }

    public boolean isOnline() {
        return this.getPlayer() != null && this.getPlayer().isOnline();
    }

    public boolean isOnlineAndVisibleTo(Player player) {
        Player target = this.getPlayer();
        return this.isOnline() && target != null && player.canSee(target);
    }

    public boolean isOffline() {
        return !this.isOnline();
    }

    public void sendMessage(String msg) {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        player.sendMessage(msg);
    }

    public void sendMessage(List<String> msgs) {
        for (String msg : msgs) {
            this.sendMessage(msg);
        }
    }
}

