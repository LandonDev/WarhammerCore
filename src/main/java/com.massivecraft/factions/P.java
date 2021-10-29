/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Server
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder
 *  org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.massivecraft.factions.cmd.CmdAutoHelp;
import com.massivecraft.factions.cmd.CommandAdminFaction;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.cmd.killtracker.CmdDeaths;
import com.massivecraft.factions.cmd.killtracker.CmdKills;
import com.massivecraft.factions.cmd.killtracker.FileManager;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.EssentialsFeatures;
import com.massivecraft.factions.listeners.*;
import com.massivecraft.factions.listeners.menu.MenuListener;
import com.massivecraft.factions.listeners.menu.fchest.FChestListener;
import com.massivecraft.factions.struct.*;
import com.massivecraft.factions.struct.managers.FChestManager;
import com.massivecraft.factions.util.*;
import com.massivecraft.factions.util.gui.CustomGUI;
import com.massivecraft.factions.util.gui.listeners.GUIListener;
import com.massivecraft.factions.util.gui.listeners.PlayerListener;
import com.massivecraft.factions.zcore.MPlugin;
import com.massivecraft.factions.zcore.util.MojangUUIDFetcher;
import com.massivecraft.factions.zcore.util.TextUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import landon.warhammercore.WarhammerCore;
import landon.warhammercore.patches.patches.fpoints.utils.FactionUtils;
import landon.warhammercore.util.customcommand.CommandManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;

public class P extends MPlugin {
    public static P p;
    public final FactionsPlayerListener playerListener;
    public final FactionsChatListener chatListener;
    public final FactionsEntityListener entityListener;
    public final FactionsExploitListener exploitListener;
    public final FactionsBlockListener blockListener;
    public final FactionsGlobalChatListener globalListener;
    public final FactionChunkOwnershipCache ownershipCacheListener;
    private final FactionPermissionListener permissionListener;
    public boolean citizensEnabled = true;
    public boolean factionPointsEnabled = false;
    public boolean outpostsEnabled = false;
    public String currentVersion = "1.1";
    public FCmdRoot cmdBase;
    public CmdAutoHelp cmdAutoHelp;
    private PermissionManager permissionManager;
    private boolean locked = false;
    private Integer AutoLeaveTask = null;
    private FLogManager flogManager;
    private FChestManager fChestManager;
    private FileManager fileManager;
    private CommandManager commandManager;
    public static WorldGuardPlugin wg = null;

    private static WarhammerCore core;

    public static WarhammerCore core() {
        return core;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }


    public P() {
        p = this;
        new Board();
        this.fChestManager = new FChestManager();
        this.permissionManager = new PermissionManager();
        this.playerListener = new FactionsPlayerListener(this);
        this.chatListener = new FactionsChatListener(this);
        this.entityListener = new FactionsEntityListener(this);
        this.exploitListener = new FactionsExploitListener();
        this.blockListener = new FactionsBlockListener(this);
        this.globalListener = new FactionsGlobalChatListener(this);
        this.ownershipCacheListener = new FactionChunkOwnershipCache();
        this.permissionListener = new FactionPermissionListener();
    }

    public static P getP() {
        return p;
    }

    public static void removeExpiredPlayers() {
        long time = (long) (Conf.autoLeaveAfterDaysOfInactivity * 24.0 * 60.0 * 60.0 * 1000.0);
        long now = System.currentTimeMillis();
        for (FPlayer fplayer : FPlayers.i.get()) {
            if (!fplayer.isOffline() || now - fplayer.getLastLoginTime() <= time) continue;
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Player " + fplayer.getNameAsync() + " was auto-removed due to inactivity, last login: " + fplayer.getLastLoginTime() + " current: " + System.currentTimeMillis());
            if (fplayer.getRole() == Role.ADMIN && fplayer.getFaction() != null) {
                fplayer.getFaction().promoteNewLeader(fplayer);
            }
            fplayer.leave(false);
            fplayer.detach();
        }
    }

    public boolean getLocked() {
        return this.locked;
    }

    public void setLocked(boolean val) {
        this.locked = val;
        this.setAutoSave(val);
    }

