/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import org.bukkit.ChatColor;

public class CmdGlobal
        extends FCommand {
    public CmdGlobal() {
        this.aliases.add("global");
        this.optionalArgs.put("on/off", "");
        this.permission = Permission.GLOBAL.node;
    }

    @Override
    public void perform() {
        if (this.args.size() > 0) {
            if (this.args.get(0).equalsIgnoreCase("off")) {
                this.msg(ChatColor.YELLOW + "Global chat is now hidden.");
                this.fme.setGlobalChatHidden(true);
            }
            if (this.args.get(0).equalsIgnoreCase("on")) {
                this.msg(ChatColor.YELLOW + "Global chat is now shown.");
                this.fme.setGlobalChatHidden(false);
            }
            return;
        }
        if (!this.fme.hasGlobalChatHidden()) {
            this.msg(ChatColor.YELLOW + "Global chat is now hidden.");
            this.fme.setGlobalChatHidden(true);
        } else {
            this.msg(ChatColor.YELLOW + "Global chat is now shown.");
            this.fme.setGlobalChatHidden(false);
        }
    }
}

