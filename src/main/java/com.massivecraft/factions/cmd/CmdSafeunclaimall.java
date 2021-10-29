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

public class CmdSafeunclaimall
        extends FCommand {
    public CmdSafeunclaimall() {
        this.aliases.add("safeunclaimall");
        this.aliases.add("safedeclaimall");
        this.permission = Permission.MANAGE_SAFE_ZONE.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
        this.setHelpShort("Unclaim all safezone land");
    }

    @Override
    public void perform() {
        Board.unclaimAll(Factions.i.getSafeZone().getId());
        this.msg("<i>You unclaimed ALL safe zone land.");
        P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
            if (Conf.logLandUnclaims) {
                P.p.log(CmdSafeunclaimall.this.fme.getNameAsync() + " unclaimed all safe zones.");
            }
        });
    }

}

