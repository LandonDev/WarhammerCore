/*
 * Decompiled with CFR 0.145.
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Relation;

public class CmdRelationEnemy
        extends FRelationCommand {
    public CmdRelationEnemy() {
        this.aliases.add("enemy");
        this.aliases.add("ENEMY");
        this.targetRelation = Relation.ENEMY;
    }
}

