package landon.warhammercore.patchapi.patches.anticrash;

import com.massivecraft.factions.P;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

final class ServerRebootTask extends BukkitRunnable {
    volatile int iteration = 60;

    private String restartWarnMessage = ChatColor.translateAlternateColorCodes('&', "&e&l *** LOCAL SERVER REBOOTING IN [time] SECONDS ***");

    public ServerRebootTask(boolean force) {
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "      *** A reboot has been queued. *** ");
        Bukkit.broadcastMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "  This process occurs when Warhammer pulls a critical core update.");
        Bukkit.broadcastMessage("");
        if (force)
            this.iteration = 0;
    }

    public synchronized void run() {
        if (this.iteration < -10)
            return;
        switch (this.iteration) {
            case 60:
                doBroadcast();
                for (Player p : Bukkit.getOnlinePlayers())
                    p.closeInventory();
                break;
            case 45:
                doBroadcast();
                break;
            case 30:
                doBroadcast();
                break;
            case 15:
                doBroadcast();
                break;
            case 10:
                doBroadcast();
                Bukkit.getScheduler().runTask(P.p, new Runnable() {
                    public void run() {
                        Bukkit.savePlayers();
                        Bukkit.getLogger().info("(CommandReboot) Saving all worlds...");
                        for (World world : Bukkit.getWorlds())
                            world.save();
                    }
                });
                break;
            case 5:
                doBroadcast();
                break;
            case 4:
                doBroadcast();
                break;
            case 3:
                doBroadcast();
                break;
            case 2:
                doBroadcast();
                break;
            case 1:
                doBroadcast();
                break;
            case -10:
                Bukkit.getLogger().info("(CommandArkReboot) Stopping the server...");
                Bukkit.shutdown();
                cancel();
                break;
        }
        this.iteration--;
    }

    private void doBroadcast() {
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(this.restartWarnMessage.replace("[time]", String.valueOf(this.iteration)));
        Bukkit.broadcastMessage("");
    }
}
