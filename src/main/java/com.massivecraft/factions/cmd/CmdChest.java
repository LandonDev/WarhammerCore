/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.google.common.collect.MapMaker
 *  org.arkhamnetwork.Arkkit.patches.combatlog.CombatLog
 *  org.arkhamnetwork.Arkkit.plugins.ArkPlugin
 *  org.arkhamnetwork.Arkkit.plugins.ArkPlugins
 *  org.bukkit.Location
 *  org.bukkit.Sound
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryView
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.MapMaker;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.RolePerm;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class CmdChest extends FCommand {
    private ConcurrentMap<Object, Object> lastExecuted;

    public CmdChest() {
        this.lastExecuted = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.MINUTES).build().asMap();
        this.aliases.add("chest");
        this.errorOnToManyArgs = false;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction fac = null;
        if (this.args.size() >= 1 && this.sender.isOp()) {
            fac = this.argAsFaction(0);
            if (fac == null || !fac.isNormal()) {
                this.sender.sendMessage(CC.Red + "No faction found with tag '" + this.argAsString(0) + "'");
                return;
            }
        }
        if (fac == null) {
            fac = this.fme.getFaction();
        }
        if (!fac.isNormal()) {
            this.sender.sendMessage(CC.RedB + "(!) " + CC.Red + "You must be in a normal faction to use this command!");
            return;
        }
        final Player player = (Player) this.sender;
        final Long lastExecuted = (Long) this.lastExecuted.get(player.getUniqueId());
        if (lastExecuted != null && lastExecuted > System.currentTimeMillis()) {
            player.sendMessage(CC.RedB + "(!) " + CC.Red + "You must wait at least 1 second before using this command again.");
            return;
        }
        this.lastExecuted.put(player.getUniqueId(), System.currentTimeMillis() + 1000L);
        if (!this.checkHasPerm(this.fme, fac, RolePerm.F_CHEST)) {
            return;
        }
        final Inventory inv = P.p.getFChestManager().getFInventory(fac);
        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1.0f, 1.0f);
        player.sendMessage(CC.GreenB + "(!) " + CC.Green + "Viewing Faction /f chest...");
    }
}

