/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.Server
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;

public class CmdAutoClaim
        extends FCommand {
    public CmdAutoClaim() {
        this.aliases.add("autoclaim");
        this.optionalArgs.put("faction", "your");
        this.permission = Permission.AUTOCLAIM.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        P.getP().getServer().getScheduler().runTask(P.getP(), new Runnable() {
            int claimed = 0;

            @Override
            public void run() {
                Faction forFaction = CmdAutoClaim.this.argAsFaction(0, CmdAutoClaim.this.myFaction);
                if (forFaction == null || forFaction == CmdAutoClaim.this.fme.getAutoClaimFor()) {
                    CmdAutoClaim.this.fme.setAutoClaimFor(null);
                    CmdAutoClaim.this.msg("<i>Auto-claiming of land disabled.");
                    return;
                }
                if (!CmdAutoClaim.this.fme.canClaimForFaction(forFaction)) {
                    if (CmdAutoClaim.this.myFaction == forFaction) {
                        CmdAutoClaim.this.msg("<b>You must be <h>%s<b> to claim land.", Role.MODERATOR.getNicename());
                    } else {
                        CmdAutoClaim.this.msg("<b>You can't claim land for <h>%s<b>.", forFaction.describeToAsync(CmdAutoClaim.this.fme));
                    }
                    return;
                }
                CmdAutoClaim.this.fme.setAutoClaimFor(forFaction);
                CmdAutoClaim.this.msg("<i>Now auto-claiming land for <h>%s<i>.", forFaction.describeToAsync(CmdAutoClaim.this.fme));
                if (CmdClaim.chunkCoordsPendingClaim.contains(CmdAutoClaim.this.me.getLocation().getChunk().getX() + "," + CmdAutoClaim.this.me.getLocation().getChunk().getZ())) {
                    CmdAutoClaim.this.msg("<b>There is a pending /f claim on the chunk you're standing in.");
                    return;
                }
                if (CmdAutoClaim.this.fme.attemptClaimAsync(forFaction, CmdAutoClaim.this.me.getLocation(), true)) {
                    P.p.logFactionEvent(forFaction, FLogType.CHUNK_CLAIMS, CmdAutoClaim.this.fme.getNameAsync(), CC.GreenB + "CLAIMED", "1", new FLocation(CmdAutoClaim.this.me.getLocation()).formatXAndZ(","));
                }
            }
        });
    }

}

