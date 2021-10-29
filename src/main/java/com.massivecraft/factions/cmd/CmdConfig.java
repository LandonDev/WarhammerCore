/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.FactionPermissions;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Set;

public class CmdConfig
        extends FCommand {
    private static final HashMap<String, String> properFieldNames = new HashMap<>();

    public CmdConfig() {
        this.aliases.add("config");
        this.requiredArgs.add("setting");
        this.requiredArgs.add("value");
        this.errorOnToManyArgs = false;
        this.permission = Permission.CONFIG.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        String success;
        block42:
        {
            String fieldName;
            String field;
            if (properFieldNames.isEmpty()) {
                Field[] fields = Conf.class.getDeclaredFields();
                for (Field value : fields) {
                    properFieldNames.put(value.getName().toLowerCase(), value.getName());
                }
            }
            field = this.argAsString(0).toLowerCase();
            if (field.equals("debug")) {
                FactionPermissions.debug = !FactionPermissions.debug;
                this.sender.sendMessage(ChatColor.RED + "Debug toggled: " + FactionPermissions.debug);
                return;
            }
            if (field.startsWith("\"") && field.endsWith("\"")) {
                field = field.substring(1, field.length() - 1);
            }
            if ((fieldName = properFieldNames.get(field)) == null || fieldName.isEmpty()) {
                this.msg("<b>No configuration setting \"<h>%s<b>\" exists.", field);
                return;
            }
            String value = this.args.get(1);
            for (int i = 2; i < this.args.size(); ++i) {
                value = value + ' ' + this.args.get(i);
            }
            try {
                Field target = Conf.class.getField(fieldName);
                if (target.getType() == Boolean.TYPE) {
                    boolean targetValue = this.strAsBool(value);
                    target.setBoolean(null, targetValue);
                    success = targetValue ? "\"" + fieldName + "\" option set to true (enabled)." : "\"" + fieldName + "\" option set to false (disabled).";
                    break block42;
                }
                if (target.getType() == Integer.TYPE) {
                    try {
                        int intVal = Integer.parseInt(value);
                        target.setInt(null, intVal);
                        success = "\"" + fieldName + "\" option set to " + intVal + ".";
                        break block42;
                    } catch (NumberFormatException ex) {
                        this.sendMessage("Cannot set \"" + fieldName + "\": integer (whole number) value required.");
                        return;
                    }
                }
                if (target.getType() == Long.TYPE) {
                    try {
                        long longVal = Long.parseLong(value);
                        target.setLong(null, longVal);
                        success = "\"" + fieldName + "\" option set to " + longVal + ".";
                        break block42;
                    } catch (NumberFormatException ex) {
                        this.sendMessage("Cannot set \"" + fieldName + "\": long integer (whole number) value required.");
                        return;
                    }
                }
                if (target.getType() == Double.TYPE) {
                    try {
                        double doubleVal = Double.parseDouble(value);
                        target.setDouble(null, doubleVal);
                        success = "\"" + fieldName + "\" option set to " + doubleVal + ".";
                        break block42;
                    } catch (NumberFormatException ex) {
                        this.sendMessage("Cannot set \"" + fieldName + "\": double (numeric) value required.");
                        return;
                    }
                }
                if (target.getType() == Float.TYPE) {
                    try {
                        float floatVal = Float.parseFloat(value);
                        target.setFloat(null, floatVal);
                        success = "\"" + fieldName + "\" option set to " + floatVal + ".";
                        break block42;
                    } catch (NumberFormatException ex) {
                        this.sendMessage("Cannot set \"" + fieldName + "\": float (numeric) value required.");
                        return;
                    }
                }
                if (target.getType() == String.class) {
                    target.set(null, value);
                    success = "\"" + fieldName + "\" option set to \"" + value + "\".";
                    break block42;
                }
                if (target.getType() == ChatColor.class) {
                    ChatColor newColor = null;
                    try {
                        newColor = ChatColor.valueOf(value.toUpperCase());
                    } catch (IllegalArgumentException illegalArgumentException) {
                        // empty catch block
                    }
                    if (newColor == null) {
                        this.sendMessage("Cannot set \"" + fieldName + "\": \"" + value.toUpperCase() + "\" is not a valid color.");
                        return;
                    }
                    target.set(null, newColor);
                    success = "\"" + fieldName + "\" color option set to \"" + value.toUpperCase() + "\".";
                    break block42;
                }
                if (target.getGenericType() instanceof ParameterizedType) {
                    ParameterizedType targSet = (ParameterizedType) target.getGenericType();
                    Type innerType = targSet.getActualTypeArguments()[0];
                    if (targSet.getRawType() != Set.class) {
                        this.sendMessage("\"" + fieldName + "\" is not a data collection type which can be modified with this command.");
                        return;
                    }
                    if (innerType == Material.class) {
                        Material newMat = null;
                        try {
                            newMat = Material.valueOf(value.toUpperCase());
                        } catch (IllegalArgumentException illegalArgumentException) {
                            // empty catch block
                        }
                        if (newMat == null) {
                            this.sendMessage("Cannot change \"" + fieldName + "\" set: \"" + value.toUpperCase() + "\" is not a valid material.");
                            return;
                        }
                        Set matSet = (Set) target.get(null);
                        if (matSet.contains(newMat)) {
                            matSet.remove(newMat);
                            target.set(null, matSet);
                            success = "\"" + fieldName + "\" set: Material \"" + value.toUpperCase() + "\" removed.";
                        } else {
                            matSet.add(newMat);
                            target.set(null, matSet);
                            success = "\"" + fieldName + "\" set: Material \"" + value.toUpperCase() + "\" added.";
                        }
                        break block42;
                    }
                    if (innerType == String.class) {
                        Set stringSet = (Set) target.get(null);
                        if (stringSet.contains(value)) {
                            stringSet.remove(value);
                            target.set(null, stringSet);
                            success = "\"" + fieldName + "\" set: \"" + value + "\" removed.";
                        } else {
                            stringSet.add(value);
                            target.set(null, stringSet);
                            success = "\"" + fieldName + "\" set: \"" + value + "\" added.";
                        }
                        break block42;
                    }
                    this.sendMessage("\"" + fieldName + "\" is not a data type set which can be modified with this command.");
                    return;
                }
                this.sendMessage("\"" + fieldName + "\" is not a data type which can be modified with this command.");
                return;
            } catch (NoSuchFieldException ex) {
                this.sendMessage("Configuration setting \"" + fieldName + "\" couldn't be matched, though it should be... please report this error.");
                return;
            } catch (IllegalAccessException ex) {
                this.sendMessage("Error setting configuration setting \"" + fieldName + "\" to \"" + value + "\".");
                return;
            }
        }
        if (!success.isEmpty()) {
            if (this.sender instanceof Player) {
                this.sendMessage(success);
                P.p.log(success + " Command was run by " + this.fme.getNameAsync() + ".");
            } else {
                P.p.log(success);
            }
        }
        Conf.save();
    }
}

