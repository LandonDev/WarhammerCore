package net.minelink.ctplus;

import com.massivecraft.factions.P;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class Settings {
    private final CombatTagPlus plugin;

    Settings(CombatTagPlus plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        Configuration defaults = P.p.getConfig().getDefaults();
        defaults.set("disabled-worlds", new ArrayList());
        defaults.set("command-blacklist", new ArrayList());
        defaults.set("command-whitelist", new ArrayList());
    }

    public void update() {
        List<Map<String, Object>> config = new ArrayList<>();
        Path path = Paths.get(P.p.getDataFolder().getAbsolutePath() + File.separator + "config.yml", new String[0]);
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(P.p.getResource("config.yml")));
        for (String key : defaultConfig.getKeys(true)) {
            String oneLineKey = StringUtils.repeat("  ", (key.split(".")).length) + key + ": ";
            Map<String, Object> section = new HashMap<>();
            section.put("key", oneLineKey);
            if (key.equals("config-version")) {
                section.put("value", defaultConfig.get(key));
            } else if (P.p.getConfig().get(key) != null) {
                section.put("value", P.p.getConfig().get(key));
            } else {
                section.put("value", defaultConfig.get(key));
            }
            config.add(section);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(P.p.getResource("config.yml")))) {
            String previous = null;
            List<String> comments = new ArrayList<>();
            String current;
            while ((current = br.readLine()) != null) {
                if (previous != null && previous.matches("(| +)#.*")) {
                    comments.add(previous);
                } else {
                    comments.clear();
                }
                for (Map<String, Object> section : config) {
                    if (section.get("key") == null)
                        continue;
                    if (comments.isEmpty())
                        continue;
                    String key = section.get("key").toString();
                    if (!current.startsWith(key.substring(0, key.length() - 1)))
                        continue;
                    section.put("comments", new ArrayList<>(comments));
                }
                previous = current;
            }
        } catch (IOException e) {
            P.p.getLogger().severe("**CONFIG ERROR**");
            P.p.getLogger().severe("Failed to read from default config within plugin jar.");
            e.printStackTrace();
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, new java.nio.file.OpenOption[0])) {
            for (int i = 0; i < config.size(); i++) {
                Map<String, Object> section = config.get(i);
                if (section.get("key") != null)
                    if (section.get("value") != null) {
                        Object comments = section.get("comments");
                        if (comments != null && comments instanceof List)
                            for (Object o : ((List)comments)) {
                                writer.write(o.toString());
                                writer.newLine();
                            }
                        String key = section.get("key").toString();
                        writer.write(key);
                        Object value = section.get("value");
                        if (value instanceof String) {
                            writer.write("'" + value.toString() + "'");
                        } else if (value instanceof List) {
                            List list = (List) value;
                            int indent = key.length() - key.replace(" ", "").length() - 1;
                            for (Object s : list) {
                                writer.newLine();
                                writer.write(StringUtils.repeat(" ", indent) + "  - '" + s.toString() + "'");
                            }
                        } else {
                            writer.write(value.toString());
                        }
                        if (config.size() > i + 1) {
                            writer.newLine();
                            writer.newLine();
                        }
                    }
            }
        } catch (IOException e) {
            P.p.getLogger().severe("**CONFIG ERROR**");
            P.p.getLogger().severe("Failed to write an updated config to the disk.");
            e.printStackTrace();
        }
        P.p.reloadConfig();
        load();
    }

    public int getConfigVersion() {
        return P.p.getConfig().getInt("config-version", 0);
    }

    public int getLatestConfigVersion() {
        return P.p.getConfig().getDefaults().getInt("config-version", 0);
    }

    public boolean isOutdated() {
        return (getConfigVersion() < getLatestConfigVersion());
    }

    public int getTagDuration() {
        return P.p.getConfig().getInt("tag-duration", 15);
    }

    public String getTagMessage() {
        String message = P.p.getConfig().getString("tag-message", "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getTagUnknownMessage() {
        String message = P.p.getConfig().getString("tag-unknown-message", "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getUntagMessage() {
        String message = P.p.getConfig().getString("untag-message", "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean resetTagOnPearl() {
        return P.p.getConfig().getBoolean("reset-tag-on-pearl");
    }

    public boolean playEffect() {
        return P.p.getConfig().getBoolean("play-effect");
    }

    public boolean alwaysSpawn() {
        return P.p.getConfig().getBoolean("always-spawn");
    }

    public boolean mobTagging() {
        return P.p.getConfig().getBoolean("mob-tagging");
    }

    public int getLogoutWaitTime() {
        return P.p.getConfig().getInt("logout-wait-time", 10);
    }

    public String getLogoutCancelledMessage() {
        String message = P.p.getConfig().getString("logout-cancelled-message", "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getLogoutSuccessMessage() {
        String message = P.p.getConfig().getString("logout-success-message", "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getLogoutPendingMessage() {
        String message = P.p.getConfig().getString("logout-pending-message", "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean instantlyKill() {
        return P.p.getConfig().getBoolean("instantly-kill");
    }

    public boolean untagOnKick() {
        return P.p.getConfig().getBoolean("untag-on-kick");
    }

    public List<String> getUntagOnKickBlacklist() {
        return P.p.getConfig().getStringList("untag-on-kick-blacklist");
    }

    public boolean onlyTagAttacker() {
        return P.p.getConfig().getBoolean("only-tag-attacker");
    }

    public boolean disableSelfTagging() {
        return P.p.getConfig().getBoolean("disable-self-tagging");
    }

    public boolean disableBlockEdit() {
        return P.p.getConfig().getBoolean("disable-block-edit");
    }

    public String getDisableBlockEditMessage() {
        String message = P.p.getConfig().getString("disable-block-edit-message", "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean disableStorageAccess() {
        return P.p.getConfig().getBoolean("disable-storage-access");
    }

    public String getDisableStorageAccessMessage() {
        String message = P.p.getConfig().getString("disable-storage-access-message", "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean disableCreativeTags() {
        return P.p.getConfig().getBoolean("disable-creative-tags");
    }

    public boolean disableEnderpearls() {
        return P.p.getConfig().getBoolean("disable-enderpearls");
    }

    public String getDisableEnderpearlsMessage() {
        String message = P.p.getConfig().getString("disable-enderpearls-message", "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean disableFlying() {
        return P.p.getConfig().getBoolean("disable-flying");
    }

    public String getDisableFlyingMessage() {
        String message = P.p.getConfig().getString("disable-flying-message", "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean disableTeleportation() {
        return P.p.getConfig().getBoolean("disable-teleportation");
    }

    public String getDisableTeleportationMessage() {
        String message = P.p.getConfig().getString("disable-teleportation-message", "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean disableCrafting() {
        return P.p.getConfig().getBoolean("disable-crafting");
    }

    public String getDisableCraftingMessage() {
        String message = P.p.getConfig().getString("disable-crafting-message", "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public int getNpcDespawnTime() {
        return P.p.getConfig().getInt("npc-despawn-time", 60);
    }

    public int getNpcDespawnMillis() {
        return getNpcDespawnTime() * 1000;
    }

    public boolean resetDespawnTimeOnHit() {
        return P.p.getConfig().getBoolean("reset-despawn-time-on-hit");
    }

    public boolean generateRandomName() {
        return P.p.getConfig().getBoolean("generate-random-name");
    }

    public String getRandomNamePrefix() {
        return P.p.getConfig().getString("random-name-prefix");
    }

    public String getKillMessage() {
        String message = P.p.getConfig().getString("kill-message", "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getKillMessageItem() {
        String message = P.p.getConfig().getString("kill-message-item", "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean useBarApi() {
        return P.p.getConfig().getBoolean("barapi");
    }

    public String getBarApiEndedMessage() {
        String message = P.p.getConfig().getString("barapi-ended-message", "&aYou are no longer in combat!");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getBarApiCountdownMessage() {
        String message = P.p.getConfig().getString("barapi-countdown-message", "&eCombatTag: &f{remaining}");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean denySafezone() {
        return P.p.getConfig().getBoolean("deny-safezone");
    }

    public boolean denySafezoneEnderpearl() {
        return P.p.getConfig().getBoolean("deny-safezone-enderpearl");
    }

    public boolean useForceFields() {
        return P.p.getConfig().getBoolean("force-fields");
    }

    public int getForceFieldRadius() {
        return P.p.getConfig().getInt("force-field-radius");
    }

    public String getForceFieldMaterial() {
        return P.p.getConfig().getString("force-field-material");
    }

    public byte getForceFieldMaterialDamage() {
        return (byte) P.p.getConfig().getInt("force-field-material-damage");
    }

    public boolean useFactions() {
        return P.p.getConfig().getBoolean("factions");
    }

    public boolean useTowny() {
        return P.p.getConfig().getBoolean("towny");
    }

    public boolean useWorldGuard() {
        return P.p.getConfig().getBoolean("worldguard");
    }

    public List<String> getDisabledWorlds() {
        return P.p.getConfig().getStringList("disabled-worlds");
    }

    public String getDisabledCommandMessage() {
        String message = P.p.getConfig().getString("disabled-command-message", "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean isCommandBlacklisted(String message) {
        if (message.charAt(0) == '/')
            message = message.substring(1);
        message = message.toLowerCase();
        for (String command : P.p.getConfig().getStringList("command-whitelist")) {
            if (command.equals("*") || message.equals(command) || message.startsWith(command + " "))
                return false;
        }
        for (String command : P.p.getConfig().getStringList("command-blacklist")) {
            if (command.equals("*") || message.equals(command) || message.startsWith(command + " "))
                return true;
        }
        return false;
    }

    public boolean untagOnPluginTeleport() {
        return P.p.getConfig().getBoolean("untag-on-plugin-teleport");
    }

    public String getCommandUntagMessage() {
        return ChatColor.translateAlternateColorCodes('&', P.p.getConfig().getString("command-untag-message"));
    }

    public String getCommandTagMessage() {
        return ChatColor.translateAlternateColorCodes('&', P.p.getConfig().getString("command-tag-message"));
    }

    public String formatDuration(long seconds) {
        List<String> parts = new ArrayList<>();
        for (TimeUnit timeUnit : new TimeUnit[]{TimeUnit.DAYS, TimeUnit.HOURS, TimeUnit.MINUTES, TimeUnit.SECONDS}) {
            long duration = seconds / TimeUnit.SECONDS.convert(1L, timeUnit);
            if (duration > 0L) {
                seconds -= TimeUnit.SECONDS.convert(duration, timeUnit);
                String englishWord = timeUnit.name().toLowerCase(Locale.ENGLISH);
                String durationWord = P.p.getConfig().getString("duration-words." + englishWord, englishWord);
                parts.add(duration + " " + durationWord);
            }
        }
        String formatted = StringUtils.join(parts, ", ");
        if (formatted.contains(", ")) {
            int index = formatted.lastIndexOf(", ");
            StringBuilder builder = new StringBuilder(formatted);
            formatted = builder.replace(index, index + 2, " and ").toString();
        }
        return formatted;
    }
}
