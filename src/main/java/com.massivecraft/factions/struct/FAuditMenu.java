/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.cosmicpvp.cosmicutils.utils.ItemBuilder
 *  com.google.common.collect.Lists
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package com.massivecraft.factions.struct;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.listeners.menu.ClickableItemStack;
import com.massivecraft.factions.listeners.menu.GUIMenu;
import com.massivecraft.factions.util.CCItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class FAuditMenu
        extends GUIMenu {
    private static int logsPerPage = 20;
    private Player player;
    private boolean showTimestamps = true;
    private Faction faction;

    public FAuditMenu(Player player, Faction faction) {
        super("Faction Logs", 18);
        this.faction = faction;
        this.player = player;
    }

    @Override
    public void drawItems() {
        int index = 0;
        for (FLogType type : FLogType.values()) {
            LinkedList recentLogs;
            int logSize;
            int logsLeft;
            FactionLogs logs;
            if (type == FLogType.F_POINTS && !P.p.factionPointsEnabled) continue;
            if (index == 9) {
                int n = index = P.p.factionPointsEnabled ? 10 : 11;
            }
            if ((logs = P.p.getFlogManager().getFactionLogMap().get(this.faction.getId())) == null) {
                logs = new FactionLogs();
            }
            if ((recentLogs = logs.getMostRecentLogs().get(type)) == null) {
                recentLogs = Lists.newLinkedList();
            }
            ArrayList lore = Lists.newArrayList((Object[]) new String[]{"", CC.GreenB + "Recent Logs " + CC.Green + "(" + CC.GreenB + recentLogs.size() + CC.Green + ")"});
            int added = 0;
            Iterator backwars = recentLogs.descendingIterator();
            while (backwars.hasNext()) {
                FactionLogs.FactionLog log = (FactionLogs.FactionLog) backwars.next();
                if (added >= logsPerPage) break;
                String length = log.getLogLine(type, this.showTimestamps);
                if (type == FLogType.SELLING && length.length() > 64) {
                    int i;
                    char[] chars = length.toCharArray();
                    for (i = 64; i < chars.length && chars[i] != ','; ++i) {
                    }
                    if (i < chars.length) {
                        String first = length.substring(0, i + 1);
                        String second = length.substring(i + 1);
                        lore.add(" " + CC.Yellow + first);
                        lore.add("  " + CC.Yellow + second.trim());
                        ++added;
                        continue;
                    }
                }
                lore.add(" " + CC.Yellow + length);
                ++added;
            }
            if ((logsLeft = (logSize = recentLogs.size()) - logsPerPage) > 0) {
                lore.add(CC.YellowB + logsLeft + CC.Yellow + " more logs...");
            }
            lore.add("");
            if (logsLeft > 0) {
                lore.add(CC.Yellow + "Left-Click " + CC.Gray + "to view more logs");
            }
            lore.add(CC.Yellow + "Right-Click " + CC.Gray + "to toggle timestamps");
            this.setItem(index++, new ClickableItemStack(new CCItemBuilder(type.getDisplayMaterial()).name(CC.GreenB + type.getDisplayName()).lore(lore).build()).setClickCallback(click -> {
                click.setCancelled(true);
                if (click.getClick() == ClickType.RIGHT) {
                    this.showTimestamps = !this.showTimestamps;
                    this.drawItems();
                } else {
                    if (logsLeft <= 0) {
                        this.player.sendMessage(CC.Red + "No extra logs to load.");
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(P.p, () -> new FAuditLogMenu(this.player, this.faction, type).open(this.player));
                }
            }));
        }
    }

    class FAuditLogMenu
            extends GUIMenu {
        private Player player;
        private Faction faction;
        private FLogType logType;
        private boolean timeStamp;

        public FAuditLogMenu(Player player, Faction faction, FLogType type) {
            super("Faction Logs", 9);
            this.timeStamp = false;
            this.player = player;
            this.faction = faction;
            this.logType = type;
        }

        @Override
        public void drawItems() {
            LinkedList<FactionLogs.FactionLog> log;
            int perPage;
            FactionLogs logs = P.p.getFlogManager().getFactionLogMap().get(this.faction.getId());
            int n = perPage = this.logType == FLogType.F_POINTS ? 25 : 20;
            if (logs != null && (log = logs.getMostRecentLogs().get(this.logType)) != null) {
                int slot = this.logType == FLogType.F_POINTS ? 0 : 3;
                int pagesToShow = (int) Math.max(1.0, Math.ceil((double) log.size() / (double) perPage));
                for (int page = 1; page <= pagesToShow; ++page) {
                    int startIndex = log.size() - (page * perPage - perPage);
                    if (startIndex >= log.size()) {
                        startIndex = log.size() - 1;
                    }
                    ArrayList lore = Lists.newArrayList((Object[]) new String[]{"", CC.GreenB + "Logs"});
                    for (int i = startIndex; i > startIndex - perPage; --i) {
                        if (i >= log.size()) continue;
                        if (i < 0) break;
                        FactionLogs.FactionLog l = log.get(i);
                        lore.add(" " + CC.Yellow + l.getLogLine(this.logType, this.timeStamp));
                    }
                    lore.add("");
                    lore.add(CC.Gray + "Click to toggle timestamp");
                    this.setItem(slot++, new ClickableItemStack(new CCItemBuilder(Material.PAPER).name(CC.GreenB + "Log #" + page).lore(lore).build()).setClickCallback(e -> {
                        e.setCancelled(true);
                        this.timeStamp = !this.timeStamp;
                        this.drawItems();
                    }));
                }
            }
            this.setItem(this.getSize() - 1, new ClickableItemStack(new CCItemBuilder(Material.ARROW).name(CC.Green + "Previous Page").lore(new String[]{"", CC.Gray + "Click to view previous page!"}).build()).setClickCallback(event -> {
                event.setCancelled(true);
                Bukkit.getScheduler().scheduleSyncDelayedTask(P.p, () -> new FAuditMenu(this.player, this.faction).open(this.player));
            }));
        }
    }

}

