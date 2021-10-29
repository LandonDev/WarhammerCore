/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.cosmicpvp.cosmicutils.utils.ItemBuilder
 *  com.google.common.collect.Lists
 *  org.bukkit.DyeColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 */
package com.massivecraft.factions.listeners.menu.faccess;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.listeners.menu.ClickableItemStack;
import com.massivecraft.factions.listeners.menu.GUIMenu;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.struct.RolePerm;
import com.massivecraft.factions.util.CCItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Set;

public class RolePermEditMenu
        extends GUIMenu {
    private Player player;
    private FPlayer fplayer;
    private Faction faction;
    private Role role;

    public RolePermEditMenu(Player player, FPlayer fplayer, Role editting, Faction faction) {
        super("Edit Role Permissions", RolePermEditMenu.fitSlots(RolePerm.values().length + 1));
        this.player = player;
        this.fplayer = fplayer;
        this.faction = faction;
        this.role = editting;
    }

    @Override
    public void drawItems() {
        int index = 0;
        Set<RolePerm> permissions = this.faction.getRolePerms(this.role);
        for (RolePerm perm : RolePerm.values()) {
            ArrayList lore = Lists.newArrayList();
            lore.addAll(perm.getDescription());
            boolean allowed = permissions.contains(perm);
            if (allowed) {
                lore.add(CC.GreenB + "ALLOWED");
            } else {
                lore.add(CC.RedB + "DENIED");
            }
            this.setItem(index++, new ClickableItemStack(new CCItemBuilder(Material.STAINED_GLASS_PANE, (allowed ? CC.GreenB : CC.RedB) + perm.getDisplay()).lore(lore).data((short) (allowed ? DyeColor.LIME : DyeColor.RED).getWoolData()).build()).setClickCallback(e -> {
                if (!this.faction.attached()) {
                    this.player.closeInventory();
                    return;
                }
                if (!this.fplayer.getRole().isAtLeast(Role.COLEADER) || this.fplayer.getFaction() != this.faction) {
                    this.player.closeInventory();
                    this.player.sendMessage(CC.RedB + "(!) " + CC.Red + "You do not have permission to edit role permissions!");
                    this.player.sendMessage(CC.Gray + "Your faction leader can modify all /f role permissions.");
                    return;
                }
                if (this.fplayer.getRole() == Role.COLEADER && !this.faction.hasRolePerm(this.fplayer, RolePerm.EDIT_PLAYER_PERMS)) {
                    this.player.sendMessage(CC.RedB + "(!) " + CC.Red + "Co-Leaders do not have access to edit /f roles!");
                    this.player.closeInventory();
                    return;
                }
                if (this.fplayer.getRole() == Role.COLEADER && this.role == Role.COLEADER && !this.fplayer.isAdminBypassing()) {
                    this.player.sendMessage(CC.RedB + "(!) " + CC.Red + "Co-Leaders do not have access to edit their own roles!");
                    this.player.closeInventory();
                    return;
                }
                boolean nowAllowed = !allowed;
                this.faction.setRolePermission(this.role, perm, nowAllowed);
                this.faction.sendMessage(CC.YellowB + "(!) " + CC.Yellow + this.player.getName() + " has " + (nowAllowed ? CC.GreenB + "GRANTED" : CC.RedB + "REMOVED") + CC.Yellow + " the role permission for '" + CC.YellowU + perm.getDisplay() + CC.Yellow + "' " + (nowAllowed ? "to" : "from") + " " + this.role.getDisplay() + CC.Yellow + "!");
                P.p.getFlogManager().log(this.faction, FLogType.ROLE_PERM_EDIT, this.player.getName(), (nowAllowed ? CC.GreenB + "GRANTED" : CC.RedB + "REMOVED") + " " + CC.YellowU + perm.getDisplay(), (nowAllowed ? "to" : "from") + " " + this.role.getDisplay());
                this.player.playSound(this.player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.1f);
                this.drawItems();
            }));
        }
        this.setItem(this.getSize() - 1, new ClickableItemStack(new CCItemBuilder(Material.ARROW, CC.RedB + "Previous Menu", new String[]{CC.Gray + "Click to return to role menu."}).build()).setClickCallback(e -> this.getPreviousMenu().open(this.player)));
    }
}

