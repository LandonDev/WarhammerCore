package landon.warhammercore.patchapi.patches.anticrash;

import java.io.File;

import com.massivecraft.factions.P;
import landon.warhammercore.patchapi.UHCFPatch;
import landon.warhammercore.util.MinecraftThreadMonitor;
import landon.warhammercore.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.spigotmc.SpigotConfig;

public class AntiCrash extends UHCFPatch {
    protected static Logger log = new Logger();

    MinecraftThreadMonitor MTM;

    public static boolean shutting_down = false;

    protected static boolean reboot_me = false;

    public static boolean disable_called = false;

    public static boolean crash = false;

    protected static boolean treasureWars = (new File("treasurewars")).exists();

    public static int reboot_task_id = -1;

    public AntiCrash(Plugin p) {
        super(p);
    }

    public void enable() {
        SpigotConfig.restartOnCrash = true;
        SpigotConfig.restartScript = "null.sh";
        SpigotConfig.timeoutTime = 300;
        getBukkitPlugin().getServer().getScheduler().runTaskLater(getBukkitPlugin(), new Runnable() {
            public void run() {
                AntiCrash.this.MTM = new MinecraftThreadMonitor();
                AntiCrash.this.MTM.start();
                AntiCrash.log.debug("Launching MinecraftThreadMonitor thread...", getClass());
            }
        }, 20L);
        if (P.p.getConfig().getBoolean("patches.anticrash.entityscan"))
            Bukkit.getScheduler().runTaskTimer(P.p, new Runnable() {
                public void run() {
                    for (World w : Bukkit.getWorlds()) {
                        if (w.getEntities().size() >= 50000) {
                            Bukkit.getLogger().info("[HotfixPlugin] Killing up to " + w.getEntities().size() + " entities in " + w.getName());
                            for (Entity ent : w.getEntities()) {
                                if (ent instanceof org.bukkit.entity.TNTPrimed || ent instanceof org.bukkit.entity.FallingBlock)
                                    ent.remove();
                            }
                        }
                    }
                }
            },  2L, 2L);
    }

    public void disable() {
        log.debug("Killing MinecraftThreadMonitor thread...", getClass());
        if (!crash) {
            this.MTM.interrupt();
            this.MTM = null;
        }
        disable_called = true;
        unregisterListeners();
    }

    public static void restartServer(int countdown) {
        CommandReboot.restart_task_exists = true;
        Bukkit.getScheduler().runTask(P.p, new Runnable() {
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers())
                    p.closeInventory();
            }
        });
        reboot_task_id = Bukkit.getScheduler().runTaskTimer(P.p, new ServerRebootTask(true), 20L, 20L).getTaskId();
    }
}
