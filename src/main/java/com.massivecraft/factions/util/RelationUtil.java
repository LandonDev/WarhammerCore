/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.util;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.ChatColor;

public class RelationUtil {
    public static String describeThatToMeAsync(RelationParticipator that, RelationParticipator me, boolean ucfirst) {
        String ret = "";
        Faction thatFaction = RelationUtil.getFaction(that);
        if (thatFaction == null) {
            return "ERROR";
        }
        Faction myFaction = RelationUtil.getFaction(me);
        if (that instanceof Faction) {
            ret = me instanceof FPlayer && myFaction == thatFaction ? "your faction" : thatFaction.getTag();
        } else if (that instanceof FPlayer) {
            FPlayer fplayerthat = (FPlayer) that;
            ret = that == me ? "you" : (thatFaction == myFaction ? fplayerthat.getNameAndTitleAsync() : fplayerthat.getNameAndTagAsync());
        }
        if (ucfirst) {
            ret = TextUtil.upperCaseFirst(ret);
        }
        return "" + RelationUtil.getColorOfThatToMe(that, me) + ret;
    }

    public static String describeThatToMeAsync(RelationParticipator that, RelationParticipator me) {
        return RelationUtil.describeThatToMeAsync(that, me, false);
    }

    public static Relation getRelationTo(RelationParticipator me, RelationParticipator that) {
        return RelationUtil.getRelationTo(that, me, false);
    }

    public static Relation getRelationTo(RelationParticipator me, RelationParticipator that, boolean ignorePeaceful) {
        Faction fthat = RelationUtil.getFaction(that);
        if (fthat == null) {
            return Relation.NEUTRAL;
        }
        Faction fme = RelationUtil.getFaction(me);
        if (fme == null) {
            return Relation.NEUTRAL;
        }
        if (!fthat.isNormal() || !fme.isNormal()) {
            return Relation.NEUTRAL;
        }
        if (fthat.equals(fme)) {
            return Relation.MEMBER;
        }
        if (!ignorePeaceful && (fme.isPeaceful() || fthat.isPeaceful())) {
            return Relation.NEUTRAL;
        }
        if (fme.getRelationWish(fthat).value >= fthat.getRelationWish(fme).value) {
            return fthat.getRelationWish(fme);
        }
        return fme.getRelationWish(fthat);
    }

    public static Faction getFaction(RelationParticipator rp) {
        if (rp instanceof Faction) {
            return (Faction) rp;
        }
        if (rp instanceof FPlayer) {
            return ((FPlayer) rp).getFaction();
        }
        return null;
    }

    public static ChatColor getColorOfThatToMe(RelationParticipator that, RelationParticipator me) {
        Faction thatFaction = RelationUtil.getFaction(that);
        if (thatFaction != null) {
            if (thatFaction.isPeaceful() && thatFaction != RelationUtil.getFaction(me)) {
                return Conf.colorPeaceful;
            }
            if (thatFaction.isSafeZone() && thatFaction != RelationUtil.getFaction(me)) {
                return Conf.colorPeaceful;
            }
            if (thatFaction.isWarZone() && thatFaction != RelationUtil.getFaction(me)) {
                return Conf.colorWar;
            }
        }
        return RelationUtil.getRelationTo(that, me).getColor();
    }
}

