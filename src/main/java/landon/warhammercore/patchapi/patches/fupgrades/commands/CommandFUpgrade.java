package landon.warhammercore.patchapi.patches.fupgrades.commands;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import landon.warhammercore.patchapi.patches.fpoints.utils.FactionUtils;
import landon.warhammercore.patchapi.patches.fupgrades.struct.menu.FUpgradeMenu;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandFUpgrade implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player))
            return true;
        Player player = (Player)sender;
        FPlayer fplayer = FPlayers.i.get(player);
        Faction toSee = fplayer.getFaction();
        if (args.length == 1) {
            toSee = FactionUtils.getFactionByTag(args[0]);
            if (toSee == null) {
                sender.sendMessage(CC.Red + "Invalid faction given, using your faction instead.");
                toSee = fplayer.getFaction();
            }
        }
        if (toSee == null || !toSee.isNormal()) {
            sender.sendMessage(CC.RedB + "(!) " + CC.Red + "You must be in a faction to view your /f upgrades!");
            return true;
        }
        (new FUpgradeMenu(player, toSee)).openGUI(P.p);
        return false;
    }
}
