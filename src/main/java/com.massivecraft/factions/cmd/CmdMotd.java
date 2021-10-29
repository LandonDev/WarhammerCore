/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.arkhamnetwork.Arkkit.patches.chat_filter.ChatUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.listeners.FactionsPlayerListener;
import com.massivecraft.factions.struct.Role;
import org.bukkit.ChatColor;

public class CmdMotd
        extends FCommand {
    public CmdMotd() {
        this.aliases.add("motd");
        this.disableOnLock = true;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = true;
        this.senderMustBeAdmin = false;
        this.senderMustBeColeader = false;
        this.errorOnToManyArgs = false;
    }

    @Override
    public void perform() {
        if (this.myFaction == null || !this.myFaction.isNormal()) {
            this.sender.sendMessage(ChatColor.RED + "You must be the Co-Leader of a faction to do that!");
            return;
        }
        Role role = this.fme.getRole();
        if (this.args.isEmpty()) {
            if (this.myFaction.getMotd() == null || this.myFaction.getMotd().equals("")) {
                this.sender.sendMessage(ChatColor.RED + "No Faction MOTD set!");
                return;
            }
            this.myFaction.sendMOTD(this.fme, false);
            return;
        }
        if (!role.isAtLeast(Role.COLEADER)) {
            this.sender.sendMessage(ChatColor.RED + "You must be atleast Co-Leader of a faction to update the MOTD!");
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (String arg : this.args) {
            builder.append(arg).append(" ");
        }
        String done = builder.toString().trim();
        if (done.length() >= 64) {
            this.sender.sendMessage(ChatColor.RED + "Faction MOTD must be less then 64 characters.");
            return;
        }
        this.sender.sendMessage(ChatColor.GREEN + "Faction MOTD updated!");
        this.myFaction.updateMOTD(done);
    }
}

