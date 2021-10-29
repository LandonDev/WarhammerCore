/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CmdToggleRequests
        extends FCommand {
    public CmdToggleRequests() {
        this.aliases.add("togglerequests");
        this.aliases.add("tr");
        this.aliases.add("rel");
        this.permission = Permission.RELATION.node;
        this.disableOnLock = false;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = true;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        FPlayer fp;
        if (this.sender instanceof Player && (fp = FPlayers.i.get((OfflinePlayer) this.sender)).getFaction() != null) {
            if (fp.getFaction().announceRelationshipRequests) {
                fp.getFaction().announceRelationshipRequests = false;
                this.msg("<i>Verbose faction relation requests <red>DISABLED.");
            } else {
                fp.getFaction().announceRelationshipRequests = true;
                this.msg("<i>Verbose faction relation requests <green>ENABLED.");
            }
        }
    }
}

