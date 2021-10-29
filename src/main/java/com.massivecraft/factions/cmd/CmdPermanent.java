/*
 * Decompiled with CFR 0.145.
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;

public class CmdPermanent
        extends FCommand {
    public CmdPermanent() {
        this.aliases.add("permanent");
        this.requiredArgs.add("faction tag");
        this.permission = Permission.SET_PERMANENT.node;
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
        if (faction.isPermanent()) {
            change = "removed permanent status from";
            faction.setPermanent(false);
        } else {
            change = "added permanent status to";
            faction.setPermanent(true);
        }
        P.p.log((this.fme == null ? "A server admin" : this.fme.getNameAsync()) + " " + change + " the faction \"" + faction.getTag() + "\".");
        for (FPlayer fplayer : FPlayers.i.getOnline()) {
            if (fplayer.getFaction() == faction) {
                fplayer.msg((this.fme == null ? "A server admin" : this.fme.describeToAsync(fplayer, true)) + "<i> " + change + " your faction.");
                continue;
            }
            fplayer.msg((this.fme == null ? "A server admin" : this.fme.describeToAsync(fplayer, true)) + "<i> " + change + " the faction \"" + faction.getTag(fplayer) + "\".");
        }
    }
}

