/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.arkhamnetwork.Arkkit.utils.com.massivecraft.factions.util.WorldGuardUtils
 *  org.bukkit.Location
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.EssentialsFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.WorldGuardUtils;
import com.massivecraft.factions.zcore.util.SmokeUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CmdHome
        extends FCommand {
    public CmdHome() {
        this.aliases.add("home");
        this.permission = Permission.HOME.node;
        this.disableOnLock = false;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (!Conf.homesEnabled) {
            this.fme.msg("<b>Sorry, Faction homes are disabled on this server.");
            return;
        }
        if (!Conf.homesTeleportCommandEnabled) {
            this.fme.msg("<b>Sorry, the ability to teleport to Faction homes is disabled on this server.");
            return;
        }
        if (!this.myFaction.hasHome()) {
            this.fme.msg("<b>Your faction does not have a home. " + (this.fme.getRole().value < Role.MODERATOR.value ? "<i> Ask your leader to:" : "<i>You should:"));
            this.fme.sendMessage(this.p.cmdBase.cmdSethome.getUseageTemplate());
            return;
        }
        if (!Conf.homesTeleportAllowedFromEnemyTerritory && this.fme.isInEnemyTerritory()) {
            this.fme.msg("<b>You cannot teleport to your faction home while in the territory of an enemy faction.");
            return;
        }
        if (!Conf.homesTeleportAllowedFromDifferentWorld && this.me.getWorld().getUID() != this.myFaction.getHome().getWorld().getUID()) {
            this.fme.msg("<b>You cannot teleport to your faction home while in a different world.");
            return;
        }
        Faction faction = Board.getFactionAt(new FLocation(this.me.getLocation()));
        Location loc = this.me.getLocation().clone();
        if (Conf.homesTeleportAllowedEnemyDistance > 0.0 && !faction.isSafeZone() && (!this.fme.isInOwnTerritory() || !Conf.homesTeleportIgnoreEnemiesIfInOwnTerritory) && !WorldGuardUtils.isPvPDisabled(loc)) {
            World w = loc.getWorld();
            double x = loc.getX();
            double y = loc.getY();
            double z = loc.getZ();
            for (Player p : this.me.getServer().getOnlinePlayers()) {
                if (p == null || !p.isOnline() || p.isDead() || p == this.me || p.getWorld() != w || this.fme.getRelationTo(FPlayers.i.get(p)) != Relation.ENEMY)
                    continue;
                Location l = p.getLocation();
                double dx = Math.abs(x - l.getX());
                double dy = Math.abs(y - l.getY());
                double dz = Math.abs(z - l.getZ());
                double max = Conf.homesTeleportAllowedEnemyDistance;
                if (dx > max || dy > max || dz > max) continue;
                this.fme.msg("<b>You cannot teleport to your faction home while an enemy is within " + Conf.homesTeleportAllowedEnemyDistance + " blocks of you.");
                return;
            }
        }
        if (EssentialsFeatures.handleTeleport(this.me, this.myFaction.getHome())) {
            return;
        }
        if (!this.payForCommand(Conf.econCostHome, "to teleport to your faction home", "for teleporting to your faction home")) {
            return;
        }
        if (Conf.homesTeleportCommandSmokeEffectEnabled) {
            ArrayList<Location> smokeLocations = new ArrayList<Location>();
            smokeLocations.add(loc);
            smokeLocations.add(loc.add(0.0, 1.0, 0.0));
            smokeLocations.add(this.myFaction.getHome());
            smokeLocations.add(this.myFaction.getHome().clone().add(0.0, 1.0, 0.0));
            SmokeUtil.spawnCloudRandom(smokeLocations, Conf.homesTeleportCommandSmokeEffectThickness);
        }
        this.me.teleport(this.myFaction.getHome());
    }
}

