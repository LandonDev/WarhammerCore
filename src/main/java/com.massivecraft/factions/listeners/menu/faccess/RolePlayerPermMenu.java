/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.cosmicpvp.cosmicutils.utils.ItemBuilder
 *  com.google.common.collect.Lists
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 */
package com.massivecraft.factions.listeners.menu.faccess;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.listeners.menu.ClickableItemStack;
import com.massivecraft.factions.listeners.menu.GUIMenu;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.struct.RolePerm;
import com.massivecraft.factions.util.CCItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Set;

public class RolePlayerPermMenu
        extends GUIMenu {
    private Faction faction;
    private Player player;

    public RolePlayerPermMenu(Player player, Faction faction) {
        super("Faction Members", RolePlayerPermMenu.fitSlots(faction.getFPlayers().size()));
        this.faction = faction;
        this.player = player;
    }

    @Override
    public void drawItems() {
        if (this.faction == null || !this.faction.isNormal()) {
            this.player.closeInventory();
            return;
        }
        ArrayList<FPlayer> fplayers = Lists.newArrayList(this.faction.getFPlayers());
        fplayers.remove(this.faction.getFPlayerAdmin());
        fplayers.sort((o1, o2) -> {
            if (!o1.getRole().equals(o2.getRole())) {
                return Integer.compare(o1.getRole().value, o2.getRole().value);
            }
            return o1.getNameAsync().compareTo(o2.getNameAsync());
        });
        FPlayer me = FPlayers.i.get(this.player);
        int index = 0;
        this.setItem(this.getSize() - 1, new ClickableItemStack(new CCItemBuilder(Material.ARROW, CC.RedB + "Previous Menu", new String[]{CC.Gray + "Click to return to role menu."}).build()).setClickCallback(e -> this.getPreviousMenu().open(this.player)));
        for (FPlayer fplayer : fplayers) {
            Role role = fplayer.getRole();
            Set<RolePerm> perms = this.faction.getRolePerms(fplayer, false);
            ArrayList lore = Lists.newArrayList();
            lore.add("");
            lore.add(CC.GoldB + "Role:");
            lore.add(" " + role.getDisplay());
            lore.add("");
            lore.add(CC.GoldB + "Permissions:");
            for (RolePerm perm : RolePerm.values()) {
                lore.add(CC.Aqua + " " + perm.getDisplay() + " - " + (perms.contains(perm) ? CC.GreenB + "YES" : CC.RedB + "NO"));
            }
            lore.add(CC.Gray + "Click to edit player role permissions");
            this.setItem(index++, new ClickableItemStack(new CCItemBuilder(Material.SKULL_ITEM, fplayer.getRole().getColor() + fplayer.getNameAsync()).lore(lore).data((short) 3).setSkullOwner(fplayer.getNameAsync()).build()).setClickCallback(e -> new RolePlayerPermEditMenu(this.player, me, fplayer, this.faction).setPreviousMenu(this).open(this.player)));
        }
    }
}

