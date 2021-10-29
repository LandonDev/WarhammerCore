/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.google.common.collect.Lists
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 */
package com.massivecraft.factions.listeners.menu.fperm;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.listeners.menu.ClickableItemStack;
import com.massivecraft.factions.listeners.menu.GUIMenu;
import com.massivecraft.factions.listeners.menu.faccess.RolePermMenu;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PermMainMenu
        extends GUIMenu {
    private FPlayer fplayer;
    private Faction faction;

    public PermMainMenu(FPlayer fplayer, Faction faction) {
        super("/f perm Menu", 27);
        this.fplayer = fplayer;
        this.faction = faction;
    }

    @Override
    public void drawItems() {
        this.setItem(12, new ClickableItemStack(new ItemStack(Material.GRASS)).setDisplayName(ChatColor.AQUA.toString() + ChatColor.BOLD + "Current Chunk Permissions").setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GRAY + "Click to view all permissions", ChatColor.GRAY + "for players and factions."})).setClickCallback(e -> {
            FLocation at = new FLocation(this.fplayer);
            Faction facAt = Board.getFactionAt(at);
            if (facAt != null && facAt.equals(this.fplayer.getFaction())) {
                new PermFacMenu(this.fplayer, this.faction).setPreviousMenu(this).open((Player) e.getWhoClicked());
            } else {
                e.getWhoClicked().closeInventory();
                this.fplayer.sendMessage(ChatColor.RED + "You must own the chunk you are trying to view permissions for!");
            }
        }));
        this.setItem(4, new ClickableItemStack(new ItemStack(Material.BOOK)).setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "/f perm Help").setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GRAY + "You can use this menu to customize", ChatColor.GRAY + "what specific types of blocks your", ChatColor.GRAY + "/f access'd factions/players can modify", ChatColor.GRAY + "in your current chunk, and by default.", "", ChatColor.AQUA + "/f access is REQUIRED for /f perm", ChatColor.GRAY + "Players and Factions will not show up", ChatColor.GRAY + "in this menu until they have /f access", ChatColor.GRAY + "to atleast one of your faction's chunks.", "", ChatColor.AQUA + "Current Chunk Permissions", ChatColor.GRAY + "Edits the permissions of the specified", ChatColor.GRAY + "player or faction in the current chunk", ChatColor.GRAY + "you are standing in. These permissions", ChatColor.GRAY + ChatColor.UNDERLINE.toString() + "OVERRULE" + ChatColor.GRAY + " any Default Permissions.", "", ChatColor.AQUA + "Default Permissions", ChatColor.GRAY + "Edits the permissions for the", ChatColor.GRAY + "specified player or faction. These settings", ChatColor.GRAY + "are what the given entity will be able to", ChatColor.GRAY + "modify in chunks where no \"Current Chunk\""})));
        this.setItem(14, new ClickableItemStack(new ItemStack(Material.BEACON)).setDisplayName(ChatColor.AQUA.toString() + ChatColor.BOLD + "Default Permissions").setLore(Lists.newArrayList((String[]) new String[]{ChatColor.GRAY + "Click to view all default permissions", ChatColor.GRAY + "for players and factions."})).setClickCallback(e -> new PermGlobalMenu(this.fplayer, this.faction).setPreviousMenu(this).open((Player) e.getWhoClicked())));
        this.setItem(22, new ClickableItemStack(new ItemStack(Material.BOOK_AND_QUILL)).setDisplayName(CC.AquaB + "/f roles").setLore(Lists.newArrayList((String[]) new String[]{CC.Gray + "Click to view your faction role permissions.", CC.Gray + "Role permissions allow you to permit different", CC.Gray + "ranking faction members to perform specific tasks."})).setClickCallback(e -> new RolePermMenu(this.fplayer, (Player) e.getWhoClicked(), this.faction).open((Player) e.getWhoClicked())));
        this.fillEmpty(new ClickableItemStack(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) DyeColor.GRAY.getWoolData())).setDisplayName(" "));
    }
}

