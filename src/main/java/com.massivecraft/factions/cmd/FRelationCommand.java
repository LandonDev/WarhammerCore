/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.cosmicpvp.cosmicutils.utils.TimeUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.PluginManager
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FactionRelationEvent;
import com.massivecraft.factions.struct.*;
import com.massivecraft.factions.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public abstract class FRelationCommand
        extends FCommand {
    public Relation targetRelation;

    public FRelationCommand() {
        this.requiredArgs.add("faction tag");
        this.permission = Permission.RELATION.node;
        this.disableOnLock = true;
        this.requiredRolePermission = RolePerm.RELATIONS;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction them = this.argAsFaction(0);
        if (them == null) {
            return;
        }
        if (!them.isNormal() || !this.myFaction.isNormal()) {
            this.msg("<b>Nope! You can't.");
            return;
        }
        if (them == this.myFaction) {
            this.msg("<b>Nope! You can't declare a relation to yourself :)");
            return;
        }
        if (this.myFaction.getRelationWish(them) == this.targetRelation) {
            this.msg("<b>You already have that relation wish set with %s.", them.getTag());
            return;
        }
        if (!this.payForCommand(this.targetRelation.getRelationCost(), "to change a relation wish", "for changing a relation wish")) {
            return;
        }
        if (this.hasMaxRelations(this.myFaction, this.targetRelation)) {
            return;
        }
        if (this.hasMaxRelations(them, this.targetRelation)) {
            return;
        }
        Relation oldRelation = this.myFaction.getRelationTo(them, true);
        if (this.targetRelation == Relation.ALLY || this.targetRelation == Relation.TRUCE) {
            int requestsAvailable = this.myFaction.getRequestsAvailable(this.targetRelation);
            if (requestsAvailable == 0) {
                long timeSinceLast = this.myFaction.getTimeSinceRelationUse(this.targetRelation);
                long timeTillValid = Conf.allyTruceCooldown - timeSinceLast;
                if (timeTillValid > 0L) {
                    this.sender.sendMessage(CC.RedB + "(!) " + CC.Red + "You must wait " + CC.RedB + TimeUtils.formatSeconds((timeTillValid / 1000L)) + CC.Red + " to " + this.targetRelation.name().toLowerCase() + " another faction!");
                    return;
                }
                Bukkit.getLogger().info("Time Since Last: " + timeSinceLast + ", tillValid: " + timeTillValid);
            }
            if (them.getRequestsAvailable(this.targetRelation) == 0) {
                this.sender.sendMessage(CC.RedB + "(!) " + CC.Red + them.getTag() + " has used all of their " + this.targetRelation.name().toLowerCase() + " changes for the day!");
                return;
            }
        }
        this.myFaction.setRelationWish(them, this.targetRelation);
        Relation currentRelation = this.myFaction.getRelationTo(them, true);
        ChatColor currentRelationColor = currentRelation.getColor();
        if (oldRelation != Relation.ENEMY && (currentRelation == Relation.ENEMY || this.targetRelation == Relation.ENEMY)) {
            FactionWrapper theirWrapper;
            PermissionManager.get().removePermissions(this.myFaction, them);
            PermissionManager.get().removePermissions(them, this.myFaction);
            FactionWrapper wrapper = FactionWrappers.get(this.myFaction);
            if (wrapper != null) {
                wrapper.removeAllFactionAccess(them, true);
            }
            if ((theirWrapper = FactionWrappers.get(them)) != null) {
                theirWrapper.removeAllFactionAccess(this.myFaction, true);
            }
            Bukkit.getLogger().info("Cleaning up permissions for " + this.myFaction.getTag() + " and " + them.getTag() + " due to ENEMY status.");
        }
        if (this.targetRelation.value == currentRelation.value) {
            FactionRelationEvent relationEvent = new FactionRelationEvent(this.myFaction, them, oldRelation, currentRelation);
            Bukkit.getServer().getPluginManager().callEvent(relationEvent);
            if (them.announceRelationshipRequests) {
                them.msg(ChatColor.YELLOW + "Your faction is now " + currentRelationColor + this.targetRelation.toString() + ChatColor.YELLOW + " to " + currentRelationColor + this.myFaction.getTag());
            }
            this.myFaction.msg(ChatColor.YELLOW.toString() + "Your faction is now " + currentRelationColor + this.targetRelation.toString() + ChatColor.YELLOW + " to " + currentRelationColor + them.getTag());
            if (this.targetRelation == Relation.ENEMY || this.targetRelation == Relation.TRUCE || this.targetRelation == Relation.ALLY || this.targetRelation == Relation.NEUTRAL) {
                P.p.logFactionEvent(this.myFaction, FLogType.RELATION_CHANGE, this.fme.getNameAsync(), this.targetRelation.getColor() + this.targetRelation.name(), oldRelation.getColor() + them.getTag());
                P.p.logFactionEvent(them, FLogType.RELATION_CHANGE, oldRelation.getColor() + this.fme.getNameAsync(), this.targetRelation.getColor() + this.targetRelation.name(), "your faction");
            }
            if (Conf.allyTruceDailyLimit > 0) {
                if (this.targetRelation == Relation.TRUCE) {
                    them.setTrucesAvailable(them.getTrucesAvailable() - 1);
                    this.myFaction.setTrucesAvailable(this.myFaction.getTrucesAvailable() - 1);
                } else if (this.targetRelation == Relation.ALLY) {
                    them.setAlliesAvailable(them.getAlliesAvailable() - 1);
                    this.myFaction.setAlliesAvailable(this.myFaction.getAlliesAvailable() - 1);
                }
            }
        } else {
            if (them.announceRelationshipRequests) {
                them.msg(currentRelationColor + this.myFaction.getTag() + ChatColor.YELLOW + " wishes to be your " + this.targetRelation.getColor() + this.targetRelation.toString());
            }
            if (them.announceRelationshipRequests) {
                them.msg(ChatColor.YELLOW + "Type <c>/" + Conf.baseCommandAliases.get(0) + " " + this.targetRelation + " " + this.myFaction.getTag() + ChatColor.YELLOW + " to accept.");
            }
            this.myFaction.msg(this.targetRelation.getColor() + "" + ChatColor.BOLD + "(!) " + currentRelationColor + them.getTag() + this.targetRelation.getColor() + " have been informed that you wish to be " + this.targetRelation.getColor() + this.targetRelation);
            if (this.targetRelation == Relation.ENEMY || this.targetRelation == Relation.TRUCE || this.targetRelation == Relation.ALLY || this.targetRelation == Relation.NEUTRAL) {
                P.p.logFactionEvent(this.myFaction, FLogType.RELATION_CHANGE, this.fme.getNameAsync(), this.targetRelation.getColor() + this.targetRelation.name(), oldRelation.getColor() + them.getTag());
            }
        }
        if (!this.targetRelation.isNeutral() && them.isPeaceful()) {
            if (them.announceRelationshipRequests) {
                them.msg(ChatColor.YELLOW + "This will have no effect while your faction is peaceful.");
            }
            this.myFaction.msg(ChatColor.YELLOW + "This will have no effect while their faction is peaceful.");
        }
        if (!this.targetRelation.isNeutral() && this.myFaction.isPeaceful()) {
            if (them.announceRelationshipRequests) {
                them.msg(ChatColor.YELLOW + "This will have no effect while their faction is peaceful.");
            }
            this.myFaction.msg(ChatColor.YELLOW + "This will have no effect while your faction is peaceful.");
        }
    }

    public boolean hasMaxRelations(Faction faction, Relation relation) {
        boolean ally = relation == Relation.ALLY;
        boolean truce = relation == Relation.TRUCE;
        if (!truce && !ally) {
            return false;
        }
        if (truce && Conf.truceLimit > 0 && faction.getRelationCount(relation) >= Conf.truceLimit) {
            this.msg("<i>Failed to set relation wish. " + faction.getTag() + " can only have %1$s %2$s.", Conf.truceLimit, relation.getPlural().toLowerCase());
            return true;
        }
        if (ally && Conf.allyLimit > 0 && faction.getRelationCount(relation) >= Conf.allyLimit) {
            this.msg("<i>Failed to set relation wish. " + faction.getTag() + " can only have %1$s %2$s.", Conf.allyLimit, relation.getPlural().toLowerCase());
            return true;
        }
        return false;
    }
}

