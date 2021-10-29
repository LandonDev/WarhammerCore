/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LandUnclaimAllEvent
        extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Faction faction;
    private FPlayer fplayer;

    public LandUnclaimAllEvent(Faction f, FPlayer p) {
        this.faction = f;
        this.fplayer = p;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Faction getFaction() {
        return this.faction;
    }

    public String getFactionId() {
        return this.faction.getId();
    }

    public String getFactionTag() {
        return this.faction.getTag();
    }

    public FPlayer getFPlayer() {
        return this.fplayer;
    }

    public Player getPlayer() {
        return this.fplayer.getPlayer();
    }
}

