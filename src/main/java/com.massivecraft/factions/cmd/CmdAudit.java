/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.FAuditMenu;
import org.bukkit.entity.Player;

public class CmdAudit
        extends FCommand {
    public CmdAudit() {
        this.aliases.add("audit");
        this.aliases.add("logs");
        this.aliases.add("log");
        this.disableOnLock = false;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
        this.senderMustBeColeader = false;
        this.errorOnToManyArgs = false;
    }

    @Override
    public void perform() {
        Faction faction = this.args.size() == 1 && this.sender.isOp() ? this.argAsFaction(0) : this.myFaction;
        new FAuditMenu((Player) this.sender, faction).open((Player) this.sender);
    }
}

