package landon.warhammercore.util;

import java.io.File;

import landon.warhammercore.patchapi.patches.anticrash.AntiCrash;
import landon.warhammercore.patchapi.patches.anticrash.CommandReboot;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.spigotmc.WatchdogThread;

public class MinecraftThreadMonitor extends Thread {
    public long previous_tick = 0L;

    public static int non_response = 0;

    long maxMemoryMB = 0L;

    long last_GC = 0L;

    public void run() {
        this.maxMemoryMB = Runtime.getRuntime().maxMemory() / 1048576L;
        while (true) {
            try {
                Thread.sleep(5000L);
            } catch (Exception err) {
                System.out.println("This stack trace is only to inform that the monitor thread was interrupted. The thread has still been killed.");
                err.printStackTrace();
                System.out.println("Exiting Monitor Thread...");
                return;
            }
            if (AntiCrash.reboot_task_id == -1) {
                long currentMemoryMB = Runtime.getRuntime().totalMemory() / 1048576L;
                long freeMemoryMB = Runtime.getRuntime().freeMemory() / 1048576L;
                if (freeMemoryMB <= this.maxMemoryMB * 0.12D) {
                    Bukkit.getLogger().info("maxMemoryMB = " + this.maxMemoryMB);
                    Bukkit.getLogger().info("currentMemoryMB = " + currentMemoryMB);
                    Bukkit.getLogger().info("memLeftMB = " + (this.maxMemoryMB - currentMemoryMB));
                    Bukkit.getLogger().info("freeMemMB = " + freeMemoryMB);
                    return;
                }
                if ((new File("/minecraft")).getUsableSpace() <= 5.0E9D) {
                    Bukkit.getLogger().info("[Arkkit (AntiCrash)] Less than 5GB of space left on primary partition, whitelisting and rebooting server.");
                    Bukkit.setWhitelist(true);
                    AntiCrash.restartServer(60);
                    return;
                }
            }
            long current_tick = MinecraftServer.currentTick;
            if ((this.previous_tick != 0L && current_tick == this.previous_tick) || !MinecraftServer.getServer().isRunning()) {
                if (non_response == 24) {
                    Bukkit.getLogger().info("The main thread cannot be recovered, attempting restart...");
                    if ((new File("plugins/ArkhamHub.jar")).exists()) {
                        Bukkit.getLogger().info("Force killing JVM.");
                        Runtime.getRuntime().exit(-1);
                    } else {
                        AntiCrash.crash = true;
                    }
                    non_response++;
                    continue;
                }
                if (AntiCrash.disable_called ? (non_response >= 162) : (non_response >= 90)) {
                    Bukkit.getLogger().info("Force killing JVM.");
                    Runtime.getRuntime().exit(-1);
                    return;
                }
                Bukkit.getLogger().info("The server's main thread seems to be frozen!");
                Bukkit.getLogger().info("previous_tick=" + this.previous_tick + ", current_tick=" + current_tick);
                non_response++;
                Bukkit.getLogger().info("non_response = " + non_response);
                if ((non_response < 5 || non_response == 15) &&
                        !AntiCrash.shutting_down && !AntiCrash.crash && !CommandReboot.restart_task_exists) {
                    WatchdogThread.dumpStack();
                }
                continue;
            }
            this.previous_tick = current_tick;
            if (non_response > 0)
                Bukkit.getLogger().info("Server thread recovered! YAY!");
            non_response = 0;
        }
    }
}
