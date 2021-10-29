/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
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
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.*;

public class PermFactionChunkListView
        extends GUIMenu {
    private FPlayer player;
    private Faction faction;
    private Faction toEdit;
    private FLocation chunk;

    public PermFactionChunkListView(FPlayer player, Faction owning, Faction toEdit, FLocation chunk) {
        super(toEdit.getTag() + " Perms in " + chunk.formatXAndZ(""), 54);
        this.player = player;
        this.faction = owning;
        this.toEdit = toEdit;
        this.chunk = chunk;
    }

    @Override
    public void drawItems() {
        Map<String, Set<FactionPermission>> currentPermissionMap;
        this.clearItems();
        if (this.getPreviousMenu() != null) {
            this.setItem(0, this.getBackButton(new MaterialData(Material.STAINED_GLASS_PANE, DyeColor.RED.getWoolData()), ChatColor.RED + "Back", ChatColor.GRAY + "Return to previous menu."));
        }
        Set<FactionPermission> chunkPermissions = (currentPermissionMap = PermissionManager.get().getPermissions(this.faction).getFactionChunkPermissionMap().get(this.chunk)) != null ? currentPermissionMap.get(this.toEdit.getId()) : null;
        for (FactionPermission permission : FactionPermission.values()) {
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
                if (permission == FactionPermission.CLAIMING) {
                    player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "This permission is per player and global only!");
                    player.sendMessage(ChatColor.GRAY + "Please visit the /f perm Default Permissions to update this permission!");
                    return;
                }
                if (this.faction.getRelationTo(this.player) == Relation.MEMBER && this.player.getRole() != null && this.player.getRole().isAtLeast(Role.COLEADER)) {
                    FactionPermissions perm = PermissionManager.get().getPermissions(this.faction);
                    Map permissions = perm.getFactionChunkPermissionMap().computeIfAbsent(this.chunk, map -> new HashMap<>());
                    Set perms = Collections.singleton(permissions.computeIfAbsent(this.toEdit.getId(), set -> new HashSet()));
                    boolean removed = perms.remove(permission);
                    if (!removed) {
                        perms.add(permission);
                        this.faction.sendMessage(ChatColor.AQUA + this.player.getRole().getPrefix() + this.player.getNameAsync() + ChatColor.LIGHT_PURPLE + " has given " + this.toEdit.getTag(this.faction) + ChatColor.LIGHT_PURPLE + " /f perm access to " + ChatColor.AQUA + "\"" + permission.getBareName() + "\"" + ChatColor.LIGHT_PURPLE + " in " + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE + this.chunk.formatXAndZ(",") + ChatColor.LIGHT_PURPLE + "!");
                        P.p.logFactionEvent(this.faction, FLogType.PERM_EDIT_CHUNK, player.getName(), ChatColor.GREEN.toString() + ChatColor.BOLD + "GRANTED", permission.getBareName(), this.toEdit.getTag(this.faction), this.chunk.formatXAndZ(","));
                    } else {
                        this.faction.sendMessage(ChatColor.AQUA + this.player.getRole().getPrefix() + this.player.getNameAsync() + ChatColor.LIGHT_PURPLE + " has removed " + this.toEdit.getTag(this.faction) + "'s" + ChatColor.LIGHT_PURPLE + " /f perm access to " + ChatColor.AQUA + "\"" + permission.getBareName() + "\"" + ChatColor.LIGHT_PURPLE + " in " + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE + this.chunk.formatXAndZ(",") + ChatColor.LIGHT_PURPLE + "!");
                        P.p.logFactionEvent(this.faction, FLogType.PERM_EDIT_CHUNK, player.getName(), ChatColor.RED.toString() + ChatColor.BOLD + "REMOVED", permission.getBareName(), this.toEdit.getTag(this.faction), this.chunk.formatXAndZ(","));
                    }
                    perm.storeChunkEditted(this.chunk, this.toEdit);
                    this.drawItems();
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to modify Faction Permissions!");
                }
            }));
        }
    }
}

