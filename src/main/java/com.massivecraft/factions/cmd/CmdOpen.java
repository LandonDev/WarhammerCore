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

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;

public class CmdOpen
        extends FCommand {
    public CmdOpen() {
        this.aliases.add("open");
        this.optionalArgs.put("yes/no", "flip");
        this.permission = Permission.OPEN.node;
        this.disableOnLock = false;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = true;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
            if (!CmdOpen.this.payForCommand(Conf.econCostOpen, "to open or close the faction", "for opening or closing the faction")) {
                return;
            }
            CmdOpen.this.myFaction.setOpen(CmdOpen.this.argAsBool(0, !CmdOpen.this.myFaction.getOpen()));
            String open = CmdOpen.this.myFaction.getOpen() ? "open" : "closed";
            CmdOpen.this.myFaction.msg("%s<i> changed the faction to <h>%s<i>.", CmdOpen.this.fme.describeToAsync(CmdOpen.this.myFaction, true), open);
        });
    }

}

