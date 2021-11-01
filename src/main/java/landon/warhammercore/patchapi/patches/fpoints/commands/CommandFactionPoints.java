package landon.warhammercore.patchapi.patches.fpoints.commands;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.TimeUtils;
import landon.warhammercore.patchapi.patches.fpoints.FactionPoints;
import landon.warhammercore.patchapi.patches.fpoints.FactionsPointsAPI;
import landon.warhammercore.patchapi.patches.fpoints.managers.PointManager;
import landon.warhammercore.patchapi.patches.fpoints.menus.PointPurchaseMenu;
import landon.warhammercore.patchapi.patches.fpoints.struct.*;
import landon.warhammercore.patchapi.patches.fpoints.utils.FactionUtils;
import landon.warhammercore.patchapi.patches.fpoints.utils.NumberUtils;
import landon.warhammercore.patchapi.patches.fpoints.utils.PlayerUtil;
import landon.warhammercore.util.GiveUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandFactionPoints implements CommandExecutor {
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd hh:mm:ss aa");

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("buy")) {
            Player player = (Player)sender;
            if (FactionPoints.get().getPointManager().getPointsBuyable()) {
                FPlayer fplayer = (FPlayer) FPlayers.i.get((OfflinePlayer)player);
                Faction current = fplayer.getFaction();
                if (current == null || !current.isNormal()) {
                    player.sendMessage(CC.RedB + "(!) " + CC.Red + "You must be in a faction to purchase Faction Points!");
                    return true;
                }
                if (!fplayer.getRole().isAtLeast(Role.COLEADER)) {
                    player.sendMessage(CC.RedB + "(!) " + CC.Red + "You must be at least a faction Co-Leader to purchase Faction Points!");
                    return true;
                }
                (new PointPurchaseMenu((Player)sender, current)).openGUI(P.p);
            } else {
                sender.sendMessage(CC.RedB + "(!) " + CC.Red + "Faction Points are not available for purchase at this time!");
            }
            return true;
        }
        if (args.length >= 1 && sender.isOp()) {
            if (args[0].equalsIgnoreCase("debug")) {
                FactionPoints.debug = !FactionPoints.debug;
                sender.sendMessage(CC.Red + "Debug: " + FactionPoints.debug);
                return true;
            }
            PointManager pointManager = FactionPoints.get().getPointManager();
            if (args[0].equalsIgnoreCase("admininfo")) {
                sender.sendMessage(CC.Red + "Current Weekly Percent: " + pointManager.getCurrentWeeklyPercent());
                sender.sendMessage(CC.Red + "Last Reset: " + TimeUtils.formatTimeToDate(pointManager.getLastSetExpiration(), "MM dd hh:mm aa", "CST"));
                sender.sendMessage(CC.Red + "Weekly Increment Amount: " + pointManager.getWeeklyIncreasePercent() + "%");
                return true;
            }
            if (args[0].equalsIgnoreCase("setweeklypercent") && args.length > 1) {
                Integer value = NumberUtils.parseIntOrNull(args[1]);
                if (value != null) {
                    sender.sendMessage(CC.Red + "Current Weekly Percent: " + value + "%");
                    pointManager.setCurrentWeeklyPercent(value.intValue());
                    pointManager.saveExpirationData(false);
                    return true;
                }
                sender.sendMessage(CC.Red + "Invalid percent entered!");
                return true;
            }
            if (args[0].equalsIgnoreCase("setlastreset") && args.length > 1) {
                try {
                    Long value = Long.valueOf(Long.parseLong(args[1]));
                    sender.sendMessage(CC.Red + "Reset time set to: " + value);
                    pointManager.setLastSetExpiration(value.longValue());
                    pointManager.saveExpirationData(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(CC.Red + "Invalid time entered!");
                    return true;
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("setweeklyincrement") && args.length > 1) {
                Integer value = NumberUtils.parseIntOrNull(args[1]);
                if (value != null) {
                    sender.sendMessage(CC.Red + "Current Weekly Increment: " + value + "%");
                    pointManager.setWeeklyIncreasePercent(value.intValue());
                    pointManager.saveExpirationData(true);
                    return true;
                }
                sender.sendMessage(CC.Red + "Invalid percent entered!");
                return true;
            }
            if (args[0].equalsIgnoreCase("audit")) {
                if (args.length < 2) {
                    sender.sendMessage(CC.Red + "You must provide a faction to lookup!");
                    return true;
                }
                Faction found = Factions.i.getByTag(args[1]);
                if (found == null || !found.isNormal()) {
                    sender.sendMessage(CC.Red + "Invalid faction entered!");
                    return true;
                }
                PointChangeLogs logs = (PointChangeLogs)FactionPoints.get().getPointManager().getFactionPointLogs().get(found.getId());
                if (logs == null || logs.getLogs() == null) {
                    sender.sendMessage(CC.Red + found.getTag() + " does not have any logged point changes!");
                    return true;
                }
                List<PointChangeLog> logList = logs.getLogs();
                Collections.sort(logList);
                int page = 1;
                double perPage = 30.0D;
                int maxPages = (int)Math.ceil(logList.size() / perPage);
                if (args.length > 2) {
                    Integer potential = NumberUtils.parseIntOrNull(args[2]);
                    if (potential != null)
                        page = potential.intValue();
                }
                page = Math.max(1, Math.min(maxPages, page));
                sender.sendMessage(CC.Red + "Viewing logged faction point changes for " + found.getTag() + " (" + page + "/" + maxPages + ")");
                int startIndex = (int)(page * perPage - perPage);
                for (int ind = startIndex; ind < startIndex + perPage &&
                        ind < logList.size(); ind++) {
                    PointChangeLog log = logList.get(ind);
                    if (log != null) {
                        String color = (log.getAmount() <= 0) ? CC.Red : CC.Green;
                        sender.sendMessage(" " + color + TimeUtils.formatSeconds((System.currentTimeMillis() - log.getTime()) / 1000L) + " ago, " + (
                                (log.getPl() != null) ? ("Player (" + log.getPl() + ") ") : "") + log.getAmount() + " points - " + log.getReason());
                    }
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("getstarttime")) {
                long start = FactionPoints.get().getStartOfMap();
                sender.sendMessage(CC.Red + "Start Time Set to: " + (
                        (start < System.currentTimeMillis()) ? (TimeUtils.formatSeconds((System.currentTimeMillis() - start) / 1000L) + " ago") : (TimeUtils.formatFutureTime(start) + " in the future from now")));
                return true;
            }
            if (args[0].equalsIgnoreCase("setstarttime")) {
                try {
                    Long time = Long.valueOf(Long.parseLong(args[1]));
                    FactionPoints.get().setStartOfMap(time.longValue());
                    P.p.getConfig().set("patches.fpoints.startOfMap", time);
                    P.p.saveConfig();
                    String timeString = (time.longValue() > System.currentTimeMillis()) ? ("in " + TimeUtils.formatFutureTime(time.longValue())) : (((System.currentTimeMillis() - time.longValue()) / 1000L) + "s ago");
                    sender.sendMessage(CC.Red + "Start of Map: " + time + " (" + timeString + ")");
                } catch (Exception e) {
                    Long value;
                    sender.sendMessage(CC.Red + "Invalid time entered! Enter time in millis!");
                }
                return true;
            }
            boolean modifyDailyLimit = args[0].equalsIgnoreCase("setdailylimit");
            if (modifyDailyLimit || args[0].equalsIgnoreCase("setpointcost")) {
                Integer val = NumberUtils.parseIntOrNull(args[1]);
                if (val != null) {
                    if (modifyDailyLimit) {
                        PointPurchaseMenu.POINTS_PER_DAY = val.intValue();
                        P.p.getConfig().set("patches.fpoints.points-purchasable-per-day", Integer.valueOf(PointPurchaseMenu.POINTS_PER_DAY));
                        sender.sendMessage(CC.RedB + "(!) " + CC.Red + "Points Per Day: " + val);
                    } else {
                        PointPurchaseMenu.COST_PER_POINT = val.intValue();
                        P.p.getConfig().set("patches.fpoints.cost-per-point", Integer.valueOf(PointPurchaseMenu.COST_PER_POINT));
                        sender.sendMessage(CC.RedB + "(!) " + CC.Red + "Costs Faction Point: $" + val);
                    }
                    P.p.saveConfig();
                    return true;
                }
                sender.sendMessage(CC.RedB + "(!) " + CC.Red + "Invalid number entered!");
                return true;
            }
            if (args[0].equalsIgnoreCase("togglebuy")) {
                sender.sendMessage(CC.Red + "Points Purchasable: " + FactionPoints.get().getPointManager().togglePointsBuyable());
                return true;
            }
            if (args[0].equalsIgnoreCase("giveitem")) {
                if (args.length >= 3) {
                    Player pl = PlayerUtil.getPlayerFromString(args[1]);
                    if (pl == null) {
                        sender.sendMessage(CC.Red + "Unable to find player!");
                        return true;
                    }
                    Double amount = NumberUtils.parseDoubleOrNull(args[2]);
                    if (amount == null) {
                        sender.sendMessage(CC.Red + "Amount entered was not valid!");
                        return true;
                    }
                    ItemStack item = null;
                    if (args.length == 4) {
                        Integer found = NumberUtils.parseIntOrNull(args[3]);
                        if (found != null) {
                            item = FactionsPointsAPI.createPointItem(amount.doubleValue(),
                                    Long.valueOf(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(found.intValue())));
                            sender.sendMessage(CC.RedB + "Creating note that expires in " + found + " seconds from now!");
                        } else {
                            sender.sendMessage(CC.Red + "Invalid number entered!");
                            return true;
                        }
                    }
                    if (item == null)
                        item = FactionsPointsAPI.createPointItem(amount.doubleValue());
                    GiveUtil.giveOrDropItem(pl, item);
                    return true;
                }
                return true;
            }
            boolean add;
            if ((add = (args[0].equalsIgnoreCase("addpoints") || args[0].equalsIgnoreCase("givepoints"))) || args[0]
                    .equalsIgnoreCase("subtractpoints") || args[0]
                    .equalsIgnoreCase("takepoints")) {
                Faction faction = FactionUtils.getFactionByTag(args[1]);
                Player pl = null;
                if (faction == null) {
                    pl = PlayerUtil.getPlayerFromString(args[1]);
                    if (pl != null) {
                        faction = FactionUtils.getFaction(pl, false);
                        if (faction == null) {
                            sender.sendMessage(CC.RedB + "(!) " + CC.Red + "That player is not currently in a faction!");
                            return true;
                        }
                    }
                }
                if (faction == null || !faction.isNormal()) {
                    sender.sendMessage(CC.Red + "Unable to find player or faction with that name!");
                    return true;
                }
                Double toGive = NumberUtils.parseDoubleOrNull(args[2]);
                if (!add && toGive.doubleValue() > 0.0D)
                    toGive = Double.valueOf(-toGive.doubleValue());
                if (add && toGive.doubleValue() < 0.0D)
                    toGive = Double.valueOf(Math.abs(toGive.doubleValue()));
                Long newPoints = pointManager.modifyFactionPoints(faction, toGive.longValue(), true);
                FactionPoints.get().getPointManager().logPointChange(faction, pl, "given via command", toGive
                        .intValue());
                sender.sendMessage(CC.GreenB + "(!) " + CC.Green + "New Faction Points: " + newPoints + " for " + faction.getTag());
                return true;
            }
            if (sender.isOp() && args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(CC.Red + "Usage: /factionpoints giveitem/givepoints/takepoints/addpoints/clearcooldown/logs/stats/togglebuy/setpointcost/setdailylimit/setlastreset/setweeklypercent/admininfo <player/faction> [amount]");
                return true;
            }
        }
        if (!sender.isOp() && args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            sender.sendMessage("");
            sender.sendMessage(CC.YellowB + "Faction Points Help");
            sender.sendMessage(CC.YellowB + "Point Multiplier: " + CC.White + (int)FactionPoints.get().getPointManager().getCurrentWeeklyPercent() + "%");
            sender.sendMessage(CC.Gray + "Faction Points are what determines your faction's /f top ranking on this server. Faction Points can be purchased in exchange for /balance, but they can also be obtained through: killing mobs/players, winning KOTH events, capturing Outpost(s), and more!");
            sender.sendMessage("");
            sender.sendMessage(CC.Yellow + " /fp buy");
            sender.sendMessage(CC.White + "  Buy Faction Points with in-game /balance.");
            sender.sendMessage("");
            sender.sendMessage(CC.Yellow + " /factionpoints");
            sender.sendMessage(CC.White + "  View your faction's current Faction Point total.");
            sender.sendMessage("");
            return true;
        }
        Faction toLookup = null;
        if (args.length > 1) {
            toLookup = FactionUtils.getFactionByTag(args[0]);
            if (toLookup == null) {
                Player pl = PlayerUtil.getPlayerFromString(args[0]);
                if (pl != null) {
                    toLookup = FactionUtils.getFaction(pl, false);
                    if (toLookup == null) {
                        sender.sendMessage(CC.RedB + "(!) " + CC.Red + "That player is not currently in a faction!");
                        return true;
                    }
                }
            }
        }
        if (toLookup == null)
            toLookup = FactionUtils.getFaction((Player)sender, false);
        if (toLookup == null) {
            sender.sendMessage(CC.RedB + "(!) " + CC.Red + "You must be in a faction to view your faction points!");
            return true;
        }
        Long points = FactionsPointsAPI.getPoints(toLookup);
        if (points == null)
            points = Long.valueOf(0L);
        sender.sendMessage(CC.GreenB + toLookup.getTag() + "'s Faction Points: " + CC.GreenU + NumberUtils.formatMoney(points.longValue()));
        sender.sendMessage(CC.Gray + "For more information about Faction Points, use /factionpoints help.");
        return false;
    }
}
