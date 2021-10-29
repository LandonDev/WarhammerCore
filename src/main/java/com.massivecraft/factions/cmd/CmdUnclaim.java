/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.PluginManager
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.*;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.*;
import com.massivecraft.factions.util.SpiralTask;
import com.massivecraft.factions.zcore.util.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Set;

public class CmdUnclaim
        extends FCommand {
    public CmdUnclaim() {
        this.aliases.add("unclaim");
        this.aliases.add("declaim");
        this.optionalArgs.put("radius", "1");
        this.optionalArgs.put("n", "");
        this.optionalArgs.put("f", "");
        this.permission = Permission.UNCLAIM.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (this.args.size() == 3 && this.argAsString(0).equalsIgnoreCase("region") && this.sender.isOp()) {
            Faction faction = this.argAsFaction(2);
            if (faction == null) {
                this.sender.sendMessage(ChatColor.RED + "Invalid faction entered!");
                return;
            }
            WorldUtil.changeClaimsInRegionWithFaction(this.sender, this.argAsString(1), this.sender instanceof Player ? ((Player) this.sender).getWorld() : Bukkit.getWorlds().get(0), faction, false);
            return;
        }
        FLocation flocation = new FLocation(this.fme);
        Faction otherFaction = Board.getFactionAt(flocation);
        if (otherFaction.isSafeZone()) {
            if (Permission.MANAGE_SAFE_ZONE.has(this.sender)) {
                Board.removeAt(flocation);
                this.msg("<b>Safe zone was unclaimed.");
                if (Conf.logLandUnclaims) {
                    P.p.log(this.fme.getNameAsync() + " unclaimed land at (" + flocation.getCoordString() + ") from the faction: " + otherFaction.getTag());
                }
            } else {
                this.msg("<b>This is a safe zone. You lack permissions to unclaim.");
            }
            return;
        }
        if (otherFaction.isWarZone()) {
            if (Permission.MANAGE_WAR_ZONE.has(this.sender)) {
                Board.removeAt(flocation);
                this.msg("<b>War zone was unclaimed.");
                if (Conf.logLandUnclaims) {
                    P.p.log(this.fme.getNameAsync() + " unclaimed land at (" + flocation.getCoordString() + ") from the faction: " + otherFaction.getTag());
                }
            } else {
                this.msg("<b>This is a war zone. You lack permissions to unclaim.");
            }
            return;
        }
        int radius = this.argAsInt(0, 1);
        if (this.fme.isAdminBypassing()) {
            Board.removeAt(flocation);
            otherFaction.msg("<b>%s<b> unclaimed your land at " + flocation.getCoordString(), this.fme.describeToAsync(otherFaction, true));
            this.msg("<b>You unclaimed this land.");
            if (Conf.logLandUnclaims) {
                P.p.log(this.fme.getNameAsync() + " unclaimed land at (" + flocation.getCoordString() + ") from the faction: " + otherFaction.getTag());
            }
            return;
        }
        boolean explicit = false;
        FactionPermissions permissions = P.p.getPermissionManager().getPermissions(otherFaction);
        if (permissions != null) {
            Set<FactionPermission> perms = permissions.getDefaultPlayerPermissions().get(this.me.getUniqueId());
            if (perms != null && !perms.contains(FactionPermission.CLAIMING)) {
                if (!this.fme.getRole().isAtLeast(Role.COLEADER) && !this.fme.isAdminBypassing()) {
                    this.me.sendMessage(CC.RedB + "(!) " + CC.Red + "You do not have permission to unclaim land for " + otherFaction.getTag() + "!");
                    this.me.sendMessage(CC.Gray + "Speak with a Co Leader+ to grant you the /f perm!");
                    return;
                }
            } else if (perms != null && perms.contains(FactionPermission.CLAIMING)) {
                explicit = true;
            }
        }
        if (!this.assertHasFaction()) {
            return;
        }
        if (!explicit && !this.assertMinRole(Role.MODERATOR)) {
            return;
        }
        if (this.myFaction != otherFaction && radius <= 1) {
            this.msg("<b>You don't own this land.");
            return;
        }
        if (radius < 1) {
            this.msg("<b>If you specify a radius, it must be greater then 1.");
            return;
        }
        if (radius > 1) {
            if (radius > 50 && !this.fme.isAdminBypassing()) {
                this.sender.sendMessage(CC.Red + "You can only unclaim a radius of 50 at a time.");
                return;
            }

            final Faction fac = this.fme.getFaction();
            final FPlayer fplayer = this.fme;
            final String flocationCoords = flocation.getCoordString();
            final FLocation fLocation = new FLocation(fplayer);

            new SpiralTask(fLocation, radius) {

                private final int limit;
                private int failCount;
                private int unclaimed;

                {
                    //  super(fLocation, radius);
                    this.failCount = 0;
                    this.limit = Conf.radiusClaimFailureLimit * 2;
                    this.unclaimed = 0;
                }

                @Override
                public boolean work() {
                    boolean success = false;
                    Faction claimed = Board.getFactionAt(this.currentFLocation());
                    success = claimed.equals(fac);
                    boolean owns = success;
                    if (!owns) {
                        ++this.failCount;
                        CmdUnclaim.this.msg("<b>You don't own this land.");
                        if (this.failCount >= this.limit) {
                            this.finish();
                            return false;
                        }
                        return true;
                    }
                    LandUnclaimEvent unclaimEvent = new LandUnclaimEvent(this.currentFLocation(), fac, fplayer);
                    Bukkit.getServer().getPluginManager().callEvent(unclaimEvent);
                    if (unclaimEvent.isCancelled()) {
                        return true;
                    }
                    if (Econ.shouldBeUsed()) {
                        double refund = Econ.calculateClaimRefund(fac.getLandRounded());
                        if (Conf.bankEnabled && Conf.bankFactionPaysLandCosts ? !Econ.modifyMoney(fac, refund, "to unclaim this land", "for unclaiming this land") : !Econ.modifyMoney(fplayer, refund, "to unclaim this land", "for unclaiming this land")) {
                            return true;
                        }
                    }
                    Board.removeAt(this.currentFLocation());
                    ++this.unclaimed;
                    fac.msg("<b>%s<b> unclaimed land at " + flocationCoords + "", fplayer.describeToAsync(fac, true));
                    if (Conf.logLandUnclaims) {
                        P.p.log(fplayer.getNameAsync() + " unclaimed land at (" + this.currentFLocation().getCoordString() + ") from the faction: " + claimed.getTag());
                    }
                    if (success) {
                        this.failCount = 0;
                    } else if (!success && this.failCount++ >= this.limit) {
                        this.finish();
                        return false;
                    }
                    return true;
                }

                @Override
                public void finish() {
                    super.finish();
                    P.p.logFactionEvent(fac, FLogType.CHUNK_CLAIMS, fplayer.getNameAsync(), CC.RedB + "UNCLAIMED", String.valueOf(this.unclaimed), new FLocation(((Player) CmdUnclaim.this.sender).getLocation()).formatXAndZ(","));
                }
            };
            return;
        }
        LandUnclaimEvent unclaimEvent = new LandUnclaimEvent(flocation, otherFaction, this.fme);
        Bukkit.getServer().getPluginManager().callEvent(unclaimEvent);
        if (unclaimEvent.isCancelled()) {
            return;
        }
        if (Econ.shouldBeUsed()) {
            double refund = Econ.calculateClaimRefund(this.myFaction.getLandRounded());
            if (Conf.bankEnabled && Conf.bankFactionPaysLandCosts ? !Econ.modifyMoney(this.myFaction, refund, "to unclaim this land", "for unclaiming this land") : !Econ.modifyMoney(this.fme, refund, "to unclaim this land", "for unclaiming this land")) {
                return;
            }
        }
        Board.removeAt(flocation);
        this.myFaction.msg("<b>%s<b> unclaimed land at " + flocation.getCoordString(), this.fme.describeToAsync(this.myFaction, true));
        if (Conf.logLandUnclaims) {
            P.p.log(this.fme.getNameAsync() + " unclaimed land at (" + flocation.getCoordString() + ") from the faction: " + otherFaction.getTag());
        }
        P.p.logFactionEvent(this.myFaction, FLogType.CHUNK_CLAIMS, this.fme.getNameAsync(), CC.RedB + "UNCLAIMED", String.valueOf(1), flocation.formatXAndZ(","));
    }

}