    public void onEnable() {
        try {
            Class.forName("com.google.gson.reflect.TypeToken");
        } catch (ClassNotFoundException ex) {
            this.log(Level.SEVERE, "GSON lib not found. Your CraftBukkit build is too old (< 1.3.2) or otherwise not compatible.");
            this.suicide();
            return;
        }
        if (!this.preEnable()) {
            return;
        }
        this.factionPointsEnabled = new File("plugins/CosmicFactionPoints.jar").exists();
        this.loadSuccessful = false;
        this.flogManager = new FLogManager();
        Conf.load();
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        FPlayers.i.loadFromDisc();
        Factions.i.loadFromDisc();
        FactionWrappers.loadFactionWrappers();
        Board.load();
        this.cmdBase = new FCmdRoot();
        this.cmdAutoHelp = new CmdAutoHelp();
        this.fileManager = new FileManager();
        wg = this.getWorldGuard();
        getFileManager().setupFiles(); // load the files...
        getCommand("kills").setExecutor(new CmdKills());
        getCommand("deaths").setExecutor(new CmdDeaths());

       this.getBaseCommands().add(this.cmdBase);
        EssentialsFeatures.setup();
        Econ.setup();
        this.getServer().getPluginManager().registerEvents(this.playerListener, this);
        this.getServer().getPluginManager().registerEvents(this.chatListener, this);
        this.getServer().getPluginManager().registerEvents(this.entityListener, this);
        this.getServer().getPluginManager().registerEvents(this.exploitListener, this);
        this.getServer().getPluginManager().registerEvents(this.blockListener, this);
        this.getServer().getPluginManager().registerEvents(this.globalListener, this);
        this.getServer().getPluginManager().registerEvents(this.ownershipCacheListener, this);
        this.getServer().getPluginManager().registerEvents(this.permissionListener, this);
        this.getServer().getPluginManager().registerEvents(new FChestListener(), this);
        this.getServer().getPluginManager().registerEvents(new MenuListener(), this);
        this.getServer().getPluginManager().registerEvents(new GUIListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.citizensEnabled = this.getServer().getPluginManager().isPluginEnabled("Citizens");
        this.outpostsEnabled = new File("plugins/CosmicOutposts.jar").exists();
        try {
            Class.forName("net.ess3.api.events.PlayerSellEvent");
            this.getServer().getPluginManager().registerEvents(new FactionEssentialsListener(), this);
            Bukkit.getLogger().info("Registering Essentials Sell Event...");
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().info("Unable to find Essentials Sell Event, wont enable support.");
        }
        this.getCommand(this.refCommand).setExecutor(this);
        this.getCommand("afaction").setExecutor(new CommandAdminFaction());
        MojangUUIDFetcher.loadFactionPlayerUUIDs();
        try {
            this.permissionManager.loadPermissionMap();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        int minute = 1200;

        //Setup Discord Bot


        this.postEnable();
        this.loadSuccessful = true;
        this.fChestManager.onEnable(this);
        this.flogManager.loadLogs(this);
        P.removeExpiredPlayers();
        if (!this.currentVersion.equalsIgnoreCase(Conf.lastLoadedVersion)) {
            this.getServer().getConsoleSender().sendMessage(CC.Red + "Detected new Factions version: " + this.currentVersion + ", previously loaded " + Conf.lastLoadedVersion);
            if (this.currentVersion.equalsIgnoreCase("1.1") && (Conf.lastLoadedVersion == null || Conf.lastLoadedVersion.equalsIgnoreCase("1.0"))) {
                this.permissionManager.getPermissionMap().forEach((tag, perms) -> {
                    perms.getDefaultPlayerPermissions().forEach((uuid, permList) -> permList.add(FactionPermission.CHEST));
                    perms.getPlayerPermissionMap().forEach((uuid, permList) -> permList.forEach((id, list) -> list.add(FactionPermission.CHEST)));
                    perms.getFactionDefaultPermissions().forEach((facId, permList) -> permList.add(FactionPermission.CHEST));
                    perms.getFactionChunkPermissionMap().forEach((loc, users) -> users.forEach((id, permList) -> permList.add(FactionPermission.CHEST)));
                    this.log("Adding default CHEST permission for " + tag);
                });
            }
            Conf.lastLoadedVersion = this.currentVersion;
        }
        this.commandManager = new CommandManager();
        CustomGUI.activeGUIs.entrySet();
        WorldGuardUtils.hook();
        core = new WarhammerCore();
        core.onEnable();
        FactionUtils.init();
    }

    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("WorldGuard");
        return plugin != null && plugin instanceof WorldGuardPlugin ? (WorldGuardPlugin)plugin : null;
    }

    @Override
    public GsonBuilder getGsonBuilder() {
        Type mapFLocToStringSetType = new TypeToken<Map<FLocation, Set<String>>>() {
        }.getType();
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().excludeFieldsWithModifiers(128, 64).registerTypeAdapter(LazyLocation.class, new MyLocationTypeAdapter()).registerTypeAdapter(mapFLocToStringSetType, new MapFLocToStringSetTypeAdapter());
    }

    @Override
    public void onDisable() {
        if (this.loadSuccessful) {
            Board.save();
            Conf.save();
            FactionWrappers.saveToDisc();
            if (this.permissionManager != null) {
                try {
                    this.permissionManager.savePermissions();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        } else {
            Bukkit.getLogger().info("[Factions] Not saving Board or config due to not loading successfully!");
        }
        if (this.AutoLeaveTask != null) {
            this.getServer().getScheduler().cancelTask(this.AutoLeaveTask);
            this.AutoLeaveTask = null;
        }
        super.onDisable();
        try {
            this.fChestManager.onDisable(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.flogManager.saveLogs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        CustomGUI.activeGUIs.values().forEach(CustomGUI::close);
        core.onDisable();
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public void startAutoLeaveTask(boolean restartIfRunning) {
        if (this.AutoLeaveTask != null) {
            if (!restartIfRunning) {
                return;
            }
            this.getServer().getScheduler().cancelTask(this.AutoLeaveTask);
        }
        if (Conf.autoLeaveRoutineRunsEveryXMinutes > 0.0) {
            // empty if block
        }
    }

    @Override
    public void postAutoSave() {
        Board.save();
        Conf.save();
    }

    @Override
    public boolean logPlayerCommands() {
        return Conf.logPlayerCommands;
    }

    @Override
    public boolean handleCommand(CommandSender sender, String commandString, boolean testOnly) {
        if (sender instanceof Player && FactionsPlayerListener.preventCommand(commandString, (Player) sender)) {
            return true;
        }
        return super.handleCommand(sender, commandString, testOnly);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (split == null || split.length == 0) {
            return true;
        }
        String cmd = Conf.baseCommandAliases.isEmpty() ? "/f" : "/" + Conf.baseCommandAliases.get(0);
        return this.handleCommand(sender, cmd + " " + TextUtil.implode(Arrays.asList(split), " "), false);
    }

    public int hookSupportVersion() {
        return 3;
    }

    public void handleFactionTagExternally(boolean notByFactions) {
    }

    public boolean shouldLetFactionsHandleThisChat(AsyncPlayerChatEvent event) {
        if (event == null) {
            return false;
        }
        return this.isPlayerFactionChatting(event.getPlayer()) || this.isFactionsCommand(event.getMessage());
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> color(List<String> string) {
        List<String> colored = new ArrayList<>();
        for (String line : string) {
            colored.add(color(line));
        }
        return colored;
    }

    public boolean isPlayerFactionChatting(Player player) {
        if (player == null) {
            return false;
        }
        FPlayer me = FPlayers.i.get(player.getUniqueId().toString());
        if (me == null) {
            return false;
        }
        return me.getChatMode().isAtLeast(ChatMode.ALLIANCE);
    }

    public boolean isFactionsCommand(String check) {
        if (check == null || check.isEmpty()) {
            return false;
        }
        return this.handleCommand(null, check, true);
    }

    public String getPlayerFactionTag(Player player) {
        return this.getPlayerFactionTagRelation(player, null);
    }

    public String getPlayerFactionTagRelation(Player speaker, Player listener) {
        FPlayer you;
        String tag = "~";
        if (speaker == null) {
            return tag;
        }
        FPlayer me = FPlayers.i.get(speaker.getUniqueId().toString());
        if (me == null) {
            return tag;
        }
        tag = listener == null ? me.getChatTag().trim() : ((you = FPlayers.i.get(listener.getUniqueId().toString())) == null ? me.getChatTag().trim() : me.getChatTag(you).trim());
        if (tag.isEmpty()) {
            tag = "~";
        }
        return tag;
    }

    public String getPlayerTitle(Player player) {
        if (player == null) {
            return "";
        }
        FPlayer me = FPlayers.i.get(player.getUniqueId().toString());
        if (me == null) {
            return "";
        }
        return me.getTitle().trim();
    }

    public Set<String> getFactionTags() {
        HashSet<String> tags = new HashSet<String>();
        for (Faction faction : Factions.i.get()) {
            tags.add(faction.getTag());
        }
        return tags;
    }

    public Set<String> getPlayersInFactionAsync(String factionTag) {
        HashSet<String> players = new HashSet<String>();
        Faction faction = Factions.i.getByTag(factionTag);
        if (faction != null) {
            for (FPlayer fplayer : faction.getFPlayers()) {
                players.add(fplayer.getNameAsync());
            }
        }
        return players;
    }

    public Economy getEcon() {
        RegisteredServiceProvider<Economy> rsp = P.p.getServer().getServicesManager().getRegistration(Economy.class);
        return rsp.getProvider();
    }

    public Set<String> getOnlinePlayersInFactionAsync(String factionTag) {
        HashSet<String> players = new HashSet<String>();
        Faction faction = Factions.i.getByTag(factionTag);
        if (faction != null) {
            for (FPlayer fplayer : faction.getFPlayersWhereOnline(true)) {
                players.add(fplayer.getNameAsync());
            }
        }
        return players;
    }

    public void logFactionEvent(Faction faction, FLogType type, String... arguments) {
        this.flogManager.log(faction, type, arguments);
    }

    public PermissionManager getPermissionManager() {
        return this.permissionManager;
    }

    public FLogManager getFlogManager() {
        return this.flogManager;
    }

    public FChestManager getFChestManager() {
        return this.fChestManager;
    }

}

