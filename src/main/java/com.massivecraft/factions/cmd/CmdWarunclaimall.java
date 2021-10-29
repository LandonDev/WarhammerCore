/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;

public class CmdWarunclaimall
        extends FCommand {
    public CmdWarunclaimall() {
        this.aliases.add("warunclaimall");
        this.aliases.add("wardeclaimall");
        this.permission = Permission.MANAGE_WAR_ZONE.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
        this.setHelpShort("unclaim all warzone land");
    }

    @Override
    public void perform() {
        Board.unclaimAll(Factions.i.getWarZone().getId());
        this.msg("<i>You unclaimed ALL war zone land.");
        P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
            if (Conf.logLandUnclaims) {
                P.p.log(CmdWarunclaimall.this.fme.getNameAsync() + " unclaimed all war zones.");
            }
        });
    }

}

