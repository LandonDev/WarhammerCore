package landon.warhammercore.patches.patches.ftop;

import com.massivecraft.factions.P;
import landon.warhammercore.patches.UHCFPatch;
import landon.warhammercore.patches.patches.ftop.commands.CommandEditFactionsTop;
import landon.warhammercore.patches.patches.ftop.commands.CommandFactionsTop;
import landon.warhammercore.patches.patches.ftop.commands.CommandRecalcFactionsTop;
import landon.warhammercore.patches.patches.ftop.listeners.CommandListener;
import landon.warhammercore.patches.patches.ftop.manager.TopManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class FactionsTop extends UHCFPatch {
    private static FactionsTop instance;

    private TopManager topManager = new TopManager();

    private static Economy economy;

    public FactionsTop(Plugin p) {
        super(p);
    }

    public TopManager getTopManager() {
        return this.topManager;
    }

    public static Economy getEconomy() {
        return economy;
    }

    private boolean gradualSpawnerValues = false;

    private boolean includeBank = false;

    private boolean includeHoppers = true;

    public boolean isGradualSpawnerValues() {
        return this.gradualSpawnerValues;
    }

    public boolean isIncludeBank() {
        return this.includeBank;
    }

    public boolean isIncludeHoppers() {
        return this.includeHoppers;
    }

    public void setGradualSpawnerValues(boolean gradualSpawnerValues) {
        this.gradualSpawnerValues = gradualSpawnerValues;
    }

    public void setIncludeBank(boolean includeBank) {
        this.includeBank = includeBank;
    }

    public void setIncludeHoppers(boolean includeHoppers) {
        this.includeHoppers = includeHoppers;
    }

    public boolean upgradesEnabled = false, factionPointsEnabled = false;

    public void enable() {
        instance = this;
        setupEconomy();
        registerListeners();
        registerCOmmands();
        this.upgradesEnabled = Bukkit.getPluginManager().isPluginEnabled("FactionUpgrades");
        this.factionPointsEnabled = true;
        this.topManager.loadTopFactions(null, 100);
        this.includeBank = P.p.getConfig().getBoolean("includeBank");
        this.gradualSpawnerValues = P.p.getConfig().getBoolean("gradualSpawnerValue");
        this.includeHoppers = P.p.getConfig().getBoolean("includeHoppers", true);
    }

    private void setupEconomy() {
        try {
            economy = (Economy) P.p.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        } catch (Exception exception) {}
    }

    private void registerListeners() {
        registerListener(new CommandListener());
    }

    private void registerCOmmands() {
        P.p.getCommand("recalcftop").setExecutor(new CommandRecalcFactionsTop());
        P.p.getCommand("ftop").setExecutor(new CommandFactionsTop());
        P.p.getCommand("editftop").setExecutor(new CommandEditFactionsTop());
    }

    public static FactionsTop get() {
        return instance;
    }
}
