/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.material.MaterialData
 */
package com.massivecraft.factions.listeners.menu.fperm;

import com.google.common.collect.Lists;
import com.massivecraft.factions.*;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.listeners.menu.ClickableItemStack;
import com.massivecraft.factions.listeners.menu.GUIMenu;
import com.massivecraft.factions.struct.FactionPermission;
import com.massivecraft.factions.struct.FactionPermissions;
import com.massivecraft.factions.struct.PermissionManager;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PermFacMenu
        extends GUIMenu {
    private FPlayer player;
    private Faction faction;
    private int page = 1;

    public PermFacMenu(FPlayer player, Faction faction) {
        super("/f perm: Current Chunk Perms", 54);
        this.player = player;
        this.faction = faction;
    }

    @Override
    public void drawItems() {
        Map<String, Set<FactionPermission>> chunkFacPerms;
        this.clearItems();
        FLocation start = new FLocation(this.player);
        FactionWrapper wrapper = FactionWrappers.get(this.faction);
        Set<String> perms = wrapper.chunkClaims.get(start);
        LinkedList factionsAndPlayersWithAccess = new LinkedList();
        FactionPermissions permissions = PermissionManager.get().getPermissions(this.faction);
        Map<UUID, Set<FactionPermission>> chunkPerms = permissions.getPlayerPermissionMap().get(start);
        if (chunkPerms != null) {
            chunkPerms.keySet().forEach(id -> {
                FPlayer pl = FPlayers.i.get(id.toString());
                if (pl != null) {
                    if (!factionsAndPlayersWithAccess.contains(pl)) {
                        factionsAndPlayersWithAccess.add(pl);
                    }
                } else {
                    Bukkit.getLogger().info("Null fplayer: " + id.toString());
                }
            });
        }
        if ((chunkFacPerms = permissions.getFactionChunkPermissionMap().get(start)) != null) {
            chunkFacPerms.keySet().forEach(id -> {
                Faction fac = Factions.i.get(id);
                if (fac != null && fac.isNormal() && !factionsAndPlayersWithAccess.contains(fac)) {
                    factionsAndPlayersWithAccess.add(fac);
                }
            });
        }
        wrapper.getFactionWithPlayersWithAccessList(this.faction, perms).stream().filter(perm -> perm != null && !factionsAndPlayersWithAccess.contains(perm)).forEach(factionsAndPlayersWithAccess::add);
        factionsAndPlayersWithAccess.sort((o1, o2) -> {
            if (o1 instanceof Faction && o2 instanceof Faction) {
                return ((Faction) o1).getTag().compareTo(((Faction) o2).getTag());
            }
            if (o1 instanceof FPlayer && o2 instanceof FPlayer) {
                return ((FPlayer) o1).getNameAsync().compareTo(((FPlayer) o2).getNameAsync());
            }
            if (o1 instanceof FPlayer && o2 instanceof Faction) {
                return -1;
            }
            return 1;
        });
        this.setInventorySize(PermFacMenu.fitSlots(factionsAndPlayersWithAccess.size() + (this.getPreviousMenu() != null ? 1 : 0)));
        int slot = 0;
        int totalLength = factionsAndPlayersWithAccess.size();
        int perPage = 52;
        int maxPages = (int) Math.ceil((double) totalLength / (double) perPage);
        if (this.getPreviousMenu() != null && this.page == 1) {
            this.setItem(slot++, this.getBackButton(null, null, null));
        }
        this.page = Math.max(Math.min(maxPages, this.page), 1);
        int startIndex = this.page * perPage - perPage;
        int end = startIndex + perPage;
        for (int i = startIndex; i < end && i < factionsAndPlayersWithAccess.size(); ++i) {
            Faction fac;
            EconomyParticipator ent = (EconomyParticipator) factionsAndPlayersWithAccess.get(i);
            if (ent == null) continue;
            Relation relation = ent.getRelationTo(this.faction);
            boolean player = ent instanceof FPlayer;
            FPlayer playerInstead = player ? (FPlayer) ent : null;
            Faction faction = fac = !player ? (Faction) ent : null;
            if (fac != null && fac.getRelationTo(this.player) == Relation.ENEMY) {
                permissions.cleanupPermissions(fac);
                factionsAndPlayersWithAccess.remove(ent);
                Bukkit.getLogger().info("Cleaning up enemy relation in permissions for " + fac.getTag());
                continue;
            }
            ArrayList lore = Lists.newArrayList();
            if (player && permissions.isDefaultPermission(start, playerInstead) || fac != null && permissions.isDefaultPermission(start, fac)) {
                lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "No chunk specific permissions have");
                lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "been set for this " + (player ? "player" : "faction") + " in this chunk");
                lore.add("");
            }
            lore.addAll(player ? Lists.newArrayList((Object[]) new String[]{ChatColor.GREEN + "Left-Click" + ChatColor.GRAY + " to view player permissions", ChatColor.GRAY + "for this chunk.", "", ChatColor.GREEN + "Right-Click" + ChatColor.GRAY + " to clear player permissions", ChatColor.GRAY + "for this chunk."}) : Lists.newArrayList((Object[]) new String[]{ChatColor.GREEN + "Click" + ChatColor.GRAY + " to view Chunk Faction Permissions"}));
            this.setItem(slot++, new ClickableItemStack(player ? ItemUtil.createPlayerHead(playerInstead.getNameAsync()) : new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) relation.getDyeColor().getWoolData())).setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + (player ? "Player: " : "Faction: ") + relation.getColor() + (playerInstead != null ? playerInstead.getNameAsync() : fac.getTag())).setLore(lore).setClickCallback(e -> {
                if (player) {
                    if (e.getClick() == ClickType.RIGHT) {
                        FactionPermissions permMap = PermissionManager.get().getPermissions(this.faction);
                        if (permMap != null) {
                            Map<UUID, Set<FactionPermission>> permissionMap = permMap.getPlayerPermissionMap().get(start);
                            if (permissionMap != null && permissionMap.remove(playerInstead.getCachedUUID()) != null) {
                                this.player.sendMessage(ChatColor.RED + "Chunk Permissions removed from " + start.formatXAndZ("") + " for " + playerInstead.getNameAsync() + "!");
                                this.player.sendMessage(ChatColor.GRAY + "Use /f access p <player> to add permissions!");
                                factionsAndPlayersWithAccess.remove(ent);
                                this.drawItems();
                            } else {
                                this.player.sendMessage(ChatColor.RED + "No Chunk Permissions found for " + playerInstead.getNameAsync() + "!");
                            }
                        }
                    } else {
                        new PlayerPermMenu(this.player, this.faction, start, playerInstead).setPreviousMenu(this).open(this.player.getPlayer());
                    }
                } else {
                    new PermFactionChunkListView(this.player, this.faction, fac, start).setPreviousMenu(this).open(this.player.getPlayer());
                }
            }));
        }
        if (maxPages > 1 && this.page < maxPages) {
            this.setItem(this.getSize() - 1, new ClickableItemStack(new ItemStack(Material.ARROW)).setDisplayName(ChatColor.GREEN + "Next Page").setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GRAY + "Click to view next page."})).setClickCallback(e -> {
                ++this.page;
                this.drawItems();
            }));
        }
        if (this.page > 1) {
            this.setItem(this.getSize() - 9, new ClickableItemStack(new ItemStack(Material.ARROW)).setDisplayName(ChatColor.RED + "Previous Page").setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GRAY + "Click to view previous page."})).setClickCallback(e -> {
                --this.page;
                this.drawItems();
            }));
        }
    }
}

