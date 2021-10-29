/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.iface;

import com.massivecraft.factions.struct.Relation;
import org.bukkit.ChatColor;

public interface RelationParticipator {
    String describeToAsync(RelationParticipator var1);

    String describeToAsync(RelationParticipator var1, boolean var2);

    Relation getRelationTo(RelationParticipator var1);

    Relation getRelationTo(RelationParticipator var1, boolean var2);

    ChatColor getColorTo(RelationParticipator var1);
}

