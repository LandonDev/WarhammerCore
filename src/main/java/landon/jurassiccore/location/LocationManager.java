package landon.jurassiccore.location;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import landon.jurassiccore.JurassicCore;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocationManager {
    private JurassicCore instance;

    public LocationManager(JurassicCore instance) {
        this.instance = instance;
    }

    public void onDisable() {
        this.instance.getFileManager().getConfig(new File(this.instance.getDataFolder(), "locations.yml")).saveFile();
    }

    public void setLocation(FileConfiguration configLoad, String path, Location loc) {
        configLoad.set(String.valueOf(path) + ".world", loc.getWorld().getName());
        configLoad.set(String.valueOf(path) + ".x", Double.valueOf(loc.getX()));
        configLoad.set(String.valueOf(path) + ".y", Double.valueOf(loc.getY()));
        configLoad.set(String.valueOf(path) + ".z", Double.valueOf(loc.getZ()));
        configLoad.set(String.valueOf(path) + ".yaw", Float.valueOf(loc.getYaw()));
        configLoad.set(String.valueOf(path) + ".pitch", Float.valueOf(loc.getPitch()));
    }

    public Location getLocation(FileConfiguration configLoad, String path) {
        Location loc = null;
        if (configLoad.contains(path)) {
            String w = configLoad.getString(String.valueOf(path) + ".world");
            double x = configLoad.getDouble(String.valueOf(path) + ".x");
            double y = configLoad.getDouble(String.valueOf(path) + ".y");
            double z = configLoad.getDouble(String.valueOf(path) + ".z");
            double yaw = configLoad.getDouble(String.valueOf(path) + ".yaw");
            double pitch = configLoad.getDouble(String.valueOf(path) + ".pitch");
            loc = new Location(Bukkit.getServer().getWorld(w), x, y, z);
            loc.setYaw((float) yaw);
            loc.setPitch((float) pitch);
        } else {
            return null;
        }
        return loc;
    }

    public List<String> getRegionNames(WorldGuardPlugin worldGuard, Location location, boolean hasPvP) {
        List<String> regionNames = new ArrayList<>();
        RegionManager regionManager = worldGuard.getRegionManager(location.getWorld());
        ApplicableRegionSet regionsAtLocation = regionManager.getApplicableRegions(location);
        if (hasPvP && regionsAtLocation.allows(DefaultFlag.PVP))
            return regionNames;
        for (ProtectedRegion region : regionsAtLocation)
            regionNames.add(region.getId());
        return regionNames;
    }

    public Location getWildernessLocation() {
        Random random = new Random();
        FileConfiguration configLoad = this.instance.getFileManager()
                .getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration();
        double radius = configLoad.getInt("Wilderness.Radius.Min") + random.nextDouble() * (
                configLoad.getInt("Wilderness.Radius.Max") - configLoad.getInt("Wilderness.Radius.Min"));
        double radians = random.nextDouble() * 2.0D * Math.PI;
        double x = radius * Math.cos(radians);
        double y = radius * Math.sin(radians);
        World world = Bukkit.getWorld(configLoad.getString("Wilderness.World"));
        Location location = new Location(world, x, 0.0D, y);
        Chunk chunk = world.getChunkAt(location);
        if (!chunk.isLoaded())
            chunk.load();
        location.setX(Math.floor(location.getX()) + 0.5D);
        location.setY(world.getHighestBlockAt(location).getLocation().getY());
        location.setZ(Math.floor(location.getZ()) + 0.5D);
        return location;
    }
}
