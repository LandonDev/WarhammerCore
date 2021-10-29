package landon.warhammercore.patches.patches.ftop.commands;

import com.google.common.collect.Lists;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.massivecraft.factions.P;
import landon.warhammercore.patches.patches.ftop.FactionsTop;
import landon.warhammercore.patches.patches.ftop.manager.TopManager;
import landon.warhammercore.patches.patches.ftop.struct.TopFaction;
import landon.warhammercore.patches.patches.ftop.utils.JSONMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandFactionsTop implements CommandExecutor {
    DecimalFormat format = new DecimalFormat("#,##0");

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player) || !((Player)sender).isValid()) {
            return true;
        }
        TopManager manager = FactionsTop.get().getTopManager();
        if (manager.isLoadingData()) {
            sender.sendMessage((Object)ChatColor.RED + "Please wait while we calculate /f top...");
            return true;
        }
        int page = 1;
        int perPage = 10;
        boolean showWealth = false;
        if (args.length >= 1 && args[0].equalsIgnoreCase("wealth")) {
            showWealth = true;
            if (args.length > 1 && StringUtils.isNumeric(args[1])) {
                page = Math.min(5, Integer.parseInt(args[1]));
            }
            Bukkit.getLogger().info("ShowWealth: " + showWealth);
        } else if (args.length == 1 && StringUtils.isNumeric(args[0])) {
            page = Math.min(5, Integer.parseInt(args[0]));
        }
        if (page < 0) {
            page = 1;
        }
        if (page > 5) {
            page = 5;
        }
        if (System.currentTimeMillis() - (showWealth ? manager.getLastWealthUpdate() : manager.getLastUpdate()) > TimeUnit.MINUTES.toMillis(manager.getRefreshTimer())) {
            sender.sendMessage((Object)ChatColor.YELLOW + ChatColor.BOLD.toString() + "(!) " + (Object)ChatColor.YELLOW + "Please wait while we calculate top Factions..");
            manager.loadTopFactions(() -> Bukkit.getScheduler().scheduleSyncDelayedTask(P.p, () -> this.onCommand(sender, command, s, args)), 0, showWealth);
            return true;
        }
        int startPosition = (page - 1) * perPage;
        sender.sendMessage("");
        sender.sendMessage((Object)ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Factions " + (Object)ChatColor.AQUA + ChatColor.BOLD.toString() + "(" + (Object)ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + page + (Object)ChatColor.AQUA + ChatColor.BOLD.toString() + "/" + (Object)ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "5" + (Object)ChatColor.AQUA + ChatColor.BOLD.toString() + ")");
        Player pl = (Player)sender;
        FPlayer fplayer = FPlayers.i.get(pl.getUniqueId().toString());
        Faction current = fplayer.getFaction();
        int place = 0;
        ArrayList topFactions = Lists.newArrayList(showWealth && FactionsTop.get().factionPointsEnabled ? manager.getWealthTopFactions().values() : manager.getTopFactions().values());
        for (int i = startPosition; i < startPosition + perPage && place < perPage && i < topFactions.size() && i <= 50; ++i) {
            TopFaction faction = (TopFaction)topFactions.get(i);
            JSONMessage message = new JSONMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + (++place + startPosition) + ". ");
            if (faction.getStoredFaction().getFaction() == null) continue;
            String worth = FactionsTop.get().factionPointsEnabled && !showWealth ? (Object)ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + this.format.format(faction.getStoredFaction().getFactionPoints()) + (Object)ChatColor.LIGHT_PURPLE + " Faction Points" : ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD.toString() + "$" + this.format.format((long)faction.getStoredFaction().getTotalWorth());
            message.addRunCommand((Object)ChatColor.WHITE + (current != null && current.isNormal() ? faction.getStoredFaction().getFaction().getTag(current) : faction.getStoredFaction().getFaction().getTag()) + (Object)ChatColor.AQUA + " - " + worth, ChatColor.WHITE, "/f who " + faction.getStoredFaction().getFaction().getTag(), faction.getStoredFaction().getHoverData(current));
            message.sendToPlayer(pl);
        }
        sender.sendMessage("");
        return false;
    }
}

