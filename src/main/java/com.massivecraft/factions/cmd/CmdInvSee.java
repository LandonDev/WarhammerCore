package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.massivecraft.factions.FPlayer;

public class CmdInvSee extends FCommand {
    public CmdInvSee() {
        super();

        this.aliases.add("invsee");

        this.requiredArgs.add("player name");

        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeColeader = true;
        senderMustBeAdmin = true;
    }

    @Override
    public void perform() {



        ArrayList<Player> players = myFaction.getOnlinePlayers();

        FPlayer target = argAsFPlayer(0);

        if (target == null  || !players.contains(target.getPlayer())) {
            fme.msg(ChatColor.RED + "That player is not currently online or is not in your faction.");
            return;
        }

        Inventory inv = Bukkit.createInventory(me, 36, target.getName() + "'s Inventory View");

        for (int i = 0; i < 36; i++)
            if (target.getPlayer().getInventory().getItem(i) != null)

                inv.setItem(i, target.getPlayer().getInventory().getItem(i));

        me.openInventory(inv);
    }
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e){
        if(e.getInventory().getName().endsWith("Inventory View"))
        {
        e.setCancelled(true);
        }
    }

}
