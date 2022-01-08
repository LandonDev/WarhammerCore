package landon.core.patchapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.massivecraft.factions.P;
import landon.core.WarhammerCore;
import landon.core.util.CommandUtils;
import landon.core.util.customcommand.CustomCommand;
import landon.core.util.customcommand.StructuredCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class UHCFPatch {
    private List<Listener> listeners = new ArrayList<>();

    private List<Integer> task_ids = new ArrayList<>();

    private List<Command> commands = new ArrayList<>();

    private Plugin bukkit_plugin = null;

    private boolean running = false;

    public long enable_time = 0L;

    public boolean disableCalled = false;

    protected static Random random = new Random();

    public UHCFPatch(Plugin p) {
        this.bukkit_plugin = p;
    }

    public void enable() {
        this.disableCalled = false;
    }

    public void disable() {
        this.disableCalled = true;
    }

    public void inject() {
        this.running = true;
        this.enable_time = System.currentTimeMillis();
        try {
            enable();
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + "[Auqkwa]" + ChatColor.WHITE
                    .toString() + " Injected " + ChatColor.YELLOW.toString() + getClass().getSimpleName() + ChatColor.WHITE.toString() + " patch.");
        } catch (Exception err) {
            err.printStackTrace();
            kill();
        }
    }

    public void kill() {
        try {
            disable();
            unregisterListeners();
            unregisterTasks();
            unregisterCommands();
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + "[Auqkwa]" + ChatColor.WHITE
                    .toString() + " Killed " + ChatColor.RED.toString() + getClass().getSimpleName() + ChatColor.WHITE.toString() + " patch.");
            this.enable_time = 0L;
            this.running = false;
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void registerListener(final Listener l) {
        this.listeners.add(l);
        Bukkit.getPluginManager().registerEvents(l, this.bukkit_plugin);
        Bukkit.getLogger().info("(" + this.getClass().getSimpleName() + ") Registered listener: " + l.getClass().getSimpleName() + "!");
    }

    public void registerCommand(CustomCommand... customCommand) {
        try {
            WarhammerCore.get().getCommandManager().registerCommands(P.p, customCommand);
            for (CustomCommand command : customCommand) {
                this.commands.add(command.getBukkitCommand());
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void registerCommand(StructuredCommand... customCommand) {
        try {
            WarhammerCore.get().getCommandManager().registerCommands(P.p, customCommand);
            for (StructuredCommand command : customCommand) {
                this.commands.add(command.getBukkitCommand());
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void registerTask(final BukkitTask bt, final Class<?> c) {
        this.task_ids.add(bt.getTaskId());
        Bukkit.getLogger().info("(" + this.getClass().getSimpleName() + ") Registered task: " + c.getSimpleName() + "!");
    }

    public void unregisterCommand(final Command pcmd) {
        Bukkit.getLogger().info("(" + this.getClass().getSimpleName() + ") Unregistered command: /" + pcmd.getName() + "!");
        CommandUtils.unRegisterBukkitCommand(pcmd);
    }

    public void unregisterListeners() {
        for (Listener l : this.listeners) {
            Bukkit.getLogger().info("(" + this.getClass().getSimpleName() + ") Unregistered listener: " + l.getClass().getSimpleName() + "!");
            HandlerList.unregisterAll(l);
            l = null;
        }
        this.listeners.clear();
    }

    public void unregisterTasks() {
        for (final int i : this.task_ids) {
            Bukkit.getScheduler().cancelTask(i);
            Bukkit.getLogger().info("(" + this.getClass().getSimpleName() + ") Unregistered task: #" + i + "!");
        }
        this.task_ids.clear();
    }

    public void unregisterCommands() {
        for (final Command pcmd : this.commands) {
            this.unregisterCommand(pcmd);
        }
        this.commands.clear();
    }

    public Plugin getBukkitPlugin() {
        return this.bukkit_plugin;
    }

    public boolean isRunning() {
        return this.running;
    }
}
