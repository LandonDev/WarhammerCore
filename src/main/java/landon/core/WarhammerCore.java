package landon.core;

import com.google.common.collect.Lists;
import com.massivecraft.factions.*;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInvsPlugin;
import landon.core.commands.CmdACore;
import landon.core.commands.CmdCustomItem;
import landon.core.listeners.CustomItemListener;
import landon.core.patchapi.UHCFPatch;
import landon.core.patchapi.patches.anticrash.AntiCrash;
import landon.core.patchapi.patches.blockvalues.BlockValue;
import landon.core.patchapi.patches.chunkgc.ChunkGC;
import landon.core.patchapi.patches.fpoints.FactionPoints;
import landon.core.patchapi.patches.ftop.FactionsTop;
import landon.core.patchapi.patches.fupgrades.FactionUpgrades;
import landon.core.patchapi.patches.pluginviewer.PluginViewer;
import landon.core.patchapi.patches.spawnerfree.SpawnerFee;
import landon.core.util.armorequip.ArmorListener;
import landon.core.util.cooldown.CooldownManager;
import landon.core.util.cooldown.Cooldowns;
import landon.core.util.customcommand.CommandManager;
import landon.core.util.items.CustomItemManager;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import java.util.*;

@Getter
@Setter
public final class WarhammerCore {
    private static WarhammerCore instance;
    private Cooldowns cooldownUtil;
    private CooldownManager cooldownManager;
    private CustomItemManager itemManager;
    private InventoryManager inventoryManager;
    private List<UHCFPatch> enabledPatches;
    private List<UHCFPatch> allPatches;
    public static Economy economy = null;

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = P.p.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null)
            economy = (Economy)economyProvider.getProvider();
        return (economy != null);
    }

    public void onEnable() {
        instance = this;
        setupEconomy();
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
        this.inventoryManager = new InventoryManager(P.p);
        this.inventoryManager.init();
        P.p.saveDefaultConfig();
        P.p.getServer().getPluginManager().registerEvents(new ArmorListener(Lists.newArrayList()), P.p);
        this.enabledPatches = new ArrayList<>();
        this.allPatches = new ArrayList<>();
        this.registerPatches(new PluginViewer(P.p), new ChunkGC(P.p), new FactionPoints(P.p), new BlockValue(P.p), new FactionsTop(P.p), new SpawnerFee(P.p), new FactionUpgrades(P.p), new AntiCrash(P.p));
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
        this.getCommandManager().registerCommand(P.p, new CmdACore());
    }

    public void listeners() {
        P.p.getServer().getPluginManager().registerEvents(new CustomItemListener(), P.p);
    }

    public static WarhammerCore get() {
        return instance;
    }

    public void onDisable() {
        for (UHCFPatch enabledPatch : this.enabledPatches) {
            enabledPatch.kill();
        }
    }
}
