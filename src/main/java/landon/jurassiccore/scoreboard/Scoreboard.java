package landon.jurassiccore.scoreboard;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.utils.NumberUtil;
import landon.jurassiccore.utils.PvPUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class Scoreboard {
    private final JurassicCore instance;
    private Player player;
    private String displayName;
    private List<String> displayList;
    private BukkitTask scheduler;
    private boolean health;
    private boolean cancel;

    public Scoreboard(final JurassicCore instance, final Player player) {
        this.health = false;
        this.cancel = false;
        this.instance = instance;
        this.player = player;
        this.displayList = new ArrayList<String>();
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public void setDisplayList(final List<String> displayList) {
        this.displayList = displayList;
    }

    public void setHealth(final boolean health) {
        this.health = health;
    }

    public void run() {
        Bukkit.getServer().getScheduler().runTask(P.p, (Runnable) new Runnable() {
            @Override
            public void run() {
                final org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                Bukkit.getServer().getScheduler().runTaskAsynchronously(P.p, (Runnable) new Runnable() {

                    @Override
                    public void run() {
                        final UUID randomUUID = UUID.randomUUID();
                        Objective sidebarObjective;
                        if (scoreboard.getObjective(randomUUID.toString().split("-")[0]) != null) {
                            sidebarObjective = scoreboard.getObjective(randomUUID.toString().split("-")[0]);
                        } else {
                            sidebarObjective = scoreboard.registerNewObjective(randomUUID.toString().split("-")[0], "dummy");
                        }
                        sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                        final String formattedDisplayName = ChatColor.translateAlternateColorCodes('&', Scoreboard.this.replaceDisplayName(Scoreboard.this.displayName));
                        if (formattedDisplayName.length() > 32) {
                            sidebarObjective.setDisplayName(ChatColor.RED + "Too long...");
                        } else {
                            sidebarObjective.setDisplayName(formattedDisplayName);
                        }
                        for (int i = 0; i < ChatColor.values().length && i != Scoreboard.this.displayList.size(); ++i) {
                            final ChatColor chatColor = ChatColor.values()[i];
                            final Team team = scoreboard.registerNewTeam(String.valueOf(randomUUID.toString().split("-")[0]) + i);
                            team.addEntry(chatColor.toString());
                            sidebarObjective.getScore(chatColor.toString()).setScore(i);
                        }
                        if (Scoreboard.this.health) {
                            final Objective belowNameObjective = scoreboard.registerNewObjective(randomUUID.toString().split("-")[1], "health");
                            belowNameObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
                            belowNameObjective.setDisplayName(ChatColor.RED + "\u2764");
                        }
                        Scoreboard.access$8(Scoreboard.this, new BukkitRunnable() {
                            int i1 = displayList.size();

                            public void run() {
                                if (Scoreboard.this.player.isOnline() && !Scoreboard.this.cancel) {
                                    try {
                                        final String formattedDisplayName = ChatColor.translateAlternateColorCodes('&', Scoreboard.this.replaceDisplayName(Scoreboard.this.displayName));
                                        if (formattedDisplayName.length() > 32) {
                                            sidebarObjective.setDisplayName(ChatColor.RED + "Too long...");
                                        } else {
                                            sidebarObjective.setDisplayName(formattedDisplayName);
                                        }
                                        for (String displayLine : Scoreboard.this.displayList) {
                                            --this.i1;
                                            displayLine = Scoreboard.this.replaceDisplayLine(displayLine);
                                            if (displayLine.length() > 32) {
                                                displayLine = "&cLine too long...";
                                            }
                                            if (displayLine.length() >= 16) {
                                                String prefixLine = displayLine.substring(0, Math.min(displayLine.length(), 16));
                                                String suffixLine = displayLine.substring(16, Math.min(displayLine.length(), displayLine.length()));
                                                if (prefixLine.substring(prefixLine.length() - 1).equals("&")) {
                                                    prefixLine = ChatColor.translateAlternateColorCodes('&', prefixLine.substring(0, prefixLine.length() - 1));
                                                    suffixLine = ChatColor.translateAlternateColorCodes('&', "&" + suffixLine);
                                                } else {
                                                    String lastColorCodes;
                                                    if (prefixLine.contains("&")) {
                                                        final String[] colorCodes = prefixLine.split("&");
                                                        String lastColorCodeText = colorCodes[colorCodes.length - 1];
                                                        lastColorCodes = "&" + lastColorCodeText.substring(0, Math.min(lastColorCodeText.length(), 1));
                                                        if (colorCodes.length >= 2 && (lastColorCodes.equals("&l") || lastColorCodes.equals("&m") || lastColorCodes.equals("&n") || lastColorCodes.equals("&o"))) {
                                                            lastColorCodeText = colorCodes[colorCodes.length - 2];
                                                            lastColorCodes = "&" + lastColorCodeText.substring(0, Math.min(lastColorCodeText.length(), 1)) + lastColorCodes;
                                                        }
                                                    } else {
                                                        lastColorCodes = "&f";
                                                    }
                                                    prefixLine = ChatColor.translateAlternateColorCodes('&', prefixLine);
                                                    suffixLine = ChatColor.translateAlternateColorCodes('&', String.valueOf(lastColorCodes) + suffixLine);
                                                }
                                                scoreboard.getTeam(String.valueOf(randomUUID.toString().split("-")[0]) + this.i1).setPrefix(prefixLine);
                                                scoreboard.getTeam(String.valueOf(randomUUID.toString().split("-")[0]) + this.i1).setSuffix(suffixLine);
                                            } else {
                                                scoreboard.getTeam(String.valueOf(randomUUID.toString().split("-")[0]) + this.i1).setPrefix(ChatColor.translateAlternateColorCodes('&', displayLine));
                                            }
                                        }
                                        this.i1 = Scoreboard.this.displayList.size();
                                    } catch (Exception e) {
                                        this.cancel();
                                    }
                                } else {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimerAsynchronously(P.p, 0L, 20L));
                        Scoreboard.this.player.setScoreboard(scoreboard);
                    }
                });
            }
        });
    }

    private String replaceDisplayName(String displayName) {
        final Calendar calendar = Calendar.getInstance();
        displayName = displayName.replace("%players_online", new StringBuilder().append(Bukkit.getServer().getOnlinePlayers().size()).toString()).replace("%players_max", new StringBuilder().append(Bukkit.getServer().getMaxPlayers()).toString()).replace("%day", new StringBuilder().append(calendar.get(5)).toString()).replace("%month", new StringBuilder().append(1 + calendar.get(2)).toString());
        if(FPlayers.i.get(this.player).getFaction() != null && !FPlayers.i.get(this.player).getFaction().isNone()) {
            displayName = displayName.replace("%faction%", new StringBuilder().append(FPlayers.i.get(this.player).getFaction().getTag()).toString());
        }
        return displayName;
    }

    private String replaceDisplayLine(String displayLine) {
        final Calendar calendar = Calendar.getInstance();
        final Faction faction = FPlayers.i.get(this.player).getFaction();
        if (faction == null || faction.isNone()) {
            displayLine = displayLine.replace("%players_online", new StringBuilder().append(Bukkit.getServer().getOnlinePlayers().size()).toString()).replace("%players_max", new StringBuilder().append(Bukkit.getServer().getMaxPlayers()).toString()).replace("%player", this.player.getName()).replace("%day", new StringBuilder().append(calendar.get(5)).toString()).replace("%month", new StringBuilder().append(1 + calendar.get(2)).toString()).replace("%xp", new StringBuilder().append(NumberUtil.formatNumberByCommas(this.player.getLevel())).toString()).replace("%kdr", new StringBuilder().append(NumberUtil.formatNumberByDecimal(PvPUtil.getKDRRation(this.player))).toString()).replace("%kills", new StringBuilder().append(PvPUtil.getKills(this.player)).toString()).replace("%deaths", new StringBuilder().append(PvPUtil.getDeaths(this.player)).toString()).replace("%balance", NumberUtil.formatNumberBySuffix(this.instance.getVaultManager().getEconomy().getBalance((OfflinePlayer) this.player)));
        } else {
            displayLine = displayLine.replace("%players_online", new StringBuilder().append(Bukkit.getServer().getOnlinePlayers().size()).toString()).replace("%faction%", new StringBuilder().append(faction.getTag()).toString()).replace("%players_max", new StringBuilder().append(Bukkit.getServer().getMaxPlayers()).toString()).replace("%player", this.player.getName()).replace("%day", new StringBuilder().append(calendar.get(5)).toString()).replace("%month", new StringBuilder().append(1 + calendar.get(2)).toString()).replace("%xp", new StringBuilder().append(NumberUtil.formatNumberByCommas(this.player.getLevel())).toString()).replace("%kdr", new StringBuilder().append(NumberUtil.formatNumberByDecimal(PvPUtil.getKDRRation(this.player))).toString()).replace("%kills", new StringBuilder().append(PvPUtil.getKills(this.player)).toString()).replace("%deaths", new StringBuilder().append(PvPUtil.getDeaths(this.player)).toString()).replace("%balance", NumberUtil.formatNumberBySuffix(this.instance.getVaultManager().getEconomy().getBalance((OfflinePlayer) this.player))).replace("%faction_online", new StringBuilder().append(faction.getOnlinePlayers().size()).toString()).replace("%faction_members", new StringBuilder().append(faction.getFPlayers().size()).toString());
        }
        return displayLine;
    }

    public void cancel() {
        this.cancel = true;
        if (this.scheduler != null) {
            this.scheduler.cancel();
        }
    }

    static /* synthetic */ void access$8(final Scoreboard scoreboard, final BukkitTask scheduler) {
        scoreboard.scheduler = scheduler;
    }
}
