package landon.warhammercore.patchapi.patches.fupgrades;

import com.massivecraft.factions.P;
import com.massivecraft.factions.util.gui.listeners.PlayerListener;
import landon.warhammercore.WarhammerCore;
import landon.warhammercore.patchapi.UHCFPatch;
import landon.warhammercore.patchapi.patches.fupgrades.commands.CommandFCrystal;
import landon.warhammercore.patchapi.patches.fupgrades.commands.CommandFUpgrade;
import landon.warhammercore.patchapi.patches.fupgrades.listeners.FactionListener;
import landon.warhammercore.patchapi.patches.fupgrades.listeners.PerkListener;
import landon.warhammercore.patchapi.patches.fupgrades.struct.UpgradeManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class FactionUpgrades extends UHCFPatch {
    private static FactionUpgrades instance;

    private UpgradeManager upgradeManager;

    private static Economy economy;

    public FactionUpgrades(Plugin p) {
        super(p);
    }

    public UpgradeManager getUpgradeManager() {
        return this.upgradeManager;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public void enable() {
        instance = this;
        economy = WarhammerCore.economy;
        P.p.getCommand("fupgrade").setExecutor((CommandExecutor)new CommandFUpgrade());
        P.p.getCommand("fcrystal").setExecutor((CommandExecutor)new CommandFCrystal());
        P.p.getServer().getPluginManager().registerEvents((Listener)new PlayerListener(), P.p);
        P.p.getServer().getPluginManager().registerEvents((Listener)new PerkListener(), P.p);
        P.p.getServer().getPluginManager().registerEvents((Listener)new FactionListener(), P.p);
        this.upgradeManager = new UpgradeManager();
        this.upgradeManager.loadUpgrades();
    }

    public void disable() {
        this.upgradeManager.saveUpgradeInfo();
    }

    public static FactionUpgrades get() {
        return instance;
    }
}
