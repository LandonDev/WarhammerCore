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
package com.massivecraft.factions.listeners.menu.fperm;

import com.google.common.collect.Lists;
import com.massivecraft.factions.*;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.listeners.menu.ClickableItemStack;
import com.massivecraft.factions.listeners.menu.GUIMenu;
import com.massivecraft.factions.struct.FactionPermissions;
import com.massivecraft.factions.struct.PermissionManager;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

public class PermGlobalMenu
        extends GUIMenu {
    private FPlayer fplayer;
    private Faction faction;
    private int page = 1;

    public PermGlobalMenu(FPlayer fplayer, Faction faction) {
        super("Default Permissions", 54);
        this.fplayer = fplayer;
        this.faction = faction;
    }

    @Override
    public void drawItems() {
        LinkedList factionsAndPlayersWithAccess = new LinkedList();
        FactionWrapper wrapper = FactionWrappers.get(this.faction);
        if (wrapper == null) {
            return;
        }
        wrapper.chunkClaims.forEach((loc, perms) -> perms.forEach(perm -> {
            Faction fac;
            FPlayer fplayer;
            String[] split = perm.split(":");
            String type = split[0];
            String name = split[1];
            if (type.equals("faction")) {
                Faction faction = Factions.i.get(name);
                if (faction != null && !factionsAndPlayersWithAccess.contains(faction) && faction.getRelationWish(this.faction) != Relation.ENEMY) {
                    factionsAndPlayersWithAccess.add(faction);
                }
            } else if (type.equals("player") && (fplayer = FPlayers.i.get(name)) != null && (fac = fplayer.getFaction()) != null && fac.isNormal() && !factionsAndPlayersWithAccess.contains(fplayer) && fac.getRelationWish(this.faction) != Relation.ENEMY) {
                factionsAndPlayersWithAccess.add(fplayer);
            }
        }));
        FactionPermissions currentPerms = PermissionManager.get().getPermissions(this.faction);
        currentPerms.getDefaultPlayerPermissions().forEach((uuid, permSet) -> {
            FPlayer player = FPlayers.i.get(uuid.toString());
            if (factionsAndPlayersWithAccess.contains(player)) {
                return;
            }
            factionsAndPlayersWithAccess.add(player);
        });
        currentPerms.getFactionDefaultPermissions().forEach((factionID, permSet) -> {
            Faction faction = Factions.i.get(factionID);
            if (faction != null && faction.isNormal() && !factionsAndPlayersWithAccess.contains(faction)) {
                factionsAndPlayersWithAccess.add(faction);
            }
        });
        this.setInventorySize(PermGlobalMenu.fitSlots(factionsAndPlayersWithAccess.size() + (this.getPreviousMenu() != null ? 1 : 0)));
        this.clearItems();
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
        int totalLength = factionsAndPlayersWithAccess.size();
        int perPage = 52;
        int maxPages = (int) Math.ceil((double) totalLength / (double) perPage);
        this.page = Math.max(Math.min(maxPages, this.page), 1);
        int slot = 0;
        if (this.getPreviousMenu() != null) {
            this.setItem(slot++, this.getBackButton(null, null, new String[]{null}));
        }
        int start = this.page * perPage - perPage;
        int end = start + perPage;
        for (int i = start; i < end && i < factionsAndPlayersWithAccess.size(); ++i) {
            EconomyParticipator ent = (EconomyParticipator) factionsAndPlayersWithAccess.get(i);
            Relation relation = ent.getRelationTo(this.faction);
            boolean player = ent instanceof FPlayer;
            FPlayer playerInstead = player ? (FPlayer) ent : null;
            Faction fac = !player ? (Faction) ent : null;
            this.setItem(slot++, new ClickableItemStack(player ? ItemUtil.createPlayerHead(playerInstead.getNameAsync()) : new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) relation.getDyeColor().getWoolData())).setDisplayName(ChatColor.GREEN.toString() + ChatColor.BOLD + (player ? "Player: " : "Faction: ") + relation.getColor() + (playerInstead != null ? playerInstead.getNameAsync() : fac.getTag())).setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GREEN + "Left-Click" + ChatColor.GRAY + " to view Default " + (player ? "Player" : "Faction") + " permissions", ChatColor.GREEN + "Right-Click" + ChatColor.GRAY + " to remove Default Permissions."})).setClickCallback(e -> {
                if (e.getClick() == ClickType.RIGHT) {
                    if (player) {
                        currentPerms.getDefaultPlayerPermissions().remove(playerInstead.getCachedUUID());
                    } else {
                        currentPerms.getFactionDefaultPermissions().remove(fac.getId());
                    }
                    this.fplayer.sendMessage(ChatColor.RED + "Default Permissions removed for " + (playerInstead != null ? playerInstead.getNameAsync() : fac.getTag()) + "!");
                    this.drawItems();
                } else if (player) {
                    new DefaultPlayerPermMenu(this.fplayer, this.faction, playerInstead).setPreviousMenu(this).open(this.fplayer.getPlayer());
                } else {
                    new DefaultFactionPermMenu(this.fplayer, this.faction, fac).setPreviousMenu(this).open(this.fplayer.getPlayer());
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

