/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FPlayerLeaveEvent
        extends Event
        implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    FPlayer FPlayer;
    Faction Faction;
    boolean cancelled = false;
    private PlayerLeaveReason reason;

    public FPlayerLeaveEvent(FPlayer p, Faction f, PlayerLeaveReason r) {
        this.FPlayer = p;
        this.Faction = f;
        this.reason = r;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public PlayerLeaveReason getReason() {
        return this.reason;
    }

    public FPlayer getFPlayer() {
        return this.FPlayer;
    }

    public Faction getFaction() {
        return this.Faction;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean c) {
        if (this.reason == PlayerLeaveReason.DISBAND || this.reason == PlayerLeaveReason.RESET) {
            this.cancelled = false;
            return;
        }
        this.cancelled = c;
    }

    public enum PlayerLeaveReason {
        KICKED,
        DISBAND,
        RESET,
        JOINOTHER,
        LEAVE

    }

}

