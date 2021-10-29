/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.arkhamnetwork.Arkkit.patches.chat_filter.ChatUtils
 *  org.arkhamnetwork.Arkkit.patches.networkevents.IgnoreUtils
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions;

import com.google.common.collect.Lists;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.*;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.factions.zcore.persist.Entity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Faction
        extends Entity
        implements EconomyParticipator {
    public final Map<String, Relation> relationWish = new ConcurrentHashMap<String, Relation>();
    private final Map<FLocation, Set<String>> claimOwnership = new ConcurrentHashMap<FLocation, Set<String>>();
    private final transient Set<FPlayer> fplayers = new HashSet<FPlayer>();
    private final Set<String> invites = new HashSet<String>();
    public transient FPlayer focusedPlayer;
    public transient long lastFocusEvent;
    public boolean announceRelationshipRequests = true;
    public int tnt = 0;
    public String motd;
    public double money = 0.0;
    private Map<Role, Set<RolePerm>> activeRolePerms = new ConcurrentHashMap<Role, Set<RolePerm>>();
    private Map<UUID, Set<RolePerm>> activePlayerRolePerms = new ConcurrentHashMap<UUID, Set<RolePerm>>();
    private long lastTruceTime;
    private long lastAllyTime;
    private int trucesAvailable;
    private int alliesAvailable;
    private boolean open = Conf.newFactionsDefaultOpen;
    private boolean peaceful = false;
    private boolean peacefulExplosionsEnabled = false;
    private boolean permanent = false;
    private String tag = "???";
    private String description = "Default faction description :(";
    private LazyLocation home;
    private transient long lastPlayerLoggedOffTime = 0L;
    private Integer permanentPower;
    private double powerBoost = 0.0;
    protected String discord;
    private String factionChatChannelId;
    private String wallNotifyChannelId = null;
    private String bufferNotifyChannelId = null;
    private String weeWooChannelId;
    private String notifyFormat = "@everyone, check %type%";
    private String weeWooFormat = "@everyone, we're being raided! Get online!";
    private String guildId;
    private String memberRoleId = null;
    private int wallCheckMinutes = 0;
    private int bufferCheckMinutes = 0;
    private Map<Long, String> checks = new ConcurrentHashMap<>();
    private Map<UUID, Integer> playerWallCheckCount = new ConcurrentHashMap<>();
    private Map<UUID, Integer> playerBufferCheckCount = new ConcurrentHashMap<>();
    private boolean weeWoo = false;


    public boolean isWeeWoo() {
        return this.weeWoo;
    }

    public void setWeeWoo(boolean weeWoo) {
        this.weeWoo = weeWoo;
    }

    public Map<UUID, Integer> getPlayerBufferCheckCount() {
        return this.playerBufferCheckCount;
    }

    public Map<UUID, Integer> getPlayerWallCheckCount() {
        return this.playerWallCheckCount;
    }

    public int getBufferCheckMinutes() {
        return this.bufferCheckMinutes;
    }

    public void setBufferCheckMinutes(int bufferCheckMinutes) {
        this.bufferCheckMinutes = bufferCheckMinutes;
    }

    public Map<Long, String> getChecks() {
        return this.checks;
    }

    public int getWallCheckMinutes() {
        return this.wallCheckMinutes;
    }

    public void setWallCheckMinutes(int wallCheckMinutes) {
        this.wallCheckMinutes = wallCheckMinutes;
    }

    public void setMemberRoleId(String memberRoleId) {
        this.memberRoleId = memberRoleId;
    }

    public String getMemberRoleId() {
        return this.memberRoleId;
    }

    public String getGuildId() {
        return this.guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public String getWeeWooFormat() {
        return this.weeWooFormat;
    }

    public void setWeeWooFormat(String weeWooFormat) {
        this.weeWooFormat = weeWooFormat;
    }

    public String getNotifyFormat() {
        return this.notifyFormat;
    }

    public void setNotifyFormat(String notifyFormat) {
        this.notifyFormat = notifyFormat;
    }

    public String getWeeWooChannelId() {
        return this.weeWooChannelId;
    }

    public void setWeeWooChannelId(String weeWooChannelId) {
        this.weeWooChannelId = weeWooChannelId;
    }

    public String getBufferNotifyChannelId() {
        return this.bufferNotifyChannelId;
    }

    public void setBufferNotifyChannelId(String bufferNotifyChannelId) {
        this.bufferNotifyChannelId = bufferNotifyChannelId;
    }

    public String getWallNotifyChannelId() {
        return this.wallNotifyChannelId;
    }

    public void setWallNotifyChannelId(String wallNotifyChannelId) {
        this.wallNotifyChannelId = wallNotifyChannelId;
    }

    public String getFactionChatChannelId() {
        return this.factionChatChannelId;
    }

    public void setFactionChatChannelId(String factionChatChannelId) {
        this.factionChatChannelId = factionChatChannelId;
    }

    public String getDiscord() {
        return this.discord;
    }

    public void setDiscord(String link) {
        this.discord = link;
    }

    public double getFactionBalance() {
        return this.money;
    }

    public boolean hasRolePerm(FPlayer player, RolePerm perm) {
        Role role = player.getRole();
        if (role == null) {
            return false;
        }
        Set<RolePerm> perms = this.activePlayerRolePerms.get(player.getCachedUUID());
        if (perms != null) {
            return perms.contains(perm);
        }
        return this.hasRolePerm(role, perm);
    }

    public boolean hasRolePerm(Role role, RolePerm perm) {
        if (role == Role.ADMIN) {
            return true;
        }
        Set<RolePerm> active = this.getRolePerms(role);
        return active.contains(perm);
    }

    public Set<RolePerm> getRolePerms(Role role) {
        Set<RolePerm> permissions = this.activeRolePerms.get(role);
        if (permissions == null) {
            permissions = new HashSet<RolePerm>();
            for (RolePerm perm : RolePerm.values()) {
                if (!role.isAtLeast(perm.getDefaultRole())) continue;
                Bukkit.getLogger().info("Giving Default " + perm.name() + " to " + role.name());
                permissions.add(perm);
            }
            Bukkit.getLogger().info("Creating default role permissions for " + role.name() + " to " + this.getTag());
            this.activeRolePerms.put(role, permissions);
        }
        return permissions;
    }

    public Set<RolePerm> getRolePerms(FPlayer fplayer, boolean fillDefault) {
        if (fplayer.getCachedUUID() == null) {
            return null;
        }
        Set<RolePerm> permissions = this.activePlayerRolePerms.get(fplayer.getCachedUUID());
        if (permissions == null) {
            permissions = this.activeRolePerms.get(fplayer.getRole());
            if (permissions != null) {
                return new HashSet<RolePerm>(permissions);
            }
            permissions = new HashSet<RolePerm>();
            for (RolePerm perm : RolePerm.values()) {
                if (!fplayer.getRole().isAtLeast(perm.getDefaultRole())) continue;
                permissions.add(perm);
            }
            if (fillDefault) {
                Bukkit.getLogger().info("Creating default role permissions for " + fplayer.getRole().name() + " to " + this.getTag());
                this.activePlayerRolePerms.put(fplayer.getCachedUUID(), permissions);
            }
        }
        return permissions;
    }

    public Set<RolePerm> setPlayerRolePermission(FPlayer fplayer, Set<RolePerm> permissions, RolePerm rolePerm, boolean active) {
        if (active) {
            permissions.add(rolePerm);
        } else {
            permissions.remove(rolePerm);
        }
        if (!this.activePlayerRolePerms.containsKey(fplayer.getCachedUUID())) {
            this.activePlayerRolePerms.put(fplayer.getCachedUUID(), permissions);
            Bukkit.getLogger().info("[Factions] Storing permissions for " + fplayer.getNameAsync());
        }
        return permissions;
    }

    public Set<RolePerm> setRolePermission(Role role, RolePerm rolePerm, boolean active) {
        Set<RolePerm> perm = this.activeRolePerms.get(role);
        if (!active && perm == null) {
            return null;
        }
        if (active) {
            if (perm == null) {
                perm = new HashSet<RolePerm>();
            }
            perm.add(rolePerm);
        } else {
            perm.remove(rolePerm);
        }
        return perm;
    }

    public int setTrucesAvailable(int available) {
        this.trucesAvailable = available;
        return this.trucesAvailable;
    }

    public int setAlliesAvailable(int available) {
        this.alliesAvailable = available;
        return this.alliesAvailable;
    }

    public int getRequestsAvailable(Relation relation) {
        if (Conf.allyTruceDailyLimit <= 0) {
            return -1;
        }
        long timeSince = this.getTimeSinceRelationUse(relation);
        if (timeSince == 0L || timeSince >= Conf.allyTruceCooldown) {
            if (relation == Relation.TRUCE) {
                this.trucesAvailable = Conf.allyTruceDailyLimit;
                this.lastTruceTime = System.currentTimeMillis();
            } else if (relation == Relation.ALLY) {
                this.alliesAvailable = Conf.allyTruceDailyLimit;
                this.lastAllyTime = System.currentTimeMillis();
            }
        }
        if (relation == Relation.TRUCE) {
            return this.trucesAvailable;
        }
        if (relation == Relation.ALLY) {
            return this.alliesAvailable;
        }
        return -1;
    }

    public long getTimeSinceRelationUse(Relation relation) {
        long lastUsed = 0;
        long l = relation == Relation.TRUCE ? this.lastTruceTime : (lastUsed = relation == Relation.ALLY ? this.lastAllyTime : -1L);
        if (lastUsed == -1L) {
            return -1L;
        }
        if (lastUsed <= 0L) {
            return 0L;
        }
        return System.currentTimeMillis() - lastUsed;
    }

    public int getRelationCount(Relation relation) {
        int count = 0;
        for (Faction faction : Factions.i.get()) {
            if (faction.getRelationTo(this) != relation) continue;
            ++count;
        }
        return count;
    }

    public boolean hasPermission(FPlayer fplayer, FLocation location, FactionPermission permission, String interact) {
        Relation to = this.getRelationTo(fplayer);
        if (to == Relation.ENEMY) {
            if (FactionPermissions.debug) {
                Bukkit.getLogger().info("Returning default enemy permissions for " + fplayer.getNameAsync());
            }
            return permission == FactionPermission.DISPENSE || permission == FactionPermission.DROPPER || permission == FactionPermission.HOPPER || permission == FactionPermission.CREEPER || permission == FactionPermission.CHEST;
        }
        Role current = fplayer.getRole();
        if (to == Relation.MEMBER && current.isAtLeast(Role.COLEADER)) {
            return true;
        }
        FactionPermissions perms = PermissionManager.get().getPermissionMap().get(this.getId());
        if (perms == null) {
            Faction their = fplayer.getFaction();
            return their != null && !their.isNone() && their.equals(this) && current.hasPermission(permission);
        }
        return perms.hasPermission(this, location, fplayer.getFaction(), fplayer, permission, interact);
    }

    public void invite(FPlayer fplayer) {
        this.invites.add(fplayer.getId());
    }

    public void deinvite(FPlayer fplayer) {
        this.invites.remove(fplayer.getId());
    }

    public boolean isInvited(FPlayer fplayer) {
        return this.invites.contains(fplayer.getId());
    }

    public Set<String> getPendingInvites() {
        return this.invites;
    }

    public boolean getOpen() {
        return this.open;
    }

    public void setOpen(boolean isOpen) {
        this.open = isOpen;
    }

    public boolean isPeaceful() {
        return this.peaceful;
    }

    public void setPeaceful(boolean isPeaceful) {
        this.peaceful = isPeaceful;
    }

    public boolean getPeacefulExplosionsEnabled() {
        return this.peacefulExplosionsEnabled;
    }

    public void setPeacefulExplosionsEnabled(boolean val) {
        this.peacefulExplosionsEnabled = val;
    }

    public boolean noExplosionsInTerritory() {
        return this.peaceful && !this.peacefulExplosionsEnabled;
    }

    public boolean isPermanent() {
        return this.permanent || !this.isNormal();
    }

    public void setPermanent(boolean isPermanent) {
        this.permanent = isPermanent;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String str) {
        if (Conf.factionTagForceUpperCase) {
            str = str.toUpperCase();
        }
        this.tag = str;
    }

    public String getTag(String prefix) {
        return prefix + this.tag;
    }

    public String getTag(Faction otherFaction) {
        if (otherFaction == null) {
            return this.getTag();
        }
        return this.getTag(this.getColorTo(otherFaction).toString());
    }

    public String describeTo(RelationParticipator that, boolean ucfirst) {
        return RelationUtil.describeThatToMeAsync(this, that, ucfirst);
    }

    public String describeTo(RelationParticipator that) {
        return RelationUtil.describeThatToMeAsync(this, that);
    }


    public String getTag(FPlayer otherFplayer) {
        if (otherFplayer == null) {
            return this.getTag();
        }
        return this.getTag(this.getColorTo(otherFplayer).toString());
    }

    public void updateMOTD(String newMOTD) {
        this.motd = newMOTD;
        this.getFPlayers().forEach(pl -> this.sendMOTD(pl, true));
    }

    public void sendMOTD(FPlayer pl, boolean updated) {
        if (this.motd == null || this.motd.isEmpty() || this.motd.equals("null")) {
            return;
        }
        pl.sendMessage("");
        pl.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "(!) Faction MOTD" + (updated ? " Updated" : "") + ": " + ChatColor.YELLOW + this.motd);
    }

    public String getComparisonTag() {
        return MiscUtil.getComparisonString(this.tag);
    }

    public boolean isWilderness() {
        return this.getId().equals("0");
    }


    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public boolean hasHome() {
        return this.getHome() != null;
    }

    public Location getHome() {
        this.confirmValidHome();
        return this.home != null ? this.home.getLocation() : null;
    }

    public void setHome(Location home) {
        this.home = new LazyLocation(home);
    }

    public void confirmValidHome() {
        if (!Conf.homesMustBeInClaimedTerritory || this.home == null || this.home.getLocation() != null && Board.getFactionAt(new FLocation(this.home.getLocation())) == this) {
            return;
        }
        this.msg("<b>Your faction home has been un-set since it is no longer in your territory.");
        this.home = null;
    }

    @Override
    public String getAccountId() {
        String aid = "faction-" + this.getId();
        return aid;
    }

    public Integer getPermanentPower() {
        return this.permanentPower;
    }

    public void setPermanentPower(Integer permanentPower) {
        this.permanentPower = permanentPower;
    }

    public boolean hasPermanentPower() {
        return this.permanentPower != null;
    }

    public double getPowerBoost() {
        return this.powerBoost;
    }

    public void setPowerBoost(double powerBoost) {
        this.powerBoost = powerBoost;
    }

    public boolean noPvPInTerritory() {
        return this.isSafeZone() || this.peaceful && Conf.peacefulTerritoryDisablePVP;
    }

    public boolean noMonstersInTerritory() {
        return this.isSafeZone() || this.peaceful && Conf.peacefulTerritoryDisableMonsters;
    }

    public boolean isNormal() {
        return !this.isNone() && !this.isSafeZone() && !this.isWarZone();
    }

    public boolean isNone() {
        return this.getId().equals("0");
    }

    public boolean isSafeZone() {
        return this.getId().equals("-1");
    }

    public boolean isWarZone() {
        return this.getId().equals("-2");
    }

    public boolean isPlayerFreeType() {
        return this.isSafeZone() || this.isWarZone();
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

    @Override
    public ChatColor getColorTo(RelationParticipator rp) {
        return RelationUtil.getColorOfThatToMe(this, rp);
    }

    public Relation getRelationWish(Faction otherFaction) {
        Relation rel = this.relationWish.get(otherFaction.getId());
        if (rel != null) {
            return rel;
        }
        return Relation.NEUTRAL;
    }

    public void setRelationWish(Faction otherFaction, Relation relation) {
        if (this.relationWish.containsKey(otherFaction.getId()) && relation.equals(Relation.NEUTRAL)) {
            this.relationWish.remove(otherFaction.getId());
        } else {
            this.relationWish.put(otherFaction.getId(), relation);
        }
    }

    public double getPower() {
        if (this.hasPermanentPower()) {
            return this.getPermanentPower();
        }
        double ret = 0.0;
        for (FPlayer fplayer : this.fplayers) {
            ret += fplayer.getPower();
        }
        if (Conf.powerFactionMax > 0.0 && ret > Conf.powerFactionMax) {
            ret = Conf.powerFactionMax;
        }
        return ret + this.powerBoost;
    }

    public double getPowerMax() {
        if (this.hasPermanentPower()) {
            return this.getPermanentPower();
        }
        double ret = 0.0;
        for (FPlayer fplayer : this.fplayers) {
            ret += fplayer.getPowerMax();
        }
        if (Conf.powerFactionMax > 0.0 && ret > Conf.powerFactionMax) {
            ret = Conf.powerFactionMax;
        }
        return ret + this.powerBoost;
    }

    public int getPowerRounded() {
        return (int) Math.round(this.getPower());
    }

    public int getPowerMaxRounded() {
        return (int) Math.round(this.getPowerMax());
    }

    public int getLandRounded() {
        return Board.getFactionCoordCount(this);
    }

    public int getLandRoundedInWorld(String worldName) {
        return Board.getFactionCoordCountInWorld(this, worldName);
    }

    public boolean hasLandInflation() {
        return this.getLandRounded() > this.getPowerRounded();
    }

    public void refreshFPlayers() {
        this.fplayers.clear();
        if (this.isPlayerFreeType()) {
            return;
        }
        HashSet<UUID> uuids = new HashSet<UUID>();
        for (FPlayer fplayer : FPlayers.i.get()) {
            if (fplayer.getFaction() != this) continue;
            this.fplayers.add(fplayer);
            UUID uuid = fplayer.getCachedUUID();
            if (uuid == null) continue;
            uuids.add(fplayer.getCachedUUID());
        }
        this.activePlayerRolePerms.entrySet().removeIf(entry -> !uuids.contains(entry.getKey()));
    }

    protected boolean addFPlayer(FPlayer fplayer) {
        if (this.isPlayerFreeType()) {
            return false;
        }
        UUID uuid = fplayer.getCachedUUID();
        if (uuid != null) {
            this.activePlayerRolePerms.remove(uuid);
        }
        return this.fplayers.add(fplayer);
    }

    protected boolean removeFPlayer(FPlayer fplayer) {
        if (this.isPlayerFreeType()) {
            return false;
        }
        UUID uuid = fplayer.getCachedUUID();
        if (uuid != null) {
            this.activePlayerRolePerms.remove(uuid);
        }
        return this.fplayers.remove(fplayer);
    }

    public Set<FPlayer> getFPlayers() {
        HashSet<FPlayer> ret = new HashSet<FPlayer>(this.fplayers);
        return ret;
    }

    public Set<FPlayer> getFPlayersWhereOnline(boolean online) {
        HashSet<FPlayer> ret = new HashSet<FPlayer>();
        for (FPlayer fplayer : this.fplayers) {
            if (fplayer.isCachedAsOnline() != online) continue;
            ret.add(fplayer);
        }
        return ret;
    }

    public FPlayer getFPlayerAdmin() {
        if (!this.isNormal()) {
            return null;
        }
        for (FPlayer fplayer : this.fplayers) {
            if (fplayer.getRole() != Role.ADMIN) continue;
            return fplayer;
        }
        return null;
    }

    public ArrayList<FPlayer> getFPlayersWhereRole(Role role) {
        ArrayList<FPlayer> ret = new ArrayList<FPlayer>();
        if (!this.isNormal()) {
            return ret;
        }
        for (FPlayer fplayer : this.fplayers) {
            if (fplayer.getRole() != role) continue;
            ret.add(fplayer);
        }
        return ret;
    }

    public ArrayList<Player> getOnlinePlayers() {
        ArrayList<Player> ret = new ArrayList<Player>();
        if (this.isPlayerFreeType()) {
            return ret;
        }
        for (Player player : P.p.getServer().getOnlinePlayers()) {
            FPlayer fplayer = FPlayers.i.get(player);
            if (fplayer.getFaction() != this) continue;
            ret.add(player);
        }
        return ret;
    }

    public boolean hasPlayersOnline() {
        if (this.isPlayerFreeType()) {
            return false;
        }
        for (Player player : P.p.getServer().getOnlinePlayers()) {
            FPlayer fplayer = FPlayers.i.get(player);
            if (fplayer.getFaction() != this) continue;
            return true;
        }
        return Conf.considerFactionsReallyOfflineAfterXMinutes > 0.0 && (double) System.currentTimeMillis() < (double) this.lastPlayerLoggedOffTime + Conf.considerFactionsReallyOfflineAfterXMinutes * 60000.0;
    }

    public void memberLoggedOff() {
        if (this.isNormal()) {
            this.lastPlayerLoggedOffTime = System.currentTimeMillis();
        }
    }

    public void promoteNewLeader(FPlayer whoLeft) {
        if (!this.isNormal()) {
            return;
        }
        if (this.isPermanent() && Conf.permanentFactionsDisableLeaderPromotion) {
            return;
        }
        final FPlayer oldLeader = this.getFPlayerAdmin();
        ArrayList<FPlayer> replacements = this.getFPlayersWhereRole(Role.COLEADER);
        if (replacements == null || replacements.isEmpty()) {
            replacements = this.getFPlayersWhereRole(Role.MODERATOR);
        }
        if (replacements == null || replacements.isEmpty()) {
            replacements = this.getFPlayersWhereRole(Role.NORMAL);
        }
        if (replacements == null || replacements.isEmpty()) {
            if (this.isPermanent()) {
                if (oldLeader != null) {
                    oldLeader.setRole(Role.NORMAL);
                }
                return;
            }
            if (Conf.logFactionDisband) {
                P.p.log("The faction " + this.getTag() + " (" + this.getId() + ") has been disbanded since it has no members left.");
            }
            for (FPlayer fplayer : FPlayers.i.getOnline()) {
                fplayer.msg("<i>%s left the faction %s<i> and it was disbanded.", whoLeft == null ? "Someone" : whoLeft.getNameAsync(), this.getTag(fplayer));
            }
            this.detach();
        } else {
            if (oldLeader != null) {
                oldLeader.setRole(Role.NORMAL);
            }
            replacements.get(0).setRole(Role.ADMIN);
            final ArrayList<FPlayer> replace = new ArrayList<FPlayer>(replacements);
            P.getP().getServer().getScheduler().runTaskAsynchronously(P.getP(), () -> {
                Faction.this.msg("<i>Faction admin <h>%s<i> has been removed. %s<i> has been promoted as the new faction admin.", oldLeader == null ? "" : oldLeader.getNameAsync(), replace.get(0).getNameAsync());
                P.p.log("Faction " + Faction.this.getTag() + " (" + Faction.this.getId() + ") admin was removed. Replacement admin: " + replace.get(0).getNameAsync());
            });
        }
    }

    @Override
    public void msg(String message, Object... args) {
        message = P.p.txt.parse(message, args);
        String pre = "";
        if (message.startsWith(ChatColor.RED.toString()) && !message.contains("(!)")) {
            pre = ChatColor.RED + "" + ChatColor.BOLD + "(!) ";
        }
        if (message.startsWith(ChatColor.YELLOW.toString()) && !message.contains("(!)")) {
            pre = ChatColor.YELLOW + "" + ChatColor.BOLD + "(!) ";
        }
        for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
            fplayer.sendMessage(pre + message);
        }
    }

    @Deprecated
    public void sendMessage(String message) {
        for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
            fplayer.sendMessage(message);
        }
    }

    public void sendMessage(Player sender, String message) {
        for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
            fplayer.sendMessage(message);
        }
    }

    public void sendMessage(Player sender, String message, boolean filter) {
        for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
            fplayer.sendMessage(message);
        }
    }

    @Deprecated
    public void sendModMessage(String message) {
        for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
            if (!fplayer.getRole().isAtLeast(Role.MODERATOR)) continue;
            fplayer.sendMessage(message);
        }
    }

    public void sendModMessage(Player sender, String message) {
        for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
            if (!fplayer.getRole().isAtLeast(Role.MODERATOR) && !fplayer.getRole().isCoLeader())
                continue;
            fplayer.sendMessage(message);
        }
    }

    public void sendMessage(List<String> messages) {
        for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
            fplayer.sendMessage(messages);
        }
    }

    public Map<FLocation, Set<String>> getClaimOwnership() {
        return this.claimOwnership;
    }

    public void clearAllClaimOwnership() {
        this.claimOwnership.clear();
        FactionWrapper wrapper = FactionWrappers.get(this);
        if (wrapper != null) {
            wrapper.chunkClaims.clear();
        }
    }

    public void clearClaimOwnership(FLocation loc) {
        this.claimOwnership.remove(loc);
    }

    public void clearClaimOwnership(FPlayer player) {
        if (this.getId() == null || this.getId().isEmpty()) {
            return;
        }
        for (Map.Entry<FLocation, Set<String>> entry : this.claimOwnership.entrySet()) {
            Set<String> ownerData = entry.getValue();
            if (ownerData == null) continue;
            Iterator<String> iter = ownerData.iterator();
            while (iter.hasNext()) {
                if (!iter.next().equals(player.getId())) continue;
                iter.remove();
            }
            if (!ownerData.isEmpty()) continue;
            this.claimOwnership.remove(entry.getKey());
        }
    }

    public int getCountOfClaimsWithOwners() {
        return this.claimOwnership.isEmpty() ? 0 : this.claimOwnership.size();
    }

    public boolean doesLocationHaveOwnersSet(FLocation loc) {
        if (this.claimOwnership.isEmpty() || !this.claimOwnership.containsKey(loc)) {
            return false;
        }
        Set<String> ownerData = this.claimOwnership.get(loc);
        return ownerData != null && !ownerData.isEmpty();
    }

    public boolean isPlayerInOwnerList(FPlayer player, FLocation loc) {
        if (this.claimOwnership.isEmpty()) {
            return false;
        }
        Set<String> ownerData = this.claimOwnership.get(loc);
        if (ownerData == null) {
            return false;
        }
        return ownerData.contains(player.getId());
    }

    public void setPlayerAsOwner(FPlayer player, FLocation loc) {
        Set<String> ownerData = this.claimOwnership.get(loc);
        if (ownerData == null) {
            ownerData = new HashSet<>();
        }
        ownerData.add(player.getId().toLowerCase());
        this.claimOwnership.put(loc, ownerData);
    }

    public void removePlayerAsOwner(FPlayer player, FLocation loc) {
        Set<String> ownerData = this.claimOwnership.get(loc);
        if (ownerData == null) {
            return;
        }
        ownerData.remove(player.getId());
        this.claimOwnership.put(loc, ownerData);
    }

    public Set<String> getOwnerList(FLocation loc) {
        return this.claimOwnership.get(loc);
    }

    public String getOwnerListStringAsync(FLocation loc) {
        ArrayList players = Lists.newArrayList();
        if (this.claimOwnership.get(loc) == null) {
            return "";
        }
        for (String player : this.claimOwnership.get(loc)) {
            players.add(FPlayers.i.get(player));
        }
        StringBuilder ownerList = new StringBuilder();
        for (Object player : players) {
            if (ownerList.length() > 0) {
                ownerList.append(", ");
            }
            ownerList.append(((FPlayer) player).getNameAsync());
        }
        return ownerList.toString();
    }

    public boolean playerHasOwnershipRights(FPlayer fplayer, FLocation loc) {
        if (fplayer.getFaction() == this && (fplayer.getRole().isAtLeast(Conf.ownedAreaModeratorsBypass ? Role.MODERATOR : Role.ADMIN) || Permission.OWNERSHIP_BYPASS.has(fplayer.getPlayer()))) {
            return true;
        }
        if (this.claimOwnership.isEmpty()) {
            return true;
        }
        Set<String> ownerData = this.claimOwnership.get(loc);
        return ownerData == null || ownerData.isEmpty() || ownerData.contains(fplayer.getId());
    }

    @Override
    public void postDetach() {
        if (Econ.shouldBeUsed()) {
            Econ.setBalance(this.getAccountId(), 0.0);
        }
        Board.clean();
        FPlayers.i.cleanSync();
        try {
            if (P.p != null && P.p.getFlogManager() != null && P.p.getFlogManager().getFactionLogMap() != null) {
                P.p.getFlogManager().getFactionLogMap().remove(this.getId());
            }
        } catch (Exception exception) {
            // empty catch block
        }
        if (P.p != null && P.p.getFChestManager() != null) {
            P.p.getFChestManager().getFchestItems().remove(this.getId());
        }
    }

    public Map<Role, Set<RolePerm>> getActiveRolePerms() {
        return this.activeRolePerms;
    }

    public void setActiveRolePerms(Map<Role, Set<RolePerm>> activeRolePerms) {
        this.activeRolePerms = activeRolePerms;
    }

    public Map<UUID, Set<RolePerm>> getActivePlayerRolePerms() {
        return this.activePlayerRolePerms;
    }

    public String getMotd() {
        return this.motd;
    }

    public long getLastTruceTime() {
        return this.lastTruceTime;
    }

    public void setLastTruceTime(long lastTruceTime) {
        this.lastTruceTime = lastTruceTime;
    }

    public long getLastAllyTime() {
        return this.lastAllyTime;
    }

    public void setLastAllyTime(long lastAllyTime) {
        this.lastAllyTime = lastAllyTime;
    }

    public int getTrucesAvailable() {
        return this.trucesAvailable;
    }

    public int getAlliesAvailable() {
        return this.alliesAvailable;
    }

}

