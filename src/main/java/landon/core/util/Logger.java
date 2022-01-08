package landon.core.util;

import org.bukkit.Bukkit;

public class Logger {
    public void notice(String s, Class<?> c) {
        Bukkit.getLogger().info("[AntiCrash (" + c.getSimpleName() + ")] " + s);
    }

    public void debug(String s, Class<?> c) {
        Bukkit.getLogger().info("(DE) [AntiCrash (" + c.getSimpleName() + ")] " + s);
    }

    public void log(String s, Class<?> c) {
        Bukkit.getLogger().info("[AntiCrash (" + c.getSimpleName() + ")] " + s);
    }

    public void warning(String s, Class<?> c) {
        Bukkit.getLogger().info("[AntiCrash (" + c.getSimpleName() + ")] " + s);
    }

    public void error(String s, Class<?> c) {
        Bukkit.getLogger().info("[AntiCrash (" + c.getSimpleName() + ")] " + s);
    }
}
