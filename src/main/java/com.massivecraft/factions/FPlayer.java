/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.cosmicpvp.factionpoints.FactionsCoreChunkAPI
 *  com.earth2me.essentials.User
 *  com.earth2me.essentials.utils.DateUtil
 *  com.google.common.collect.Lists
 *  org.arkhamnetwork.Arkkit.utils.com.massivecraft.factions.util.WorldGuardUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import com.google.common.collect.Lists;

import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.LandClaimEvent;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.*;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.factions.util.WorldGuardUtils;
import com.massivecraft.factions.zcore.persist.PlayerEntity;
import mkremins.fanciful.FancyMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class FPlayer
        extends PlayerEntity
        implements EconomyParticipator {
    private final transient DecimalFormat formatter = new DecimalFormat("#,###");
    public String lastFactionMessage = "";
    private transient FLocation lastStoodAt = new FLocation();
    private String factionId;
    private boolean onlyAllowTpFromFactionMembers;
    private boolean hasGlobalChatHidden;
    private transient Faction cachedFaction;
    private Role role;
    private String title;
    private double power;
    private double powerBoost;
    private long lastPowerUpdateTime;
    private long lastLoginTime;
    private transient boolean mapAutoUpdating;
    private transient Faction autoClaimFor;
    private transient boolean autoSafeZoneEnabled;
    private transient boolean autoWarZoneEnabled;
    private transient boolean isAdminBypassing = false;
    private transient boolean loginPvpDisabled;
    private transient boolean deleteMe;
    protected int mapHeight; // default to old value
    private ChatMode chatMode;
    private transient boolean spyingChat = false;
    private String cachedLastKnownName = null;
    public boolean discordSetup = false;
    public String discordUserID = "";
    protected String name;
    boolean inspectMode = false;

    public FPlayer() {
        this.resetFactionData();
        this.power = Conf.powerPlayerStarting;
        this.lastPowerUpdateTime = System.currentTimeMillis();
        this.lastLoginTime = System.currentTimeMillis();
        this.mapAutoUpdating = false;
        this.autoClaimFor = null;
        this.autoSafeZoneEnabled = false;
        this.autoWarZoneEnabled = false;
        this.loginPvpDisabled = Conf.noPVPDamageToOthersForXSecondsAfterLogin > 0;
        this.deleteMe = false;
        this.powerBoost = 0.0;
        this.mapHeight = Conf.mapHeight;

        if (!Conf.newPlayerStartingFactionID.equals("0") && Factions.i.exists(Conf.newPlayerStartingFactionID)) {
            this.factionId = Conf.newPlayerStartingFactionID;
        }
    }

    public Faction getFaction() {
        if (this.factionId == null) {
            return null;
        }
        if (this.cachedFaction != null && this.cachedFaction.attached()) {
            return this.cachedFaction;
        }
        try {
            this.cachedFaction = Factions.i.get(this.factionId);
            return this.cachedFaction;
        } catch (NullPointerException npe) {
            Bukkit.getLogger().info("Invalid factionId lookup for player " + this.getNameAsync() + " [" + this.factionId + "]");
            npe.printStackTrace();
            this.factionId = "0";
            return Factions.i.get("0");
        }
    }

    public void setFaction(Faction faction) {
        Faction oldFaction = this.getFaction();
        if (oldFaction != null) {
            oldFaction.removeFPlayer(this);
        }
        faction.addFPlayer(this);
        this.factionId = faction.getId();
        this.cachedFaction = faction;
    }


    public boolean isInspectMode() {
        return inspectMode;
    }

    public void setInspectMode(boolean status) {
        inspectMode = status;
    }

    public boolean discordSetup() {return this.discordSetup;}

    public String discordUserID() {return this.discordUserID;}

    public void setDiscordSetup(Boolean b) {this.discordSetup = b;}

    public void setDiscordUserID(String s) {this.discordUserID = s;}


    public String getFactionId() {
        return this.factionId;
    }

    public boolean hasFaction() {
        return !this.factionId.equals("0");
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
        Faction fac = this.getFaction();
        if (fac != null && this.getCachedUUID() != null) {
            fac.getActivePlayerRolePerms().remove(this.getCachedUUID());
        }
    }

    public boolean hasMoney(int amt) {
        Economy econ = P.p.getEcon();
        if (econ.getBalance(getPlayer()) >= amt) {
            return true;
        } else {
            getPlayer().closeInventory();
            msg(ChatColor.translateAlternateColorCodes('&', "&cYou dont have enough money!"));
            return false;
        }
    }

    public String commas(final double amount) {
        final DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(amount);
    }

    public boolean takeMoney(int amt) {
        if (hasMoney(amt)) {
            Economy econ = P.p.getEcon();
            if (econ.withdrawPlayer(getPlayer(), amt).transactionSuccess()) {
                sendMessage(ChatColor.translateAlternateColorCodes('&', "&c{amount} has been taken from your account.").replace("{amount}", commas(amt)));
                return true;
            }
        }
        return false;
    }

    public double getPowerBoost() {
        return this.powerBoost;
    }

    public void setPowerBoost(double powerBoost) {
        this.powerBoost = powerBoost;
    }

    public Faction getAutoClaimFor() {
        return this.autoClaimFor;
    }

    public void setAutoClaimFor(Faction faction) {
        this.autoClaimFor = faction;
        if (this.autoClaimFor != null) {
            this.autoSafeZoneEnabled = false;
            this.autoWarZoneEnabled = false;
        }
    }

    public boolean isAutoSafeClaimEnabled() {
        return this.autoSafeZoneEnabled;
    }

    public void setIsAutoSafeClaimEnabled(boolean enabled) {
        this.autoSafeZoneEnabled = enabled;
        if (enabled) {
            this.autoClaimFor = null;
            this.autoWarZoneEnabled = false;
        }
    }

    public boolean isAutoWarClaimEnabled() {
        return this.autoWarZoneEnabled;
    }

    public void setIsAutoWarClaimEnabled(boolean enabled) {
        this.autoWarZoneEnabled = enabled;
        if (enabled) {
            this.autoClaimFor = null;
            this.autoSafeZoneEnabled = false;
        }
    }

    public boolean isAdminBypassing() {
        return this.isAdminBypassing;
    }

    public void setIsAdminBypassing(boolean val) {
        this.isAdminBypassing = val;
    }

    public ChatMode getChatMode() {
        if (this.factionId.equals("0") || !Conf.factionOnlyChat) {
            this.chatMode = ChatMode.PUBLIC;
        }
        return this.chatMode;
    }

    public void setChatMode(ChatMode chatMode) {
        this.chatMode = chatMode;
    }

    public boolean isSpyingChat() {
        return this.spyingChat;
    }

    public void setSpyingChat(boolean chatSpying) {
        this.spyingChat = chatSpying;
    }

    @Override
    public String getAccountId() {
        return this.getId();
    }

    public final void resetFactionData() {
        if (this.getFaction() != null && Factions.i.exists(this.getFactionId())) {
            Faction currentFaction = this.getFaction();
            if (!currentFaction.removeFPlayer(this)) {
                P.p.log(ChatColor.RED + this.getNameAsync() + " is unable to leave faction " + currentFaction.getTag() + " (" + currentFaction.getId() + ")!");
            }
            if (currentFaction.isNormal()) {
                currentFaction.clearClaimOwnership(this);
            }
        }
        this.factionId = "0";
        this.cachedFaction = null;
        this.chatMode = ChatMode.PUBLIC;
        this.role = Role.RECRUIT;
        this.title = "";
        this.autoClaimFor = null;
    }

    public long getLastLoginTime() {
        return this.lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.losePowerFromBeingOffline();
        this.lastLoginTime = lastLoginTime;
        this.lastPowerUpdateTime = lastLoginTime;
        if (Conf.noPVPDamageToOthersForXSecondsAfterLogin > 0) {
            this.loginPvpDisabled = true;
        }
    }

    public boolean isMapAutoUpdating() {
        return this.mapAutoUpdating;
    }

    public void setMapAutoUpdating(boolean mapAutoUpdating) {
        this.mapAutoUpdating = mapAutoUpdating;
    }

    public boolean hasLoginPvpDisabled() {
        if (!this.loginPvpDisabled) {
            return false;
        }
        if (this.lastLoginTime + (long) (Conf.noPVPDamageToOthersForXSecondsAfterLogin * 1000) < System.currentTimeMillis()) {
            this.loginPvpDisabled = false;
            return false;
        }
        return true;
    }

    public FLocation getLastStoodAt() {
        return this.lastStoodAt;
    }

    public void setLastStoodAt(FLocation flocation) {
        this.lastStoodAt = flocation;
    }

    public boolean onlyAllowTpFromFactionMembers() {
        return this.onlyAllowTpFromFactionMembers;
    }

    public void setAllowTpFromAll(boolean b) {
        this.onlyAllowTpFromFactionMembers = !b;
    }

    public boolean hasGlobalChatHidden() {
        return this.hasGlobalChatHidden;
    }

    public void setGlobalChatHidden(boolean b) {
        this.hasGlobalChatHidden = b;
    }



    public void markForDeletion(boolean delete) {
        this.deleteMe = delete;
    }

    public String getTitle() {
        if (this.title.length() > 18) {
            this.title = this.title.substring(0, 15) + ChatColor.RESET;
        }
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNameAsync() {
        Player player;
        if (this.cachedLastKnownName != null && this.cachedLastKnownName.length() != 36) {
            return this.cachedLastKnownName;
        }
        if (this.isCachedAsOnline() && (player = this.getPlayer()) != null) {
            this.cachedLastKnownName = player.getName();
            return this.cachedLastKnownName;
        }
        if (this.cachedLastKnownName == null || this.cachedLastKnownName.length() == 36) {
            File playerFile = new File(P.p.getServer().getWorldContainer() + File.separator + P.p.getServer().getWorlds().get(0).getName() + File.separator + "playerdata" + File.separator + this.getId() + ".dat");
            try {
                NBTInputStream stream = new NBTInputStream(new FileInputStream(playerFile));

                CompoundTag tag = (CompoundTag) stream.readTag();
                List<Tag> list = tag.getValue();
                CompoundTag tag2 = (CompoundTag) list.get(list.indexOf("bukkit"));
                List<Tag> list2 = tag2.getValue();
                StringTag stringTag = (StringTag) list2.get(list2.indexOf("lastKnownName")).getValue();
                this.cachedLastKnownName = stringTag.getValue();
                return this.cachedLastKnownName;
            } catch (FileNotFoundException stream) {
            } catch (IOException stream) {
                // empty catch block
            }
        }
        return this.getId();
    }

    public void updateLastKnownName(String name) {
        if (name == null) {
            return;
        }
        if (!name.equals(this.cachedLastKnownName)) {
            P.p.log("Updating cachedLastKnownName for " + name + ", previous=" + this.cachedLastKnownName);
            this.cachedLastKnownName = name;
        }
    }

    public String getTag() {
        if (!this.hasFaction()) {
            return "";
        }
        return this.getFaction().getTag();
    }

    public String getNameAndSomethingAsync(String something) {
        String ret = this.role.getPrefix();
        if (something.length() > 0) {
            ret = ret + something + " ";
        }
        ret = ret + this.getNameAsync();
        return ret;
    }

    public String getNameAndTitleAsync() {
        return this.getNameAndSomethingAsync(this.getTitle());
    }

    public String getNameAndTagAsync() {
        return this.getNameAndSomethingAsync(this.getTag());
    }

    public String getNameAndTitleAsync(Faction faction) {
        return this.getColorTo(faction) + this.getNameAndTitleAsync();
    }

    public String getNameAndTitleAsync(FPlayer fplayer) {
        return this.getColorTo(fplayer) + this.getNameAndTitleAsync();
    }

    public FancyMessage applyColorPower(FancyMessage fancy_message) {
        if (this.getPower() >= 5.0 && this.getPower() <= 10.0) {
            fancy_message.then("(").color(Conf.colorPOWERBrackets).then(String.valueOf(this.getPowerRounded())).color(Conf.colorHighPower).then(")").color(Conf.colorPOWERBrackets);
        } else if (this.getPower() > 0.0 && this.getPower() <= 5.0) {
            fancy_message.then("(").color(Conf.colorPOWERBrackets).then(String.valueOf(this.getPowerRounded())).color(Conf.colorMiddlePower).then(")").color(Conf.colorPOWERBrackets);
        } else if (this.getPower() <= 10.0) {
            fancy_message.then("(").color(Conf.colorPOWERBrackets).then(String.valueOf(this.getPowerRounded())).color(Conf.colorMinusPower).then(")").color(Conf.colorPOWERBrackets);
        }
        return fancy_message;
    }

    public FancyMessage appendNameAndTitleWithPowerAsync(User user, double balance, FPlayer fplayer, FancyMessage fancy_message, Player instance) {
        ChatColor relation_color = this.getColorTo(fplayer);
        for (String word : this.getNameAndSomethingAsync(this.getTitle()).split(" ")) {
            ArrayList messages = Lists.newArrayList((Object[]) new FancyMessage[]{new FancyMessage(ChatColor.valueOf(Conf.hoverNames) + "Username: " + ChatColor.valueOf(Conf.hoverChatColor) + this.getNameAsync()), new FancyMessage(ChatColor.valueOf(Conf.hoverNames) + "Balance: " + ChatColor.valueOf(Conf.hoverChatColor) + "$" + this.formatter.format(balance)), new FancyMessage(ChatColor.valueOf(Conf.hoverNames) + "Power: " + ChatColor.valueOf(Conf.hoverChatColor) + this.getPowerRounded() + ChatColor.valueOf(Conf.hoverSlashColor) + "/" + ChatColor.valueOf(Conf.hoverChatColor) + (int) Conf.powerPlayerMax), new FancyMessage(ChatColor.valueOf(Conf.hoverNames) + "Last Login: " + ChatColor.valueOf(Conf.hoverChatColor) + DateUtil.formatDateDiff(user.getLastLogin()) + " ago")});
            //if (instance != null) {
            //    messages.add(new FancyMessage(ChatColor.AQUA + "Cosmic Client: " + (instance.hasMetadata("cosmicClientVersion") ? ChatColor.GREEN.toString() + ChatColor.BOLD + "\u2713" : ChatColor.RED.toString() + ChatColor.BOLD + "\u2717")));
           // }
            fancy_message.then(word + " ").color(relation_color).command("/seen " + this.getNameAsync()).formattedTooltip(messages);
        }
        this.applyColorPower(fancy_message);
        return fancy_message;
    }

    public String getChatTag() {
        if (!this.hasFaction()) {
            return "";
        }
        return String.format(Conf.chatTagFormat, this.role.getPrefix() + this.getTag());
    }

    public String getChatTag(Faction faction) {
        if (!this.hasFaction()) {
            return "";
        }
        return this.getRelationTo(faction).getColor() + this.getChatTag();
    }

    public String getChatTag(FPlayer fplayer) {
        if (!this.hasFaction()) {
            return "";
        }
        return this.getColorTo(fplayer) + this.getChatTag();
    }

    @Override
    public String describeToAsync(RelationParticipator that, boolean ucfirst) {
        return RelationUtil.describeThatToMeAsync(this, that, ucfirst);
    }

    @Override
    public String describeToAsync(RelationParticipator that) {
        return RelationUtil.describeThatToMeAsync(this, that);
    }

    @Override
    public Relation getRelationTo(RelationParticipator rp) {
        return RelationUtil.getRelationTo(this, rp);
    }

    @Override
    public Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful) {
        return RelationUtil.getRelationTo(this, rp, ignorePeaceful);
    }

    public Relation getRelationToLocation() {
        return Board.getFactionAt(new FLocation(this)).getRelationTo(this);
    }

    @Override
    public ChatColor getColorTo(RelationParticipator rp) {
        return RelationUtil.getColorOfThatToMe(this, rp);
    }

    public void heal(int amnt) {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        player.setHealth(player.getHealth() + (double) amnt);
    }

    public double getPower() {
        this.updatePower();
        return this.power;
    }

    protected void alterPower(double delta) {
        this.power += delta;
        if (this.power > this.getPowerMax()) {
            this.power = this.getPowerMax();
        } else if (this.power < this.getPowerMin()) {
            this.power = this.getPowerMin();
        }
    }

    public double getPowerMax() {
        return Conf.powerPlayerMax + this.powerBoost;
    }

    public double getPowerMin() {
        return Conf.powerPlayerMin + this.powerBoost;
    }

    public int getPowerRounded() {
        return (int) Math.round(this.getPower());
    }

    public int getPowerMaxRounded() {
        return (int) Math.round(this.getPowerMax());
    }

    public int getPowerMinRounded() {
        return (int) Math.round(this.getPowerMin());
    }

    protected void updatePower() {
        if (this.isOffline()) {
            this.losePowerFromBeingOffline();
            if (!Conf.powerRegenOffline) {
                return;
            }
        }
        long now = System.currentTimeMillis();
        long millisPassed = now - this.lastPowerUpdateTime;
        this.lastPowerUpdateTime = now;
        Player thisPlayer = this.getPlayer();
        if (thisPlayer != null && thisPlayer.isDead()) {
            return;
        }
        int millisPerMinute = 60000;
        this.alterPower((double) millisPassed * Conf.powerPerMinute / (double) millisPerMinute);
    }

    protected void losePowerFromBeingOffline() {
        if (Conf.powerOfflineLossPerDay > 0.0 && this.power > Conf.powerOfflineLossLimit) {
            long now = System.currentTimeMillis();
            long millisPassed = now - this.lastPowerUpdateTime;
            this.lastPowerUpdateTime = now;
            double loss = (double) millisPassed * Conf.powerOfflineLossPerDay / 8.64E7;
            if (this.power - loss < Conf.powerOfflineLossLimit) {
                loss = this.power;
            }
            this.alterPower(-loss);
        }
    }

    public void onDeath() {
        double old = this.getPower();
        this.updatePower();
        this.alterPower(-Conf.powerPerDeath);
        Faction fac = this.getFaction();
        Bukkit.getLogger().info("[Factions] " + this.getNameAsync() + " died, Power: " + old + " => " + this.getPower() + " (-" + Conf.powerPerDeath + ") " + (fac == null ? "N/A" : fac.getTag() + " Power/Land: " + fac.getPower() + " Land: " + fac.getLandRounded()));
    }

    public boolean isInOwnTerritory() {
        return Board.getFactionAt(new FLocation(this)) == this.getFaction();
    }

    public boolean isInOthersTerritory() {
        try {
            Faction factionHere = Board.getFactionAt(new FLocation(this));
            return factionHere != null && factionHere.isNormal() && factionHere != this.getFaction();
        } catch (NullPointerException npe) {
            Bukkit.getLogger().info("(DEBUG) Null FLocation returned by ID:" + this.getId() + " bukkitPlayer:" + this.bukkitPlayer + " Location:" + (this.bukkitPlayer == null ? "N/A" : this.bukkitPlayer.getLocation()));
            npe.printStackTrace();
            return true;
        }
    }

    public boolean isInAllyTerritory() {
        return Board.getFactionAt(new FLocation(this)).getRelationTo(this).isAlly();
    }

    public boolean isInNeutralTerritory() {
        return Board.getFactionAt(new FLocation(this)).getRelationTo(this).isNeutral();
    }

    public boolean isInEnemyTerritory() {
        return Board.getFactionAt(new FLocation(this)).getRelationTo(this).isEnemy();
    }

    public String getFactionHereMessage() {
        Faction factionHere = Board.getFactionAt(this.getLastStoodAt());
        String factionTag = factionHere.getTag(this);
        if (ChatColor.stripColor(factionTag).equals("WarZone")) {
            if (WorldGuardUtils.isPvPDisabled(this.getPlayer().getLocation())) {
                return ChatColor.GREEN + " ~ " + ChatColor.GREEN + "SafeZone - PvP is " + ChatColor.UNDERLINE + "disabled" + ChatColor.GREEN + " here.";
            }
            return ChatColor.DARK_RED + " ~ " + ChatColor.DARK_RED + "WarZone - PvP is " + ChatColor.UNDERLINE + "enabled" + ChatColor.DARK_RED + " here.";
        }
        if (ChatColor.stripColor(factionTag).equals("Wilderness")) {
            return ChatColor.DARK_GREEN + " ~ " + ChatColor.DARK_GREEN + "Wilderness - PvP is " + ChatColor.UNDERLINE + "enabled" + ChatColor.DARK_GREEN + " in this unclaimed land.";
        }
        String msg = factionHere.getRelationTo(this).getColor() + " ~ " + factionHere.getTag(this);
        if (factionHere.getDescription().length() > 0) {
            msg = msg + " - " + factionHere.getDescription();
        }
        return msg;
    }

    public void sendFactionHereMessage() {
        this.lastFactionMessage = this.getFactionHereMessage();
        this.sendMessage(this.lastFactionMessage);
    }

    public void leave(boolean makePay) {
        Object rel;
        Faction factionAtLocation;
        final Faction myFaction = this.getFaction();
        boolean bl = makePay = makePay && Econ.shouldBeUsed() && !this.isAdminBypassing();
        if (myFaction == null) {
            this.resetFactionData();
            return;
        }
        boolean perm = myFaction.isPermanent();
        if (!perm && this.getRole() == Role.ADMIN && myFaction.getFPlayers().size() > 1) {
            this.msg("<b>You must give the admin role to someone else first.");
            return;
        }
        if (!Conf.canLeaveWithNegativePower && this.getPower() < 0.0) {
            this.msg("<b>You cannot leave until your power is positive.");
            return;
        }
        if (this.bukkitPlayer != null && this.bukkitPlayer.isOnline() && (((Relation) (rel = (factionAtLocation = Board.getFactionAt(new FLocation(this.bukkitPlayer.getLocation()))).getRelationTo(this))).isMember() || ((Relation) rel).isAlly())) {
            if (((Relation) rel).isMember()) {
                this.msg("<b>You cannot leave a faction while in a chunk owned by that faction.");
            } else {
                this.msg("<b>You cannot leave a faction while in a chunk owned by an ally!");
            }
            return;
        }
        if (makePay && !Econ.hasAtLeast(this, Conf.econCostLeave, "to leave your faction.")) {
            return;
        }
        FPlayerLeaveEvent leaveEvent = new FPlayerLeaveEvent(this, myFaction, FPlayerLeaveEvent.PlayerLeaveReason.LEAVE);
        Bukkit.getServer().getPluginManager().callEvent(leaveEvent);
        if (leaveEvent.isCancelled()) {
            return;
        }
        if (makePay && !Econ.modifyMoney(this, -Conf.econCostLeave, "to leave your faction.", "for leaving your faction.")) {
            return;
        }
        if (myFaction.isNormal()) {
            for (final FPlayer fplayer : myFaction.getFPlayersWhereOnline(true)) {
                P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> fplayer.msg("<b>%s<b> left the faction %s", FPlayer.this.describeToAsync(fplayer, true), myFaction.describeToAsync(fplayer)));
            }
            if (Conf.logFactionLeave) {
                P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> P.p.log(FPlayer.this.getNameAsync() + " left the faction: " + myFaction.getTag()));
            }
        }
        this.resetFactionData();
        P.p.logFactionEvent(myFaction, FLogType.INVITES, this.getNameAsync(), CC.Red + "left", "the faction");
        if (myFaction.isNormal() && !perm && myFaction.getFPlayers().isEmpty()) {
            double amount;
            for (final FPlayer fplayer : FPlayers.i.getOnline()) {
                fplayer.msg("<i>** <i>%s<i> was disbanded by %s", myFaction.describeToAsync(fplayer, true), this.getNameAsync());
            }
            if (Conf.bankEnabled && (amount = Econ.getFactionBalance(myFaction)) > 0.0) {
                Econ.transferAllBankMoney(myFaction, this);
                String amountString = Econ.moneyString(amount);
                this.msg("<i>You have been given the disbanded faction's bank, totaling %s.", amountString);
                P.p.log(this.getNameAsync() + " has been given bank holdings of " + amountString + " from disbanding " + myFaction.getTag() + ".");
            }
            myFaction.detach();
            if (Conf.logFactionDisband) {
                P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> P.p.log("The faction " + myFaction.getTag() + " (" + myFaction.getId() + ") was disbanded due to the last player (" + FPlayer.this.getNameAsync() + ") leaving."));
            }
        }
    }

    public boolean canClaimForFaction(Faction forFaction) {
        if (forFaction.isNone()) {
            return false;
        }
        return this.isAdminBypassing() || forFaction == this.getFaction() && this.getRole().isAtLeast(Role.MODERATOR) || forFaction.isSafeZone() && Permission.MANAGE_SAFE_ZONE.has(this.getPlayer()) || forFaction.isWarZone() && Permission.MANAGE_WAR_ZONE.has(this.getPlayer());
    }

    public boolean canClaimForFactionAtLocationAsync(Faction forFaction, Location location, boolean notifyFailure) {
        String error = null;
        FLocation flocation = new FLocation(location);
        Faction myFaction = this.getFaction();
        Faction currentFaction = Board.getFactionAt(flocation);
        int ownedLand = forFaction.getLandRounded();
        Player player = this.getPlayer();
        boolean hasExplicitAccess = false;
        FactionPermissions permissions = P.p.getPermissionManager().getPermissions(forFaction);
        if (permissions != null && player != null) {
            Set<FactionPermission> perms = permissions.getDefaultPlayerPermissions().get(player.getUniqueId());
            if (perms != null && !perms.contains(FactionPermission.CLAIMING)) {
                if (!this.getRole().isAtLeast(Role.COLEADER)) {
                    if (notifyFailure) {
                        this.sendMessage(CC.RedB + "(!) " + CC.Red + "You do not have permission to claim land for " + forFaction.getTag() + "!");
                        this.sendMessage(CC.Gray + "Speak with a Co Leader+ to grant you the /f perm!");
                    }
                    return false;
                }
            } else if (perms != null && perms.contains(FactionPermission.CLAIMING)) {
                hasExplicitAccess = true;
            }
        }
        if (Conf.worldsNoClaiming.contains(flocation.getWorldName())) {
            error = P.p.txt.parse("<b>Sorry, this world has land claiming disabled.");
        } else {
            if (this.isAdminBypassing()) {
                return true;
            }
            if (forFaction.isSafeZone() && Permission.MANAGE_SAFE_ZONE.has(this.getPlayer())) {
                return true;
            }
            if (forFaction.isWarZone() && Permission.MANAGE_WAR_ZONE.has(this.getPlayer())) {
                return true;
            }
            if (myFaction != forFaction) {
                error = P.p.txt.parse("<b>You can't claim land for <h>%s<b>.", forFaction.describeToAsync(this));
            } else if (forFaction == currentFaction) {
                error = P.p.txt.parse("<b>%s<b> already owns this land.", forFaction.describeToAsync(this, true));
            } else if (this.getRole().value < Role.MODERATOR.value && !hasExplicitAccess) {
                error = P.p.txt.parse("<b>You must be <h>%s<b> to claim land.", Role.MODERATOR.toString());
            } else if (forFaction.getFPlayers().size() < Conf.claimsRequireMinFactionMembers) {
                error = P.p.txt.parse("Factions must have at least <h>%s<b> members to claim land.", Conf.claimsRequireMinFactionMembers);
            } else if (currentFaction.isSafeZone()) {
                error = P.p.txt.parse("<b>You can not claim a Safe Zone.");
            } else if (currentFaction.isWarZone()) {
                error = P.p.txt.parse("<b>You can not claim a War Zone.");
            } else if (ownedLand >= forFaction.getPowerRounded()) {
                error = P.p.txt.parse("<b>You can't claim more land! You need more power!");
            } else if (Conf.claimedLandsMax != 0 && ownedLand >= Conf.claimedLandsMax && forFaction.isNormal()) {
                error = P.p.txt.parse("<b>Limit reached. You can't claim more land!");
            } else if (currentFaction.getRelationTo(forFaction) == Relation.ALLY) {
                error = P.p.txt.parse("<b>You can't claim the land of your allies.");
            } else if (!(!Conf.claimsMustBeConnected || this.isAdminBypassing() || myFaction.getLandRoundedInWorld(flocation.getWorldName()) <= 0 || Board.isConnectedLocation(flocation, myFaction) || Conf.claimsCanBeUnconnectedIfOwnedByOtherFaction && currentFaction.isNormal())) {
                error = Conf.claimsCanBeUnconnectedIfOwnedByOtherFaction ? P.p.txt.parse("<b>You can only claim additional land which is connected to your first claim or controlled by another faction!") : P.p.txt.parse("<b>You can only claim additional land which is connected to your first claim!");
            } else if (currentFaction.isNormal()) {
                if (myFaction.isPeaceful()) {
                    error = P.p.txt.parse("<b>%s<b> owns this land. Your faction is peaceful, so you cannot claim land from other factions.", currentFaction.getTag(this));
                } else if (currentFaction.isPeaceful()) {
                    error = P.p.txt.parse("<b>%s<b> owns this land, and is a peaceful faction. You cannot claim land from them.", currentFaction.getTag(this));
                } else if (!currentFaction.hasLandInflation()) {
                    error = P.p.txt.parse("<b>%s<b> owns this land and is strong enough to keep it.", currentFaction.getTag(this));
                } else if (!Board.isBorderLocation(flocation)) {
                    error = P.p.txt.parse("<b>You must start claiming land at the border of the territory.");
                }
                /*if (P.p.factionPointsEnabled && error == null && !currentFaction.equals(myFaction) && FactionsCoreChunkAPI.isCoreChunk(flocation) && Board.getFactionCoordCount(currentFaction) > 1) {
                    error = P.p.txt.parse("<b>You cannot overclaim %s's<b> Core Chunk unless it is their only claim!", currentFaction.getTag(this));
                }*/
            }
        }
        if (notifyFailure && error != null) {
            this.msg(error);
        }
        return error == null;
    }


    public boolean attemptClaimAsync(Faction forFaction, Location location, boolean notifyFailure) {
        final FLocation flocation = new FLocation(location);
        final Faction currentFaction = Board.getFactionAt(flocation);
        if (!this.canClaimForFactionAtLocationAsync(forFaction, location, notifyFailure)) {
            return false;
        }
        Player player = this.getPlayer();
        if (player == null) {
            return false;
        }
        LandClaimEvent claimEvent = new LandClaimEvent(flocation, forFaction, this);
        Bukkit.getServer().getPluginManager().callEvent(claimEvent);
        if (claimEvent.isCancelled()) {
            return false;
        }
        HashSet<FPlayer> informTheseFPlayers = new HashSet<FPlayer>();
        informTheseFPlayers.add(this);
        informTheseFPlayers.addAll(forFaction.getFPlayersWhereOnline(true));
        for (final FPlayer fp : informTheseFPlayers) {
            P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> fp.msg(ChatColor.GREEN + "" + ChatColor.BOLD + "(!) %s <g>claimed land at " + flocation.getCoordString() + " for %s<g> from %s", FPlayer.this.describeToAsync(fp, true), forFaction.describeToAsync(fp), currentFaction.describeToAsync(fp)));
        }
        Board.setFactionAt(forFaction, flocation);
        if (Conf.logLandClaims) {
            P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> P.p.log(FPlayer.this.getNameAsync() + " claimed land at (" + flocation.getCoordString() + ") for the faction: " + forFaction.getTag()));
        }
        return true;
    }

    @Override
    public boolean shouldBeSaved() {
        if (!(this.hasFaction() || this.getPowerRounded() != this.getPowerMaxRounded() && this.getPowerRounded() != (int) Math.round(Conf.powerPlayerStarting) || this.hasGlobalChatHidden)) {
            return false;
        }
        return !this.deleteMe;
    }

    public String getName() {
        if (this.name == null) {
            // Older versions of FactionsUUID don't save the name,
            // so `name` will be null the first time it's retrieved
            // after updating
            OfflinePlayer offline = Bukkit.getOfflinePlayer(UUID.fromString(getId()));
            this.name = offline.getName() != null ? offline.getName() : getId();
        }
        return name;
    }

    public String getNameAndSomething(String something) {
        String ret = this.role.getPrefix();
        if (something.length() > 0) {
            ret += something + " ";
        }
        ret += this.getName();
        return ret;
    }

    public void sendFancyMessage(FancyMessage message) {
        Player player = getPlayer();
        if (player == null || !player.isOnGround()) {
            return;
        }

        message.send(player);
    }

    public int getMapHeight() {
        if (this.mapHeight < 1) {
            this.mapHeight = Conf.mapHeight;
        }

        return this.mapHeight;
    }

    public void setMapHeight(int height) {
        this.mapHeight = Math.min(height, (Conf.mapHeight * 2));
    }


    public void sendFancyMessage(List<FancyMessage> messages) {
        Player player = getPlayer();
        if (player == null) {
            return;
        }

        for (FancyMessage msg : messages) {
            msg.send(player);
        }
    }

    public String getNameAndTag() {
        return this.getNameAndSomething(this.getTag());
    }


    @Override
    public void msg(String str, Object... args) {
        this.sendMessage(P.p.txt.parse(str, args));
    }

}

