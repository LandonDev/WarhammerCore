/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  com.cosmicpvp.cosmicutils.utils.NumberUtils
 *  com.cosmicpvp.cosmicutils.utils.ReflectionUtils
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.util.reflection.ReflectionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;

public class CommandAdminFaction
        implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.isOp()) {
            return true;
        }
        if (!(sender instanceof CommandExecutor) && !sender.getName().equalsIgnoreCase("driftay")) {
            sender.sendMessage(CC.Red + "You must execute this command from the console!");
            return true;
        }
        if (args.length == 5) {
            if (args[0].equalsIgnoreCase("setvalue")) {
                Faction faction = Factions.i.getByTag(args[1]);
                if (faction == null) {
                    sender.sendMessage(CC.Red + "Null faction given!");
                    return true;
                }
                String valueToSet = args[4];
                Field field = null;
                try {
                    field = ReflectionUtils.getField(Faction.class, args[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(CC.Red + "Unable to find field: " + e.getMessage());
                }
                String type = args[3];
                Object toSet = null;
                if (type.endsWith("int") && (toSet = parseIntOrNull(valueToSet)) == null) {
                    sender.sendMessage(CC.Red + "Invalid Integer: " + valueToSet);
                    return true;
                }
                if (type.endsWith("long")) {
                    try {
                        toSet = Long.parseLong(valueToSet);
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                    if (toSet == null) {
                        sender.sendMessage(CC.Red + "Invalid Long: " + valueToSet);
                        return true;
                    }
                }
                if (type.equalsIgnoreCase("string")) {
                    toSet = valueToSet;
                }
                if (type.equalsIgnoreCase("NULL")) {
                    toSet = null;
                }
                if (field == null) {
                    sender.sendMessage(CC.Red + "Invalid field!");
                    return true;
                }
                try {
                    field.set(faction, toSet);
                    sender.sendMessage(CC.Red + "Set " + args[2] + " to " + valueToSet + " type: " + type);
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(CC.Red + "Error setting value: " + args[2]);
                }
                return true;
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("getvalue")) {
            Faction faction = Factions.i.getByTag(args[1]);
            if (faction == null) {
                sender.sendMessage(CC.Red + "Null faction given!");
                return true;
            }
            Field field = ReflectionUtils.getField(Faction.class, args[1]);
            if (field == null) {
                sender.sendMessage(CC.Red + "Invalid field given!");
                return true;
            }
            try {
                sender.sendMessage(CC.Red + "Value of " + args[1] + ": " + field.get(faction));
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(CC.Red + "Unable to get field value!");
            }
            return true;
        }
        sender.sendMessage(CC.Red + "/afaction setvalue <faction> <fieldName> <type> <value>");
        sender.sendMessage(CC.Red + "Prefixes on the value set the type: int, long, string, NULL");
        return false;
    }

    public Integer parseIntOrNull(String val) {
        try {
            return Integer.valueOf(Integer.parseInt(val));
        } catch (Exception exception) {
            return null;
        }
    }
}

