package landon.core.patchapi.patches.ftop.utils;

import java.util.concurrent.TimeUnit;

import landon.core.patchapi.patches.blockvalues.BlockValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class BlockValueLocation {
    private int x;

    private int y;

    private int z;

    private int chunkX;

    private int chunkZ;

    private String worldName;

    private Material blockType;

    private Location bukkitLocation;

    private long placeTime;

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public Material getBlockType() {
        return this.blockType;
    }

    public Location getBukkitLocation() {
        return this.bukkitLocation;
    }

    public void setPlaceTime(long placeTime) {
        this.placeTime = placeTime;
    }

    public long getPlaceTime() {
        return this.placeTime;
    }

    @Deprecated
    public BlockValueLocation(int x, int y, int z, String worldName, Material blockType) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.chunkX = this.x >> 4;
        this.chunkZ = this.z >> 4;
        this.worldName = worldName;
        this.blockType = blockType;
        this.placeTime = 0L;
    }

    public BlockValueLocation(int x, int y, int z, String worldName, Material blockType, long placeTime) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.chunkX = this.x >> 4;
        this.chunkZ = this.z >> 4;
        this.worldName = worldName;
        this.blockType = blockType;
        this.placeTime = placeTime;
    }

    public String string() {
        return this.worldName + "," + this.x + "," + this.y + "," + this.z + "," + this.blockType.name() + "," + this.placeTime;
    }

    public Location getLocation() {
        if (this.bukkitLocation == null &&
                Bukkit.getWorld(this.worldName) != null)
            this.bukkitLocation = new Location(Bukkit.getWorld(this.worldName), this.x, this.y, this.z);
        return this.bukkitLocation;
    }

    public long getMillisecondsSincePlace() {
        return System.currentTimeMillis() - this.placeTime;
    }

    public int getBlockValue() {
        long day = TimeUnit.DAYS.toMillis(1L);
        int baseValue = BlockValue.getBlockValueBase(this.blockType);
        long aliveTime = getMillisecondsSincePlace();
        long days = aliveTime / day;
        double percent = Math.min(100L, Math.max(20L, 20L + days * 20L));
        baseValue = (int)(baseValue * 0.01D * percent);
        return baseValue;
    }
}

