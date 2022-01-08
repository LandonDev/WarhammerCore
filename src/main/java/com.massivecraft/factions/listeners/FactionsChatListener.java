/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.compilex.CosmicReputation.ReputationAPI
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.earth2me.essentials.User
 *  com.earth2me.essentials.utils.DateUtil
 *  com.google.common.collect.Lists
 *  me.ifamasssxd.cosmicduel.struct.DuelPlayer
 *  net.milkbowl.vault.economy.Economy
 *  org.arkhamnetwork.Arkkit.libs.SeasonalWin
 *  org.arkhamnetwork.Arkkit.patches.SeasonalPrefixes
 *  org.arkhamnetwork.Arkkit.patches.chat_filter.ChatUtils
 *  org.arkhamnetwork.Arkkit.patches.networkevents.IgnoreUtils
 *  org.arkhamnetwork.Arkkit.plugins.ArkPlugin
 *  org.arkhamnetwork.Arkkit.plugins.ArkPlugins
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.metadata.MetadataValue
 */
package com.massivecraft.factions.listeners;

import com.google.common.collect.Lists;
import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.CmdShow;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import landon.jurassiccore.JurassicCore;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FactionsChatListener implements Listener {
    public P p;

    private final DecimalFormat eloFormat;

    private final DecimalFormat formatter;

    private final Pattern webpattern;

    private final Pattern COLOR_PATTERN;

    private final boolean vanillaGalaxy;

    public FactionsChatListener(P p) {
        this.eloFormat = new DecimalFormat("#.#");
        this.formatter = new DecimalFormat("#,###");
        this.webpattern = Pattern.compile("[-a-zA-Z0-9@:%_\\\\+.~#?&//=]{2,256}\\\\.[a-z]{2,4}\\\\b(\\\\/[-a-zA-Z0-9@:%_\\\\+.~#?&//=]*)?");
        this.COLOR_PATTERN = Pattern.compile("(?i)\" + String.valueOf(') + \"[0-9A-FK-OR]");
        this.vanillaGalaxy = false;
        this.p = p;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerEarlyChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;
        Player talkingPlayer = event.getPlayer();
        String msg = event.getMessage();
        FPlayer me = FPlayers.i.get(talkingPlayer.getUniqueId().toString());
        ChatMode chat = me.getChatMode();
        if (chat != ChatMode.PUBLIC && this.p.handleCommand((CommandSender)talkingPlayer, msg, false, true)) {
            if (Conf.logPlayerCommands)
                Bukkit.getLogger().log(Level.INFO, "[PLAYER_COMMAND] " + talkingPlayer.getName() + ": " + msg);
            event.setCancelled(true);
            return;
        }
        String upper = msg.toUpperCase();
        boolean showingSomething = (upper.contains("[BRAG]") || (upper.contains("[ITEM]") && talkingPlayer.getItemInHand() != null && talkingPlayer.getItemInHand().getType() != Material.AIR));
        boolean silentMuted = talkingPlayer.hasMetadata("silentMute");
        if (chat == ChatMode.FACTION) {
            Faction myFaction = me.getFaction();
            String playerName = me.getNameAsync();
            String nameAndTitle = me.describeToAsync((RelationParticipator)myFaction).replace(playerName, "");
            if (showingSomething)
                return;
            if (silentMuted) {
                talkingPlayer.sendMessage(String.format(Conf.factionChatFormat, new Object[] { nameAndTitle, playerName, msg }));
            } else {
                myFaction.getFPlayersWhereOnline(true).stream().forEach(fpl -> {
                    if (!fpl.isCachedAsOnline())
                        return;
                    fpl.getPlayer().sendMessage(String.format(Conf.factionChatFormat, new Object[] { nameAndTitle, playerName, msg }));
                });
            }
            Bukkit.getLogger().log(Level.INFO, ChatColor.stripColor("FactionChat (" + myFaction.getTag() + ") " + talkingPlayer.getName() + ": " + msg));
            for (FPlayer fplayer : FPlayers.i.getOnline()) {
                if (fplayer.isSpyingChat() && fplayer.getFaction() != myFaction)
                    fplayer.sendMessage("[FCspy] " + myFaction.getTag() + ": " + String.format(Conf.factionChatFormat, new Object[] { nameAndTitle, playerName, msg }));
            }
            event.setCancelled(true);
            return;
        }
        if (chat == ChatMode.MOD) {
            Faction myFaction = me.getFaction();
            if (!me.getRole().isAtLeast(Role.MODERATOR)) {
                me.getPlayer().sendMessage(ChatColor.RED + "You must be a faction moderator to speak in /f c m");
                event.setCancelled(true);
                return;
            }
            String playerName = me.getNameAsync();
            String nameAndTitle = ChatColor.stripColor(me.describeToAsync((RelationParticipator)myFaction).replace(playerName, ""));
            if (showingSomething)
                return;
            if (silentMuted) {
                talkingPlayer.sendMessage(String.format(Conf.modChatFormat, new Object[] { nameAndTitle, playerName, msg }));
            } else {
                myFaction.getFPlayersWhereOnline(true).stream().filter(fplayer -> (fplayer.getRole().isAtLeast(Role.MODERATOR) || fplayer.getRole().isCoLeader())).forEach(fpl -> fpl.getPlayer().sendMessage(String.format(Conf.modChatFormat, new Object[] { nameAndTitle, playerName, msg })));
            }
            Bukkit.getLogger().log(Level.INFO, ChatColor.stripColor("ModChat (" + myFaction.getTag() + ") " + talkingPlayer.getName() + ": " + msg));
            for (FPlayer fplayer : FPlayers.i.getOnline()) {
                if (fplayer.isSpyingChat() && fplayer.getFaction() != myFaction)
                    fplayer.sendMessage("[FMCspy] " + myFaction.getTag() + ": " + String.format(Conf.modChatFormat, new Object[] { nameAndTitle, playerName, msg }));
            }
            event.setCancelled(true);
            return;
        }
        if (chat == ChatMode.ALLIANCE) {
            Faction myFaction = me.getFaction();
            if (showingSomething)
                return;
            if (silentMuted) {
                talkingPlayer.sendMessage(String.format(Conf.allianceChatFormat, new Object[] { ChatColor.stripColor(me.getNameAndTagAsync()), msg }));
            } else {
                myFaction.getFPlayersWhereOnline(true).stream().forEach(fpl -> fpl.getPlayer().sendMessage(String.format(Conf.allianceChatFormat, new Object[] { ChatColor.stripColor(me.getNameAndTagAsync()), msg })));
            }
            for (FPlayer fplayer : FPlayers.i.getOnline()) {
                if (myFaction.getRelationTo((RelationParticipator)fplayer) == Relation.ALLY) {
                    fplayer.sendMessage(String.format(Conf.allianceChatFormat, new Object[] { ChatColor.stripColor(me.getNameAndTagAsync()), msg }));
                    continue;
                }
                if (fplayer.isSpyingChat())
                    fplayer.sendMessage("[ACspy] " + myFaction.getTag() + ": " + String.format(Conf.allianceChatFormat, new Object[] { ChatColor.stripColor(me.getNameAndTagAsync()), msg }));
            }
            Bukkit.getLogger().log(Level.INFO, ChatColor.stripColor("AllianceChat: (" + myFaction.getTag() + ") " + talkingPlayer.getName() + ": " + msg));
            event.setCancelled(true);
            return;
        }
        if (chat == ChatMode.TRUCE) {
            Faction myFaction = me.getFaction();
            if (showingSomething)
                return;
            if (silentMuted) {
                talkingPlayer.sendMessage(msg);
            } else {
                myFaction.getFPlayersWhereOnline(true).stream().forEach(fpl -> fpl.getPlayer().sendMessage(String.format(Conf.truceChatFormat, new Object[] { ChatColor.stripColor(me.getNameAndTagAsync()), msg })));
                for (FPlayer fplayer : FPlayers.i.getOnline()) {
                    if (myFaction.getRelationTo((RelationParticipator)fplayer) == Relation.TRUCE) {
                        fplayer.sendMessage(String.format(Conf.truceChatFormat, new Object[] { ChatColor.stripColor(me.getNameAndTagAsync()), msg }));
                        continue;
                    }
                    if (fplayer.isSpyingChat())
                        fplayer.sendMessage("[FTCspy] " + myFaction.getTag() + ": " + String.format(Conf.truceChatFormat, new Object[] { ChatColor.stripColor(me.getNameAndTagAsync()), msg }));
                }
            }
            Bukkit.getLogger().log(Level.INFO, ChatColor.stripColor("TruceChat: (" + myFaction.getTag() + ") " + talkingPlayer.getName() + ": " + msg + " " + (silentMuted ? "SILENCED" : "")));
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChatMonitor(AsyncPlayerChatEvent event) {
        FPlayer follower = FPlayers.i.get(event.getPlayer().getUniqueId().toString());
        double balance = 0.0D;
        if (CmdShow.isUUID(follower.getAccountId())) {
            balance = JurassicCore.getInstance().getBalanceManager().getBalance(follower.getName()).getBalance();
        } else {
            balance = JurassicCore.getInstance().getBalanceManager().getBalance(follower.getName()).getBalance();
        }
        List<FancyMessage> player_faction_info = Lists.newArrayList(new FancyMessage[] { new FancyMessage(
                ChatColor.valueOf(Conf.hoverNames) + "Username: " + ChatColor.valueOf(Conf.hoverChatColor) + event.getPlayer().getName()), new FancyMessage(
                ChatColor.valueOf(Conf.hoverNames) + "Nick: " + ChatColor.valueOf(Conf.hoverChatColor) + event.getPlayer().getDisplayName()), new FancyMessage(
                ChatColor.valueOf(Conf.hoverNames) + "Faction: " + ChatColor.valueOf(Conf.hoverChatColor) + follower.getFaction().getTag()), new FancyMessage(
                        ChatColor.valueOf(Conf.hoverNames) + "Balance: " + ChatColor.valueOf(Conf.hoverChatColor) + "$" + this.formatter.format(balance)) });
        String player_display_name = event.getPlayer().getDisplayName();
        player_faction_info.add(new FancyMessage(
                ChatColor.valueOf(Conf.hoverNames) + "Power: " + ChatColor.valueOf(Conf.hoverChatColor) + follower.getPowerRounded() +
                        ChatColor.valueOf(Conf.hoverSlashColor) + "/" + ChatColor.valueOf(Conf.hoverChatColor) + Conf.powerPlayerMax));
        event.setCancelled(true);
        for (Player player : event.getRecipients()) {
            FPlayer you = FPlayers.i.get(player);
            String current_string = event.getFormat();
            current_string = current_string.replace("[FACTION]", follower.getChatTag(you).trim());
            current_string = current_string.replace("%1$s", "/UniqueSplitString/");
            String[] current_message_string = current_string.split("/UniqueSplitString/");
            FancyMessage fancy_message = new FancyMessage(current_message_string[0]);
            if (current_message_string.length > 1) {
                String playerRankAndName = player_display_name;
                fancy_message.then(playerRankAndName).formattedTooltip(player_faction_info).command("/f who " + event.getPlayer().getName());
                int current_count = 0;
                for (String split : current_message_string) {
                    current_count++;
                    if (current_count > 1) {
                        split = split.replace("%2$s", event.getMessage());
                        List<ChatColor> last_colors = new ArrayList<>();
                        for (String message_part : split.split(" ")) {
                            fancy_message.then(message_part);
                            Matcher matcher = this.COLOR_PATTERN.matcher(message_part);
                            if (matcher.find() && matcher.group().length() > 0) {
                                last_colors.clear();
                                for (int i = 0; i <= matcher.groupCount(); i++)
                                    last_colors.add(ChatColor.getByChar(matcher.group(i).replace("§", "")));
                            }
                            if (!last_colors.isEmpty())
                                for (ChatColor color : last_colors)
                                    fancy_message.color(color);
                            message_part = ChatColor.stripColor(message_part);
                            if (checkMessageForWebPattern(message_part))
                                fancy_message.link(message_part);
                            fancy_message.then(" ");
                        }
                    }
                }
            }
            fancy_message.send(player);
        }
        Bukkit.getConsoleSender().sendMessage(String.format(event.getFormat(), new Object[] { event.getPlayer().getDisplayName(), event.getMessage() }).replace("•", "-"));
    }

    private boolean checkMessageForWebPattern(String message) {
        if (!message.startsWith("http://") && !message.startsWith("https://"))
            return false;
        Matcher regexMatcherurl = this.webpattern.matcher(message);
        while (regexMatcherurl.find()) {
            String text = regexMatcherurl.group().trim().replaceAll("www.", "").replaceAll("http://", "").replaceAll("https://", "");
            if (regexMatcherurl.group().length() != 0 && text.length() != 0 &&
                    this.webpattern.matcher(message).find())
                return true;
        }
        return false;
    }
}