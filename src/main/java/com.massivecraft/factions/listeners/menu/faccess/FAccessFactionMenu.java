/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
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
import com.massivecraft.factions.struct.FactionPermissions;
import com.massivecraft.factions.struct.PermissionManager;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.*;

public class FAccessFactionMenu
        extends GUIMenu {
    private Player player;
    private Faction faction;

    public FAccessFactionMenu(Player viewing, Faction faction) {
        super("/f access: Factions", 54);
        this.player = viewing;
        this.faction = faction;
    }

    @Override
    public void drawItems() {
        this.clearItems();
        FactionWrapper wrapper = FactionWrappers.get(this.faction);
        LinkedHashMap<Faction, Set<FLocation>> chunkAccess = new LinkedHashMap<>();
        wrapper.chunkClaims.forEach((loc, access) -> access.forEach(entry -> {
            Faction faction;
            String[] split = entry.split(":");
            if (split.length > 0 && split[0].equals("faction") && (faction = Factions.i.get(split[1])) != null) {
                Set<FLocation> claimed = chunkAccess.computeIfAbsent(faction, set -> new HashSet<>());
                claimed.add(loc);
            }
        }));
        this.setInventorySize(FAccessFactionMenu.fitSlots(chunkAccess.size() + (this.getPreviousMenu() != null ? 1 : 0)));
        int slot = 0;
        if (this.getPreviousMenu() != null) {
            this.setItem(slot++, this.getBackButton(new MaterialData(Material.STAINED_GLASS_PANE, DyeColor.RED.getWoolData()), null, null));
        }
        for (Map.Entry<Faction, Set<FLocation>> entry : chunkAccess.entrySet()) {
            Faction fac = entry.getKey();
            Set<FLocation> found = entry.getValue();
            int shown = 0;
            ArrayList lore = Lists.newArrayList((Object[]) new String[]{"", ChatColor.YELLOW + ChatColor.BOLD.toString() + "Chunk Access (" + found.size() + ")"});
            for (FLocation loc2 : found) {
                if (shown >= 10) {
                    lore.add(ChatColor.YELLOW.toString() + ChatColor.BOLD + " * " + ChatColor.WHITE + (found.size() - shown) + " more...");
                    break;
                }
                ++shown;
                lore.add(ChatColor.YELLOW + ChatColor.BOLD.toString() + " * " + ChatColor.WHITE + FLocation.chunkToBlock((int) loc2.getX()) + "x " + FLocation.chunkToBlock((int) loc2.getZ()) + "z");
            }
            lore.add("");
            lore.add(ChatColor.GREEN + "Right-Click " + ChatColor.GRAY + "to clear all access.");
            FPlayer admin = fac.getFPlayerAdmin();
            this.setItem(slot++, new ClickableItemStack(new ItemStack(Material.ANVIL)).setDisplayName(ChatColor.GREEN + fac.getTag()).setLore(lore).setClickCallback(e -> {
                if (e.getClick() == ClickType.RIGHT) {
                    found.forEach(loc -> wrapper.removeFactionAccess(loc, fac));
                    FactionPermissions perms = PermissionManager.get().getPermissions(this.faction);
                    perms.cleanupPermissions(fac);
                    this.player.sendMessage(ChatColor.RED + "All chunk access has been revoked from " + fac.getTag());
                    this.drawItems();
                }
            }));
        }
    }
}

