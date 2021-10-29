/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  net.ess3.api.events.PlayerSellEvent
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.material.MaterialData
 */
package com.massivecraft.factions.listeners;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.FLogManager;
import com.massivecraft.factions.struct.LogTimer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FactionEssentialsListener
        implements Listener {
    private long sellTimeframe = TimeUnit.SECONDS.toMillis(30L);

    /*@EventHandler
    public void onPlayerSellItems(PlayerSellEvent event) {
        boolean sellHand = false;
        if (event.getSellType() != null && (event.getSellType().equals("all") || (sellHand = event.getSellType().equals("hand")) || event.getSellType().equals("inventory") || event.getSellType().equals("invent"))) {
            FPlayer fplayer = FPlayers.i.get(event.getPlayer());
            if (fplayer == null) {
                return;
            }
            Faction faction = fplayer.getFaction();
            if (faction == null || faction.isNone()) {
                return;
            }
            FLogManager manager = P.p.getFlogManager();
            LogTimer logTimer = manager.getLogTimers().computeIfAbsent(event.getPlayer().getUniqueId(), e -> new LogTimer(event.getPlayer().getName(), faction.getId()));
            LogTimer.Timer timer = logTimer.attemptLog(LogTimer.TimerType.SELL, sellHand ? LogTimer.TimerSubType.SELL_HAND : LogTimer.TimerSubType.SELL_ALL, event.getPrice().longValue());
            Map<MaterialData, AtomicInteger> currentCounts = timer.getExtraData() == null ? new HashMap<>() : (Map) timer.getExtraData();
            for (ItemStack sold : event.getItemsSelling()) {
                if (sold == null) continue;
                currentCounts.computeIfAbsent(sold.getData(), e -> new AtomicInteger(0)).addAndGet(sold.getAmount());
            }
            timer.setExtraData(currentCounts);
            if (timer.isReadyToLog(this.sellTimeframe)) {
                logTimer.pushLogs(faction, LogTimer.TimerType.SELL);
            }
        }
    }*/
}

