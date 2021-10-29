/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.cosmicpvp.cosmicutils.utils.NBTWrapper
 *  org.apache.commons.lang.StringUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.inventory.meta.ItemMeta
 */
package com.massivecraft.factions.listeners.menu.fchest;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.FLogType;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class FChestListener
        implements Listener {
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory() != null && event.getInventory().getName().startsWith("/f chest")) {
            event.setCancelled(true);
            Player p = (Player) event.getWhoClicked();
            p.sendMessage(CC.RedB + "(!) " + CC.Red + "You cannot drag items while viewing a /f chest!");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerClickInventory(InventoryClickEvent event) {
        if (event.getInventory().getName().startsWith("/f chest")) {
            Faction faction;
            Material cursorItemType;
            Material currentItemType;
            Player player = (Player) event.getWhoClicked();
            if (event.getClick() == ClickType.UNKNOWN) {
                event.setCancelled(true);
                player.sendMessage(CC.RedB + "(!) " + CC.Red + "You cannot use that click type inside the /f chest!");
                return;
            }
            ItemStack currentItem = event.getCurrentItem();
            if (event.getClick() == ClickType.NUMBER_KEY) {
                currentItem = event.getClickedInventory().getItem(event.getSlot());
            }
            Material material = currentItemType = currentItem != null ? currentItem.getType() : Material.AIR;
            if (currentItemType != Material.AIR && !this.canPlaceItemInside(currentItem)) {
                event.setCancelled(true);
                player.sendMessage(CC.RedB + "(!) " + CC.Red + "You cannot interact with that item while viewing your /f chest!");
                return;
            }
            ItemStack cursorItem = event.getCursor();
            if (event.getClick() == ClickType.NUMBER_KEY) {
                cursorItem = player.getInventory().getItem(event.getHotbarButton());
            }
            Material material2 = cursorItemType = cursorItem != null ? cursorItem.getType() : Material.AIR;
            if (cursorItemType != Material.AIR && !this.canPlaceItemInside(cursorItem)) {
                event.setCancelled(true);
                player.sendMessage(CC.RedB + "(!) " + CC.Red + "You cannot interact with that cursor item while viewing your /f chest!");
                return;
            }
            FPlayer fplayer = FPlayers.i.get(player);
            if (fplayer == null || !(faction = fplayer.getFaction()).isNormal()) {
                player.closeInventory();
                player.sendMessage(CC.RedB + "(!) " + CC.Red + "You are no longer in your faction!");
                return;
            }
            if (event.getClickedInventory() == null) {
                return;
            }
            if (event.getClickedInventory().getName().startsWith("/f chest")) {
                if (currentItemType != Material.AIR) {
                    Inventory ours = P.p.getFChestManager().getFchestItems().get(faction.getId());
                    if (ours == null || !ours.contains(currentItem)) {
                        event.setCancelled(true);
                        player.sendMessage(CC.RedB + "(!) That item not longer exists!");
                        Bukkit.getLogger().info("[FactionChest] " + player.getName() + " tried to remove " + currentItem + " from /f chest when it didnt contain! Items: " + (ours == null ? "none" : Arrays.toString(ours.getContents())));
                        player.closeInventory();
                        return;
                    }
                    this.logRemoveItem(currentItem, fplayer, player);
                } else if (cursorItemType != Material.AIR && !event.isShiftClick()) {
                    this.logAddItem(cursorItem, fplayer, player);
                }
            } else if (event.isShiftClick() && currentItemType != Material.AIR) {
                this.logAddItem(currentItem, fplayer, player);
            }
        }
    }

    private void logAddItem(ItemStack cursorItem, FPlayer fplayer, Player player) {
        String itemName = cursorItem.hasItemMeta() && cursorItem.getItemMeta().hasDisplayName() ? cursorItem.getItemMeta().getDisplayName() : StringUtils.capitaliseAllWords(cursorItem.getType().name().replace("_", " ").toLowerCase());
        P.p.logFactionEvent(fplayer.getFaction(), FLogType.FCHEST_EDIT, player.getName(), CC.GreenB + "ADDED", itemName);
    }

    private void logRemoveItem(ItemStack currentItem, FPlayer fplayer, Player player) {
        String itemName = currentItem.hasItemMeta() && currentItem.getItemMeta().hasDisplayName() ? currentItem.getItemMeta().getDisplayName() : StringUtils.capitaliseAllWords(currentItem.getType().name().replace("_", " ").toLowerCase());
        P.p.logFactionEvent(fplayer.getFaction(), FLogType.FCHEST_EDIT, player.getName(), CC.RedB + "TOOK", itemName);
    }

    private boolean canPlaceItemInside(ItemStack item) {
        String materialName;
        Material material = item.getType();
        return material == Material.QUARTZ || material == Material.INK_SACK && item.getDurability() == 14 && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore() || (materialName = material.name()).endsWith("_PICKAXE") || materialName.endsWith("_SPADE") || new NBTItem(item).getString("cosmicType").equals("fPointNote") || material == Material.BUCKET || material == Material.LAVA_BUCKET || material == Material.WATER_BUCKET || material == Material.MOB_SPAWNER;
    }
}

