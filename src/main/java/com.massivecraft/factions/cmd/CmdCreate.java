package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FactionCreateEvent;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class CmdCreate extends FCommand {
    public CmdCreate() {
        this.aliases.add("create");
        this.requiredArgs.add("faction tag");
        this.permission = Permission.CREATE.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    protected static final List<String> badTags = Lists.newArrayList(new String[] { "safe" });

    public void perform() {
        String tag = argAsString(0);
        if (this.fme.hasFaction()) {
            msg("<b>You must leave your current faction first.", new Object[0]);
            return;
        }
        if (Factions.i.isTagTaken(tag)) {
            msg("<b>That tag is already in use.", new Object[0]);
            return;
        }
        for (String s : badTags) {
            if (tag.toLowerCase().contains(s)) {
                msg("<b>That faction tag cannot be used.", new Object[0]);
                return;
            }
        }
        ArrayList<String> tagValidationErrors = Factions.validateTag(tag);
        if (tagValidationErrors.size() > 0) {
            sendMessage(tagValidationErrors);
            return;
        }
        if (!canAffordCommand(Conf.econCostCreate, "to create a new faction"))
            return;
        if (this.fme.getPlayer().hasMetadata("lastFactionCreate") &&
                System.currentTimeMillis() - ((MetadataValue)this.fme.getPlayer().getMetadata("lastFactionCreate").get(0)).asLong() < 30000L) {
            this.fme.getPlayer().sendMessage(ChatColor.RED + "* You can only use /f create once per 30 seconds.");
            return;
        }
        FactionCreateEvent createEvent = new FactionCreateEvent(this.me, tag);
        Bukkit.getServer().getPluginManager().callEvent((Event)createEvent);
        if (createEvent.isCancelled())
            return;
        if (!payForCommand(Conf.econCostCreate, "to create a new faction", "for creating a new faction"))
            return;
        Faction faction = (Faction)Factions.i.create();
        if (faction == null) {
            msg("<b>There was an internal error while trying to create your faction. Please try again.", new Object[0]);
            return;
        }
        this.fme.getPlayer().setMetadata("lastFactionCreate", (MetadataValue)new FixedMetadataValue((Plugin)P.getP(), Long.valueOf(System.currentTimeMillis())));
        faction.setTag(tag);
        FPlayerJoinEvent joinEvent = new FPlayerJoinEvent((FPlayer)FPlayers.i.get((OfflinePlayer)this.me), faction, FPlayerJoinEvent.PlayerJoinReason.CREATE);
        Bukkit.getServer().getPluginManager().callEvent((Event)joinEvent);
        this.fme.setRole(Role.ADMIN);
        this.fme.setFaction(faction);
        msg("", new Object[0]);
        msg(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD.toString() + " *** " + ChatColor.AQUA + ChatColor.BOLD + "YOUR FACTION HAS BEEN CREATED!" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " ***", new Object[0]);
        msg(ChatColor.LIGHT_PURPLE + "You are now the proud leader of " + ChatColor.AQUA + ChatColor.UNDERLINE + tag, new Object[0]);
        msg("", new Object[0]);
        msg(ChatColor.GRAY + "Use " + ChatColor.UNDERLINE + "/f access" + CC.Gray + " and " + CC.GrayU + "/f perm" + CC.Gray + " to edit claim access.", new Object[0]);
        msg(ChatColor.GRAY + "Use " + ChatColor.UNDERLINE + "/f audit" + ChatColor.GRAY + " to view recent faction activity.", new Object[0]);
        msg(ChatColor.GRAY + "Use " + ChatColor.UNDERLINE + "/f help" + ChatColor.GRAY + " to review all faction commands.", new Object[0]);
        msg("", new Object[0]);
        if (Conf.bankEnabled) {
            faction.money = 0.0D;
            Econ.clearFactionBankIfDoesntExist(faction.getAccountId());
        }
        if (Conf.broadcastFactionCreation)
            for (FPlayer follower : FPlayers.i.getOnline()) {
                follower.msg("%s<i> created a new faction %s", new Object[] { this.fme.describeToAsync((RelationParticipator)follower, true), faction.getTag(follower) });
            }
        if (Conf.logFactionCreate)
            P.p.log(this.fme.getNameAsync() + " created a new faction: " + tag);
    }
}
