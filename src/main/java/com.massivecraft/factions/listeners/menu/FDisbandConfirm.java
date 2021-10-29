/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.gui.CosmicGUI
 *  com.cosmicpvp.cosmicutils.gui.InventoryItem
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.cosmicpvp.cosmicutils.utils.ItemBuilder
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 */
package com.massivecraft.factions.listeners.menu;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.util.CCItemBuilder;
import com.massivecraft.factions.util.gui.CustomGUI;
import com.massivecraft.factions.util.gui.InventoryItem;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class FDisbandConfirm
        extends CustomGUI {
    private Faction faction;

    public FDisbandConfirm(Player player, Faction faction) {
        super(player, "Confirm /f disband", 9);
        this.faction = faction;
    }

    public void redraw() {
        int i;
        InventoryItem item = new InventoryItem(new CCItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) DyeColor.LIME.getWoolData())).name(CC.GreenB + "Confirm").lore(new String[]{"", CC.Gray + "Confirm disbanding of " + CC.GreenB + this.faction.getTag() + CC.Gray + "!"}).build()).click(() -> {
            this.player.closeInventory();
            this.player.setMetadata("disband_confirm", new FixedMetadataValue(P.p, System.currentTimeMillis()));
            this.player.performCommand("f disband");
        });
        for (i = 0; i < 4; ++i) {
            this.setItem(i, item);
        }
        this.setItem(4, new CCItemBuilder(Material.STAINED_GLASS_PANE).data((short) DyeColor.BLACK.getWoolData()).name(" ").build(), null);
        for (i = 5; i < this.size; ++i) {
            this.setItem(i, new CCItemBuilder(Material.STAINED_GLASS_PANE, CC.RedB + "Cancel", "", CC.Gray + "Click to cancel faction disband.").data((short) DyeColor.RED.getWoolData()).build(), () -> {
                this.player.closeInventory();
                this.player.sendMessage(CC.RedB + "(!) " + CC.Red + "Disband cancelled.");
            });
        }
    }
}

