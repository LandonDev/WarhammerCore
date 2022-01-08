package net.minelink.ctplus;

import com.massivecraft.factions.P;
import net.minelink.ctplus.compat.api.NpcNameGeneratorFactory;
import net.minelink.ctplus.compat.api.NpcPlayerHelper;
import net.minelink.ctplus.hook.Hook;
import net.minelink.ctplus.hook.HookManager;
import net.minelink.ctplus.listener.*;
import net.minelink.ctplus.task.ForceFieldTask;
import net.minelink.ctplus.task.SafeLogoutTask;
import net.minelink.ctplus.util.ReflectionUtils;
import net.minelink.ctplus.util.Version;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public final class CombatTagPlus {
    private final PlayerCache playerCache = new PlayerCache();

    private Settings settings;

    private HookManager hookManager;

    private TagManager tagManager;

    private NpcPlayerHelper npcPlayerHelper;

    private NpcManager npcManager;

    public PlayerCache getPlayerCache() {
        return this.playerCache;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public HookManager getHookManager() {
        return this.hookManager;
    }

    public TagManager getTagManager() {
        return this.tagManager;
    }

    public NpcPlayerHelper getNpcPlayerHelper() {
        return this.npcPlayerHelper;
    }

    public NpcManager getNpcManager() {
        return this.npcManager;
    }

    public void onEnable() {
        this.settings = new Settings(this);
        if (this.settings.isOutdated()) {
            this.settings.update();
            P.p.getLogger().info("Configuration file has been updated.");
        }
        if (!checkVersionCompatibility()) {
            Bukkit.getPluginManager().disablePlugin(P.p);
            return;
        }
        this.hookManager = new HookManager(this);
        this.tagManager = new TagManager(this);
        if (this.npcPlayerHelper != null)
            this.npcManager = new NpcManager(this);
        NpcNameGeneratorFactory.setNameGenerator(new NpcNameGeneratorImpl(this));
        integrateFactions();
        integrateTowny();
        integrateWorldGuard();
        for (Player player : Bukkit.getOnlinePlayers())
            getPlayerCache().addPlayer(player);
        Bukkit.getPluginManager().registerEvents((Listener) new ForceFieldListener(this), P.p);
        Bukkit.getPluginManager().registerEvents((Listener) new InstakillListener(this), P.p);
        if (getNpcManager() != null)
            Bukkit.getPluginManager().registerEvents((Listener) new NpcListener(this), P.p);
        Bukkit.getPluginManager().registerEvents((Listener) new PlayerListener(this), P.p);
        Bukkit.getPluginManager().registerEvents((Listener) new TagListener(this), P.p);
        ForceFieldTask.run(this);
        Bukkit.getScheduler().runTaskTimer(P.p, new Runnable() {
            public void run() {
                CombatTagPlus.this.getTagManager().purgeExpired();
                SafeLogoutTask.purgeFinished();
            }
        }, 3600L, 3600L);
    }

    public void onDisable() {
    }

    private boolean checkVersionCompatibility() {
        Class<?> helperClass = ReflectionUtils.getCompatClass("NpcPlayerHelperImpl");
        if (helperClass == null) {
            if (this.settings.instantlyKill() && !this.settings.alwaysSpawn())
                return true;
            P.p.getLogger().severe("**VERSION ERROR**");
            P.p.getLogger().severe("Server API version detected: " + ReflectionUtils.API_VERSION);
            P.p.getLogger().severe("This version of CombatTagPlus is not compatible with your CraftBukkit.");
            return false;
        }
        try {
            this.npcPlayerHelper = (NpcPlayerHelper) helperClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private void integrateFactions() {
        Version v;
        if (!getSettings().useFactions())
            return;
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Factions");
        if (plugin == null)
            return;
        try {
            v = new Version(plugin.getDescription().getVersion());
        } catch (IllegalArgumentException e) {
            v = new Version("0.0");
        }
        String version = null;
        if (v.compareTo(new Version("1.6")) < 0) {
            version = "1_6";
        } else if (v.compareTo(new Version("2.7")) > 0) {
            version = "2_7";
        }
        if (version == null) {
            String[] parts = v.toString().split("\\.");
            version = parts[0] + "_" + parts[1];
        }
        String className = "net.minelink.ctplus.factions.v" + version + ".FactionsHook";
        try {
            getHookManager().addHook((Hook) Class.forName(className).newInstance());
        } catch (Exception e) {
            P.p.getLogger().warning("**WARNING**");
            P.p.getLogger().warning("Failed to enable Factions integration due to errors.");
            P.p.getLogger().warning("This is most likely due to a newer Factions.");
            e.printStackTrace();
        }
    }

    private void integrateTowny() {
        if (!getSettings().useTowny())
            return;
    }

    private void integrateWorldGuard() {
        if (!getSettings().useWorldGuard())
            return;
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (plugin == null)
            return;
        String v = plugin.getDescription().getVersion();
        String className = "net.minelink.ctplus.worldguard.v" + (v.startsWith("5") ? 5 : 6) + ".WorldGuardHook";
        try {
            getHookManager().addHook((Hook) Class.forName(className).newInstance());
        } catch (Exception e) {
            P.p.getLogger().warning("**WARNING**");
            P.p.getLogger().warning("Failed to enable WorldGuard integration due to errors.");
            P.p.getLogger().warning("This is most likely due to a newer WorldGuard.");
            e.printStackTrace();
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equals("ctplusreload")) {
            P.p.reloadConfig();
            getSettings().load();
            if (sender instanceof Player)
                sender.sendMessage(ChatColor.GREEN + P.p.getName() + " config reloaded.");
            P.p.getLogger().info("Config reloaded by " + sender.getName());
        } else if (cmd.getName().equals("combattagplus")) {
            if (!(sender instanceof Player))
                return false;
            UUID uniqueId = ((Player) sender).getUniqueId();
            Tag tag = getTagManager().getTag(uniqueId);
            if (tag == null || tag.isExpired() || !getTagManager().isTagged(uniqueId)) {
                sender.sendMessage(getSettings().getCommandUntagMessage());
                return true;
            }
            String duration = this.settings.formatDuration(tag.getTagDuration());
            sender.sendMessage(getSettings().getCommandTagMessage().replace("{time}", duration));
        } else if (cmd.getName().equals("ctpluslogout")) {
            if (!(sender instanceof Player))
                return false;
            Player player = (Player) sender;
            if (SafeLogoutTask.hasTask(player))
                return false;
            SafeLogoutTask.run(this, player);
        } else if (cmd.getName().equals("ctplusuntag")) {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Please specify a player to untag");
                return true;
            }
            Player player = P.p.getServer().getPlayer(args[0]);
            if (player == null || getNpcPlayerHelper().isNpc(player)) {
                sender.sendMessage(ChatColor.RED + args[0] + " is not currently online!");
                return true;
            }
            UUID uniqueId = player.getUniqueId();
            if (getTagManager().untag(uniqueId)) {
                sender.sendMessage(ChatColor.GREEN + "Successfully untagged " + player.getName() + ".");
            } else {
                sender.sendMessage(ChatColor.GREEN + player.getName() + " is already untagged.");
            }
        }
        return true;
    }
}
