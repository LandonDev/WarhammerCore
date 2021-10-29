/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  lombok.NonNull
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.material.MaterialData
 */
package com.massivecraft.factions.listeners.menu.fperm;

import com.google.common.collect.Lists;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.listeners.menu.ClickableItemStack;
import com.massivecraft.factions.listeners.menu.GUIMenu;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.ItemUtil;
import org.bukkit.ChatColor;

import java.util.LinkedHashMap;
import java.util.UUID;

public class PermMenu
        extends GUIMenu {
    private Faction faction;
    private Faction ownerFaction;
    private FPlayer player;

    public PermMenu(FPlayer player, Faction faction, Faction ownerFaction) {
        super("/f perm: " + faction.getTag() + " members", 54);
        if (faction == null) {
            throw new NullPointerException("faction");
        }
        if (ownerFaction == null) {
            throw new NullPointerException("ownerFaction");
        }
        this.player = player;
        this.ownerFaction = ownerFaction;
        this.faction = faction;
    }

    @Override
    public void drawItems() {
        LinkedHashMap<UUID, FPlayer> toShow = new LinkedHashMap<UUID, FPlayer>();
        FLocation start = new FLocation(this.player.getPlayer().getLocation());
        for (FPlayer fp : this.faction.getFPlayers()) {
            if (toShow.containsKey(fp.getCachedUUID())) continue;
            toShow.put(fp.getCachedUUID(), fp);
        }
        FLocation currentLoc = new FLocation(this.player.getPlayer());
        this.setInventorySize(PermMenu.fitSlots(toShow.size() + (this.getPreviousMenu() != null ? 1 : 0)));
        int slot = 0;
        if (this.getPreviousMenu() != null) {
            this.setItem(slot++, this.getBackButton(null, null, null));
        }
        for (FPlayer fPlayer : toShow.values()) {
            Relation rel = this.ownerFaction.getRelationTo(fPlayer);
            this.setItem(slot++, new ClickableItemStack(ItemUtil.createPlayerHead(fPlayer.getNameAsync())).setDisplayName(rel.getColor() + fPlayer.getNameAsync()).setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GRAY + "Click to view current permissions", ChatColor.GRAY + "for this player in chunk: " + ChatColor.GREEN + currentLoc.formatXAndZ("")})).setClickCallback(e -> {
                Relation current = this.ownerFaction.getRelationTo(this.faction);
                if (current != Relation.ENEMY) {
                    new PlayerPermMenu(this.player, this.ownerFaction, start, fPlayer).setPreviousMenu(this).open(this.player.getPlayer());
                }
            }));
        }
    }
}

