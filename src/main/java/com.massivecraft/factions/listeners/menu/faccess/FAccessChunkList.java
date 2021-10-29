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
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.listeners.menu.ClickableItemStack;
import com.massivecraft.factions.listeners.menu.GUIMenu;
import com.massivecraft.factions.listeners.menu.fperm.PlayerPermMenu;
import com.massivecraft.factions.struct.FactionPermission;
import com.massivecraft.factions.struct.FactionPermissions;
import com.massivecraft.factions.struct.PermissionManager;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class FAccessChunkList
        extends GUIMenu {
    private FPlayer player;
    private Faction faction;
    private FLocation chunk;
    private int page = 1;

    public FAccessChunkList(FPlayer player, Faction faction, FLocation chunk) {
        super("/f access at " + chunk.formatXAndZ(""), 54);
        this.player = player;
        this.faction = faction;
        this.chunk = chunk;
    }

    @Override
    public void drawItems() {
        this.clearItems();
        FactionWrapper wrapper = FactionWrappers.get(this.faction);
        Set<String> perms = wrapper.chunkClaims.get(this.chunk);
        FactionPermissions permissions = PermissionManager.get().getPermissions(this.faction);
        LinkedList permList = new LinkedList();
        Map<UUID, Set<FactionPermission>> chunkPerms = permissions.getPlayerPermissionMap().get(this.chunk);
        if (chunkPerms != null) {
            chunkPerms.keySet().forEach(id -> {
                FPlayer pl = FPlayers.i.get(id.toString());
                if (pl != null && !permList.contains(pl)) {
                    permList.add(pl);
                }
            });
        }
        LinkedList<EconomyParticipator> foundList = wrapper.getFactionWithPlayersWithAccessList(this.faction, perms);
        foundList.forEach(e -> {
            if (!permList.contains(e)) {
                permList.add(e);
            }
        });
        this.setInventorySize(FAccessChunkList.fitSlots(permList.size() + (this.getPreviousMenu() != null ? 1 : 0)));
        int slot = 0;
        int totalLength = permList.size();
        int perPage = 52;
        int maxPages = (int) Math.ceil((double) totalLength / (double) perPage);
        if (this.getPreviousMenu() != null && this.page == 1) {
            this.setItem(slot++, this.getBackButton(null, null, null));
        }
        this.page = Math.max(Math.min(maxPages, this.page), 1);
        int startIndex = this.page * perPage - perPage;
        int end = startIndex + perPage;
        for (int i = startIndex; i < end && i < permList.size(); ++i) {
            EconomyParticipator ent = (EconomyParticipator) permList.get(i);
            Relation relation = ent.getRelationTo(this.faction);
            boolean player = ent instanceof FPlayer;
            FPlayer playerInstead = player ? (FPlayer) ent : null;
            Faction fac = !player ? (Faction) ent : null;
            ArrayList lore = Lists.newArrayList();
            if (player) {
                lore = Lists.newArrayList((Object[]) new String[]{ChatColor.GREEN + "Left-Click" + ChatColor.GRAY + " to view their chunk permissions", ChatColor.GREEN + "Right-Click" + ChatColor.GRAY + " to remove their access to this chunk"});
            } else {
                lore.add(ChatColor.GREEN + "Right-Click" + ChatColor.GRAY + " to remove Faction /f access.");
            }
            ItemStack pane = player ? ItemUtil.createPlayerHead(playerInstead.getNameAsync()) : new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) relation.getDyeColor().getWoolData());
            this.setItem(slot++, new ClickableItemStack(pane).setDisplayName(fac != null ? ChatColor.GREEN + ChatColor.BOLD.toString() + "Faction: " + relation.getColor() + fac.getTag() : ChatColor.GREEN + ChatColor.BOLD.toString() + "Player: " + relation.getColor() + playerInstead.getNameAsync()).setLore(lore).setClickCallback(e -> {
                if (playerInstead != null) {
                    if (e.getClick() == ClickType.LEFT) {
                        new PlayerPermMenu(this.player, this.faction, this.chunk, playerInstead).setPreviousMenu(this).open(this.player.getPlayer());
                    } else if (e.getClick() == ClickType.RIGHT) {
                        if (wrapper.removePlayerAccess(this.chunk, playerInstead)) {
                            this.player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "The player " + playerInstead.getNameAsync() + " no longer has access to the chunk " + this.chunk.formatXAndZ("") + "!");
                            playerInstead.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "You no longer have access to " + this.faction.getTag() + "'s claimed chunk at " + this.chunk.formatXAndZ("") + "!");
                            permList.remove(playerInstead);
                            this.drawItems();
                        } else {
                            this.player.sendMessage(ChatColor.RED + "That player no longer has access to that chunk!");
                        }
                    }
                } else if (wrapper.removeFactionAccess(this.chunk, fac)) {
                    this.faction.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "The faction " + fac.getTag() + " no longer has access to the chunk " + this.chunk.formatXAndZ(",") + "!");
                    this.drawItems();
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

