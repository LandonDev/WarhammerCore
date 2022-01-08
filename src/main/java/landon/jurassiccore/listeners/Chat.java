package landon.jurassiccore.listeners;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.faction.FactionManager;
import landon.jurassiccore.file.FileManager;
import landon.jurassiccore.utils.ItemUtil;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class Chat implements Listener {
    private final JurassicCore instance;

    public Chat(JurassicCore instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChatFormat1(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled())
            return;
        FactionManager factionManager = this.instance.getFactionManager();
        FileManager fileManager = this.instance.getFileManager();
        FileConfiguration configLoadMain = fileManager.getConfig(new File(this.instance.getDataFolder(), "config.yml"))
                .getFileConfiguration();
        FileConfiguration configLoadLanguage = fileManager.getConfig(new File(this.instance.getDataFolder(), "language.yml"))
                .getFileConfiguration();
        if (!configLoadMain.getBoolean("Listeners.Chat.Format.Enable"))
            return;
        event.setCancelled(true);
        Faction playerFaction = FPlayers.i.get(player).getFaction();
        String primaryGroup = StringUtils.capitalize(LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId()).getPrimaryGroup());
        String messageFormat;
        if (primaryGroup == null || primaryGroup.isEmpty()) {
            if (playerFaction == null || playerFaction.isNone()) {
                messageFormat = configLoadMain.getString("Listeners.Chat.Format.Formats.None.Player");
            } else {
                messageFormat = configLoadMain.getString("Listeners.Chat.Format.Formats.Faction.Player");
            }
        } else if (playerFaction == null || playerFaction.isNone()) {
            if (configLoadMain.getString("Listeners.Chat.Format.Formats.Faction." + primaryGroup) == null) {
                messageFormat = configLoadMain.getString("Listeners.Chat.Format.Formats.None.Player");
            } else {
                messageFormat = configLoadMain.getString("Listeners.Chat.Format.Formats.None." + primaryGroup);
            }
        } else if (configLoadMain.getString("Listeners.Chat.Format.Formats.Faction." + primaryGroup) == null) {
            messageFormat = configLoadMain.getString("Listeners.Chat.Format.Formats.Faction.Player");
        } else {
            messageFormat = configLoadMain.getString("Listeners.Chat.Format.Formats.Faction." + primaryGroup);
        }
        messageFormat = ChatColor.translateAlternateColorCodes('&',
                messageFormat.replace("%prefix", LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getPrefix()).replace("%player", player.getName()))
                .replace("%message", event.getMessage());
        if (configLoadMain.getBoolean("Listeners.Chat.Item.Enable") &&
                configLoadMain.getBoolean("Listeners.Chat.Brag.Enable") && messageFormat.contains("[item]") &&
                messageFormat.contains("[brag]")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoadLanguage.getString("Listeners.Chat.Tags.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            return;
        }
        if (handleTag(player, messageFormat, MessageTag.Item))
            return;
        if (handleTag(player, messageFormat, MessageTag.Brag))
            return;
        for (Player all : Bukkit.getOnlinePlayers()) {
            Faction targetPlayerFaction = FPlayers.i.get(all).getFaction();
            String targetPlayerMessageFormat = messageFormat;
            if (targetPlayerMessageFormat.contains("%faction_role_color"))
                targetPlayerMessageFormat = targetPlayerMessageFormat.replace("%faction_role_color",
                        factionManager.getRelationColor(targetPlayerFaction, playerFaction) +
                                factionManager.getRoleSymbol(player));
            if (targetPlayerMessageFormat.contains("%faction_role"))
                targetPlayerMessageFormat = targetPlayerMessageFormat.replace("%faction_role",
                        factionManager.getRoleSymbol(player));
            if (targetPlayerMessageFormat.contains("%faction_color"))
                targetPlayerMessageFormat = targetPlayerMessageFormat.replace("%faction_color",
                        factionManager.getRelationColor(targetPlayerFaction, playerFaction) + playerFaction.getTag());
            if (targetPlayerMessageFormat.contains("%faction"))
                targetPlayerMessageFormat = targetPlayerMessageFormat.replace("%faction", playerFaction.getTag());
            all.sendMessage(targetPlayerMessageFormat);
        }
        System.out.println(ChatColor.translateAlternateColorCodes('&', messageFormat)
                .replace("%faction_color", playerFaction.getTag()).replace("%faction", playerFaction.getTag()));
    }

    public boolean handleTag(Player player, String messageFormat, MessageTag messageTag) {
        String lastColorCodes;
        FactionManager factionManager = this.instance.getFactionManager();
        FileManager fileManager = this.instance.getFileManager();
        FileConfiguration configLoadMain = fileManager.getConfig(new File(this.instance.getDataFolder(), "config.yml"))
                .getFileConfiguration();
        FileConfiguration configLoadLanguage = fileManager.getConfig(new File(this.instance.getDataFolder(), "language.yml"))
                .getFileConfiguration();
        Faction playerFaction = FPlayers.i.get(player).getFaction();
        String messageTagVariable = "[" + messageTag.name().toLowerCase() + "]";
        if (!configLoadMain.getBoolean("Listeners.Chat." + messageTag + ".Enable") ||
                !messageFormat.contains(messageTagVariable))
            return false;
        if (!player.hasPermission("jurassiccore." + messageTag.name().toLowerCase()) &&
                !player.hasPermission("jurassiccore.*")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoadLanguage.getString("Listeners.Chat." + messageTag + ".Permission.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            return true;
        }
        if (StringUtils.countMatches(messageFormat, messageTagVariable) != 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    configLoadLanguage.getString("Listeners.Chat." + messageTag + ".Tag.Message")));
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            return true;
        }
        TextComponent messageTagTextComponent = new TextComponent("");
        if (messageTag == MessageTag.Item) {
            ItemStack hand = player.getItemInHand();
            if (hand == null || hand.getType() == Material.AIR) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        configLoadLanguage.getString("Listeners.Chat.Item.Hand.Message")));
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
                return true;
            }
            if (hand.hasItemMeta() && hand.getItemMeta().hasDisplayName()) {
                messageTagTextComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&',
                        configLoadLanguage.getString("Listeners.Chat.Item.Item.Displayname")
                                .replace("%item", ItemUtil.getItemName(hand))
                                .replace("%displayname", hand.getItemMeta().getDisplayName())
                                .replace("%amount", String.valueOf(hand.getAmount()))));
            } else {
                messageTagTextComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&',
                        configLoadLanguage.getString("Listeners.Chat.Item.Item.Empty")
                                .replace("%item", ItemUtil.getItemName(hand))
                                .replace("%amount", String.valueOf(hand.getAmount()))));
            }
            messageTagTextComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                    new BaseComponent[]{(BaseComponent) new TextComponent(ItemUtil.convertItemStackToJson(hand))}));
        } else if (messageTag == MessageTag.Brag) {
            messageTagTextComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&', configLoadLanguage
                    .getString("Listeners.Chat.Brag.Brag.Display").replace("%player", player.getName())));
            messageTagTextComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (BaseComponent[]) new TextComponent[]{new TextComponent(ChatColor.translateAlternateColorCodes('&', configLoadLanguage
                    .getString("Listeners.Chat.Brag.Brag.Hover").replace("%player", player.getName())))}));
            messageTagTextComponent
                    .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/invsee " + player.getName()));
        }
        String[] messages = messageFormat.split(" ");
        if (messageFormat.contains("§")) {
            String[] colorCodes = messageFormat.split("§");
            String lastColorCodeText = colorCodes[colorCodes.length - 1];
            lastColorCodes = "§" + lastColorCodeText.substring(0, Math.min(lastColorCodeText.length(), 1));
            if (colorCodes.length >= 2 && (lastColorCodes.equals("§l") || lastColorCodes.equals("§m") ||
                    lastColorCodes.equals("§n") || lastColorCodes.equals("§o"))) {
                lastColorCodeText = colorCodes[colorCodes.length - 2];
                lastColorCodes = "§" + lastColorCodeText.substring(0, Math.min(lastColorCodeText.length(), 1)) +
                        lastColorCodes;
            }
        } else {
            lastColorCodes = "§f";
        }
        for (Player all : Bukkit.getOnlinePlayers()) {
            Faction targetPlayerFaction = FPlayers.i.get(all).getFaction();
            TextComponent messageComponent = new TextComponent("");
            for (int i = 0; i < messages.length; i++) {
                String message = messages[i];
                if (message.contains("%faction_role_color"))
                    message = message.replace("%faction_role_color",
                            factionManager.getRelationColor(targetPlayerFaction, playerFaction) +
                                    factionManager.getRoleSymbol(player));
                if (message.contains("%faction_role"))
                    message = message.replace("%faction_role", factionManager.getRoleSymbol(player));
                if (message.contains("%faction_color"))
                    message = message.replace("%faction_color",
                            factionManager.getRelationColor(targetPlayerFaction, playerFaction) +
                                    playerFaction.getTag());
                if (message.contains("%faction"))
                    message = message.replace("%faction", playerFaction.getTag());
                if (message.contains(messageTagVariable)) {
                    String[] itemMessages = message.split("\\[" + messageTag.name().toLowerCase() + "\\]");
                    if (itemMessages.length == 0) {
                        messageComponent.addExtra((BaseComponent) messageTagTextComponent);
                    } else if (itemMessages.length == 1) {
                        if (message.equals(String.valueOf(itemMessages[0]) + messageTagVariable)) {
                            messageComponent.addExtra(
                                    String.valueOf(ChatColor.translateAlternateColorCodes('&', lastColorCodes)) + itemMessages[0]);
                            messageComponent.addExtra((BaseComponent) messageTagTextComponent);
                        } else {
                            messageComponent.addExtra((BaseComponent) messageTagTextComponent);
                            messageComponent.addExtra(
                                    String.valueOf(ChatColor.translateAlternateColorCodes('&', lastColorCodes)) + itemMessages[0]);
                        }
                    } else if (itemMessages.length == 2) {
                        messageComponent.addExtra(
                                String.valueOf(ChatColor.translateAlternateColorCodes('&', lastColorCodes)) + itemMessages[0]);
                        messageComponent.addExtra((BaseComponent) messageTagTextComponent);
                        messageComponent.addExtra(
                                String.valueOf(ChatColor.translateAlternateColorCodes('&', lastColorCodes)) + itemMessages[1]);
                    }
                } else {
                    messageComponent.addExtra(
                            (BaseComponent) new TextComponent(String.valueOf(ChatColor.translateAlternateColorCodes('&', lastColorCodes)) + message));
                }
                messageComponent.addExtra((BaseComponent) new TextComponent(" "));
            }
            all.spigot().sendMessage((BaseComponent) messageComponent);
        }
        System.out.println(ChatColor.translateAlternateColorCodes('&', messageFormat)
                .replace("%faction_color", playerFaction.getTag()).replace("%faction", playerFaction.getTag()));
        return true;
    }

    public enum MessageTag {
        Item, Brag;
    }
}
