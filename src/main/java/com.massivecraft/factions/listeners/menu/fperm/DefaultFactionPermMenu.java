/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.HumanEntity
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
import com.massivecraft.factions.P;
import com.massivecraft.factions.listeners.menu.ClickableItemStack;
import com.massivecraft.factions.listeners.menu.GUIMenu;
import com.massivecraft.factions.struct.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Set;

public class DefaultFactionPermMenu
        extends GUIMenu {
    private Faction owning;
    private Faction faction;
    private FPlayer player;

    public DefaultFactionPermMenu(FPlayer player, Faction owning, Faction faction) {
        super("/f perm: " + faction.getTag() + " Faction Permissions", 54);
        this.owning = owning;
        this.faction = faction;
        this.player = player;
    }

    @Override
    public void drawItems() {
        FactionPermissions currentPerms;
        if (this.getPreviousMenu() != null) {
            this.setItem(0, this.getBackButton(null, null, new String[]{null}));
        }
        Set<FactionPermission> chunkPermissions = (currentPerms = PermissionManager.get().getPermissions(this.owning)) != null ? currentPerms.getFactionDefaultPermissions().get(this.faction.getId()) : null;
        FLocation start = new FLocation(this.player.getPlayer());
        for (FactionPermission permission : FactionPermission.values()) {
            if (permission == FactionPermission.CLAIMING) continue;
            boolean hasPerm = chunkPermissions != null && chunkPermissions.contains(permission);
            ArrayList lore = Lists.newArrayList(permission.getDescription());
            lore.add("");
            lore.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "Permission");
            if (hasPerm) {
                lore.add(ChatColor.GREEN + ChatColor.BOLD.toString() + "ALLOWED");
            } else {
                lore.add(ChatColor.RED + ChatColor.BOLD.toString() + "NOT ALLOWED");
            }
            ItemStack item = new ItemStack(permission.getDisplayMaterial(), 1, permission.getDurability());
            this.setItem(permission.getGuiSlot(), new ClickableItemStack(item).setDisplayName(ChatColor.GOLD + permission.getName()).setLore(lore).setClickCallback(e -> {
                Player player = (Player) e.getWhoClicked();
                if (this.owning.getRelationTo(this.player) == Relation.MEMBER && this.player.getRole() != null && this.player.getRole().isAtLeast(Role.COLEADER)) {
                    FactionPermissions perm = PermissionManager.get().getPermissions(this.owning);
                    Set perms = perm.getFactionDefaultPermissions().computeIfAbsent(this.faction.getId(), newSet -> perm.getDefaultPermissions());
                    boolean removed = perms.remove(permission);
                    if (!removed) {
                        perms.add(permission);
                        this.owning.sendMessage(ChatColor.AQUA + this.player.getRole().getPrefix() + this.player.getNameAsync() + ChatColor.LIGHT_PURPLE + " has given " + this.faction.getTag(this.owning) + ChatColor.LIGHT_PURPLE + " /f perm access to " + ChatColor.AQUA + "\"" + permission.getBareName() + "\"" + ChatColor.LIGHT_PURPLE + " in " + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE + start.formatXAndZ(",") + ChatColor.LIGHT_PURPLE + "!");
                        P.p.logFactionEvent(this.owning, FLogType.PERM_EDIT_DEFAULTS, player.getName(), ChatColor.GREEN.toString() + ChatColor.BOLD + "GRANTED", permission.getBareName(), this.faction.getTag(this.owning), start.formatXAndZ(","));
                    } else {
                        this.owning.sendMessage(ChatColor.AQUA + this.player.getRole().getPrefix() + this.player.getNameAsync() + ChatColor.LIGHT_PURPLE + " has removed " + this.faction.getTag(this.owning) + "'s" + ChatColor.LIGHT_PURPLE + " /f perm access to " + ChatColor.AQUA + "\"" + permission.getBareName() + "\"" + ChatColor.LIGHT_PURPLE + " in " + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE + start.formatXAndZ(",") + ChatColor.LIGHT_PURPLE + "!");
                        P.p.logFactionEvent(this.owning, FLogType.PERM_EDIT_DEFAULTS, player.getName(), ChatColor.RED.toString() + ChatColor.BOLD + "REMOVED", permission.getBareName(), this.faction.getTag(this.owning), start.formatXAndZ(","));
                    }
                    perm.updateClaimsWithDefaultPermissions(this.faction);
                    this.drawItems();
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to modify Faction Permissions!");
                }
            }).setFakeEnchanted(hasPerm));
        }
    }
}

