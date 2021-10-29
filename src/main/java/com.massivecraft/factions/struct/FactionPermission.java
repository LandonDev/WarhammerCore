/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.EntityType
 *  org.bukkit.material.MaterialData
 */
package com.massivecraft.factions.struct;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;

import java.util.List;

public enum FactionPermission {
    FULL(Material.BEACON, "Full Access", "Everything", 12, ChatColor.GRAY + "Access to all faction permissions.", "", ChatColor.GRAY + "If this is " + ChatColor.GRAY + ChatColor.UNDERLINE + "ALLOWED" + ChatColor.GRAY + ", no other", ChatColor.GRAY + "permissions will apply. The", ChatColor.GRAY + "entity will have full access to", ChatColor.GRAY + "your chunk."),
    CHEST(Material.CHEST, "Chests", 13),
    CLAIMING(Material.DIAMOND_AXE, "Claim/Unclaim Land", 14, ChatColor.GRAY + "Grant the ability to claim or", ChatColor.GRAY + "unclaim any chunks for your faction."),
    OTHER(Material.GRASS, "All Other Blocks", 19, ChatColor.GRAY + "Grant the ability to break and place other blocks."),
    SPONGE(Material.SPONGE, "Sponges", 20),
    DISPENSE(Material.DISPENSER, "Dispensers", 21),
    DROPPER(Material.DROPPER, "Droppers", 22),
    CREEPER(Material.MONSTER_EGG, EntityType.CREEPER.getTypeId(), "Can Place Creeper Eggs", "Creeper Eggs", 23, ChatColor.GRAY + "Grant the ability to place Creeper Eggs."),
    TNT(Material.TNT, "TNT", 24),
    OBBY(Material.OBSIDIAN, "Obsidian", 25),
    SPAWNER(Material.MOB_SPAWNER, "Mob Spawners", 28),
    HOPPER(Material.HOPPER, "Hoppers", 29),
    DOOR(Material.WOOD_DOOR, "Doors & Gates", 30),
    PRESSURE(Material.WOOD_PLATE, "Pressure Plates", 31),
    LEVER(Material.LEVER, "Levers", 32),
    BUTTON(Material.STONE_BUTTON, "Buttons", 33),
    TRAP_DOOR(Material.TRAP_DOOR, "Trap Doors", 34);

    private static List<Material> containers;

    static {
        containers = Lists.newArrayList((Material[]) new Material[]{Material.BEACON, Material.BREWING_STAND, Material.ENCHANTMENT_TABLE, Material.FURNACE, Material.BURNING_FURNACE, Material.ANVIL});
    }

    private String bareName;
    private Material displayMaterial;
    private short durability = 0;
    private String name;
    private List<String> description;
    private int guiSlot;

    FactionPermission(Material material, String name, int guiSlot) {
        this(material, name, (short) 0, guiSlot);
    }

    FactionPermission(Material material, String name, short durability, int guiSlot) {
        this(material, durability, "Can Use " + name, name, guiSlot, ChatColor.GRAY + "Grant the ability to place, break", ChatColor.GRAY + "and interact with " + name + ".");
    }

    FactionPermission(Material dis, short durability, String name, String barename, int slot, String... desc) {
        this(dis, durability, name, slot, desc);
        this.bareName = barename;
    }

    FactionPermission(Material dis, short durability, String name, int slot, String... desc) {
        this.displayMaterial = dis;
        this.name = name;
        this.durability = durability;
        this.guiSlot = slot;
        this.description = Lists.newArrayList((String[]) desc);
    }

    FactionPermission(Material dis, String name, int slot, String... desc) {
        this(dis, (short) 0, name, slot, desc);
        this.bareName = name;
    }

    FactionPermission(Material dis, String name, String barename, int slot, String... desc) {
        this(dis, (short) 0, name, slot, desc);
        this.bareName = barename;
    }

    FactionPermission(String bareName, Material displayMaterial, short durability, String name, List<String> description, int guiSlot) {
        this.bareName = bareName;
        this.displayMaterial = displayMaterial;
        this.durability = durability;
        this.name = name;
        this.description = description;
        this.guiSlot = guiSlot;
    }

    public static FactionPermission getFromMaterial(MaterialData item) {
        for (FactionPermission perm : FactionPermission.values()) {
            if (perm.getDisplayMaterial() != item.getItemType() || perm.getDurability() != item.getData()) continue;
            return perm;
        }
        return FactionPermission.getFromBlock(item);
    }

    public static FactionPermission getFromItem(MaterialData item) {
        for (FactionPermission perm : FactionPermission.values()) {
            if (perm.getDisplayMaterial() != item.getItemType() || perm.getDurability() != item.getData()) continue;
            return perm;
        }
        return null;
    }

    public static boolean isAlwaysOpennableContainer(Material material) {
        return containers.contains(material);
    }

    public static boolean isConsumable(Material material) {
        return material == Material.POTION || material.isEdible() || material == Material.ENDER_PEARL;
    }

    public static FactionPermission getFromBlock(MaterialData block) {
        Material itemType = block.getItemType();
        if (itemType == Material.WOOD_BUTTON) {
            return BUTTON;
        }
        if (itemType == Material.FENCE_GATE) {
            return DOOR;
        }
        if (itemType == Material.STONE_PLATE || itemType == Material.IRON_PLATE || itemType == Material.GOLD_PLATE) {
            return PRESSURE;
        }
        if (itemType == Material.IRON_DOOR_BLOCK || itemType == Material.WOODEN_DOOR) {
            return DOOR;
        }
        if (itemType == Material.MOB_SPAWNER) {
            return SPAWNER;
        }
        if (itemType == Material.SPONGE) {
            return SPONGE;
        }
        if (itemType == Material.TNT) {
            return TNT;
        }
        if (itemType == Material.TRAP_DOOR) {
            return TRAP_DOOR;
        }
        if (itemType == Material.LEVER) {
            return LEVER;
        }
        if (itemType == Material.HOPPER) {
            return HOPPER;
        }
        if (itemType == Material.CHEST || itemType == Material.TRAPPED_CHEST) {
            return CHEST;
        }
        for (FactionPermission perm : FactionPermission.values()) {
            if (perm.getDisplayMaterial() != itemType || perm.getDurability() != 0 && perm.getDurability() != block.getData())
                continue;
            return perm;
        }
        return OTHER;
    }

    public boolean isPlaceableOn() {
        return this == SPONGE || this == SPAWNER || this == TNT || this == OBBY;
    }

    public String getBareName() {
        return this.bareName;
    }

    public Material getDisplayMaterial() {
        return this.displayMaterial;
    }

    public short getDurability() {
        return this.durability;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getDescription() {
        return this.description;
    }

    public int getGuiSlot() {
        return this.guiSlot;
    }
}

