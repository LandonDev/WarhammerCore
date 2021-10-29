/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.ItemSerialization
 *  com.cosmicpvp.cosmicutils.utils.JSONUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken
 *  org.bukkit.inventory.Inventory
 */
package com.massivecraft.factions.struct.managers;

import com.google.common.reflect.TypeToken;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.util.ItemSerialization;
import com.massivecraft.factions.util.JSONUtils;
import com.massivecraft.factions.zcore.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class FChestManager {
    private File fChestFile;
    private Type token = new TypeToken<Map<String, String>>(){}.getType();
    private Map<String, Inventory> fchestItems = new HashMap<String, Inventory>();

    public void onEnable(P p) {
        try {
            this.fChestFile = JSONUtils.getOrCreateFile(p.getDataFolder(), "factionChests.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map factionItems = null;
        try {
            factionItems = (Map) JSONUtils.fromJson(this.fChestFile, this.token);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (factionItems != null) {
            factionItems.forEach((factionId, serializedItems) -> {
                try {
                    Inventory inv = ItemSerialization.getInventoryFromString((String)serializedItems, "/f chest", 54);
                    this.fchestItems.put((String)factionId, inv);
                    return;
                }
                catch (Exception e) {
                    Bukkit.getLogger().info("Unable to load items for " + factionId + " itemString: " + serializedItems);
                    e.printStackTrace();
                }
            });
        }
        try {
            Class.forName("com.cosmicpvp.cosmicutils.utils.ItemSerialization");
            return;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Inventory getFInventory(Faction faction) {
        return this.fchestItems.computeIfAbsent(faction.getId(), e -> Bukkit.createInventory(null, 54, "/f chest"));
    }

    public void onDisable(P p) {
        HashMap toSave = new HashMap();
        this.fchestItems.forEach((fId, items) -> {
            try {
                if (ItemUtil.getItemCount(items) <= 0) {
                    return;
                }
                toSave.put(fId, ItemSerialization.inventoryToString(items));
                return;
            }
            catch (Exception ex) {
                Bukkit.getLogger().info("[Factions] Unable to serialize inventory items from " + fId + " items=" + items);
                ex.printStackTrace();
            }
        });
        try {
            JSONUtils.saveJSONToFile(this.fChestFile, toSave, this.token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (toSave.size() <= 0) return;
        Bukkit.getLogger().info("[Factions] Saving " + toSave.size() + " faction chests!");
    }

    public Map<String, Inventory> getFchestItems() {
        return this.fchestItems;
    }

}