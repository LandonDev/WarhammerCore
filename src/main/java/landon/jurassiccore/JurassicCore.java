package landon.jurassiccore;

import com.massivecraft.factions.P;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import landon.jurassiccore.balance.BalanceManager;
import landon.jurassiccore.commands.*;
import landon.jurassiccore.cooldown.CooldownTask;
import landon.jurassiccore.expiry.ExpiryTask;
import landon.jurassiccore.faction.FactionManager;
import landon.jurassiccore.file.FileManager;
import landon.jurassiccore.listeners.*;
import landon.jurassiccore.location.LocationManager;
import landon.jurassiccore.nametag.NametagManager;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.scoreboard.ScoreboardManager;
import landon.jurassiccore.tasks.ClearLagTask;
import landon.jurassiccore.tasks.FlyTask;
import landon.jurassiccore.tasks.ItemProtectTask;
import landon.jurassiccore.timeout.TimeoutTask;
import landon.jurassiccore.utils.ItemDB;
import landon.jurassiccore.utils.NMSUtil;
import landon.jurassiccore.vault.VaultManager;
import lombok.Getter;
import me.goodandevil.jurassiccore.commands.*;
import me.goodandevil.jurassiccore.listeners.*;
import net.minelink.ctplus.CombatTagPlus;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.lang.reflect.Field;

@Getter
public class JurassicCore {
    private static JurassicCore instance;

    private CombatTagPlus combatTagPlus;

    private WorldGuardPlugin worldGuard;

    private FileManager fileManager;

    private VaultManager vaultManager;

    private PlayerDataManager playerDataManager;

    private LocationManager locationManager;

    private ScoreboardManager scoreboardManager;

    private NametagManager nametagManager;
  
    private BalanceManager balanceManager;

    private FactionManager factionManager;

    private ItemDB itemDB;

