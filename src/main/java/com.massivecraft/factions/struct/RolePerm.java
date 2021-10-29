/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang.StringUtils
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.struct;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public enum RolePerm {
    INVITE("Invite Members", "Grants the ability to Invite\nplayers to your faction.", Role.MODERATOR),
    KICK("Kick Members", "Grants the ability to Kick lower\n ranking faction members (REQ: Member+)", Role.MODERATOR),
    DEMOTE("Demote Members", "Grants the ability to Demote lower ranking\nmembers in your faction (REQ: Mod+)", Role.COLEADER),
    PROMOTE("Promote Members", "Grants the ability to Promote lower\nranking members in your faction.", Role.COLEADER),
    RELATIONS("Edit Relations", "Grants the ability to change\nrelationships with other factions.", Role.MODERATOR),
    SETHOME("/f sethome", "Grants the ability to set the faction home.", Role.MODERATOR),
    SETWARP("/f setwarp", "Grants the ability to set Faction and Ally Warps.", Role.MODERATOR),
    WITHDRAW("/f bank withdraw", "Grants the ability to Withdraw\nmoney from the faction bank.", Role.MODERATOR),
    DEPOSIT("/f bank deposit", "Grants the ability to Deposit\nmoney into the faction bank.", Role.RECRUIT),
    TP_NON_MEMBERS("TP Allies", "Grants the ability to Teleport\nnon-faction members into your land.", Role.COLEADER),
    CHANGE_NAME("Edit Faction Tag", "Grants the ability to change\nthe name of your faction.", Role.COLEADER),
    CHANGE_MEMBER_TITLE("Edit Member Titles", "Grants the ability to change Titles of\nlower ranking faction members (REQ: Member+)", Role.COLEADER),
    DISBAND("Disband", "Grants the ability to DISBAND the faction.", Role.COLEADER),
    UNCLAIMALL("Unclaim All", "Grants the ability to Unclaim ALL\n of your factions land.", Role.COLEADER),
    EDIT_PLAYER_PERMS("Edit /f perms", "Grants the ability to Edit\nFaction Player Permissions", Role.COLEADER),
    F_TNT_DEPOSIT("/f tnt deposit", "Grants the ability to deposit TNT into the /f tnt inventory.", Role.NORMAL, "deposit TNT"),
    F_TNT_WITHDRAW("/f tnt withdraw", "Grants the ability to withdraw TNT into the /f tnt inventory.", Role.MODERATOR, "withdraw TNT"),
    F_CHEST("/f chest", "Grants the ability to access the /f chest inventory.", Role.MODERATOR);

    private String display;
    private String description;
    private Role defaultRole;
    private String denyDescription;

    RolePerm() {
        this.display = CC.Red + StringUtils.capitaliseAllWords(this.name().replace("_", " ").toLowerCase());
        this.defaultRole = Role.MODERATOR;
    }

    RolePerm(String name, String description, Role role) {
        this(name, description, role, null);
    }

    RolePerm(String name, String description, Role role, String desc) {
        this.display = name;
        this.description = description;
        this.defaultRole = role;
        this.denyDescription = desc;
    }

    public List<String> getDescription() {
        ArrayList retr = Lists.newArrayList();
        if (this.description.contains("\n")) {
            for (String str : this.description.split(Pattern.quote("\n"))) {
                retr.add(ChatColor.GRAY + str.trim());
            }
        } else {
            retr.add(ChatColor.GRAY + this.description.trim());
        }
        return retr;
    }

    public String toString() {
        return this.name();
    }

    public String getDisplay() {
        return this.display;
    }

    public Role getDefaultRole() {
        return this.defaultRole;
    }

    public String getDenyDescription() {
        return this.denyDescription;
    }
}

