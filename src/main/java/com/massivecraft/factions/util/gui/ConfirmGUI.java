package com.massivecraft.factions.util.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConfirmGUI extends CustomGUI {
    private InventoryItem confirmItem;

    private InventoryItem cancelItem;

    private InventoryItem fillerItem;

    public InventoryItem getConfirmItem() {
        return this.confirmItem;
    }

    public InventoryItem getCancelItem() {
        return this.cancelItem;
    }

    public InventoryItem getFillerItem() {
        return this.fillerItem;
    }

    private boolean cancelled = false, closed = false, confirmed = false;

    public boolean isCancelled() {
        return this.cancelled;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public boolean isConfirmed() {
        return this.confirmed;
    }

    public ConfirmGUI(Player player, String title, int size, ItemStack confirmButton, ItemStack cancelButton, ItemStack fillerPane, Runnable confirmConsumer, Runnable cancelConsumer) {
        super(player, title, size);
        this.fillerItem = new InventoryItem(fillerPane);
        this.cancelItem = (new InventoryItem(cancelButton)).click(() -> {
            this.cancelled = true;
            cancelConsumer.run();
        });
        this.confirmItem = (new InventoryItem(confirmButton)).click(() -> {
            this.confirmed = true;
            confirmConsumer.run();
        });
    }

    public void onInventoryClose() {
        if (this.cancelled || this.closed || this.confirmed)
            return;
        this.closed = true;
        if (this.cancelItem.getRunnable() != null)
            this.cancelItem.getRunnable().run();
        super.onInventoryClose();
    }

    public void redraw() {
        this.cancelled = false;
        int i;
        for (i = 0; i < 4; i++)
            setItem(i, this.confirmItem);
        for (i = 5; i < this.size; i++)
            setItem(i, this.cancelItem);
        for (i = 0; i < this.size; i++) {
            ItemStack current = this.inventory.getItem(i);
            if (current == null || current.getType() == Material.AIR)
                setItem(i, this.fillerItem);
        }
    }
}
