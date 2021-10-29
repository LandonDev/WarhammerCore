/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 */
package com.massivecraft.factions.struct;

import com.massivecraft.factions.Conf;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public enum Relation {
    MEMBER(4, "&lMEMBER"),
    ALLY(3, "&lALLY", "Allies"),
    TRUCE(2, "&lTRUCE", "Truces"),
    NEUTRAL(1, "&lNEUTRAL", "Neutrals"),
    ENEMY(0, "&lENEMY", "Enemies");

    public final int value;
    public final String nicename;
    private String plural;

    Relation(int value, String nicename, String plural) {
        this.value = value;
        this.nicename = ChatColor.translateAlternateColorCodes('&', nicename);
        this.plural = plural;
    }

    Relation(int value, String nicename) {
        this(value, nicename, null);
    }

    public String toString() {
        return this.nicename;
    }

    public boolean isMember() {
        return this == MEMBER;
    }

    public boolean isAlly() {
        return this == ALLY;
    }

    public boolean isNeutral() {
        return this == NEUTRAL;
    }

    public boolean isEnemy() {
        return this == ENEMY;
    }

    public boolean isTruce() {
        return this == TRUCE;
    }

    public boolean isAtLeast(Relation relation) {
        return this.value >= relation.value;
    }

    public boolean isAtMost(Relation relation) {
        return this.value <= relation.value;
    }

    public DyeColor getDyeColor() {
        return this == ENEMY ? DyeColor.RED : (this == ALLY ? DyeColor.PURPLE : (this == TRUCE ? DyeColor.CYAN : DyeColor.WHITE));
    }

    public ChatColor getColor() {
        if (this == MEMBER) {
            return Conf.colorMember;
        }
        if (this == ALLY) {
            return Conf.colorAlly;
        }
        if (this == NEUTRAL) {
            return Conf.colorNeutral;
        }
        if (this == TRUCE) {
            return Conf.colorTruce;
        }
        return Conf.colorEnemy;
    }

    public boolean confDenyBuild(boolean online) {
        if (this.isMember()) {
            return false;
        }
        if (online) {
            if (this.isEnemy()) {
                return Conf.territoryEnemyDenyBuild;
            }
            if (this.isAlly()) {
                return Conf.territoryAllyDenyBuild;
            }
            if (this.isTruce()) {
                return Conf.territoryTruceDenyBuild;
            }
            return Conf.territoryDenyBuild;
        }
        if (this.isEnemy()) {
            return Conf.territoryEnemyDenyBuildWhenOffline;
        }
        if (this.isAlly()) {
            return Conf.territoryAllyDenyBuildWhenOffline;
        }
        if (this.isTruce()) {
            return Conf.territoryTruceDenyBuildWhenOffline;
        }
        return Conf.territoryDenyBuildWhenOffline;
    }

    public boolean confPainBuild(boolean online) {
        if (this.isMember()) {
            return false;
        }
        if (online) {
            if (this.isEnemy()) {
                return Conf.territoryEnemyPainBuild;
            }
            if (this.isAlly()) {
                return Conf.territoryAllyPainBuild;
            }
            if (this.isTruce()) {
                return Conf.territoryTrucePainBuild;
            }
            return Conf.territoryPainBuild;
        }
        if (this.isEnemy()) {
            return Conf.territoryEnemyPainBuildWhenOffline;
        }
        if (this.isAlly()) {
            return Conf.territoryAllyPainBuildWhenOffline;
        }
        if (this.isTruce()) {
            return Conf.territoryTrucePainBuildWhenOffline;
        }
        return Conf.territoryPainBuildWhenOffline;
    }

    public boolean confDenyUseage() {
        if (this.isMember()) {
            return false;
        }
        if (this.isEnemy()) {
            return Conf.territoryEnemyDenyUseage;
        }
        if (this.isAlly()) {
            return Conf.territoryAllyDenyUseage;
        }
        if (this.isTruce()) {
            return Conf.territoryTruceDenyUseage;
        }
        return Conf.territoryDenyUseage;
    }

    public double getRelationCost() {
        if (this.isEnemy()) {
            return Conf.econCostEnemy;
        }
        if (this.isAlly()) {
            return Conf.econCostAlly;
        }
        if (this.isTruce()) {
            return Conf.econCostTruce;
        }
        return Conf.econCostNeutral;
    }

    public String getPlural() {
        return this.plural;
    }
}

