/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.server.ServerCommandEvent
 */
package com.massivecraft.factions.zcore;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

public class MPluginSecretServerListener
        implements Listener {
    private MPlugin p;

    public MPluginSecretServerListener(MPlugin p) {
        this.p = p;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerCommand(ServerCommandEvent event) {
        if (event.getCommand().length() == 0) {
            return;
        }
        if (this.p.handleCommand(event.getSender(), event.getCommand())) {
            event.setCommand(this.p.refCommand);
        }
    }
}

