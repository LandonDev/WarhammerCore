package net.minelink.ctplus.listener;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.massivecraft.factions.P;
import net.minelink.ctplus.CombatTagPlus;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ForceFieldListener implements Listener {
    private static final List<BlockFace> ALL_DIRECTIONS = (List<BlockFace>) ImmutableList.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    private final CombatTagPlus plugin;

    private final Map<UUID, Set<Location>> previousUpdates = new HashMap<>();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor((new ThreadFactoryBuilder()).setNameFormat("CombatTagPlus ForceField Thread").build());

    public ForceFieldListener(CombatTagPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void shutdown(PluginDisableEvent event) {
        if (event.getPlugin() != P.p)
            return;
        this.executorService.shutdown();
        try {
            this.executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException interruptedException) {
        }
        for (UUID uuid : this.previousUpdates.keySet()) {
            Player player = this.plugin.getPlayerCache().getPlayer(uuid);
            if (player == null)
                continue;
            for (Location location : this.previousUpdates.get(uuid)) {
                Block block = location.getBlock();
                player.sendBlockChange(location, block.getType(), block.getData());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void updateViewedBlocks(PlayerMoveEvent event) {
        if (!this.plugin.getSettings().useForceFields())
            return;
        Location t = event.getTo();
        Location f = event.getFrom();
        if (t.getBlockX() == f.getBlockX() && t.getBlockY() == f.getBlockY() && t
                .getBlockZ() == f.getBlockZ())
            return;
        final Player player = event.getPlayer();
        this.executorService.submit(new Runnable() {
            public void run() {
                Set<Location> removeBlocks;
                UUID uuid = player.getUniqueId();
                if (!ForceFieldListener.this.plugin.getPlayerCache().isOnline(uuid)) {
                    ForceFieldListener.this.previousUpdates.remove(uuid);
                    return;
                }
                Set<Location> changedBlocks = ForceFieldListener.this.getChangedBlocks(player);
                Material forceFieldMaterial = Material.getMaterial(ForceFieldListener.this.plugin.getSettings().getForceFieldMaterial());
                byte forceFieldMaterialDamage = ForceFieldListener.this.plugin.getSettings().getForceFieldMaterialDamage();
                if (ForceFieldListener.this.previousUpdates.containsKey(uuid)) {
                    removeBlocks = (Set<Location>) ForceFieldListener.this.previousUpdates.get(uuid);
                } else {
                    removeBlocks = new HashSet<>();
                }
                for (Location location : changedBlocks) {
                    player.sendBlockChange(location, forceFieldMaterial, forceFieldMaterialDamage);
                    removeBlocks.remove(location);
                }
                for (Location location : removeBlocks) {
                    Block block = location.getBlock();
                    player.sendBlockChange(location, block.getType(), block.getData());
                }
                ForceFieldListener.this.previousUpdates.put(uuid, changedBlocks);
            }
        });
    }

    private Set<Location> getChangedBlocks(Player player) {
        Set<Location> locations = new HashSet<>();
        if (!this.plugin.getTagManager().isTagged(player.getUniqueId()))
            return locations;
        int r = this.plugin.getSettings().getForceFieldRadius();
        Location l = player.getLocation();
        Location loc1 = l.clone().add(r, 0.0D, r);
        Location loc2 = l.clone().subtract(r, 0.0D, r);
        int topBlockX = (loc1.getBlockX() < loc2.getBlockX()) ? loc2.getBlockX() : loc1.getBlockX();
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX()) ? loc2.getBlockX() : loc1.getBlockX();
        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ()) ? loc2.getBlockZ() : loc1.getBlockZ();
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ()) ? loc2.getBlockZ() : loc1.getBlockZ();
        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                Location location = new Location(l.getWorld(), x, l.getY(), z);
                if (!this.plugin.getHookManager().isPvpEnabledAt(location))
                    if (isPvpSurrounding(location))
                        for (int i = -r; i < r; i++) {
                            Location loc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
                            loc.setY(loc.getY() + i);
                            if (loc.getBlock().getType().equals(Material.AIR))
                                locations.add(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                        }
            }
        }
        return locations;
    }

    private boolean isPvpSurrounding(Location loc) {
        for (BlockFace direction : ALL_DIRECTIONS) {
            if (this.plugin.getHookManager().isPvpEnabledAt(loc.getBlock().getRelative(direction).getLocation()))
                return true;
        }
        return false;
    }
}
