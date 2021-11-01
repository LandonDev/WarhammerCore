package landon.warhammercore.patchapi.patches.fpoints.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

import com.massivecraft.factions.util.reflection.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerUtil {
    static Method method = ReflectionUtils.getMethod(Bukkit.getServer().getClass(), "getOnlinePlayers");

    public static Player getPlayerFromString(String name) {
        if (name.length() > 16 && name.contains("-"))
            try {
                UUID uuid = UUID.fromString(name);
                return Bukkit.getPlayer(uuid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return Bukkit.getPlayer(name);
    }

    public static Collection<? extends Player> getOnlinePlayers() {
        try {
            Object retr = method.invoke(Bukkit.getServer(), new Object[0]);
            if (retr instanceof Player[]) {
                Player[] players = (Player[])retr;
                return (Collection<? extends Player>)Arrays.<Player>stream(players).collect(Collectors.toList());
            }
            return (Collection<? extends Player>)retr;
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().info("Unable to retreive online players from getOnlinePlayers reflection call!");
            return new HashSet<>();
        }
    }
}
