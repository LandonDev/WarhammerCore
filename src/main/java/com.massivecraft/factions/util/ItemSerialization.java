package com.massivecraft.factions.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTReadLimiter;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ItemSerialization {
    private static final int VERSION = 1;

    private static final String INVENTORY_CUSTOM = "custom";

    private static final String INVENTORY_PLAYER = "player";

    private static final int NBT_TYPE_COMPOUND = 10;

    private static final String NBT_TYPE = "type";

    private static final String NBT_ITEMS = "items";

    private static final String NBT_VERSION = "version";

    private static final String NBT_PLAYER_HELD_INDEX = "player_held_index";

    private static final String NBT_PLAYER_NAME = "player_name";

    private static Method WRITE_NBT;

    private static Method READ_NBT;

    public static String inventoryToString(Inventory inv) {
        return toBase64(inv);
    }

    public static Inventory getInventoryFromString(String s, String name, int size) {
        return fromBase64(s, name, size);
    }

    public static String itemListToBase64(List<org.bukkit.inventory.ItemStack> items) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(outputStream);
        NBTTagCompound root = new NBTTagCompound();
        NBTTagList itemList = new NBTTagList();
        for (org.bukkit.inventory.ItemStack is : items) {
            NBTTagCompound outputObject = new NBTTagCompound();
            CraftItemStack craft = getCraftVersion(is);
            ItemStack nmsCopy = CraftItemStack.asNMSCopy(craft);
            if (nmsCopy != null)
                nmsCopy.save(outputObject);
            itemList.add((NBTBase)outputObject);
        }
        root.setInt("version", 1);
        root.set("items", (NBTBase)itemList);
        writeNbt((NBTBase)root, dataOutput);
        return Base64Coder.encodeLines(outputStream.toByteArray());
    }

    public static byte[] itemListToNBTByteArray(org.bukkit.inventory.ItemStack[] items) {
        if (items == null || items.length == 0)
            return null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(outputStream);
        NBTTagCompound root = new NBTTagCompound();
        NBTTagList itemList = new NBTTagList();
        int size = items.length;
        for (int i = 0; i < size; i++) {
            NBTTagCompound outputObject = new NBTTagCompound();
            CraftItemStack craft = getCraftVersion(items[i]);
            ItemStack nmsCopy = CraftItemStack.asNMSCopy(craft);
            if (nmsCopy != null)
                nmsCopy.save(outputObject);
            itemList.add((NBTBase)outputObject);
        }
        root.setString("type", "custom");
        root.setInt("version", 1);
        root.set("items", (NBTBase)itemList);
        writeNbt((NBTBase)root, dataOutput);
        return outputStream.toByteArray();
    }

    public static byte[] singleItemToNBTByteArray(org.bukkit.inventory.ItemStack item) {
        if (item == null)
            return null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(outputStream);
        ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);
        NBTTagCompound root = new NBTTagCompound();
        nmsCopy.save(root);
        writeNbt((NBTBase)root, dataOutput);
        return outputStream.toByteArray();
    }

    public static CraftItemStack parseItemFromByteArray(byte[] array) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(array);
        NBTTagCompound root = (NBTTagCompound)readNbt(new DataInputStream(inputStream), 0);
        if (root.isEmpty())
            return null;
        return CraftItemStack.asCraftMirror(ItemStack.createStack(root));
    }

    public static String toBase64(Inventory inventory) {
        if (inventory == null)
            return null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(outputStream);
        NBTTagCompound root = new NBTTagCompound();
        NBTTagList itemList = new NBTTagList();
        String type = "custom";
        int size = inventory.getSize();
        if (inventory instanceof PlayerInventory) {
            PlayerInventory playerInventory = (PlayerInventory)inventory;
            root.setInt("player_held_index", playerInventory.getHeldItemSlot());
            root.setString("player_name", playerInventory.getHolder().getName());
            type = "player";
            size += 4;
        }
        for (int i = 0; i < size; i++) {
            NBTTagCompound outputObject = new NBTTagCompound();
            CraftItemStack craft = getCraftVersion(inventory.getItem(i));
            ItemStack nmsCopy = CraftItemStack.asNMSCopy(craft);
            if (nmsCopy != null)
                nmsCopy.save(outputObject);
            itemList.add((NBTBase)outputObject);
        }
        root.setString("type", type);
        root.setInt("version", 1);
        root.set("items", (NBTBase)itemList);
        writeNbt((NBTBase)root, dataOutput);
        return Base64Coder.encodeLines(outputStream.toByteArray());
    }

    public static Inventory fromBase64(String data, String name, int size) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
        NBTTagCompound root = (NBTTagCompound)readNbt(new DataInputStream(inputStream), 0);
        if (root.getInt("version") != 1)
            throw new IllegalArgumentException("Incompatible version: " + root.getInt("version"));
        NBTTagList itemList = root.getList("items", 10);
        String type = root.getString("type");
        return parseInventory(root, size, itemList, type, name);
    }

    private static Inventory parseInventory(NBTTagCompound root, int size, NBTTagList itemList, String type, String name) {
        CraftInventoryCustom craftInventoryCustom = new CraftInventoryCustom(null, size, name);
        for (int i = 0; i < itemList.size(); i++) {
            NBTTagCompound inputObject = itemList.get(i);
            if (i < size &&
                    !inputObject.isEmpty())
                craftInventoryCustom.setItem(i, CraftItemStack.asCraftMirror(ItemStack.createStack(inputObject)));
        }
        return (Inventory)craftInventoryCustom;
    }

    private static void writeNbt(NBTBase base, DataOutput output) {
        if (WRITE_NBT == null)
            try {
                WRITE_NBT = NBTCompressedStreamTools.class.getDeclaredMethod("a", new Class[] { NBTBase.class, DataOutput.class });
                WRITE_NBT.setAccessible(true);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to find private write method.", e);
            }
        try {
            WRITE_NBT.invoke((Object)null, new Object[] { base, output });
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to write " + base + " to " + output, e);
        }
    }

    private static NBTBase readNbt(DataInput input, int level) {
        if (READ_NBT == null)
            try {
                READ_NBT = NBTCompressedStreamTools.class.getDeclaredMethod("a", new Class[] { DataInput.class, int.class, NBTReadLimiter.class });
                READ_NBT.setAccessible(true);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to find private read method.", e);
            }
        try {
            return (NBTBase)READ_NBT.invoke((Object)null, new Object[] { input, Integer.valueOf(level), NBTReadLimiter.a });
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to read from " + input, e);
        }
    }

    private static CraftItemStack getCraftVersion(org.bukkit.inventory.ItemStack stack) {
        if (stack instanceof CraftItemStack)
            return (CraftItemStack)stack;
        if (stack != null)
            return CraftItemStack.asCraftCopy(stack);
        return null;
    }
}

