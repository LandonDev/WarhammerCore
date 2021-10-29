/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
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

import com.cosmicpvp.cosmicutils.utils.CC;
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

import java.util.*;

public class PlayerPermMenu
        extends GUIMenu {
    private FPlayer viewing;
    private FPlayer toEdit;
    private Faction faction;
    private FLocation start;

    public PlayerPermMenu(FPlayer viewing, Faction faction, FLocation location, FPlayer toEdit) {
        super(toEdit.getNameAsync() + " for " + location.formatXAndZ(""), 45);
        this.viewing = viewing;
        this.toEdit = toEdit;
        this.start = location;
        this.faction = faction;
    }

    @Override
    public void drawItems() {
        Set<FactionPermission> chunkPermissions;
        FactionPermissions currentPerms = PermissionManager.get().getPermissions(this.faction);
        Map<UUID, Set<FactionPermission>> allChunkPerms = currentPerms.getPlayerPermissionMap().get(this.start);
        Set<FactionPermission> set = chunkPermissions = allChunkPerms != null ? allChunkPerms.get(this.toEdit.getCachedUUID()) : null;
        if (this.getPreviousMenu() != null) {
            this.setItem(0, this.getBackButton(null, null, new String[]{null}));
        }
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
                if (this.faction.getRelationTo(this.viewing) == Relation.MEMBER && this.viewing.getRole() != null && this.faction.hasRolePerm(this.viewing, RolePerm.EDIT_PLAYER_PERMS)) {
                    if (this.faction.getRelationTo(this.toEdit) == Relation.MEMBER && this.viewing.getRole().value <= this.toEdit.getRole().value) {
                        player.sendMessage(CC.RedB + "(!) " + CC.Red + "You cannot control " + this.toEdit.getNameAsync() + "'s /f perm's as they are an equal or higher faction rank!");
                        return;
                    }
                    FactionPermissions perm = PermissionManager.get().getPermissions(this.faction);
                    Map chunkPerms = perm.getPlayerPermissionMap().computeIfAbsent(this.start, k -> new HashMap<>());
                    Set<FactionPermission> perms = (Set<FactionPermission>) chunkPerms.computeIfAbsent(this.toEdit.getCachedUUID(), newSet -> new HashSet<>());
                    Relation rel = this.faction.getRelationTo(this.toEdit);
                    boolean removed = perms.remove(permission);
                    if (!removed) {
                        perms.add(permission);
                        this.faction.sendMessage(ChatColor.AQUA + this.viewing.getRole().getPrefix() + this.viewing.getNameAsync() + ChatColor.LIGHT_PURPLE + " has given " + rel.getColor() + this.toEdit.getNameAndTagAsync() + ChatColor.LIGHT_PURPLE + " /f perm access to " + ChatColor.AQUA + "\"" + permission.getBareName() + "\"" + ChatColor.LIGHT_PURPLE + " in " + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE + this.start.formatXAndZ(",") + ChatColor.LIGHT_PURPLE + "!");
                        P.p.logFactionEvent(this.faction, FLogType.PERM_EDIT_CHUNK, player.getName(), ChatColor.GREEN.toString() + ChatColor.BOLD + "GRANTED", permission.getBareName(), this.toEdit.getNameAsync(), this.start.formatXAndZ(","));
                    } else {
                        this.faction.sendMessage(ChatColor.AQUA + this.viewing.getRole().getPrefix() + this.viewing.getNameAsync() + ChatColor.LIGHT_PURPLE + " has removed " + rel.getColor() + this.toEdit.getNameAndTagAsync() + "'s" + ChatColor.LIGHT_PURPLE + " /f perm access to " + ChatColor.AQUA + "\"" + permission.getBareName() + "\"" + ChatColor.LIGHT_PURPLE + " in " + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE + this.start.formatXAndZ(",") + ChatColor.LIGHT_PURPLE + "!");
                        P.p.logFactionEvent(this.faction, FLogType.PERM_EDIT_CHUNK, player.getName(), ChatColor.RED.toString() + ChatColor.BOLD + "REMOVED", permission.getBareName(), this.toEdit.getNameAsync(), this.start.formatXAndZ(","));
                    }
                    perm.storeChunkEditted(this.start, this.toEdit);
                    this.drawItems();
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to modify Faction Permissions!");
                }
            }));
        }
    }
}

