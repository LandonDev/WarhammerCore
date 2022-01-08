package landon.core.util.cooldown;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class CooldownManager {
    private JavaPlugin main;

    private HashMap<UUID, HashMap<String, Long>> cooldowns = new HashMap<>();

    private CooldownManager() {}

    public CooldownManager(JavaPlugin main) {
        this.main = main;
    }

    HashMap<UUID, HashMap<String, Long>> getCooldowns() {
        return this.cooldowns;
    }

    void setCooldowns(HashMap<UUID, HashMap<String, Long>> cooldowns) {
        this.cooldowns = cooldowns;
    }

    public boolean isOnCooldown(UUID uuid, String id) {
        return (System.currentTimeMillis() < getUserCooldown(uuid, id));
    }

    public long getUserCooldown(UUID uuid, String id) {
        HashMap<String, Long> userCooldowns = this.cooldowns.get(uuid);
        if (userCooldowns == null)
            return 0L;
        if (userCooldowns.containsKey(id)) {
            long cooldown = ((Long)userCooldowns.get(id)).longValue();
            if (cooldown <= 0L) {
                userCooldowns.remove(id);
                return 0L;
            }
            return cooldown;
        }
        return 0L;
    }

    public void setUserCooldown(UUID player, String id, TimeUnit timeUnit, long cooldown) {
        long cooldownEnd = System.currentTimeMillis() + timeUnit.toMillis(cooldown);
        HashMap<String, Long> userCooldowns = this.cooldowns.get(player);
        if (userCooldowns == null) {
            HashMap<String, Long> newCooldowns = new HashMap<>();
            this.cooldowns.put(player, newCooldowns);
            userCooldowns = newCooldowns;
        }
        userCooldowns.put(id, Long.valueOf(cooldownEnd));
    }

    public void setUserCooldown(UUID player, String id, TimeUnit timeUnit, long cooldown, BiConsumer<UUID, String> function) {
        long cooldownEnd = System.currentTimeMillis() + timeUnit.toMillis(cooldown);
        HashMap<String, Long> userCooldowns = this.cooldowns.get(player);
        if (userCooldowns == null) {
            HashMap<String, Long> newCooldowns = new HashMap<>();
            this.cooldowns.put(player, newCooldowns);
            userCooldowns = newCooldowns;
        }
        userCooldowns.put(id, Long.valueOf(cooldownEnd));
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.main, () -> function.accept(player, id), timeUnit.toMillis(cooldown) / 15L);
    }

    public String getCooldownFormatted(UUID player, String id) {
        long secondsLeftOnCooldown = (this.getUserCooldown(player, id) - System.currentTimeMillis()) / 1000;
        int days = (int) TimeUnit.SECONDS.toDays(secondsLeftOnCooldown);
        long hours = TimeUnit.SECONDS.toHours(secondsLeftOnCooldown) - (days *24);
        long minutes = TimeUnit.SECONDS.toMinutes(secondsLeftOnCooldown) - (TimeUnit.SECONDS.toHours(secondsLeftOnCooldown)* 60);
        long seconds = TimeUnit.SECONDS.toSeconds(secondsLeftOnCooldown) - (TimeUnit.SECONDS.toMinutes(secondsLeftOnCooldown) *60);
        String cooldownPlaceholder = null;
        if(days != 0) {
            cooldownPlaceholder = days + "d " + hours + "h " + minutes + "m " + seconds + "s";
        } else if(hours != 0) {
            cooldownPlaceholder = hours + "h " + minutes + "m " + seconds + "s";
        } else if(minutes != 0) {
            cooldownPlaceholder = minutes + "m " + seconds + "s";
        } else {
            cooldownPlaceholder = seconds + "s";
        }
        return cooldownPlaceholder;
    }
}

