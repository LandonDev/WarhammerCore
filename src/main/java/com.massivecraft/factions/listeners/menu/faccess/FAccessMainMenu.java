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
 */
package com.massivecraft.factions.listeners.menu.faccess;

import com.google.common.collect.Lists;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.listeners.menu.ClickableItemStack;
import com.massivecraft.factions.listeners.menu.GUIMenu;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FAccessMainMenu
        extends GUIMenu {
    private Faction faction;
    private FPlayer fPlayer;

    public FAccessMainMenu(Faction faction, FPlayer player) {
        super("/f access: Select Type", 18);
        this.faction = faction;
        this.fPlayer = player;
    }

    @Override
    public void drawItems() {
        this.setItem(12, new ClickableItemStack(new ItemStack(Material.SKULL_ITEM, 1, (short) 3)).setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "View Players").setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GRAY + "Click to view all players", ChatColor.GRAY + "that have explicit chunk access."})).setClickCallback(e -> new FAccessPlayerMenu(this.fPlayer.getPlayer(), this.faction).setPreviousMenu(this).open(this.fPlayer.getPlayer())));
        this.setItem(14, new ClickableItemStack(new ItemStack(Material.BEACON)).setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "View Factions").setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GRAY + "Click to view all factions", ChatColor.GRAY + "that have explicit chunk access."})).setClickCallback(e -> new FAccessFactionMenu(this.fPlayer.getPlayer(), this.faction).setPreviousMenu(this).open(this.fPlayer.getPlayer())));
        this.setItem(4, new ClickableItemStack(new ItemStack(Material.BOOK)).setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "/f access Help").setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GRAY + "You can use this menu to customize", ChatColor.GRAY + "both per-player and per-faction access", ChatColor.GRAY + "to your faction's claims.", "", ChatColor.AQUA + "/f access <p/f> <name> <yes/no/none/all>", ChatColor.GRAY + "Give a player (p) or faction (f) with <name>", ChatColor.GRAY + "access to the current chunk you are in.", "", ChatColor.AQUA + "yes" + ChatColor.GRAY + " = Give Access", ChatColor.AQUA + "no" + ChatColor.GRAY + " = Revoke Access", ChatColor.AQUA + "none" + ChatColor.GRAY + " = Revoke Access to ALL chunks", ChatColor.AQUA + "all" + ChatColor.GRAY + " = Give Access to ALL chunks", "", ChatColor.AQUA + "/f access list", ChatColor.GRAY + "List all entities with access to", ChatColor.GRAY + "the current chunk you are in.", "", ChatColor.AQUA + "/f access clear", ChatColor.GRAY + "Revokes access from all entities to", ChatColor.GRAY + "the current chunk you are in."})));
        this.fillEmpty(new ClickableItemStack(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) DyeColor.GRAY.getWoolData())).setDisplayName(" "));
    }
}

