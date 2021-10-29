package com.massivecraft.factions.util.gui;

import java.util.HashMap;
import java.util.Map;

import com.massivecraft.factions.util.CCItemBuilder;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryItem {
    private ItemStack item;

    public ItemStack getItem() {
        return this.item;
    }

    private Map<ClickType, Runnable> clickMap = new HashMap<>();

    private Runnable runnable;

    public Map<ClickType, Runnable> getClickMap() {
        return this.clickMap;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }

    public InventoryItem(ItemStack original) {
        this.item = original;
    }

    public InventoryItem(CCItemBuilder original) {
        this(original.build());
    }

    public InventoryItem click(ClickType type, Runnable runnable) {
        this.clickMap.put(type, runnable);
        return this;
    }

    public InventoryItem click(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    public void handleClick(InventoryClickEvent event) {
        if (this.clickMap.isEmpty() && this.runnable != null) {
            this.runnable.run();
        } else {
            Runnable found = this.clickMap.get(event.getClick());
            if (found != null)
                found.run();
        }
    }
}
