/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.FUnclaimAllConfirm;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.RolePerm;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CmdUnclaimall
        extends FCommand {
    public CmdUnclaimall() {
        this.aliases.add("unclaimall");
        this.aliases.add("declaimall");
        this.optionalArgs.put("", "");
        this.permission = Permission.UNCLAIM_ALL.node;
        this.disableOnLock = true;
        this.requiredRolePermission = RolePerm.UNCLAIMALL;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
    }

    @Override
    public void perform() {
        boolean allowed = false;
        if (this.argIsSet(0) && this.argAsString(0).equalsIgnoreCase("confirm")) {
            allowed = true;
        }
        if (!allowed) {
            new FUnclaimAllConfirm((Player) this.sender, Board.getFactionCoordCount(this.myFaction)).openGUI(P.p);
            return;
        }
        if (Econ.shouldBeUsed()) {
            double refund = Econ.calculateTotalLandRefund(this.myFaction.getLandRounded());
            if (Conf.bankEnabled && Conf.bankFactionPaysLandCosts ? !Econ.modifyMoney(this.myFaction, refund, "to unclaim all faction land", "for unclaiming all faction land") : !Econ.modifyMoney(this.fme, refund, "to unclaim all faction land", "for unclaiming all faction land")) {
                return;
            }
        }
        LandUnclaimAllEvent unclaimAllEvent = new LandUnclaimAllEvent(this.myFaction, this.fme);
        Bukkit.getServer().getPluginManager().callEvent(unclaimAllEvent);
        this.me.removeMetadata("unclaimall_attempt", P.p);
        int unclaimed = Board.unclaimAll(this.myFaction.getId());
        P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
            CmdUnclaimall.this.myFaction.msg("%s<i> unclaimed ALL of your faction's land.", CmdUnclaimall.this.fme.describeToAsync(CmdUnclaimall.this.myFaction, true));
            if (Conf.logLandUnclaims) {
                P.p.log(CmdUnclaimall.this.fme.getNameAsync() + " unclaimed everything for the faction: " + CmdUnclaimall.this.myFaction.getTag());
            }
        });
        P.p.logFactionEvent(this.myFaction, FLogType.CHUNK_CLAIMS, this.fme.getNameAsync(), CC.RedB + "UNCLAIMED", String.valueOf(unclaimed), new FLocation(((Player) this.sender).getLocation()).formatXAndZ(","));
    }

}

