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

public class CmdBoom
        extends FCommand {
    public CmdBoom() {
        this.aliases.add("noboom");
        this.optionalArgs.put("on/off", "flip");
        this.permission = Permission.NO_BOOM.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = true;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
            if (!CmdBoom.this.myFaction.isPeaceful()) {
                CmdBoom.this.fme.msg("<b>This command is only usable by factions which are specially designated as peaceful.");
                return;
            }
            if (!CmdBoom.this.payForCommand(Conf.econCostNoBoom, "to toggle explosions", "for toggling explosions")) {
                return;
            }
            CmdBoom.this.myFaction.setPeacefulExplosionsEnabled(CmdBoom.this.argAsBool(0, !CmdBoom.this.myFaction.getPeacefulExplosionsEnabled()));
            String enabled = CmdBoom.this.myFaction.noExplosionsInTerritory() ? "disabled" : "enabled";
            CmdBoom.this.myFaction.msg("%s<i> has " + enabled + " explosions in your faction's territory.", CmdBoom.this.fme.describeToAsync(CmdBoom.this.myFaction));
        });
    }

}

