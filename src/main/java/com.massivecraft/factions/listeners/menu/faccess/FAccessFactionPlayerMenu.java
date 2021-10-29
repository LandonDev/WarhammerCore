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
package com.massivecraft.factions.listeners.menu.faccess;

import com.google.common.collect.Lists;
import com.massivecraft.factions.*;
import com.massivecraft.factions.listeners.menu.ClickableItemStack;
import com.massivecraft.factions.listeners.menu.GUIMenu;
import com.massivecraft.factions.listeners.menu.fperm.PlayerPermMenu;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;

import java.util.Set;

@Deprecated
public class FAccessFactionPlayerMenu
        extends GUIMenu {
    private FPlayer player;
    private Faction owning;
    private Faction toEdit;
    private Set<FPlayer> toShow;
    private FLocation chunk;

    public FAccessFactionPlayerMenu(FPlayer viewing, Faction owning, Faction toEdit, FLocation chunk, Set<FPlayer> toShow) {
        super("/f access: " + toEdit.getTag() + " at " + chunk.formatXAndZ(""), 54);
        this.player = viewing;
        this.owning = owning;
        this.toEdit = toEdit;
        this.toShow = toShow;
        this.chunk = chunk;
    }

    @Override
    public void drawItems() {
        int slot = 0;
        this.setInventorySize(FAccessFactionPlayerMenu.fitSlots(this.toShow.size() + (this.getPreviousMenu() != null ? 1 : 0)));
        this.clearItems();
        if (this.getPreviousMenu() != null) {
            this.setItem(slot++, this.getBackButton(null, null, null));
        }
        for (FPlayer player : this.toShow) {
            Relation rel = player.getRelationTo(this.owning);
            this.setItem(slot++, new ClickableItemStack(ItemUtil.createPlayerHead(player.getNameAsync())).setDisplayName(rel.getColor() + ChatColor.BOLD.toString() + "Player: " + rel.getColor() + player.getNameAsync()).setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GREEN + "Left-Click" + ChatColor.GRAY + " to view their chunk permissions", ChatColor.GREEN + "Right-Click" + ChatColor.GRAY + " to remove their access to this chunk"})).setClickCallback(e -> {
                if (e.getClick() == ClickType.LEFT) {
                    new PlayerPermMenu(this.player, this.owning, this.chunk, player).setPreviousMenu(this).open(this.player.getPlayer());
                } else if (e.getClick() == ClickType.RIGHT) {
                    FactionWrapper wrapper = FactionWrappers.get(this.owning);
                    if (wrapper.removePlayerAccess(this.chunk, player)) {
                        this.player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "The player " + player.getNameAsync() + " no longer has access to the chunk " + this.chunk.formatXAndZ("") + "!");
                        this.toEdit.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "You no longer have access to " + this.owning.getTag() + "'s claimed chunk at " + this.chunk.formatXAndZ("") + "!");
                        this.toShow.remove(player);
                        this.drawItems();
                    } else {
                        this.player.sendMessage(ChatColor.RED + "That player no longer has access to that chunk!");
                    }
                }
            }));
        }
    }
}

