/*
 * Decompiled with CFR 0.145.
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Relation;

public class CmdRelationAlly
        extends FRelationCommand {
    public CmdRelationAlly() {
        this.aliases.add("ally");
        this.aliases.add("ALLY");
        this.targetRelation = Relation.ALLY;
    }
}

