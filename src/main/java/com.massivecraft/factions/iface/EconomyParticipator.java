/*
 * Decompiled with CFR 0.145.
 */
package com.massivecraft.factions.iface;

public interface EconomyParticipator
        extends RelationParticipator {
    String getAccountId();

    void msg(String var1, Object... var2);
}

