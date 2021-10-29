/*
 * Decompiled with CFR 0.145.
 */
package com.massivecraft.factions.struct;

public enum ChatMode {
    MOD(4, "mod chat"),
    TRUCE(3, "truce chat"),
    FACTION(2, "faction chat"),
    ALLIANCE(1, "alliance chat"),
    PUBLIC(0, "public chat");

    public final int value;
    public final String nicename;

    ChatMode(int value, String nicename) {
        this.value = value;
        this.nicename = nicename;
    }

    public boolean isAtLeast(ChatMode role) {
        return this.value >= role.value;
    }

    public boolean isAtMost(ChatMode role) {
        return this.value <= role.value;
    }

    public String toString() {
        return this.nicename;
    }

    public ChatMode getNext() {
        if (this == PUBLIC) {
            return ALLIANCE;
        }
        if (this == ALLIANCE) {
            return TRUCE;
        }
        if (this == TRUCE) {
            return FACTION;
        }
        if (this == FACTION) {
            return MOD;
        }
        return PUBLIC;
    }
}

