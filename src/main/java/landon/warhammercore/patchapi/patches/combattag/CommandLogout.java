package landon.warhammercore.patchapi.patches.combattag;

import java.util.List;

import com.massivecraft.factions.P;
import landon.warhammercore.patchapi.patches.anticrash.AntiCrash;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

final class CommandLogout implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1 && sender.isOp()) {
            if (CombatLogLogoutListener.spawnNPC) {
                CombatLogLogoutListener.spawnNPC = false;
                P.p.getConfig().set("patches.combat_log.spawnNPC", Boolean.valueOf(false));
                P.p.saveConfig();
            } else if (!CombatLogLogoutListener.spawnNPC) {
                CombatLogLogoutListener.spawnNPC = true;
                P.p.getConfig().set("patches.combat_log.spawnNPC", Boolean.valueOf(true));
                P.p.saveConfig();
            }
            sender.sendMessage("combatLog.spawnNPC => " + CombatLogLogoutListener.spawnNPC);
            return true;
        }
        if (sender instanceof Player) {
            Player p = (Player)sender;
            if (AntiCrash.shutting_down && !AntiCrash.crash && CombatLog.inCombat(p))
                if (!CombatLog.isPvPDisabled(p.getLocation())) {
                    p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "(!) You cannot logout until you are out of combat.");
                    p.sendMessage(ChatColor.GRAY + "You will be out of combat in " + ChatColor.RED + getCombatSecondsLeft(p) + " second(s)");
                    return true;
                }
            for (int x = 0; x < 20; x++)
                p.sendMessage("");
            p.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "LOGGING OUT...");
            p.removeMetadata("cl_combat", P.p);
            p.kickPlayer("Logged out successfully.");
        }
        return true;
    }

    protected static int getCombatSecondsLeft(Player pl) {
        if (pl.hasMetadata("cl_combat") && pl.getMetadata("cl_combat").get(0) != null)
            return (int)(CombatLog.getCombatTagDuration(pl) - System.currentTimeMillis() - ((MetadataValue)pl.getMetadata("cl_combat").get(0)).asLong()) / 1000;
        return 0;
    }
}

