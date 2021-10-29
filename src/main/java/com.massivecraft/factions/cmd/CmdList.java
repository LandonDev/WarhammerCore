/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Server
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.LinkedList;

public class CmdList
        extends FCommand {
    private static LinkedList<Faction> cachedFactionList;
    private static long lastSort;

    static {
        lastSort = 0L;
    }

    public CmdList() {
        this.aliases.add("list");
        this.aliases.add("ls");
        this.optionalArgs.put("page", "1");
        this.permission = Permission.LIST.node;
        this.disableOnLock = false;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
        cachedFactionList = new LinkedList<>();
    }

    @Override
    public void perform() {
        if (!this.payForCommand(Conf.econCostList, "to list the factions", "for listing the factions")) {
            return;
        }
        P.getP().getServer().getScheduler().runTaskAsynchronously(this.p, () -> {
            ArrayList<Faction> factionList;
            if (System.currentTimeMillis() - lastSort > 300000L) {
                Bukkit.getLogger().info("[Factions] Updating /f list cachedFactionList object...");
                factionList = new ArrayList<>(Factions.i.get());
                factionList.remove(Factions.i.getNone());
                factionList.remove(Factions.i.getSafeZone());
                factionList.remove(Factions.i.getWarZone());
                factionList.sort((f1, f2) -> {
                    int f2Size;
                    int f1Size = f1.getFPlayers().size();
                    if (f1Size < (f2Size = f2.getFPlayers().size())) {
                        return 1;
                    }
                    if (f1Size > f2Size) {
                        return -1;
                    }
                    return 0;
                });
                factionList.sort((f1, f2) -> {
                    int f2Size;
                    int f1Size = f1.getFPlayersWhereOnline(true).size();
                    if (f1Size < (f2Size = f2.getFPlayersWhereOnline(true).size())) {
                        return 1;
                    }
                    if (f1Size > f2Size) {
                        return -1;
                    }
                    return 0;
                });
                factionList.add(0, Factions.i.getNone());
                cachedFactionList = new LinkedList<>(factionList);
                lastSort = System.currentTimeMillis();
            } else {
                factionList = new ArrayList<>(cachedFactionList);
            }
            ArrayList<String> lines = new ArrayList<>();
            int pageheight = 9;
            int pagenumber = CmdList.this.argAsInt(0, 1);
            int pagecount = factionList.size() / 9 + 1;
            if (pagenumber > pagecount) {
                pagenumber = pagecount;
            } else if (pagenumber < 1) {
                pagenumber = 1;
            }
            int start = (pagenumber - 1) * 9;
            int end = start + 9;
            if (end > factionList.size()) {
                end = factionList.size();
            }
            lines.add(CmdList.this.p.txt.titleize(ChatColor.BOLD + "Factions (" + ChatColor.WHITE + ChatColor.BOLD + pagenumber + ChatColor.GRAY + ChatColor.BOLD + "/" + ChatColor.WHITE + ChatColor.BOLD + pagecount + ChatColor.DARK_RED + ChatColor.BOLD + ")"));
            for (Faction faction : factionList.subList(start, end)) {
                if (faction.isNone()) continue;
                lines.add(CmdList.this.p.txt.parse(ChatColor.WHITE + "%s<c> %d/%d online, <p>%d/%d/%d", faction.getTag(CmdList.this.fme), faction.getFPlayersWhereOnline(true).size(), faction.getFPlayers().size(), faction.getLandRounded(), faction.getPowerRounded(), faction.getPowerMaxRounded()));
            }
            CmdList.this.sendMessage(lines);
        });
    }

}