    public void onEnable() {
        instance = this;
        this.itemDB = new ItemDB();
        this.fileManager = new FileManager(P.p);
        this.vaultManager = new VaultManager(this);
        this.locationManager = new LocationManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.nametagManager = new NametagManager(this);
        this.balanceManager = new BalanceManager(this);
        this.factionManager = new FactionManager();
        (new FlyTask(this.playerDataManager)).runTaskTimerAsynchronously(P.p, 0L, 2L);
        (new ClearLagTask(this)).runTaskTimerAsynchronously(P.p, 0L, (
                this.fileManager.getConfig(new File(P.p.getDataFolder(), "config.yml")).getFileConfiguration()
                        .getInt("ClearLag.Time") * 20));
        (new CooldownTask(this.playerDataManager)).runTaskTimerAsynchronously(P.p, 0L, 20L);
        (new ExpiryTask(this)).runTaskTimerAsynchronously(P.p, 0L, 1L);
        (new TimeoutTask(this)).runTaskTimerAsynchronously(P.p, 0L, 1L);
        (new ItemProtectTask(this)).runTaskTimerAsynchronously(P.p, 0L, 1L);
        PluginManager pluginManager = P.p.getServer().getPluginManager();
        pluginManager.registerEvents((Listener) new Tab(this), P.p);
        pluginManager.registerEvents((Listener) new Chat(this), P.p);
        pluginManager.registerEvents((Listener) new Food(this), P.p);
        pluginManager.registerEvents((Listener) new Join(this), P.p);
        pluginManager.registerEvents((Listener) new Quit(this), P.p);
        pluginManager.registerEvents((Listener) new Move(this), P.p);
        pluginManager.registerEvents((Listener) new Item(this), P.p);
        pluginManager.registerEvents((Listener) new Craft(this), P.p);
        pluginManager.registerEvents((Listener) new Death(this), P.p);
        pluginManager.registerEvents((Listener) new Bucket(this), P.p);
        pluginManager.registerEvents((Listener) new Combat(this), P.p);
        pluginManager.registerEvents((Listener) new Entity(this), P.p);
        pluginManager.registerEvents((Listener) new Respawn(this), P.p);
        pluginManager.registerEvents((Listener) new Faction(this), P.p);
        pluginManager.registerEvents((Listener) new Command(this), P.p);
        pluginManager.registerEvents((Listener) new Interact(this), P.p);
        pluginManager.registerEvents((Listener) new Inventory(this), P.p);
        P.p.getCommand("feed").setExecutor((CommandExecutor) new FeedCommand(this));
        P.p.getCommand("heal").setExecutor((CommandExecutor) new HealCommand(this));
        P.p.getCommand("setspawn").setExecutor((CommandExecutor) new SetSpawnCommand(this));
        P.p.getCommand("spawn").setExecutor((CommandExecutor) new SpawnCommand(this));
        P.p.getCommand("back").setExecutor((CommandExecutor) new BackCommand(this));
        P.p.getCommand("setwarp").setExecutor((CommandExecutor) new SetWarpCommand(this));
        P.p.getCommand("delwarp").setExecutor((CommandExecutor) new DelWarpCommand(this));
        P.p.getCommand("warps").setExecutor((CommandExecutor) new WarpsCommand(this));
        P.p.getCommand("i").setExecutor(new GiveCommand());
        PluginCommand WarpCommand = P.p.getCommand("warp");
        WarpCommand.setExecutor((CommandExecutor) new WarpCommand(this));
        WarpCommand.setTabCompleter((TabCompleter) new WarpCommand(this));
        PluginCommand HomeCommand = P.p.getCommand("home");
        HomeCommand.setExecutor((CommandExecutor) new HomeCommand(this));
        HomeCommand.setTabCompleter((TabCompleter) new HomeCommand(this));
        P.p.getCommand("homes").setExecutor((CommandExecutor) new HomesCommand(this));
        P.p.getCommand("sethome").setExecutor((CommandExecutor) new SetHomeCommand(this));
        PluginCommand DelHomeCommand = P.p.getCommand("delhome");
        DelHomeCommand.setExecutor((CommandExecutor) new DelHomeCommand(this));
        DelHomeCommand.setTabCompleter((TabCompleter) new DelHomeCommand(this));
        P.p.getCommand("withdraw").setExecutor((CommandExecutor) new WithdrawCommand(this));
        P.p.getCommand("xpbottle").setExecutor((CommandExecutor) new XPBottleCommand(this));
        P.p.getCommand("jcreload").setExecutor((CommandExecutor) new JCReloadCommand(this));
        P.p.getCommand("near").setExecutor((CommandExecutor) new NearCommand(this));
        P.p.getCommand("tpa").setExecutor((CommandExecutor) new TPACommand(this));
        P.p.getCommand("tpahere").setExecutor((CommandExecutor) new TPAHereCommand(this));
        P.p.getCommand("tpaccept").setExecutor((CommandExecutor) new TPAcceptCommand(this));
        P.p.getCommand("tpdeny").setExecutor((CommandExecutor) new TPDenyCommand(this));
        P.p.getCommand("tpacancel").setExecutor((CommandExecutor) new TPACancelCommand(this));
        PluginCommand FixCommand = P.p.getCommand("fix");
        FixCommand.setExecutor((CommandExecutor) new FixCommand(this));
        FixCommand.setTabCompleter((TabCompleter) new FixCommand(this));
        P.p.getCommand("bless").setExecutor((CommandExecutor) new BlessCommand(this));
        P.p.getCommand("donation").setExecutor((CommandExecutor) new DonationCommand(this));
        P.p.getCommand("giveall").setExecutor((CommandExecutor) new GiveAllCommand(this));
        P.p.getCommand("ecogiveall").setExecutor((CommandExecutor) new EcoGiveAllCommand(this));
        P.p.getCommand("xpgiveall").setExecutor((CommandExecutor) new XPGiveAllCommand(this));
        P.p.getCommand("expgiveall").setExecutor((CommandExecutor) new EXPGiveAllCommand(this));
        P.p.getCommand("depthstridercrystal").setExecutor((CommandExecutor) new DepthStriderCrystalCommand(this));
        P.p.getCommand("enderchest").setExecutor((CommandExecutor) new EnderChestCommand(this));
        P.p.getCommand("sudo").setExecutor((CommandExecutor) new SudoCommand(this));
        P.p.getCommand("top").setExecutor((CommandExecutor) new TopCommand(this));
        P.p.getCommand("clear").setExecutor((CommandExecutor) new ClearCommand(this));
        P.p.getCommand("rename").setExecutor((CommandExecutor) new RenameCommand(this));
        P.p.getCommand("fly").setExecutor((CommandExecutor) new FlyCommand(this));
        P.p.getCommand("god").setExecutor((CommandExecutor) new GodCommand(this));
        P.p.getCommand("msgtoggle").setExecutor((CommandExecutor) new MSGToggleCommand(this));
        P.p.getCommand("ignore").setExecutor((CommandExecutor) new IgnoreCommand(this));
        P.p.getCommand("msg").setExecutor((CommandExecutor) new MSGCommand(this));
        P.p.getCommand("gamemode").setExecutor((CommandExecutor) new GameModeCommand(this));
        P.p.getCommand("reply").setExecutor((CommandExecutor) new ReplyCommand(this));
        P.p.getCommand("balance").setExecutor((CommandExecutor) new BalanceCommand(this));
        P.p.getCommand("balancetop").setExecutor((CommandExecutor) new BalanceTopCommand(this));
        P.p.getCommand("ignores").setExecutor((CommandExecutor) new IgnoresCommand(this));
        P.p.getCommand("pay").setExecutor((CommandExecutor) new PayCommand(this));
        P.p.getCommand("invsee").setExecutor((CommandExecutor) new InvseeCommand(this));
        P.p.getCommand("paytoggle").setExecutor((CommandExecutor) new PayToggleCommand(this));
        P.p.getCommand("tp").setExecutor((CommandExecutor) new TPCommand(this));
        P.p.getCommand("eco").setExecutor((CommandExecutor) new EcoCommand(this));
        P.p.getCommand("exp").setExecutor((CommandExecutor) new EXPCommand(this));
        P.p.getCommand("xp").setExecutor((CommandExecutor) new XPCommand(this));
        P.p.getCommand("wilderness").setExecutor((CommandExecutor) new WildernessCommand(this));
        P.p.getCommand("workbench").setExecutor((CommandExecutor) new WorkbenchCommand(this));
        P.p.getCommand("lore").setExecutor((CommandExecutor) new LoreCommand(this));
        P.p.getCommand("enchant").setExecutor(new EnchantCommand());
        FileConfiguration configLoad = this.fileManager.getConfig(new File(P.p.getDataFolder(), "config.yml"))
                .getFileConfiguration();
        if (configLoad.getBoolean("BucketStack.Enable"))
            try {
                Field maxStackSizeField = NMSUtil.getNMSClass("Item").getDeclaredField("maxStackSize");
                maxStackSizeField.setAccessible(true);
                Class<?> ItemsClass = NMSUtil.getNMSClass("Items");
                maxStackSizeField.set(ItemsClass.getField("WATER_BUCKET").get((Object) null),
                        Integer.valueOf(configLoad.getInt("BucketStack.Stack")));
                maxStackSizeField.set(ItemsClass.getField("LAVA_BUCKET").get((Object) null),
                        Integer.valueOf(configLoad.getInt("BucketStack.Stack")));
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
    }

    public void onDisable() {
        this.vaultManager.onDisable();
        this.playerDataManager.onDisable();
        this.locationManager.onDisable();
    }

    public CombatTagPlus getCombatTagPlus() {
        return P.p.combatTagPlus;
    }

    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin = P.p.getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin != null)
            this.worldGuard = (WorldGuardPlugin) plugin;
        return this.worldGuard;
    }

    public FileManager getFileManager() {
        return this.fileManager;
    }

    public VaultManager getVaultManager() {
        return this.vaultManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return this.playerDataManager;
    }

    public LocationManager getLocationManager() {
        return this.locationManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return this.scoreboardManager;
    }

    public NametagManager getNametagManager() {
        return this.nametagManager;
    }

    public BalanceManager getBalanceManager() {
        return this.balanceManager;
    }

    public FactionManager getFactionManager() {
        return this.factionManager;
    }

    public static JurassicCore getInstance() {
        return instance;
    }

    public File getDataFolder() {
        return P.p.getDataFolder();
    }

    public PluginDescriptionFile getDescription() {
        return P.p.getDescription();
    }
}
