package com.massivecraft.factions.listeners.menu.faccess;

import com.google.common.collect.Lists;
import com.massivecraft.factions.*;
import com.massivecraft.factions.listeners.menu.ClickableItemStack;
import com.massivecraft.factions.listeners.menu.GUIMenu;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.*;

public class FAccessPlayerMenu extends GUIMenu {
    int page;
    private Player player;
    private Faction faction;

    public FAccessPlayerMenu(final Player viewing, final Faction faction) {
        super("/f access: Players", 54);
        this.page = 1;
        this.player = viewing;
        this.faction = faction;
    }

    @Override
    public void drawItems() {
        this.clearItems();
        FactionWrapper wrapper = FactionWrappers.get(this.faction);
        wrapper.chunkClaims.forEach((loc, access) -> access.forEach(entry -> {

            String[] split;
            FPlayer fPlayer;
            Map<FPlayer, Set<FLocation>> map = new HashMap<>();
            Set<FLocation> claimed;
            split = entry.split(":");
            if (split.length > 0 && split[0].equals("player")) {
                fPlayer = FPlayers.i.get(split[1]);
                if (fPlayer != null) {
                    claimed = map.computeIfAbsent(fPlayer, set -> new HashSet<>());
                    claimed.add(loc);
                }
            }
        }));
        Map<FPlayer, Set<FLocation>> chunkAccess = new LinkedHashMap<FPlayer, Set<FLocation>>();
        final List<Map.Entry<FPlayer, Set<FLocation>>> sortedEntries = new LinkedList<Map.Entry<FPlayer, Set<FLocation>>>(chunkAccess.entrySet());
        sortedEntries.sort(Comparator.comparing(o -> o.getKey().getNameAsync()));
        this.setInventorySize(GUIMenu.fitSlots(sortedEntries.size() + ((this.getPreviousMenu() != null) ? 1 : 0)));
        final int perPage = 51;
        final int maxPages = (int) Math.ceil(sortedEntries.size() / perPage);
        this.page = Math.max(1, Math.min(this.page, maxPages));
        final int start = this.page * perPage - perPage;
        int slot = 0;
        if (this.getPreviousMenu() != null) {
            this.setItem(slot++, this.getBackButton(new MaterialData(Material.STAINED_GLASS_PANE, DyeColor.RED.getWoolData()), null, (String[]) null));
        }
        for (int i = start; i < start + perPage && i < sortedEntries.size(); ++i) {
            final Map.Entry<FPlayer, Set<FLocation>> entry2 = sortedEntries.get(i);
            final FPlayer player = entry2.getKey();
            final Set<FLocation> found = entry2.getValue();
            final Faction playerFaction = player.getFaction();
            final Relation rel = this.faction.getRelationTo(playerFaction);
            final List<String> lore = Lists.newArrayList((String[]) new String[]{"", rel.getColor() + ChatColor.BOLD.toString() + "Faction", ChatColor.WHITE + playerFaction.getTag() + rel.getColor() + " (" + ChatColor.stripColor(rel.nicename) + ")", "", ChatColor.YELLOW + ChatColor.BOLD.toString() + "Chunk Access (" + found.size() + ")"});
            int limit = 0;
            for (final FLocation loc2 : found) {
                if (limit >= 10) {
                    lore.add(ChatColor.YELLOW + ChatColor.BOLD.toString() + " * " + ChatColor.WHITE + (found.size() - limit) + " more...");
                    break;
                }
                lore.add(ChatColor.YELLOW + ChatColor.BOLD.toString() + " * " + ChatColor.WHITE + FLocation.chunkToBlock((int) loc2.getX()) + "x " + FLocation.chunkToBlock((int) loc2.getZ()) + "z");
                ++limit;
            }
            lore.add("");
            lore.add(ChatColor.GREEN + "Right-Click " + ChatColor.GRAY + "to clear all access.");
            final Iterable iterable = entry2.getValue();
            final FactionWrapper factionWrapper;
            this.setItem(slot++, new ClickableItemStack(ItemUtil.createPlayerHead(player.getNameAsync())).setDisplayName(rel.getColor() + player.getNameAsync()).setLore(lore).setClickCallback(e -> {
                if (e.getClick() == ClickType.RIGHT) {
                    iterable.forEach(loc -> wrapper.removePlayerAccess((FLocation) loc, player));
                    this.player.sendMessage(ChatColor.RED + "All chunk access has been revoked from " + player.getNameAsync());
                    this.drawItems();
                }
                return;
            }));
        }
        if (this.page >= 1 && this.page < maxPages) {
            this.setItem(this.getSize() - 1, new ClickableItemStack(new ItemStack(Material.ARROW)).setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Next").setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GRAY + "Click to view the next page of players."})).setClickCallback(e -> {
                ++this.page;
                this.drawItems();
                return;
            }));
        }
        if (this.page > 1) {
            this.setItem(this.getSize() - 9, new ClickableItemStack(new ItemStack(Material.ARROW)).setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Previous").setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GRAY + "Click to view the last page of players."})).setClickCallback(e -> {
                --this.page;
                this.drawItems();
            }));
        }
    }
}
