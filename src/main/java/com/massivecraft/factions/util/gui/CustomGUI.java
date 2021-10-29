package com.massivecraft.factions.util.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.massivecraft.factions.P;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CustomGUI {
    public static Set<String> allGUINames = new HashSet<>();

    public static Map<UUID, CustomGUI> activeGUIs = new ConcurrentHashMap<>();

    public CustomGUI parentGUI;

    protected String title;

    protected int size;

    protected Player player;

    protected Inventory inventory;

    public CustomGUI getParentGUI() {
        return this.parentGUI;
    }

    private Map<Integer, InventoryItem> inventoryItems = new HashMap<>();

    private String owningPluginName;

    private Runnable closeRunnable;

    public Map<Integer, InventoryItem> getInventoryItems() {
        return this.inventoryItems;
    }

    public String getOwningPluginName() {
        return this.owningPluginName;
    }

    public void setOwningPluginName(String owningPluginName) {
        this.owningPluginName = owningPluginName;
    }

    public Runnable getCloseRunnable() {
        return this.closeRunnable;
    }

    public CustomGUI(Player player, String title, int size) {
        this(player, title, size, InventoryType.CHEST);
    }

    public CustomGUI(Player player, String title, int size, InventoryType type) {
        this.inventory = (type == InventoryType.CHEST) ? Bukkit.createInventory(null, size, title) : Bukkit.createInventory(null, type, title);
        this.player = player;
        this.size = size;
        this.title = title;
        allGUINames.add(this.title);
    }

    public CustomGUI setParentGUI(CustomGUI parent) {
        this.parentGUI = parent;
        return this;
    }

    public void onUnknownItemClick(InventoryClickEvent event) {}

    public void openGUI(JavaPlugin owning) {
        this.owningPluginName = owning.getName();
        CustomGUI currentlyActive = activeGUIs.get(this.player.getUniqueId());
        if (currentlyActive != null) {
            Bukkit.getLogger().info("Closing already open menu first!");
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)owning, () -> {
                currentlyActive.close();
                activeGUIs.put(this.player.getUniqueId(), this);
                redraw();
                this.player.openInventory(this.inventory);
            });
            return;
        }
        activeGUIs.put(this.player.getUniqueId(), this);
        redraw();
        this.player.openInventory(this.inventory);
    }

    public void setItem(int slot, InventoryItem inventoryItem) {
        if (inventoryItem == null || inventoryItem.getItem() == null) {
            removeItem(slot);
            return;
        }
        this.inventoryItems.put(Integer.valueOf(slot), inventoryItem);
        this.inventory.setItem(slot, inventoryItem.getItem());
    }

    public void closeWithDelay() {
        closeWithDelay(null);
    }

    public void closeWithDelay(Consumer<Player> afterClose) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(P.p, () -> {
            this.player.closeInventory();
            if (afterClose != null)
                afterClose.accept(this.player);
        }, 1L);
    }

    public void setItem(int slot, ItemStack item, Runnable runnable) {
        setItem(slot, (new InventoryItem(item)).click(runnable));
    }

    public void onInventoryClose() {
        if (this.closeRunnable != null)
            this.closeRunnable.run();
    }

    public void close() {
        onInventoryClose();
        this.player.closeInventory();
    }

    public void removeItem(int slot) {
        this.inventory.setItem(slot, null);
        this.inventoryItems.remove(Integer.valueOf(slot));
    }

    public CustomGUI setOnClose(Runnable runnable) {
        this.closeRunnable = runnable;
        return this;
    }

    public boolean isInventory(Inventory inventory) {
        return this.inventory.equals(inventory);
    }

    public static CustomGUI getActiveGUI(UUID uuid) {
        return activeGUIs.get(uuid);
    }

    public static void removeGUI(UUID uuid) {
        activeGUIs.remove(uuid);
    }

    public abstract void redraw();
}
