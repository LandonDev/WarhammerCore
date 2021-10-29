/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.material.MaterialData
 */
package com.massivecraft.factions.listeners.menu.faccess;

import com.google.common.collect.Lists;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionWrapper;
import com.massivecraft.factions.listeners.menu.ClickableItemStack;
import com.massivecraft.factions.listeners.menu.GUIMenu;
import com.massivecraft.factions.struct.PermissionManager;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.LinkedList;
import java.util.Set;

public class FAccessChunkViewMenu
        extends GUIMenu {
    int page = 1;
    private Player player;
    private FPlayer editting;
    private Faction owningFac;
    private Faction factionEditting;
    private FactionWrapper wrapper;
    private Set<FLocation> chunksAccessTo;

    public FAccessChunkViewMenu(Player player, FPlayer editting, Faction factionEditting, Faction owning, FactionWrapper wrapper, Set<FLocation> chunks) {
        super("Chunk Access", 54);
        this.player = player;
        this.owningFac = owning;
        this.editting = editting;
        this.factionEditting = factionEditting;
        this.wrapper = wrapper;
        this.chunksAccessTo = chunks;
    }

    @Override
    public void drawItems() {
        int start;
        this.clearItems();
        LinkedList<FLocation> locs = new LinkedList<FLocation>(this.chunksAccessTo);
        this.setInventorySize(FAccessChunkViewMenu.fitSlots(locs.size() + (this.getPreviousMenu() != null ? 1 : 0)));
        int slot = 0;
        if (this.getPreviousMenu() != null) {
            this.setItem(slot++, this.getBackButton(new MaterialData(Material.STAINED_GLASS_PANE, DyeColor.RED.getWoolData()), ChatColor.GREEN + "Previous Menu", ChatColor.GRAY + "Click to return to previous menu."));
        }
        int perPage = 51;
        int maxPages = (int) Math.ceil((double) this.chunksAccessTo.size() / (double) perPage);
        this.page = Math.max(1, Math.min(this.page, maxPages));
        for (int i = start = this.page * perPage - perPage; i < start + perPage && i < locs.size(); ++i) {
            FLocation loc = locs.get(i);
            if (slot == this.getSize() - 9 || slot == this.getSize() - 1) {
                ++slot;
            }
            if (slot >= this.getSize()) break;
            this.setItem(slot++, new ClickableItemStack(new ItemStack(Material.BEACON)).setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + FLocation.chunkToBlock((int) loc.getX()) + "x " + FLocation.chunkToBlock((int) loc.getZ()) + "z").setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GRAY + "Click to remove access to this chunk."})).setClickCallback(e -> {
                this.chunksAccessTo.remove(loc);
                if (this.editting != null) {
                    this.wrapper.removePlayerAccess(loc, this.editting);
                    PermissionManager.get().removePermissions(this.owningFac, loc, this.editting.getCachedUUID());
                    this.player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "(!) " + ChatColor.RED + "Chunk Access removed from " + this.editting.getNameAsync() + " at " + ChatColor.RED + ChatColor.BOLD + loc.formatXAndZ(",") + ChatColor.RED + "!");
                } else if (this.factionEditting != null) {
                    this.wrapper.removeFactionAccess(loc, this.factionEditting);
                    this.player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "(!) " + ChatColor.RED + "Faction Chunk Access removed from " + this.factionEditting.getTag(this.owningFac) + ChatColor.RED + " at " + ChatColor.RED + ChatColor.BOLD + loc.formatXAndZ(",") + ChatColor.RED + "!");
                }
                this.drawItems();
            }));
        }
        if (maxPages > 1 && this.page < maxPages) {
            this.setItem(this.getSize() - 1, new ClickableItemStack(new ItemStack(Material.ARROW)).setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Next").setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GRAY + "Click to view the next page of chunks."})).setClickCallback(e -> {
                ++this.page;
                this.drawItems();
            }));
        }
        if (maxPages > 1 && this.page > 1) {
            this.setItem(this.getSize() - 9, new ClickableItemStack(new ItemStack(Material.ARROW)).setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Previous").setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GRAY + "Click to view the previous page of chunks."})).setClickCallback(e -> {
                --this.page;
                this.drawItems();
            }));
        }
    }
}

