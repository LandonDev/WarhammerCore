/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.EssentialsFeatures;
import com.massivecraft.factions.struct.Relation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class CmdTpAlly
        extends FCommand {
    String red = ChatColor.RED + ChatColor.BOLD.toString() + "(!) ";

    public CmdTpAlly() {
        this.aliases.add("warpally");
        this.aliases.add("allywarp");
        this.setHelpShort("Lists open warps available to your faction in your ally's claimed territory.");
        this.requiredArgs.add("allyfaction");
        this.optionalArgs.put("yourfaction/warpname", "yourfaction");
    }

    @Override
    public void perform() {
        if (!(this.sender instanceof Player)) {
            return;
        }
        Player p = (Player) this.sender;
        String tpto = this.argAsString(1, "").toLowerCase();
        Faction me = this.myFaction;
        if (me.isNormal()) {
            Faction otherFac = this.argAsFaction(0);
            if (otherFac == null || !otherFac.isNormal()) {
                p.sendMessage(this.red + ChatColor.RED + "Please enter a valid ally faction to teleport to.");
                return;
            }
            FactionWrapper ofwrap = FactionWrappers.get(otherFac);
            if (ofwrap.getAllyWarp(me) == null && ofwrap.getAllAllyWarps().size() <= 0) {
                p.sendMessage(this.red + ChatColor.RED + "That faction does not have a warp set for your faction.");
                return;
            }
            if (tpto.equalsIgnoreCase(me.getTag())) {
                if (ofwrap.getAllyWarp(me) == null) {
                    p.sendMessage(this.red + ChatColor.RED + "That faction does not have a warp set for your faciton.");
                    return;
                }
                Location l = ofwrap.getAllyWarp(me);
                FLocation fl = new FLocation(l);
                if (Board.getFactionAt(fl).equals(otherFac)) {
                    FLocation currentLocation = new FLocation(p.getLocation());
                    Faction currentStanding = Board.getFactionAt(currentLocation);
                    if (currentStanding.getRelationTo(me).equals(Relation.ENEMY)) {
                        p.sendMessage(this.red + ChatColor.RED + "You cannot tp while in enemy territory.");
                        return;
                    }
                    if (otherFac.getRelationTo(me).equals(Relation.ALLY)) {
                        if (EssentialsFeatures.handleTeleport(p, l)) {
                            return;
                        }
                        p.teleport(l);
                    } else {
                        p.sendMessage(this.red + ChatColor.RED + "Your faction is not allies with " + otherFac.getTag() + ".");
                        ofwrap.getAllyWarps().remove(me.getTag());
                    }
                } else {
                    p.sendMessage(this.red + ChatColor.RED + "That warp is no longer available.");
                    ofwrap.getAllyWarps().remove(me.getTag());
                }
            } else if (this.args.size() == 2) {
                if (ofwrap.getAllAllyWarps().size() <= 0) {
                    p.sendMessage(ChatColor.RED + otherFac.getTag() + " does not have any open ally warps.");
                    return;
                }
                Location l = ofwrap.getAllAllyWarps().get(tpto);
                if (l == null) {
                    p.sendMessage(this.red + ChatColor.RED + "That is not a valid open ally warp.");
                    p.sendMessage(ChatColor.YELLOW + "Use '/f warpally " + otherFac.getTag() + "' to view their open ally warps.");
                    return;
                }
                FLocation fl = new FLocation(l);
                if (Board.getFactionAt(fl).equals(otherFac)) {
                    FLocation currentLocation = new FLocation(p.getLocation());
                    Faction currentStanding = Board.getFactionAt(currentLocation);
                    if (currentStanding.getRelationTo(me).equals(Relation.ENEMY)) {
                        p.sendMessage(ChatColor.RED + "You cannot tp while in enemy territory.");
                        return;
                    }
                    if (otherFac.equals(me)) {
                        p.sendMessage(this.red + ChatColor.RED + "You can not teleport to your own ally warps.");
                        return;
                    }
                    if (otherFac.getRelationTo(me).equals(Relation.ALLY)) {
                        if (EssentialsFeatures.handleTeleport(p, l)) {
                            return;
                        }
                        p.teleport(l);
                    } else {
                        p.sendMessage(this.red + ChatColor.RED + "Your faction is no longer allies with " + otherFac.getTag() + ".");
                    }
                } else {
                    p.sendMessage(this.red + ChatColor.RED + "That warp is no longer available.");
                    ofwrap.getAllAllyWarps().remove(l);
                }
            } else {
                if (!me.getRelationTo(otherFac).equals(Relation.ALLY) && !p.isOp()) {
                    p.sendMessage(ChatColor.RED + "You are not an ally so you cannot view the ally warp list.");
                    return;
                }
                p.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + otherFac.getTag() + " has " + ofwrap.getAllAllyWarps().size() + " Open Ally Warps.");
                p.sendMessage("");
                for (Map.Entry<String, Location> warp : ofwrap.getAllAllyWarps().entrySet()) {
                    Location l = warp.getValue();
                    String name = warp.getKey();
                    p.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + name + " - " + ChatColor.YELLOW + "x" + l.getBlockX() + " y" + l.getBlockY() + " z" + l.getBlockZ());
                }
                if (ofwrap.getAllyWarp(me) != null) {
                    Location l = ofwrap.getAllyWarp(me);
                    p.sendMessage("");
                    p.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Your faction warp: " + ChatColor.GREEN + "x" + l.getBlockX() + " y" + l.getBlockY() + " z" + l.getBlockZ());
                }
                p.sendMessage(ChatColor.YELLOW + "Use /f warpally " + otherFac.getTag() + " <warpname>/yourfaction");
            }
        } else {
            p.sendMessage(this.red + ChatColor.RED + "You must be in a faction to teleport to an ally.");
        }
    }
}

