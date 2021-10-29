/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 */
package com.massivecraft.factions.listeners;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.ChatMode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class FactionsGlobalChatListener
        implements Listener {
    public P p;

    public FactionsGlobalChatListener(P p) {
        this.p = p;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        Player talkingplayer = e.getPlayer();
        FPlayer f_talkingplayer = FPlayers.i.get(talkingplayer);
        ChatMode chat = f_talkingplayer.getChatMode();
        if (f_talkingplayer.hasGlobalChatHidden() && chat.equals(ChatMode.PUBLIC)) {
            talkingplayer.sendMessage(ChatColor.RED + "You currently have global chat disabled and cannot chat in global chat please type /f global to enable it!");
            e.setCancelled(true);
            return;
        }
        if (chat == ChatMode.PUBLIC) {
            for (FPlayer fp : FPlayers.i.getOnline()) {
                if (!fp.hasGlobalChatHidden()) continue;
                e.getRecipients().remove(fp.getPlayer());
            }
        }
    }
}

