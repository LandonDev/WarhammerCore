package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class CmdHelp extends FCommand {
    public ArrayList<ArrayList<String>> helpPages;

    public CmdHelp() {
        this.aliases.add("help");
        this.aliases.add("h");
        this.aliases.add("?");
        this.optionalArgs.put("page", "1");
        this.permission = Permission.HELP.node;
        this.disableOnLock = false;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    public void perform() {
        if (this.helpPages == null)
            updateHelp();
        int page = argAsInt(0, Integer.valueOf(1)).intValue();
        sendMessage(this.p.txt.titleize("Factions Help (" + page + "/" + this.helpPages.size() + ")"));
        page--;
        if (page < 0 || page >= this.helpPages.size()) {
            msg("<b>This page does not exist", new Object[0]);
            return;
        }
        sendMessage(this.helpPages.get(page));
    }

    public void updateHelp() {
        this.helpPages = new ArrayList<>();
        ArrayList<String> pageLines = new ArrayList<>();
        pageLines.add(this.p.cmdBase.cmdHelp.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdList.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdGlobal.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdShow.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdPower.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdJoin.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdLeave.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdChat.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdHome.getUseageTemplate(true));
        pageLines.add(ChatColor.AQUA + "/f access " + ChatColor.GRAY + "Grant access to players or factions to edit blocks in your claims.");
        pageLines.add(ChatColor.AQUA + "/f perm " + ChatColor.GRAY + "Specify what blocks players and factions can edit in claims they have /f access to.");
        pageLines.add(this.p.txt.parse("<i>Learn how to create a faction on the next page."));
        this.helpPages.add(pageLines);
        pageLines = new ArrayList<>();
        pageLines.add(this.p.cmdBase.cmdCreate.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdDescription.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdTag.getUseageTemplate(true));
        pageLines.add(this.p.txt.parse("<i>You might want to close it and use invitations:"));
        pageLines.add(this.p.cmdBase.cmdOpen.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdInvite.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdDeinvite.getUseageTemplate(true));
        pageLines.add(this.p.txt.parse("<i>And don't forget to set your home:"));
        pageLines.add(this.p.cmdBase.cmdSethome.getUseageTemplate(true));
        pageLines.add(ChatColor.AQUA + "/f showclaims " + ChatColor.GRAY + "List all your faction's claims.");
        this.helpPages.add(pageLines);
        if (Econ.isSetup() && Conf.econEnabled && Conf.bankEnabled) {
            pageLines = new ArrayList<>();
            pageLines.add("");
            pageLines.add(this.p.txt.parse("<i>Your faction has a bank which is used to pay for certain"));
            pageLines.add(this.p.txt.parse("<i>things, so it will need to have money deposited into it."));
            pageLines.add(this.p.txt.parse("<i>To learn more, use the money command."));
            pageLines.add("");
            pageLines.add(this.p.cmdBase.cmdMoney.getUseageTemplate(true));
            pageLines.add("");
            pageLines.add(ChatColor.AQUA + "/f wealth " + ChatColor.GRAY + "Display the richest factions.");
            pageLines.add("");
            pageLines.add(ChatColor.AQUA + "/f top " + ChatColor.GRAY + "Display the top factions.");
            pageLines.add("");
            this.helpPages.add(pageLines);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("CosmicFactionPoints")) {
            pageLines = new ArrayList<>();
            pageLines.add("");
            pageLines.add(this.p.txt.parse("<i>Your faction's leaderboard position is determined by"));
            pageLines.add(this.p.txt.parse("<i>Faction Points. To learn more, use the /fp command."));
            pageLines.add("");
            pageLines.add(ChatColor.AQUA + "/fp help " + ChatColor.GRAY + "Display information about Faction Points.");
            pageLines.add("");
            this.helpPages.add(pageLines);
        }
        pageLines = new ArrayList<>();
        pageLines.add(this.p.cmdBase.cmdClaim.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdClaimLine.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdAutoClaim.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdUnclaim.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdUnclaimall.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdKick.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdMod.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdAdmin.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdTitle.getUseageTemplate(true));
        pageLines.add(ChatColor.AQUA + "/f scoreboard " + ChatColor.GRAY + "Toggle your side faction scoreboard.");
        pageLines.add(ChatColor.AQUA + "/f tptoggle " + ChatColor.GRAY + "Toggle non-member players from requesting to teleport to you.");
        pageLines.add(ChatColor.AQUA + "/f upgrade " + ChatColor.GRAY + "View your Faction Upgrades.");
        pageLines.add(ChatColor.AQUA + "/f status " + ChatColor.GRAY + "View your Faction Members balance and 2FA status.");
        pageLines.add(this.p.txt.parse("<i>Player titles are just for fun. No rules connected to them."));
        this.helpPages.add(pageLines);
        pageLines = new ArrayList<>();
        pageLines.add(this.p.cmdBase.cmdMap.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdBoom.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdOwner.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdOwnerUnclaim.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdOwnerList.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdAccess.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdSetAllyWarp.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdDeleteAllyWarp.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdTpAlly.getUseageTemplate(true));
        pageLines.add(ChatColor.AQUA + "/f focus " + ChatColor.LIGHT_PURPLE + "<player name>" + ChatColor.GRAY + "Focus enemy players.");
        pageLines.add(ChatColor.AQUA + "/f unfocus" + ChatColor.GRAY + "Clear any focused enemy players.");
        pageLines.add(ChatColor.AQUA + "/f togglerequests " + ChatColor.GRAY + "Toggle faction ally/enemy request verbosity.");
        pageLines.add(ChatColor.AQUA + "/f tptoggle " + ChatColor.GRAY + "Toggle between faction only and all player teleport requests.");
        pageLines.add(this.p.txt.parse("<i>Claimed land with ownership set is further protected so"));
        pageLines.add(this.p.txt.parse("<i>that only the owner(s), faction admin, and"));
        pageLines.add(this.p.txt.parse("<i>faction moderators have full access."));
        this.helpPages.add(pageLines);
        pageLines = new ArrayList<>();
        pageLines.add(this.p.cmdBase.cmdDisband.getUseageTemplate(true));
        pageLines.add("");
        pageLines.add(this.p.cmdBase.cmdRelationTruce.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdRelationAlly.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdRelationNeutral.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdRelationEnemy.getUseageTemplate(true));
        pageLines.add(this.p.txt.parse("<i>Set the relation you WISH to have with another faction."));
        pageLines.add(this.p.txt.parse("<i>Your default relation with other factions will be neutral."));
        pageLines.add(this.p.txt.parse("<i>If BOTH factions choose \"ally\" you will be allies."));
        pageLines.add(this.p.txt.parse("<i>If ONE faction chooses \"enemy\" you will be enemies."));
        this.helpPages.add(pageLines);
        pageLines = new ArrayList<>();
        pageLines.add(this.p.txt.parse("<i>You can never hurt members or allies."));
        pageLines.add(this.p.txt.parse("<i>You can not hurt neutrals in their own territory."));
        pageLines.add(this.p.txt.parse("<i>You can always hurt enemies and players without faction."));
        pageLines.add("");
        pageLines.add(this.p.txt.parse("<i>Damage from enemies is reduced in your own territory."));
        pageLines.add(this.p.txt.parse("<i>When you die you lose power. It is restored over time."));
        pageLines.add(this.p.txt.parse("<i>The power of a faction is the sum of all member power."));
        pageLines.add(this.p.txt.parse("<i>The power of a faction determines how much land it can hold."));
        pageLines.add(this.p.txt.parse("<i>You can claim land from factions with too little power."));
        this.helpPages.add(pageLines);
        pageLines = new ArrayList<>();
        pageLines.add(this.p.txt.parse("<i>Only faction members can build and destroy in their own"));
        pageLines.add(this.p.txt.parse("<i>territory. Usage of the following items is also restricted:"));
        pageLines.add(this.p.txt.parse("<i>Door, Chest, Furnace, Dispenser, Diode."));
        pageLines.add("");
        pageLines.add(this.p.txt.parse("<i>Make sure to put pressure plates in front of doors for your"));
        pageLines.add(this.p.txt.parse("<i>guest visitors. Otherwise they can't get through. You can"));
        pageLines.add(this.p.txt.parse("<i>also use this to create member only areas."));
        pageLines.add(this.p.txt.parse("<i>As dispensers are protected, you can create traps without"));
        pageLines.add(this.p.txt.parse("<i>worrying about those arrows getting stolen."));
        this.helpPages.add(pageLines);
        pageLines = new ArrayList<>();
        pageLines.add("Finally some commands for the server admins:");
        pageLines.add(this.p.cmdBase.cmdBypass.getUseageTemplate(true));
        pageLines.add(this.p.txt.parse("<c>/f claim safezone <i>claim land for the Safe Zone"));
        pageLines.add(this.p.txt.parse("<c>/f claim warzone <i>claim land for the War Zone"));
        pageLines.add(this.p.txt.parse("<c>/f autoclaim [safezone|warzone] <i>take a guess"));
        pageLines.add(this.p.cmdBase.cmdSafeunclaimall.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdWarunclaimall.getUseageTemplate(true));
        pageLines.add(this.p.txt.parse("<i>Note: " + this.p.cmdBase.cmdUnclaim.getUseageTemplate(false) + P.p.txt.parse("<i>") + " works on safe/war zones as well."));
        pageLines.add(this.p.cmdBase.cmdPeaceful.getUseageTemplate(true));
        this.helpPages.add(pageLines);
        pageLines = new ArrayList<>();
        pageLines.add(this.p.txt.parse("<i>More commands for server admins:"));
        pageLines.add(this.p.cmdBase.cmdChatSpy.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdPermanent.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdPermanentPower.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdPowerBoost.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdConfig.getUseageTemplate(true));
        this.helpPages.add(pageLines);
        pageLines = new ArrayList<>();
        pageLines.add(this.p.txt.parse("<i>Even more commands for server admins:"));
        pageLines.add(this.p.cmdBase.cmdLock.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdReload.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdSaveAll.getUseageTemplate(true));
        pageLines.add(this.p.cmdBase.cmdVersion.getUseageTemplate(true));
        this.helpPages.add(pageLines);
    }
}
