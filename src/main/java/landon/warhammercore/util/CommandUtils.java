package landon.warhammercore.util;

import com.massivecraft.factions.P;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.HashMap;

public class CommandUtils {
    private static Object getPrivateField(final Object object, final String field) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        final Class<?> clazz = object.getClass();
        final Field objectField = clazz.getDeclaredField(field);
        objectField.setAccessible(true);
        final Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }

    public static void unRegisterBukkitCommand(final Command cmd) {
        try {
            final Object result = getPrivateField(P.p.getServer().getPluginManager(), "commandMap");
            final SimpleCommandMap commandMap = (SimpleCommandMap) result;
            final Object map = getPrivateField(commandMap, "knownCommands");
            final HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            knownCommands.remove(cmd.getName());
            for (final String alias : cmd.getAliases()) {
                if (knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(P.p.getName())) {
                    knownCommands.remove(alias);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unRegisterBukkitCommand(final Plugin p, final Command cmd) {
        try {
            final Object result = getPrivateField(p.getServer().getPluginManager(), "commandMap");
            final SimpleCommandMap commandMap = (SimpleCommandMap) result;
            final Object map = getPrivateField(commandMap, "knownCommands");
            final HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            knownCommands.remove(cmd.getName());
            for (final String alias : cmd.getAliases()) {
                if (knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(p.getName())) {
                    knownCommands.remove(alias);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
