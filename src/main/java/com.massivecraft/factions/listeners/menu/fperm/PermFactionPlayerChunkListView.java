/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.material.MaterialData
 */
package com.massivecraft.factions.listeners.menu.fperm;

import com.google.common.collect.Lists;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.listeners.menu.ClickableItemStack;
import com.massivecraft.factions.listeners.menu.GUIMenu;
import com.massivecraft.factions.struct.FactionPermission;
import com.massivecraft.factions.struct.FactionPermissions;
import com.massivecraft.factions.struct.PermissionManager;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Deprecated
public class PermFactionPlayerChunkListView
        extends GUIMenu {
    private FPlayer viewing;
    private Faction owning;
    private Faction toEdit;
    private Set<FPlayer> playersToShow;
    private FLocation chunk;

    public PermFactionPlayerChunkListView(FPlayer viewing, Faction owning, FLocation chunk, Faction toEdit, Set<FPlayer> playersToShow) {
        super("Player Access to " + chunk.formatXAndZ(""), 54);
        this.viewing = viewing;
        this.owning = owning;
        this.toEdit = toEdit;
        this.chunk = chunk;
        this.playersToShow = playersToShow;
    }

    @Override
    public void drawItems() {
        int slot = 0;
        this.clearItems();
        this.setInventorySize(PermFactionPlayerChunkListView.fitSlots(this.playersToShow.size() + (this.getPreviousMenu() != null ? 1 : 0)));
        if (this.getPreviousMenu() != null) {
            this.setItem(slot++, this.getBackButton(null, null, new String[]{null}));
        }
        Relation relation = this.toEdit.getRelationTo(this.owning);
        for (FPlayer player : this.playersToShow) {
            this.setItem(slot++, new ClickableItemStack(ItemUtil.createPlayerHead(player.getNameAsync())).setDisplayName(relation.getColor().toString() + ChatColor.BOLD + player.getNameAsync()).setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GREEN + "Left-Click" + ChatColor.GRAY + " to view player permissions", ChatColor.GRAY + "for this chunk.", "", ChatColor.GREEN + "Right-Click" + ChatColor.GRAY + " to clear player permissions", ChatColor.GRAY + "for this chunk."})).setClickCallback(e -> {
                if (e.getClick() == ClickType.RIGHT) {
                    FactionPermissions permissions = PermissionManager.get().getPermissions(this.owning);
                    if (permissions != null) {
                        Map<UUID, Set<FactionPermission>> permissionMap = permissions.getPlayerPermissionMap().get(this.chunk);
                        if (permissionMap.remove(player.getCachedUUID()) != null) {
                            this.viewing.sendMessage(ChatColor.RED + "Chunk Permissions removed from " + this.chunk.formatXAndZ("") + " for " + player.getNameAsync() + "!");
                            this.viewing.sendMessage(ChatColor.GRAY + "Use /f access p <player> to add permissions!");
                            this.playersToShow.remove(player);
                            this.drawItems();
                        } else {
                            this.viewing.sendMessage(ChatColor.RED + "No Chunk Permissions found for " + player.getNameAsync() + "!");
                        }
                    }
                } else {
                    new PlayerPermMenu(this.viewing, this.owning, this.chunk, player).setPreviousMenu(this).open(this.viewing.getPlayer());
                }
            }));
        }
    }
}

