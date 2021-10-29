/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.craftbukkit.libs.com.google.gson.Gson
 *  org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder
 *  org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.massivecraft.factions.zcore;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.zcore.persist.EM;
import com.massivecraft.factions.zcore.persist.SaveTask;
import com.massivecraft.factions.zcore.util.PermUtil;
import com.massivecraft.factions.zcore.util.Persist;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;

public abstract class MPlugin
        extends JavaPlugin {
    public Persist persist;
    public TextUtil txt;
    public PermUtil perm;
    public Gson gson;
    public String refCommand = "";
    public Map<String, String> rawTags = new LinkedHashMap<String, String>();
    protected boolean loadSuccessful = false;
    private Integer saveTask = null;
    private boolean autoSave = true;
    private MPluginSecretPlayerListener mPluginSecretPlayerListener;
    private MPluginSecretServerListener mPluginSecretServerListener;
    private List<MCommand<?>> baseCommands = new ArrayList<>();
    private long timeEnableStart;

    public boolean getAutoSave() {
        return this.autoSave;
    }

    public void setAutoSave(boolean val) {
        this.autoSave = val;
    }

    public List<MCommand<?>> getBaseCommands() {
        return this.baseCommands;
    }

    public boolean preEnable() {
        this.log("=== ENABLE START ===");
        this.timeEnableStart = System.currentTimeMillis();
        this.getDataFolder().mkdirs();
        this.perm = new PermUtil(this);
        this.persist = new Persist(this);
        this.gson = this.getGsonBuilder().create();
        this.txt = new TextUtil();
        this.initTXT();
        try {
            Map refCmd = this.getDescription().getCommands();
            if (refCmd != null && !refCmd.isEmpty()) {
                this.refCommand = (String) refCmd.keySet().toArray()[0];
            }
        } catch (ClassCastException refCmd) {
            // empty catch block
        }
        this.mPluginSecretPlayerListener = new MPluginSecretPlayerListener(this);
        this.mPluginSecretServerListener = new MPluginSecretServerListener(this);
        this.getServer().getPluginManager().registerEvents(this.mPluginSecretPlayerListener, this);
        this.getServer().getPluginManager().registerEvents(this.mPluginSecretServerListener, this);
        if (this.saveTask == null && Conf.saveToFileEveryXMinutes > 0.0) {
            long saveTicks = (long) (1200.0 * Conf.saveToFileEveryXMinutes);
            this.saveTask = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, new SaveTask(this), saveTicks, saveTicks).getTaskId();
        }
        this.loadSuccessful = true;
        return true;
    }

    public void postEnable() {
        this.log("=== ENABLE DONE (Took " + (System.currentTimeMillis() - this.timeEnableStart) + "ms) ===");
    }

    public void onDisable() {
        if (this.saveTask != null) {
            this.getServer().getScheduler().cancelTask(this.saveTask);
            this.saveTask = null;
        }
        if (this.loadSuccessful) {
            if (EM.saveAllToDisc(true)) {
                Bukkit.getLogger().info("Successfully saved data to disk..");
            } else {
                Bukkit.getLogger().info("Unable to successfully saveAllToDisc!");
            }
            Bukkit.getLogger().info("Finished saving data to disk..");
        } else {
            Bukkit.getLogger().info("loadSuccessful = false, not saving players!");
        }
        this.log("Disabled");
    }

    public void suicide() {
        this.log("Now I suicide!");
        this.getServer().getPluginManager().disablePlugin(this);
    }

    public GsonBuilder getGsonBuilder() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().excludeFieldsWithModifiers(128, 64);
    }

    public void addRawTags() {
        this.rawTags.put("l", "<green>");
        this.rawTags.put("a", "<gold>");
        this.rawTags.put("n", "<silver>");
        this.rawTags.put("i", "<yellow>");
        this.rawTags.put("g", "<lime>");
        this.rawTags.put("b", "<rose>");
        this.rawTags.put("h", "<pink>");
        this.rawTags.put("c", "<aqua>");
        this.rawTags.put("p", "<teal>");
    }

    public void initTXT() {
        this.addRawTags();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map tagsFromFile = this.persist.load(type, "tags");
        if (tagsFromFile != null) {
            this.rawTags.putAll(tagsFromFile);
        }
        this.persist.save(this.rawTags, "tags");
        for (Map.Entry<String, String> rawTag : this.rawTags.entrySet()) {
            this.txt.tags.put(rawTag.getKey(), TextUtil.parseColor(rawTag.getValue()));
        }
    }

    public boolean logPlayerCommands() {
        return true;
    }

    public boolean handleCommand(CommandSender sender, String commandString, boolean testOnly) {
        return this.handleCommand(sender, commandString, testOnly, false);
    }

    public boolean handleCommand(final CommandSender sender, String commandString, boolean testOnly, boolean async) {
        boolean noSlash = true;
        if (commandString.startsWith("/")) {
            noSlash = false;
            commandString = commandString.substring(1);
        }
        for (final MCommand<?> command : this.getBaseCommands()) {
            if (noSlash && !command.allowNoSlashAccess) continue;
            for (String alias : command.aliases) {
                if (commandString.startsWith(alias + "  ")) {
                    return false;
                }
                if (!commandString.startsWith(alias + " ") && !commandString.equals(alias)) continue;
                final ArrayList<String> args = new ArrayList<String>(Arrays.asList(commandString.split("\\s+")));
                args.remove(0);
                if (testOnly) {
                    return true;
                }
                if (async) {
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> command.execute(sender, args));
                } else {
                    command.execute(sender, args);
                }
                return true;
            }
        }
        return false;
    }

    public boolean handleCommand(CommandSender sender, String commandString) {
        return this.handleCommand(sender, commandString, false);
    }

    public void preAutoSave() {
    }

    public void postAutoSave() {
    }

    public void log(Object msg) {
        this.log(Level.INFO, msg);
    }

    public void log(String str, Object... args) {
        this.log(Level.INFO, this.txt.parse(str, args));
    }

    public void log(Level level, String str, Object... args) {
        this.log(level, this.txt.parse(str, args));
    }

    public void log(Level level, Object msg) {
        Bukkit.getLogger().log(level, "[" + this.getDescription().getFullName() + "] " + msg);
    }

}

