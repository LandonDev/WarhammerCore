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
import java.util.HashSet;
import java.util.Set;

public class DefaultPlayerPermMenu
        extends GUIMenu {
    private Faction owning;
    private FPlayer player;
    private FPlayer toEdit;

    public DefaultPlayerPermMenu(FPlayer player, Faction owning, FPlayer toEdit) {
        super("/f perm: " + toEdit.getNameAsync() + " Permissions", 54);
        this.owning = owning;
        this.toEdit = toEdit;
        this.player = player;
    }

    @Override
    public void drawItems() {
        if (this.getPreviousMenu() != null) {
            this.setItem(0, this.getBackButton(null, null, new String[]{null}));
        }
        Relation relation = this.owning.getRelationTo(this.toEdit);
        FactionPermissions currentPerms = PermissionManager.get().getPermissions(this.owning);
        Set<FactionPermission> playerPermissions = currentPerms != null ? currentPerms.getDefaultPlayerPermissions().get(this.toEdit.getCachedUUID()) : null;
        for (FactionPermission permission : FactionPermission.values()) {
            boolean hasPerm = playerPermissions != null && playerPermissions.contains(permission);
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
                if (this.owning.getRelationTo(this.player) == Relation.MEMBER && this.player.getRole() != null && this.owning.hasRolePerm(this.player, RolePerm.EDIT_PLAYER_PERMS)) {
                    if (this.owning.getRelationTo(this.toEdit) == Relation.MEMBER && this.player.getRole().value <= this.toEdit.getRole().value) {
                        player.sendMessage(CC.RedB + "(!) " + CC.Red + "You cannot control " + this.toEdit.getNameAsync() + "'s /f perm's as they are an equal or higher faction rank!");
                        return;
                    }
                    FactionPermissions perm = PermissionManager.get().getPermissions(this.owning);
                    Set perms = perm.getDefaultPlayerPermissions().computeIfAbsent(this.toEdit.getCachedUUID(), newSet -> new HashSet<>());
                    boolean removed = perms.remove(permission);
                    if (!removed) {
                        perms.add(permission);
                        this.owning.sendMessage(ChatColor.AQUA + this.player.getRole().getPrefix() + this.player.getNameAsync() + ChatColor.LIGHT_PURPLE + " has given " + relation.getColor() + this.toEdit.getNameAsync() + ChatColor.LIGHT_PURPLE + " default /f perm access to " + ChatColor.AQUA + "\"" + permission.getBareName() + "\"" + ChatColor.LIGHT_PURPLE + "!");
                        P.p.logFactionEvent(this.owning, FLogType.PERM_EDIT_DEFAULTS, player.getName(), ChatColor.GREEN.toString() + ChatColor.BOLD + "GRANTED", permission.getBareName(), relation.getColor() + this.toEdit.getNameAsync());
                    } else {
                        this.owning.sendMessage(ChatColor.AQUA + this.player.getRole().getPrefix() + this.player.getNameAsync() + ChatColor.LIGHT_PURPLE + " has removed " + relation.getColor() + this.toEdit.getNameAsync() + "'s" + ChatColor.LIGHT_PURPLE + " default /f perm access to " + ChatColor.AQUA + "\"" + permission.getBareName() + "\"" + ChatColor.LIGHT_PURPLE + "!");
                        P.p.logFactionEvent(this.owning, FLogType.PERM_EDIT_DEFAULTS, player.getName(), ChatColor.RED.toString() + ChatColor.BOLD + "REMOVED", permission.getBareName(), relation.getColor() + this.toEdit.getNameAsync());
                    }
                    perm.updateClaimsWithDefaultPermissions(this.toEdit);
                    this.drawItems();
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to modify Faction Permissions!");
                }
            }));
        }
    }
}

