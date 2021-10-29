/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.material.MaterialData
 */
package com.massivecraft.factions.listeners.menu.fperm;

import com.google.common.collect.Lists;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.listeners.menu.ClickableItemStack;
import com.massivecraft.factions.listeners.menu.GUIMenu;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;

import java.util.Set;

public class PermPlayerGlobalMenu
        extends GUIMenu {
    private FPlayer viewing;
    private Faction affecting;
    private Faction fac;
    private Set<FPlayer> players;

    public PermPlayerGlobalMenu(FPlayer viewing, Faction affecting, Faction fac, Set<FPlayer> players) {
        super("/f perm: " + fac.getTag(), 9);
        this.viewing = viewing;
        this.affecting = affecting;
        this.fac = fac;
        this.players = players;
    }

    @Override
    public void drawItems() {
        Relation rel = this.affecting.getRelationTo(this.fac);
        this.setInventorySize(PermPlayerGlobalMenu.fitSlots(this.players.size() + (this.getPreviousMenu() != null ? 1 : 0)));
        int slot = 0;
        if (this.getPreviousMenu() != null) {
            this.setItem(slot++, this.getBackButton(null, null, new String[]{null}));
        }
        for (FPlayer player : this.players) {
            this.setItem(slot++, new ClickableItemStack(ItemUtil.createPlayerHead(player.getNameAsync())).setDisplayName(rel.getColor() + ChatColor.BOLD.toString() + player.getNameAsync()).setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GREEN + "Left-Click" + ChatColor.GRAY + " to view Default Permissions", ChatColor.GREEN + "Right-Click" + ChatColor.GRAY + " to remove Default Permissions"})).setClickCallback(e -> {
                if (e.getClick() == ClickType.RIGHT) {
                    this.players.remove(player);
                } else {
                    new DefaultPlayerPermMenu(this.viewing, this.affecting, player).setPreviousMenu(this).open(this.viewing.getPlayer());
                }
            }));
        }
    }
}

