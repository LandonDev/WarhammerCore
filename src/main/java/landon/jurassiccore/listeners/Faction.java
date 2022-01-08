package landon.jurassiccore.listeners;

import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionRelationEvent;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.nametag.NametagManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;

public class Faction implements Listener {
    private final JurassicCore instance;

    public Faction(JurassicCore instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onFPlayerJoin(FPlayerJoinEvent event) {
        if (event.getFPlayer() == null)
            return;
        final Player player = event.getFPlayer().getPlayer();
        if (player == null)
            return;
        FileConfiguration configLoad = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration();
        if (configLoad.getBoolean("Scoreboard.Enable"))
            this.instance.getScoreboardManager().sendFactionScoreboard(player);
        if (configLoad.getBoolean("Nametag.Enable"))
            Bukkit.getServer().getScheduler().runTaskAsynchronously(P.p, new Runnable() {
                public void run() {
                    NametagManager nametagManager = Faction.this.instance.getNametagManager();
                    for (Player all : Bukkit.getOnlinePlayers())
                        nametagManager.sendNametag(all, player);
                }
            });
    }

    @EventHandler
    public void onFPlayerLeave(FPlayerLeaveEvent event) {
        if (event.getFPlayer() == null)
            return;
        final Player player = event.getFPlayer().getPlayer();
        if (player == null)
            return;
        FileConfiguration configLoad = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration();
        if (configLoad.getBoolean("Scoreboard.Enable"))
            this.instance.getScoreboardManager().sendDefaultScoreboard(player);
        if (configLoad.getBoolean("Nametag.Enable"))
            Bukkit.getServer().getScheduler().runTaskAsynchronously(P.p, new Runnable() {
                public void run() {
                    NametagManager nametagManager = Faction.this.instance.getNametagManager();
                    for (Player all : Bukkit.getOnlinePlayers())
                        nametagManager.sendNametag(all, player);
                }
            });
    }

    @EventHandler
    public void onFactionRelation(final FactionRelationEvent event) {
        FileConfiguration configLoad = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration();
        if (!configLoad.getBoolean("Nametag.Enable"))
            return;
        if (!configLoad.getBoolean("Nametag.Faction.Relation"))
            return;
        Bukkit.getServer().getScheduler().runTaskAsynchronously(P.p, new Runnable() {
            public void run() {
                NametagManager nametagManager = Faction.this.instance.getNametagManager();
                for (Player factionPlayer : event.getFaction().getOnlinePlayers()) {
                    for (Player targetFactionPlayer : event.getTargetFaction().getOnlinePlayers()) {
                        nametagManager.sendNametag(factionPlayer, targetFactionPlayer);
                        nametagManager.sendNametag(targetFactionPlayer, factionPlayer);
                    }
                }
            }
        });
    }
}
