/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CoreProtectUtils
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 *  org.bukkit.event.player.PlayerLoginEvent
 */
package com.massivecraft.factions.zcore;

import com.massivecraft.factions.zcore.persist.EM;
import com.massivecraft.factions.zcore.persist.Entity;
import com.massivecraft.factions.zcore.persist.EntityCollection;
import com.massivecraft.factions.zcore.persist.PlayerEntityCollection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.logging.Level;

public class MPluginSecretPlayerListener
        implements Listener {
    private MPlugin p;

    public MPluginSecretPlayerListener(MPlugin p) {
        this.p = p;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (this.p.handleCommand(event.getPlayer(), event.getMessage())) {
            if (this.p.logPlayerCommands()) {
                this.p.getServer().getLogger().log(Level.INFO, "[PLAYER_COMMAND] {0}: {1}", new Object[]{event.getPlayer().getName(), event.getMessage()});
            }
            event.setCancelled(true);
            String cmd = event.getMessage();
            event.setMessage("/null");
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (this.p.handleCommand(event.getPlayer(), event.getMessage(), false, true)) {
            if (this.p.logPlayerCommands()) {
                this.p.getServer().getLogger().log(Level.INFO, "[PLAYER_COMMAND] {0}: {1}", new Object[]{event.getPlayer().getName(), event.getMessage()});
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreLogin(PlayerLoginEvent event) {
        for (EntityCollection<? extends Entity> ecoll : EM.class2Entities.values()) {
            if (!(ecoll instanceof PlayerEntityCollection)) continue;
            ecoll.get(event.getPlayer().getName());
        }
    }
}

