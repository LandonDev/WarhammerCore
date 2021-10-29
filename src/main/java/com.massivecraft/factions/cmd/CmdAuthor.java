/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

public class CmdAuthor
        extends FCommand {
    public CmdAuthor() {
        this.aliases.add("author");
    }

    @Override
    public void perform() {
        this.msg(ChatColor.WHITE + "This plugin was made by" + ChatColor.DARK_RED + "Dragon Development Team");
    }
}

