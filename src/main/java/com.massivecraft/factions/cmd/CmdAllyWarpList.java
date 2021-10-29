/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionWrapper;
import com.massivecraft.factions.FactionWrappers;
import com.massivecraft.factions.Factions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public class CmdAllyWarpList
        extends FCommand {
    public CmdAllyWarpList() {
        this.aliases.add("allywarplist");
        this.setHelpShort("View the ally warp list you faction has set.");
        this.senderMustBeModerator = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
    }

    @Override
    public void perform() {
        Location l;
        Player p = this.me;
        Faction f = this.myFaction;
        if (!f.isNormal()) {
            p.sendMessage(ChatColor.RED + "You must be in a faction to do this.");
            return;
        }
        FactionWrapper wrapper = FactionWrappers.get(f);
        p.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + ChatColor.UNDERLINE + "Your faction has " + wrapper.getAllAllyWarps().size() + " Open Ally Warps");
        p.sendMessage("");
        for (String name : wrapper.getAllAllyWarps().keySet()) {
            l = wrapper.getAllAllyWarps().get(name);
            p.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + name + " - " + ChatColor.YELLOW + "x" + l.getBlockX() + " y" + l.getBlockY() + " z" + l.getBlockZ());
        }
        p.sendMessage("");
        p.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + ChatColor.UNDERLINE + "Your faction has " + wrapper.getAllyWarps().size() + " Closed Ally Warps");
        p.sendMessage("");
        for (Map.Entry locs : wrapper.getAllyWarps().entrySet()) {
            l = (Location) locs.getValue();
            Faction fac = Factions.i.get((String) locs.getKey());
            if (fac == null) {
                System.out.println("null faction: " + locs.getKey());
                wrapper.getAllyWarps().remove(locs.getKey());
                continue;
            }
            p.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + fac.getTag() + ChatColor.GREEN + " - x" + l.getBlockX() + " y" + l.getBlockY() + " z" + l.getBlockZ());
        }
        p.sendMessage("");
    }
}

