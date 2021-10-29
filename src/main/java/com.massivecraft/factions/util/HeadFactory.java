package com.massivecraft.factions.util;

import com.massivecraft.factions.zcore.util.XMaterial;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * @author Saser
 */
public class HeadFactory {
    //                https://minecraft-heads.com
    public static ItemStack getSkull(String textureData) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", textureData));

        Field profileField = null;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }

    public static ItemStack createHead(String headOwner) {
        ItemStack skull;
        skull = getSkull(headOwner);
        return skull;
    }
}
