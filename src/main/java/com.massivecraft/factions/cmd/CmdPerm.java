/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.listeners.menu.fperm.PermMainMenu;
import com.massivecraft.factions.listeners.menu.fperm.PermMenu;
import com.massivecraft.factions.listeners.menu.fperm.PlayerPermMenu;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.RolePerm;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CmdPerm
        extends FCommand {
    public CmdPerm() {
        this.aliases.add("perm");
        this.aliases.add("perms");
        this.optionalArgs.put("player/factionName", "");
        this.disableOnLock = false;
        this.requiredRolePermission = RolePerm.EDIT_PLAYER_PERMS;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
    }

    @Override
    public void perform() {
        Relation rel;
        Relation rel2;
        FPlayer their;
        String name = this.argAsString(0);
        Player pl = (Player) this.sender;
        if (name == null || name.equals("list")) {
            new PermMainMenu(this.fme, this.myFaction).open(pl);
            return;
        }
        Faction fac = null;
        Player online = Bukkit.getPlayer(name);
        if (online == null && (fac = Factions.i.getByTag(name)) == null) {
            this.sender.sendMessage(ChatColor.RED + "Unable to find player or faction with that name!");
            return;
        }
        if (!(online == null || (their = FPlayers.i.get(online.getUniqueId().toString())) != null && their.hasFaction() && (rel = this.myFaction.getRelationTo(their)) != Relation.ENEMY)) {
            this.sender.sendMessage(ChatColor.RED + "You can only view the Faction Permissions of non enemy members!");
            return;
        }
        if (fac != null && (rel2 = this.myFaction.getRelationTo(fac)) == Relation.ENEMY) {
            this.sender.sendMessage(ChatColor.RED + "You can only view the Faction Permissions of non enemy factions!");
            return;
        }
        if (fac != null) {
            new PermMenu(this.fme, fac, this.myFaction).open(this.me);
        } else {
            new PlayerPermMenu(this.fme, this.myFaction, new FLocation(pl.getLocation()), FPlayers.i.get(online.getUniqueId().toString())).open(this.me);
        }
    }
}

