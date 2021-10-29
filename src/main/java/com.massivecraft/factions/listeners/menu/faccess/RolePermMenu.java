/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.cosmicpvp.cosmicutils.utils.ItemBuilder
 *  com.google.common.collect.Lists
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 */
package com.massivecraft.factions.listeners.menu.faccess;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.listeners.menu.ClickableItemStack;
import com.massivecraft.factions.listeners.menu.GUIMenu;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.struct.RolePerm;
import com.massivecraft.factions.util.CCItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class RolePermMenu
        extends GUIMenu {
    public Player player;
    public FPlayer fplayer;
    public Faction faction;

    public RolePermMenu(FPlayer fplayer, Player player, Faction faction) {
        super("Faction Roles", 18);
        this.player = player;
        this.fplayer = fplayer;
        this.faction = faction;
    }

    @Override
    public void drawItems() {
        int index = 1;
        ArrayList<Role> roles = Lists.newArrayList((Role[]) Role.values());
        Collections.reverse(roles);
        for (Role role : roles) {
            if (role == Role.ADMIN) continue;
            Set<RolePerm> activePermissions = this.faction.getRolePerms(role);
            ArrayList lore = Lists.newArrayList((Object[]) new String[]{"", CC.GoldB + "Permissions:"});
            RolePerm[] arrrolePerm = RolePerm.values();
            int n = arrrolePerm.length;
            for (RolePerm rolePerm : arrrolePerm) {
                RolePerm perm = rolePerm;
                lore.add(CC.Aqua + " " + perm.getDisplay() + " - " + (activePermissions.contains(rolePerm) ? CC.GreenB + "YES" : CC.RedB + "NO"));
            }
            this.setItem(index, new ClickableItemStack(new CCItemBuilder(Material.STAINED_GLASS_PANE, CC.GreenB + role.getDisplay()).lore(lore).data((short) role.getPaneColor().getWoolData()).build()).setClickCallback(e -> {
                e.setCancelled(true);
                new RolePermEditMenu(this.player, this.fplayer, role, this.faction).setPreviousMenu(this).open(this.player);
            }));
            index += 2;
        }
        this.setItem(this.getSize() - 5, new ClickableItemStack(new CCItemBuilder(Material.BOOK_AND_QUILL, CC.AquaB + "Player Role Permissions", new String[]{CC.Gray + "Click to view faction members", CC.Gray + "and their role permissions."}).build()).setClickCallback(e -> new RolePlayerPermMenu(this.player, this.faction).setPreviousMenu(this).open(this.player)));
        this.fillEmpty(new ClickableItemStack(new CCItemBuilder(Material.STAINED_GLASS_PANE).data((short) DyeColor.GRAY.getWoolData()).build()));
    }
}

