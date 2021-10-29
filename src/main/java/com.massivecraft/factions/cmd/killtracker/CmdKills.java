package com.massivecraft.factions.cmd.killtracker;

import com.massivecraft.factions.P;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Saser
 */
public class CmdKills implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("kills")) {
            if (sender.isOp()) {
                if (args.length == 3) {
                    String e = args[0];
                    Player p = Bukkit.getPlayerExact(args[1]);
                    int g = Integer.parseInt(args[2]);
                    if (p != null) {
                        if (e.equalsIgnoreCase("add")) {
                            if (g >= 1) {
                                KillHandler.giveKills(p, g);
                                sender.sendMessage(P.color("&aAdded &e" + g + "&a kills to &d" + p.getName() + "&a!"));
                            } else {
                                sender.sendMessage(P.color("&aCannot add a negative or zero amount of kills!"));
                            }
                        }
                        if (e.equalsIgnoreCase("take")) {
                            if (g >= 1) {
                                KillHandler.takeKills(p, g);
                                sender.sendMessage(P.color("&aRemoved &e" + g + "&a kills from &d" + p.getName() + "&a!"));
                            } else {
                                sender.sendMessage(P.color("&aCannot take a negative or zero amount of kills!"));
                            }
                        }
                    } else {
                        sender.sendMessage(P.color("&aCannot find this player..."));
                    }
                }
                if (args.length == 0) {
                    sender.sendMessage(P.color("&aTry: /kills add|take player amount"));
                }
                if (args.length == 1) {
                    Player p = Bukkit.getPlayerExact(args[0]);
                    if (p != null) {
                        sender.sendMessage(P.color("&a" + p.getName() + "'s kills: &e" + KillHandler.getKills(p)));
                    } else {
                        sender.sendMessage(P.color("&aCannot find this player..."));
                    }
                }
            }
        }
        return false;
    }
}
