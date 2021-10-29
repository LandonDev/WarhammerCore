/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  net.minecraft.util.org.apache.commons.lang3.StringUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.SpiralTask;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

public class CmdOwner
        extends FCommand {
    public CmdOwner() {
        this.aliases.add("owner");
        this.optionalArgs.put("player name", "you");
        this.optionalArgs.put("radius", "1");
        this.permission = Permission.OWNER.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        boolean hasBypass = this.fme.isAdminBypassing();
        if (!hasBypass && !this.assertHasFaction()) {
            return;
        }
        if (!Conf.ownedAreasEnabled) {
            this.fme.msg("<b>Sorry, but owned areas are disabled on this server.");
            return;
        }
        if (!hasBypass && Conf.ownedAreasLimitPerFaction > 0 && this.myFaction.getCountOfClaimsWithOwners() >= Conf.ownedAreasLimitPerFaction) {
            this.fme.msg("<b>Sorry, but you have reached the server's <h>limit of %d <b>owned areas per faction.", Conf.ownedAreasLimitPerFaction);
            return;
        }
        if (!hasBypass && !this.assertMinRole(Conf.ownedAreasModeratorsCanSet ? Role.MODERATOR : Role.ADMIN)) {
            return;
        }
        final FLocation flocation = new FLocation(this.fme);
        Faction factionHere = Board.getFactionAt(flocation);
        if (factionHere != this.myFaction) {
            if (!hasBypass) {
                this.fme.msg("<b>This land is not claimed by your faction, so you can't set ownership of it.");
                return;
            }
            if (!factionHere.isNormal()) {
                this.fme.msg("<b>This land is not claimed by a faction. Ownership is not possible.");
                return;
            }
        }
        final int radius = this.args.size() > 1 ? this.argAsInt(1, 1) : (StringUtils.isNumeric(this.argAsString(0)) ? this.argAsInt(0, 1) : 1);
        System.out.println("Radius: " + radius);
        FPlayer target = this.fme;
        if (radius > 1 && this.args.size() > 1) {
            target = this.argAsBestFPlayerMatch(0);
        } else if (this.args.size() == 1 && radius <= 1) {
            target = this.argAsBestFPlayerMatch(0);
        }
        if (target == null) {
            this.fme.sendMessage(ChatColor.RED + "No player found.");
            return;
        }
        final String playerName = target.getNameAsync();
        if (target.getFaction() != this.myFaction) {
            this.fme.msg("%s<i> is not a member of this faction.", playerName);
            return;
        }
        final Faction mine = this.fme.getFaction();
        if (!mine.isNormal()) {
            this.me.sendMessage(ChatColor.RED + "You need a faction to do this.");
            return;
        }
        if (radius > 20) {
            this.sendMessage(ChatColor.RED + "You can only claim 20 chunks at a time.");
            return;
        }
        if (radius > 1) {
            final FPlayer fplayercopy = target;
            new BukkitRunnable() {
                public void run() {
                    final AtomicInteger complete = new AtomicInteger();
                    final AtomicInteger removed = new AtomicInteger();
                    new SpiralTask(flocation, radius) {

                        @Override
                        public boolean work() {
                            Faction current = Board.getFactionAt(this.currentFLocation());
                            if (current != mine) {
                                return true;
                            }
                            if (CmdOwner.this.args.isEmpty() && CmdOwner.this.myFaction.doesLocationHaveOwnersSet(this.currentFLocation())) {
                                CmdOwner.this.myFaction.clearClaimOwnership(this.currentFLocation());
                                removed.incrementAndGet();
                                CmdOwner.this.fme.msg("<i>You have cleared ownership for this claimed area.");
                                return true;
                            }
                            if (!CmdOwner.this.payForCommand(Conf.econCostOwner, "to set ownership of claimed land", "for setting ownership of claimed land")) {
                                return true;
                            }
                            complete.incrementAndGet();
                            CmdOwner.this.myFaction.setPlayerAsOwner(fplayercopy, this.currentFLocation());
                            CmdOwner.this.fme.msg("<i>You have added %s<i> to the owner list for this claimed land.", playerName);
                            return true;
                        }
                    };
                }

            }.runTaskAsynchronously(P.p);
            return;
        }
        if (this.args.isEmpty() && this.myFaction.doesLocationHaveOwnersSet(flocation)) {
            this.myFaction.clearClaimOwnership(flocation);
            this.fme.msg("<i>You have cleared ownership for this claimed area.");
            return;
        }
        if (this.myFaction.isPlayerInOwnerList(target, flocation)) {
            this.myFaction.removePlayerAsOwner(target, flocation);
            this.fme.msg("<i>You have removed ownership of this claimed land from %s<i>.", playerName);
            return;
        }
        if (!this.payForCommand(Conf.econCostOwner, "to set ownership of claimed land", "for setting ownership of claimed land")) {
            return;
        }
        this.myFaction.setPlayerAsOwner(target, flocation);
        this.fme.msg("<i>You have added %s<i> to the owner list for this claimed land.", playerName);
    }

}

