package landon.warhammercore;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.zcore.util.TextUtil;
import fr.minuskube.inv.SmartInvsPlugin;
import jdk.nashorn.internal.ir.Block;
import landon.warhammercore.commands.CmdACore;
import landon.warhammercore.commands.CmdCustomItem;
import landon.warhammercore.deathbans.DeathbanManager;
import landon.warhammercore.deathbans.lives.LifeListeners;
import landon.warhammercore.deathbans.lives.LifeManager;
import landon.warhammercore.listeners.CustomItemListener;
import landon.warhammercore.listeners.EnderChestListener;
import landon.warhammercore.patches.UHCFPatch;
import landon.warhammercore.patches.patches.blockvalues.BlockValue;
import landon.warhammercore.patches.patches.chat.ChatFilter;
import landon.warhammercore.patches.patches.fpoints.FactionPoints;
import landon.warhammercore.patches.patches.ftop.FactionsTop;
import landon.warhammercore.scoreboard.ScoreboardManager;
import landon.warhammercore.titles.cmds.CmdTitle;
import landon.warhammercore.titles.listeners.VoucherListener;
import landon.warhammercore.titles.mongo.TitleManager;
import landon.warhammercore.util.cooldown.CooldownManager;
import landon.warhammercore.util.cooldown.Cooldowns;
import landon.warhammercore.util.customcommand.CommandManager;
import landon.warhammercore.util.items.CustomItemManager;
import landon.warhammercore.util.mongo.MongoDB;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

@Getter
@Setter
public final class WarhammerCore {
    private static WarhammerCore instance;
    private Cooldowns cooldownUtil;
    private CooldownManager cooldownManager;
    private CustomItemManager itemManager;
    private SmartInvsPlugin smartInvsPlugin;
    private LifeManager lifeManager;
    private List<UHCFPatch> enabledPatches;
    private List<UHCFPatch> allPatches;

    public void onEnable() {
        instance = this;
        P.p.saveDefaultConfig();
        cooldownUtil = new Cooldowns(P.p);
        cooldownUtil.loadCooldowns();
        cooldownManager = cooldownUtil.getCooldownManager();
        P.p.getServer().getServicesManager().register(CooldownManager.class, this.cooldownManager, P.p, ServicePriority.High);
        itemManager = new CustomItemManager(P.p);
        try {
            commands();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        listeners();
        smartInvsPlugin = new SmartInvsPlugin();
        smartInvsPlugin.onEnable();
        P.p.saveDefaultConfig();
        if(P.p.getConfig().getBoolean("mongodb.use-uri")) {
            MongoDB.get().connect(P.p.getConfig().getString("mongodb.uri"));
        } else {
            MongoDB.get().connect(P.p.getConfig().getString("mongodb.ip"), P.p.getConfig().getInt("mongodb.port"), P.p.getConfig().getString("mongodb.user"), P.p.getConfig().getString("mongodb.password").toCharArray(), P.p.getConfig().getString("mongodb.database"));
        }
        this.lifeManager = new LifeManager();
        DeathbanManager.get().loadDeathbans();
        P.p.getServer().getScheduler().runTaskTimer(P.p, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ScoreboardManager.get().updateScoreboard(player);
            }
        }, 50L, 50L);
        Bukkit.getScheduler().runTaskLater(P.p, () -> {
            TitleManager.get().load();
        }, 20L);
        this.enabledPatches = new ArrayList<>();
        this.allPatches = new ArrayList<>();
        this.registerPatches(new ChatFilter(P.p), new FactionPoints(P.p), new BlockValue(P.p), new FactionsTop(P.p));
    }

    public void registerPatches(UHCFPatch... patches) {
        for (UHCFPatch plugin : patches) {
            plugin.inject();
            this.enabledPatches.add(plugin);
            this.allPatches.add(plugin);
        }
    }

    public void disablePatch(UHCFPatch patch) {
        this.enabledPatches.remove(patch);
        patch.kill();
    }

    public UHCFPatch getPatch(String name) {
        for (UHCFPatch patch : this.allPatches) {
            if(patch.getClass().getSimpleName().equalsIgnoreCase(name)) {
                return patch;
            }
        }
        return null;
    }

    public CommandManager getCommandManager() {
        return P.p.getCommandManager();
    }

    public void commands() throws NoSuchFieldException, IllegalAccessException {
        P.p.getCommand("customitem").setExecutor(new CmdCustomItem());
        P.p.getCommand("title").setExecutor(new CmdTitle());
        this.getCommandManager().registerCommand(P.p, new CmdACore());
    }

    public void listeners() {
        P.p.getServer().getPluginManager().registerEvents(new CustomItemListener(), P.p);
        P.p.getServer().getPluginManager().registerEvents(new EnderChestListener(), P.p);
        P.p.getServer().getPluginManager().registerEvents(new LifeListeners(), P.p);
        P.p.getServer().getPluginManager().registerEvents(new VoucherListener(), P.p);
    }

    public static WarhammerCore get() {
        return instance;
    }

    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(DeathbanManager.get().getTotalKills(player.getUniqueId()) <= 0) {
                continue;
            }
            DeathbanManager.get().storeKills(player.getUniqueId(), true);
        }
        for (UHCFPatch enabledPatch : this.enabledPatches) {
            enabledPatch.kill();
        }
    }
}
