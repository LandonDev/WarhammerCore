/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.google.common.collect.Lists
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 */
package com.massivecraft.factions.struct;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import com.massivecraft.factions.Conf;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import java.util.List;

public enum Role {
    ADMIN(4, "admin", ChatColor.RED, "Admin", DyeColor.RED, null),
    COLEADER(3, "co-leader", ChatColor.YELLOW, "Co-Leader", DyeColor.YELLOW, null),
    MODERATOR(2, "moderator", ChatColor.AQUA, "Moderator", DyeColor.LIGHT_BLUE, null),
    NORMAL(1, "normal member", ChatColor.GREEN, "Member", DyeColor.LIME, null),
    RECRUIT(0, "recruit", ChatColor.WHITE, "Recruit", DyeColor.WHITE, Lists.newArrayList());

    public final int value;
    public final String nicename;
    public ChatColor color;
    public String display;
    public DyeColor paneColor;
    private List<FactionPermission> defaultPermission;

    Role(int value, String nicename, ChatColor color, String display, DyeColor dye, List<FactionPermission> perms) {
        this.value = value;
        this.defaultPermission = perms;
        this.nicename = nicename;
        this.color = color;
        this.display = color + CC.Bold + display;
        this.paneColor = dye;
    }

    Role(int value, String nicename, ChatColor color) {
        this.value = value;
        this.nicename = nicename;
    }

    public boolean hasPermission(FactionPermission permission) {
        if (this == RECRUIT) {
            boolean perm = this.defaultPermission.contains(permission);
            return perm;
        }
        return true;
    }

    public String getRoleCapitalized() {
        return this.nicename.replace(Character.toString(nicename.charAt(0)), Character.toString(nicename.charAt(0)).toUpperCase());
    }

    public boolean isAtLeast(Role role) {
        return this.value >= role.value;
    }

    public boolean isAtMost(Role role) {
        return this.value <= role.value;
    }

    public boolean isCoLeader() {
        return this == COLEADER || this == ADMIN;
    }

    public String toString() {
        return this.name();
    }

    public String getPrefix() {
        if (this == ADMIN) {
            return Conf.prefixAdmin;
        }
        if (this == COLEADER) {
            return Conf.prefixColeader;
        }
        if (this == MODERATOR) {
            return Conf.prefixMod;
        }
        if (this == RECRUIT) {
            return Conf.prefixRecruit;
        }
        return "";
    }

    public String getNicename() {
        return this.nicename;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public String getDisplay() {
        return this.display;
    }

    public DyeColor getPaneColor() {
        return this.paneColor;
    }
}

