/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.ChatColor;

public class CmdChat
        extends FCommand {
    public CmdChat() {
        this.aliases.add("c");
        this.aliases.add("chat");
        this.optionalArgs.put("mode", "next");
        this.permission = Permission.CHAT.node;
        this.disableOnLock = false;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (!Conf.factionOnlyChat) {
            this.msg("<b>The built in chat chat channels are disabled on this server.");
            return;
        }
        String modeString = this.argAsString(0);
        ChatMode modeTarget = this.fme.getChatMode().getNext();
        if (modeString != null) {
            modeString.toLowerCase();
            if (modeString.startsWith("p")) {
                modeTarget = ChatMode.PUBLIC;
            } else if (modeString.startsWith("a")) {
                modeTarget = ChatMode.ALLIANCE;
            } else if (modeString.startsWith("t")) {
                modeTarget = ChatMode.TRUCE;
            } else if (modeString.startsWith("f")) {
                modeTarget = ChatMode.FACTION;
            } else if (modeString.startsWith("m")) {
                modeTarget = ChatMode.MOD;
            } else {
                this.msg("<b>Unrecognised chat mode... <i>Please use either 'a','f','t','m' or 'p'");
                return;
            }
        }
        this.fme.setChatMode(modeTarget);
        if (this.fme.getChatMode() == ChatMode.PUBLIC) {
            this.msg(ChatColor.YELLOW + "Public chat mode.");
        } else if (this.fme.getChatMode() == ChatMode.ALLIANCE) {
            this.msg(ChatColor.YELLOW + "Alliance only chat mode.");
        } else if (this.fme.getChatMode() == ChatMode.TRUCE) {
            this.msg(ChatColor.YELLOW + "Truce only chat mode.");
        } else if (this.fme.getChatMode() == ChatMode.MOD) {
            this.msg(ChatColor.YELLOW + "Mod only chat mode.");
        } else {
            this.msg(ChatColor.YELLOW + "Faction only chat mode.");
        }
    }
}

