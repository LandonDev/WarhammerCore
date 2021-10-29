/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.listeners.menu.faccess.RolePermMenu;
import org.bukkit.entity.Player;

public class CmdRoles
        extends FCommand {
    public CmdRoles() {
        this.aliases.add("roles");
        this.aliases.add("role");
        this.optionalArgs.put("faction tag", "yours");
        this.disableOnLock = false;
        this.senderMustBePlayer = true;
    }

    @Override
    public void perform() {
        Faction faction = this.myFaction;
        if (this.argIsSet(0) && this.sender.hasPermission("factions.command.roles.others") && (faction = this.argAsFaction(0)) == null) {
            this.sender.sendMessage(CC.RedB + "(!) " + CC.Red + "Invalid faction given!");
            return;
        }
        if (faction == null || !faction.isNormal()) {
            this.sender.sendMessage(CC.Red + "You must be in a faction to do that!");
            return;
        }
        new RolePermMenu(this.fme, (Player) this.sender, faction).open((Player) this.sender);
    }
}

