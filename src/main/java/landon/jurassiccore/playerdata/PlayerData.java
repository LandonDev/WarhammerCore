package landon.jurassiccore.playerdata;

import landon.jurassiccore.cooldown.Cooldown;
import landon.jurassiccore.cooldown.CooldownType;
import landon.jurassiccore.expiry.Expiry;
import landon.jurassiccore.expiry.ExpiryType;
import landon.jurassiccore.scoreboard.Scoreboard;
import landon.jurassiccore.timeout.Timeout;
import landon.jurassiccore.timeout.TimeoutType;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {
    private UUID uuid;

    private double balance;

    private boolean payEnabled = true;

    private boolean msgEnabled = true;

    private boolean godMode = false;

    private boolean flying;

    private boolean teleportToPlayer;

    private List<String> ignores = new ArrayList<>();

    private List<Cooldown> cooldowns = new ArrayList<>();

    private List<Expiry> expiries = new ArrayList<>();

    private List<Timeout> timeouts = new ArrayList<>();

    private List<Home> homes = new ArrayList<>();

    private List<Item> items = new ArrayList<>();

    private Location lastLocation;

    private String reply;

    private String warp;

    private String home;

    private int page;

    private UUID teleport;

    private Location teleportLocation;

    private Scoreboard scoreboard;

    public PlayerData(UUID uuid, boolean flying) {
        this.uuid = uuid;
        this.flying = flying;
        byte b;
        int i;
        CooldownType[] arrayOfCooldownType;
        for (i = (arrayOfCooldownType = CooldownType.values()).length, b = 0; b < i; ) {
            CooldownType cooldownType = arrayOfCooldownType[b];
            this.cooldowns.add(new Cooldown(cooldownType));
            b++;
        }
        ExpiryType[] arrayOfExpiryType;
        for (i = (arrayOfExpiryType = ExpiryType.values()).length, b = 0; b < i; ) {
            ExpiryType expiryType = arrayOfExpiryType[b];
            this.expiries.add(new Expiry(expiryType));
            b++;
        }
        TimeoutType[] arrayOfTimeoutType;
        for (i = (arrayOfTimeoutType = TimeoutType.values()).length, b = 0; b < i; ) {
            TimeoutType timeoutType = arrayOfTimeoutType[b];
            this.timeouts.add(new Timeout(timeoutType));
            b++;
        }
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isPayEnabled() {
        return this.payEnabled;
    }

    public void setPayEnabled(boolean payEnabled) {
        this.payEnabled = payEnabled;
    }

    public boolean isMSGEnabled() {
        return this.msgEnabled;
    }

    public void setMSGEnabled(boolean msgEnabled) {
        this.msgEnabled = msgEnabled;
    }

    public boolean hasGodMode() {
        return this.godMode;
    }

    public void setGodMode(boolean godMode) {
        this.godMode = godMode;
    }

    public boolean isFlying() {
        return this.flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public boolean isTeleportToPlayer() {
        return this.teleportToPlayer;
    }

    public void setTeleportToPlayer(boolean teleportToPlayer) {
        this.teleportToPlayer = teleportToPlayer;
    }

    public void addIgnore(String ignore) {
        this.ignores.add(ignore);
    }

    public void removeIgnore(String ignore) {
        this.ignores.remove(ignore);
    }

    public boolean isIgnored(String ignore) {
        return this.ignores.contains(ignore);
    }

    public void setIgnores(List<String> ignores) {
        this.ignores = ignores;
    }

    public List<String> getIgnores() {
        return this.ignores;
    }

    public Cooldown getCooldown(CooldownType type) {
        for (Cooldown cooldown : this.cooldowns) {
            if (cooldown.getType() == type)
                return cooldown;
        }
        return null;
    }

    public Expiry getExpiry(ExpiryType type) {
        for (Expiry expiry : this.expiries) {
            if (expiry.getType() == type)
                return expiry;
        }
        return null;
    }

    public boolean hasPendingExpiry() {
        return (getPendingExpiry() != null);
    }

    public Expiry getPendingExpiry() {
        for (Expiry expiry : this.expiries) {
            if (expiry.getTime() != 0L)
                return expiry;
        }
        return null;
    }

    public Timeout getTimeout(TimeoutType type) {
        for (Timeout timeout : this.timeouts) {
            if (timeout.getType() == type)
                return timeout;
        }
        return null;
    }

    public boolean hasHome(String name) {
        return (getHome(name) != null);
    }

    public Home getHome(String name) {
        for (Home home : this.homes) {
            if (home.getName().equalsIgnoreCase(name))
                return home;
        }
        return null;
    }

    public List<Home> getHomes() {
        return this.homes;
    }

    public void addHome(Home home) {
        this.homes.add(home);
    }

    public void removeHome(Home home) {
        this.homes.remove(home);
    }

    public void removeHome(String name) {
        for (Home home : this.homes) {
            if (home.getName().equalsIgnoreCase(name)) {
                this.homes.remove(home);
                return;
            }
        }
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public void removeItem(Item item) {
        this.items.remove(item);
    }

    public Item getItem(Item searchItem) {
        for (Item item : this.items) {
            if (item.getItem().getEntityId() == searchItem.getItem().getEntityId())
                return item;
        }
        return null;
    }

    public List<Item> getItems() {
        return this.items;
    }

    public Location getLastLocation() {
        return this.lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public String getReply() {
        return this.reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getPendingWarp() {
        return this.warp;
    }

    public void setPendingWarp(String warp) {
        this.warp = warp;
    }

    public String getPendingHome() {
        return this.home;
    }

    public void setPendingHome(String home) {
        this.home = home;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public UUID getTeleport() {
        return this.teleport;
    }

    public void setTeleport(UUID teleport) {
        this.teleport = teleport;
    }

    public Location getTeleportLocation() {
        return this.teleportLocation;
    }

    public void setTeleportLocation(Location teleportLocation) {
        this.teleportLocation = teleportLocation;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }
}
