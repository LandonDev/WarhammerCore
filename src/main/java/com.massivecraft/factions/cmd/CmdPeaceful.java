/*
 * Decompiled with CFR 0.145.
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;

public class CmdPeaceful
        extends FCommand {
    public CmdPeaceful() {
        this.aliases.add("peaceful");
        this.requiredArgs.add("faction tag");
        this.permission = Permission.SET_PEACEFUL.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        String change;
        Faction faction = this.argAsFaction(0);
        if (faction == null) {
            return;
        }
        if (faction.isPeaceful()) {
            change = "removed peaceful status from";
            faction.setPeaceful(false);
        } else {
            change = "granted peaceful status to";
            faction.setPeaceful(true);
        }
        for (FPlayer fplayer : FPlayers.i.getOnline()) {
            if (fplayer.getFaction() == faction) {
                fplayer.msg((this.fme == null ? "A server admin" : this.fme.describeToAsync(fplayer, true)) + "<i> has " + change + " your faction.");
                continue;
            }
            fplayer.msg((this.fme == null ? "A server admin" : this.fme.describeToAsync(fplayer, true)) + "<i> has " + change + " the faction \"" + faction.getTag(fplayer) + "<i>\".");
        }
    }
}

