/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class CmdAdmin
        extends FCommand {
    public CmdAdmin() {
        this.aliases.add("admin");
        this.requiredArgs.add("player name");
        this.permission = Permission.ADMIN.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
            FPlayer admin;
            FPlayer fyou = CmdAdmin.this.argAsBestFPlayerMatch(0);
            if (fyou == null) {
                return;
            }
            boolean permAny = Permission.ADMIN_ANY.has(CmdAdmin.this.sender, false);
            Faction targetFaction = fyou.getFaction();
            if (targetFaction != CmdAdmin.this.myFaction && !permAny) {
                CmdAdmin.this.msg("%s<i> is not a member in your faction.", fyou.describeToAsync(CmdAdmin.this.fme, true));
                return;
            }
            if (CmdAdmin.this.fme != null && CmdAdmin.this.fme.getRole() != Role.ADMIN && !permAny) {
                CmdAdmin.this.msg("<b>You are not the faction admin.");
                return;
            }
            if (fyou == CmdAdmin.this.fme && !permAny) {
                CmdAdmin.this.msg("<b>The target player musn't be yourself.");
                return;
            }
            if (fyou.getFaction() != targetFaction) {
                FPlayerJoinEvent event = new FPlayerJoinEvent(FPlayers.i.get(CmdAdmin.this.me), targetFaction, FPlayerJoinEvent.PlayerJoinReason.LEADER);
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }
            }
            if (fyou == (admin = targetFaction.getFPlayerAdmin())) {
                targetFaction.promoteNewLeader(admin);
                CmdAdmin.this.msg("<i>You have demoted %s<i> from the position of faction admin.", fyou.describeToAsync(CmdAdmin.this.fme, true));
                fyou.msg("<i>You have been demoted from the position of faction admin by %s<i>.", CmdAdmin.this.senderIsConsole ? "a server admin" : CmdAdmin.this.fme.describeToAsync(fyou, true));
                P.p.getFlogManager().log(targetFaction, FLogType.RANK_EDIT, CmdAdmin.this.sender.getName(), admin.getNameAsync(), ChatColor.YELLOW + "Member");
                return;
            }
            if (admin != null) {
                admin.setRole(Role.MODERATOR);
                P.p.getFlogManager().log(targetFaction, FLogType.RANK_EDIT, CmdAdmin.this.sender.getName(), admin.getNameAsync(), ChatColor.LIGHT_PURPLE + "Mod");
            }
            fyou.setRole(Role.ADMIN);
            CmdAdmin.this.msg("<i>You have promoted %s<i> to the position of faction admin.", fyou.describeToAsync(CmdAdmin.this.fme, true));
            P.p.getFlogManager().log(targetFaction, FLogType.RANK_EDIT, CmdAdmin.this.sender.getName(), fyou.getNameAsync(), ChatColor.RED + "Admin");
        });
    }

}

