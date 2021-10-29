/*
 * Decompiled with CFR 0.145.
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Relation;

public class CmdRelationNeutral
        extends FRelationCommand {
    public CmdRelationNeutral() {
        this.aliases.add("neutral");
        this.aliases.add("NEUTRAL");
        this.targetRelation = Relation.NEUTRAL;
    }
}

