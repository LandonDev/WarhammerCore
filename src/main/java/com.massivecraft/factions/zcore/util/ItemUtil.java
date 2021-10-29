/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  net.minecraft.server.v1_7_R4.ItemStack
 *  net.minecraft.server.v1_7_R4.NBTBase
 *  net.minecraft.server.v1_7_R4.NBTTagCompound
 *  net.minecraft.server.v1_7_R4.NBTTagList
 *  org.bukkit.Material
 *  org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.SkullMeta
 */
package com.massivecraft.factions.zcore.util;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;

public class ItemUtil {
    private static Map<String, ItemStack> cachedSkulls = new HashMap<String, ItemStack>();

    public static int getItemCount(Inventory inventory) {
        if (inventory == null) {
            return 0;
        }
        int itemsFound = 0;
        for (int i = 0; i < inventory.getSize(); ++i) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            ++itemsFound;
        }
        return itemsFound;
    }

    public static ItemStack createPlayerHead(String name) {
        ItemStack skull = cachedSkulls.get(name);
        if (skull != null) {
            return skull.clone();
        }
        skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta sm = (SkullMeta) skull.getItemMeta();
        sm.setOwner(name);
        skull.setItemMeta(sm);
        cachedSkulls.put(name, skull.clone());
        return skull;
    }

    public static ItemStack addUnsafeEnchantment(ItemStack item) {
        net.minecraft.server.v1_8_R3.ItemStack is = CraftItemStack.asNMSCopy(item);
        if (is != null) {
            NBTTagCompound comp = is.getTag();
            if (comp == null) {
                comp = new NBTTagCompound();
            }
            comp.set("ench", new NBTTagList());
            is.setTag(comp);
        }
        return CraftItemStack.asCraftMirror(is);
    }
}

