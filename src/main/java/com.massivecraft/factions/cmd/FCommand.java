/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.struct.RolePerm;
import com.massivecraft.factions.zcore.MCommand;
import com.massivecraft.factions.zcore.util.MojangUUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public abstract class FCommand
        extends MCommand<P> {
    public boolean disableOnLock = true;
    public FPlayer fme;
    public Faction myFaction;
    public boolean senderMustBeMember = false;
    public boolean senderMustBeModerator = false;
    public boolean senderMustBeAdmin = false;
    public boolean senderMustBeColeader = false;
    public RolePerm requiredRolePermission;
    public boolean isMoneyCommand = false;
    P p = P.getP();

    public FCommand() {
        super(P.p);
    }

    @Override
    public void execute(CommandSender sender, List<String> args, List<MCommand<?>> commandChain) {
        if (sender instanceof Player) {
            this.fme = FPlayers.i.get((OfflinePlayer) sender);
            this.myFaction = this.fme.getFaction();
        } else {
            this.fme = null;
            this.myFaction = null;
        }
        super.execute(sender, args, commandChain);
    }

    @Override
    public boolean isEnabled() {
        if (this.p.getLocked() && this.disableOnLock) {
            this.msg("<b>Factions was locked by an admin. Please try again later.");
            return false;
        }
        if (this.isMoneyCommand && !Conf.econEnabled) {
            this.msg("<b>Faction economy features are disabled on this server.");
            return false;
        }
        if (this.isMoneyCommand && !Conf.bankEnabled) {
            this.msg("<b>The faction bank system is disabled on this server.");
            return false;
        }
        return true;
    }

    @Override
    public boolean validSenderType(CommandSender sender, boolean informSenderIfNot) {
        boolean superValid = super.validSenderType(sender, informSenderIfNot);
        if (!superValid) {
            return false;
        }
        FPlayer fplayer = null;
        if (this.requiredRolePermission != null && sender instanceof Player && (fplayer = FPlayers.i.get((OfflinePlayer) sender)).hasFaction() && !fplayer.getFaction().hasRolePerm(fplayer, this.requiredRolePermission) && !fplayer.isAdminBypassing()) {
            sender.sendMessage(this.p.txt.parse("<b>Your faction role does not have permission to %s.", this.getHelpShort()));
            return false;
        }
        if (!(this.senderMustBeMember || this.senderMustBeModerator || this.senderMustBeAdmin || this.senderMustBeColeader)) {
            return true;
        }
        if (!(sender instanceof Player)) {
            return false;
        }
        if (this.requiredRolePermission != null) {
            Bukkit.getLogger().info("Allowing " + sender.getName() + " to perform " + this.requiredRolePermission.name() + " due to role permissions!");
        }
        if (fplayer == null) {
            fplayer = FPlayers.i.get((OfflinePlayer) sender);
        }
        if (!fplayer.hasFaction()) {
            sender.sendMessage(this.p.txt.parse("<b>You are not member of any faction."));
            return false;
        }
        if (this.senderMustBeModerator && !fplayer.getRole().isAtLeast(Role.MODERATOR)) {
            sender.sendMessage(this.p.txt.parse("<b>Only faction moderators can %s.", this.getHelpShort()));
            return false;
        }
        if (this.senderMustBeAdmin && !fplayer.getRole().isAtLeast(Role.ADMIN) && !fplayer.isAdminBypassing()) {
            sender.sendMessage(this.p.txt.parse("<b>Only faction admins can %s.", this.getHelpShort()));
            return false;
        }
        if (this.senderMustBeColeader && !fplayer.getRole().isCoLeader() && !fplayer.isAdminBypassing()) {
            sender.sendMessage(this.p.txt.parse("<b>Only faction co leaders can %s.", this.getHelpShort()));
            return false;
        }
        return true;
    }

    public boolean assertHasFaction() {
        if (this.me == null) {
            return true;
        }
        if (!this.fme.hasFaction()) {
            this.sendMessage("You are not member of any faction.");
            return false;
        }
        return true;
    }

    public boolean assertMinRole(Role role) {
        if (this.me == null) {
            return true;
        }
        if (this.fme.getRole().value < role.value) {
            this.msg("<b>You <h>must be " + role.getNicename() + "<b> to " + this.getHelpShort() + ".");
            return false;
        }
        return true;
    }

    public boolean checkHasPerm(FPlayer fplayer, Faction faction, RolePerm perm) {
        if (!faction.hasRolePerm(fplayer, perm)) {
            fplayer.sendMessage(CC.RedB + "(!) " + CC.Red + "Your faction role does not have permission to " + (perm.getDenyDescription() != null ? perm.getDenyDescription() : perm.name().toLowerCase() + " players."));
            return false;
        }
        return true;
    }

    public FPlayer strAsFPlayer(String name, FPlayer def, boolean msg) {
        FPlayer ret = def;
        if (name != null) {
            Player playerOnline = Bukkit.getPlayer(name);
            if (playerOnline != null && playerOnline.isOnline()) {
                FPlayer fplayer = FPlayers.i.get(playerOnline.getUniqueId().toString());
                if (fplayer != null) {
                    ret = fplayer;
                }
            } else {
                try {
                    UUID uuid = MojangUUIDFetcher.getUUIDOf(name);
                    FPlayer fplayer = FPlayers.i.get(uuid.toString());
                    if (fplayer != null) {
                        ret = fplayer;
                    }
                } catch (Exception e) {
                    this.msg("<b>No player found with a uuid!", "");
                }
            }
        }
        if (msg && ret == null) {
            this.msg("<b>No player \"<p>%s<b>\" could be found.", name);
        }
        return ret;
    }

    public FPlayer argAsFPlayer(int idx, FPlayer def, boolean msg) {
        return this.strAsFPlayer(this.argAsString(idx), def, msg);
    }

    public FPlayer argAsFPlayer(int idx, FPlayer def) {
        return this.argAsFPlayer(idx, def, true);
    }

    public FPlayer argAsFPlayer(int idx) {
        return this.argAsFPlayer(idx, null);
    }

    public FPlayer strAsBestFPlayerMatch(String name, FPlayer def, boolean msg) {
        return this.strAsFPlayer(name, def, msg);
    }

    public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def, boolean msg) {
        return this.strAsBestFPlayerMatch(this.argAsString(idx), def, msg);
    }

    public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def) {
        return this.argAsBestFPlayerMatch(idx, def, true);
    }

    public FPlayer argAsBestFPlayerMatch(int idx) {
        return this.argAsBestFPlayerMatch(idx, null);
    }

    public Faction strAsFaction(String name, Faction def, boolean msg) {
        Faction ret = def;
        if (name != null) {
            Faction faction = null;
            if (faction == null) {
                faction = Factions.i.getByTag(name);
            }
            if (faction == null) {
                faction = Factions.i.getBestTagMatch(name);
            }
            if (faction == null) {
                Player playerOnline = Bukkit.getPlayer(name);
                if (playerOnline != null && playerOnline.isOnline()) {
                    FPlayer fplayer = FPlayers.i.get(playerOnline.getUniqueId().toString());
                    if (fplayer != null) {
                        faction = fplayer.getFaction();
                    }
                } else {
                    FPlayer fplayer;
                    OfflinePlayer player = Bukkit.getOfflinePlayer(name);
                    if (player.hasPlayedBefore() && (fplayer = FPlayers.i.get(player.getUniqueId().toString())) != null) {
                        faction = fplayer.getFaction();
                    }
                }
            }
            if (faction != null) {
                ret = faction;
            }
        }
        if (msg && ret == null) {
            this.msg("<b>The faction or player \"<p>%s<b>\" could not be found.", name);
        }
        return ret;
    }

    public Faction argAsFaction(int idx, Faction def, boolean msg) {
        return this.strAsFaction(this.argAsString(idx), def, msg);
    }

    public Faction argAsFaction(int idx, Faction def) {
        return this.argAsFaction(idx, def, true);
    }

    public Faction argAsFaction(int idx) {
        return this.argAsFaction(idx, null);
    }

    public boolean canIAdministerYou(FPlayer i, FPlayer you) {
        if (!i.getFaction().equals(you.getFaction())) {
            i.sendMessage(this.p.txt.parse("%s <b>is not in the same faction as you.", you.describeToAsync(i, true)));
            return false;
        }
        if (i.getRole().value > you.getRole().value || i.getRole().equals(Role.ADMIN)) {
            return true;
        }
        if (you.getRole().equals(Role.ADMIN)) {
            i.sendMessage(this.p.txt.parse("<b>Only the faction admin can do that."));
        } else if (i.getRole().equals(Role.MODERATOR) || i.getRole().equals(Role.COLEADER)) {
            if (i == you) {
                return true;
            }
            i.sendMessage(this.p.txt.parse("<b>Moderators can't control each other..."));
        } else {
            i.sendMessage(this.p.txt.parse("<b>You must be a faction moderator to do that."));
        }
        return false;
    }

    public boolean payForCommand(double cost, String toDoThis, String forDoingThis) {
        if (!Econ.shouldBeUsed() || this.fme == null || cost == 0.0 || this.fme.isAdminBypassing()) {
            return true;
        }
        if (Conf.bankEnabled && Conf.bankFactionPaysCosts && this.fme.hasFaction()) {
            return Econ.modifyMoney(this.myFaction, -cost, toDoThis, forDoingThis);
        }
        return Econ.modifyMoney(this.fme, -cost, toDoThis, forDoingThis);
    }

    public boolean canAffordCommand(double cost, String toDoThis) {
        if (!Econ.shouldBeUsed() || this.fme == null || cost == 0.0 || this.fme.isAdminBypassing()) {
            return true;
        }
        if (Conf.bankEnabled && Conf.bankFactionPaysCosts && this.fme.hasFaction()) {
            return Econ.hasAtLeast(this.myFaction, cost, toDoThis);
        }
        return Econ.hasAtLeast(this.fme, cost, toDoThis);
    }
}

