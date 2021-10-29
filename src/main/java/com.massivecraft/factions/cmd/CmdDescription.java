/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.arkhamnetwork.Arkkit.patches.chat_filter.ChatUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Server
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class CmdDescription
        extends FCommand {
    public CmdDescription() {
        this.aliases.add("desc");
        this.requiredArgs.add("desc");
        this.errorOnToManyArgs = false;
        this.permission = Permission.DESCRIPTION.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = true;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
            if (!CmdDescription.this.payForCommand(Conf.econCostDesc, "to change faction description", "for changing faction description")) {
                return;
            }
            String desc = TextUtil.implode(CmdDescription.this.args, " ").replaceAll("(&([a-f0-9]))", "& $2");
            CmdDescription.this.myFaction.setDescription(desc);
            Bukkit.getScheduler().scheduleSyncDelayedTask(P.p, () -> P.p.logFactionEvent(CmdDescription.this.myFaction, FLogType.FDESC_EDIT, CmdDescription.this.fme.getNameAsync(), desc));
            if (!Conf.broadcastDescriptionChanges) {
                CmdDescription.this.fme.msg("<l>You have changed the description of <h>%s<l> to:", CmdDescription.this.myFaction.describeToAsync(CmdDescription.this.fme));
                CmdDescription.this.fme.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "\"" + CmdDescription.this.myFaction.getDescription() + "\"");
                return;
            }
            for (FPlayer fplayer : FPlayers.i.getOnline()) {
                fplayer.msg("<i>The faction %s<i> changed their description to:", CmdDescription.this.myFaction.describeToAsync(fplayer));
                fplayer.sendMessage(CmdDescription.this.myFaction.getDescription());
            }
        });
    }

}

