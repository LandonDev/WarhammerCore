package com.massivecraft.factions.util;

import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class CCItemBuilder {
    private ItemStack item;

    public CCItemBuilder(ItemStack existing) {
        this.item = existing.clone();
    }

    public CCItemBuilder(Material type) {
        this.item = new ItemStack(type, 1);
    }

    public CCItemBuilder(Material type, String name, String... lore) {
        this.item = new ItemStack(type, 1);
        nameAndLore(name, lore);
    }

    public CCItemBuilder nameAndLore(String name, List<String> str) {
        ItemMeta im = this.item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(str);
        this.item.setItemMeta(im);
        return this;
    }

    public CCItemBuilder setSkullOwner(String owner) {
        if (this.item == null || !this.item.getType().equals(Material.SKULL_ITEM))
            return this;
        SkullMeta sm = (SkullMeta)this.item.getItemMeta();
        sm.setOwner(owner);
        this.item.setItemMeta((ItemMeta)sm);
        return this;
    }

    public CCItemBuilder nameAndLore(String name, String... lore) {
        return nameAndLore(name, Lists.newArrayList(lore));
    }

    public CCItemBuilder name(String name) {
        ItemMeta im = this.item.getItemMeta();
        im.setDisplayName(name);
        this.item.setItemMeta(im);
        return this;
    }

    public CCItemBuilder amount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public CCItemBuilder addLoreLines(List<String> line) {
        ItemMeta im = this.item.getItemMeta();
        List<String> lore = (im.getLore() == null) ? Lists.newArrayList() : im.getLore();
        lore.addAll(line);
        im.setLore(lore);
        this.item.setItemMeta(im);
        return this;
    }

    public CCItemBuilder addLoreLine(String... line) {
        return addLoreLines(Lists.newArrayList(line));
    }

    public CCItemBuilder lore(List<String> lore) {
        ItemMeta im = this.item.getItemMeta();
        im.setLore(lore);
        this.item.setItemMeta(im);
        return this;
    }

    public CCItemBuilder lore(String... lore) {
        return lore(Lists.newArrayList(lore));
    }

    public CCItemBuilder data(short data) {
        this.item.setDurability(data);
        return this;
    }

    public CCItemBuilder enchant(Enchantment enchant, int level) {
        this.item.addUnsafeEnchantment(enchant, level);
        return this;
    }

    public CCItemBuilder removeEnchant(Enchantment enchant) {
        this.item.removeEnchantment(enchant);
        return this;
    }

    public ItemStack build() {
        return this.item;
    }
}
