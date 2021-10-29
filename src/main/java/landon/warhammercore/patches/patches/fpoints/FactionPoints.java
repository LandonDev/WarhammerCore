package landon.warhammercore.patches.patches.fpoints;

import com.massivecraft.factions.P;
import landon.warhammercore.patches.UHCFPatch;
import landon.warhammercore.patches.patches.fpoints.commands.CommandFactionPoints;
import landon.warhammercore.patches.patches.fpoints.listeners.FactionListener;
import landon.warhammercore.patches.patches.fpoints.listeners.FactionPointListener;
import landon.warhammercore.patches.patches.fpoints.managers.PointManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public final class FactionPoints extends UHCFPatch {
    private static FactionPoints instance;

    public PointManager pointManager;

    public FactionPoints(Plugin p) {
        super(p);
    }

    public PointManager getPointManager() {
        return this.pointManager;
    }

    public static boolean debug = false;

    private long startOfMap;

    public long getStartOfMap() {
        return this.startOfMap;
    }

    public void setStartOfMap(long startOfMap) {
        this.startOfMap = startOfMap;
    }

    private boolean vanillaPlanet = false;

    public boolean isVanillaPlanet() {
        return this.vanillaPlanet;
    }

    @Override
    public void enable() {
        instance = this;
        this.pointManager = new PointManager();
        this.pointManager.onEnable(this);
        registerCommands();
        registerListeners();
        this.startOfMap = P.p.getConfig().getLong("patches.fpoints.startOfMap");
        this.vanillaPlanet = P.p.getConfig().getBoolean("patches.fpoints.vanilla", false);
    }

    @Override
    public void disable() {
        this.pointManager.onDisable(this);
        instance = null;
    }

    private void registerCommands() {
        P.p.getCommand("factionpoints").setExecutor(new CommandFactionPoints());
    }

    private void registerListeners() {
        registerListener(new FactionPointListener());
        registerListener(new FactionListener());
    }

    public static void debug(String debugString) {
        if (!debug)
            return;
        Bukkit.getLogger().info("[FactionPoints] " + debugString);
    }

    public static void log(String debugString) {
        Bukkit.getLogger().info("[FactionPoints] " + debugString);
    }

    public static FactionPoints get() {
        return instance;
    }
}
