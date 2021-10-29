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

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;

public class CmdDeinvite
        extends FCommand {
    public CmdDeinvite() {
        this.aliases.add("deinvite");
        this.aliases.add("deinv");
        this.requiredArgs.add("player name");
        this.permission = Permission.DEINVITE.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = true;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
            FPlayer you = CmdDeinvite.this.argAsBestFPlayerMatch(0);
            if (you == null) {
                return;
            }
            if (you.getFaction() == CmdDeinvite.this.myFaction) {
                CmdDeinvite.this.msg("%s<i> is already a member of %s", you.getNameAsync(), CmdDeinvite.this.myFaction.getTag());
                CmdDeinvite.this.msg("<i>You might want to: %s", CmdDeinvite.this.p.cmdBase.cmdKick.getUseageTemplate(false));
                return;
            }
            CmdDeinvite.this.myFaction.deinvite(you);
            CmdDeinvite.this.myFaction.msg("%s<i> revoked %s's<i> invitation.", CmdDeinvite.this.fme.describeToAsync(CmdDeinvite.this.myFaction), you.describeToAsync(CmdDeinvite.this.myFaction));
        });
    }

}

