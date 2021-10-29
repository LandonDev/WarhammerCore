/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.massivecraft.factions.event;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Relation;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FactionRelationEvent
        extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Faction fsender;
    private Faction ftarget;
    private Relation foldrel;
    private Relation frel;

    public FactionRelationEvent(Faction sender, Faction target, Relation oldrel, Relation rel) {
        this.fsender = sender;
        this.ftarget = target;
        this.foldrel = oldrel;
        this.frel = rel;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Relation getOldRelation() {
        return this.foldrel;
    }

    public Relation getRelation() {
        return this.frel;
    }

    public Faction getFaction() {
        return this.fsender;
    }

    public Faction getTargetFaction() {
        return this.ftarget;
    }
}

