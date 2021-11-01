package landon.warhammercore.patchapi.patches.spawnerfree;

import com.massivecraft.factions.*;
import com.massivecraft.factions.iface.RelationParticipator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import landon.warhammercore.WarhammerCore;
import landon.warhammercore.patchapi.patches.ftop.utils.SpawnerLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.Plugin;

final class SpawnerFeeListener implements Listener {
    HashMap<Block, Long> blockPlaceEvent = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getBlock().getType() == Material.MOB_SPAWNER) {
            SpawnerFee.playerSpawners.put(convertBlockToString(e.getBlock()), convertBlockToSpawnerLocation(e.getBlock()));
            this.blockPlaceEvent.put(e.getBlock(), Long.valueOf(System.currentTimeMillis()));
            e.getPlayer().sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + "(!) " + ChatColor.YELLOW + "You will be fined $" + getMiningPrice(e.getPlayer(), e.getBlockPlaced()) + " for collecting this spawner in the future unless you break it within the next " + ChatColor.BOLD + "5 minutes.");
            FPlayer fp = (FPlayer)FPlayers.i.get((OfflinePlayer)e.getPlayer());
            Faction f = Board.getFactionAt(new FLocation(e.getBlockPlaced()));
            if (f.isNone())
                e.getPlayer().sendMessage(ChatColor.GRAY + "Mob Spawners placed in Wilderness spawn 60% less monsters than those placed in claimed land.");
            if (f.isNormal() && fp.getRelationTo((RelationParticipator)f).isMember())
                e.getPlayer().sendMessage(ChatColor.GRAY + "Mob Spawners placed in your claims gradually add value to your faction's /f top over time.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.MOB_SPAWNER &&
                SpawnerFee.playerSpawners.containsKey(convertBlockToString(e.getBlock()))) {
            if (this.blockPlaceEvent.containsKey(e.getBlock()) &&
                    System.currentTimeMillis() - ((Long)this.blockPlaceEvent.get(e.getBlock())).longValue() <= 300000L) {
                SpawnerFee.playerSpawners.remove(convertBlockToString(e.getBlock()));
                return;
            }
            Player p = e.getPlayer();
            int cost = getMiningPrice(p, e.getBlock());
            int baseMiningCost = getMiningPriceNoSpawnerCount(p, e.getBlock());
            CreatureSpawner ms = (CreatureSpawner)e.getBlock().getState();
            int spawnerCount = 1;
            WarhammerCore.economy.withdrawPlayer((OfflinePlayer)p, cost);
            p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "- $" + cost + ChatColor.GRAY + "($" + baseMiningCost + "x" + spawnerCount + " spawners)");
            p.sendMessage(ChatColor.GRAY + "This 10% value fee is applied whenever you collect a spawner.");
            p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 0.7F);
            SpawnerFee.playerSpawners.remove(convertBlockToString(e.getBlock()));
            this.blockPlaceEvent.remove(e.getBlock());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreakAttempt(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.MOB_SPAWNER && SpawnerFee.playerSpawners.containsKey(convertBlockToString(e.getBlock()))) {
            if (this.blockPlaceEvent.containsKey(e.getBlock()) &&
                    System.currentTimeMillis() - ((Long)this.blockPlaceEvent.get(e.getBlock())).longValue() <= 300000L)
                return;
            Player p = e.getPlayer();
            int cost = getMiningPrice(p, e.getBlock());
            int baseMiningCost = getMiningPriceNoSpawnerCount(p, e.getBlock());
            CreatureSpawner ms = (CreatureSpawner)e.getBlock().getState();
            int spawnerCount = 1;
            if (!WarhammerCore.economy.has((OfflinePlayer)p, cost)) {
                e.setCancelled(true);
                e.setExpToDrop(0);
                p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "You do not have enough $ to collect this spawner.");
                p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "COST: " + ChatColor.GRAY + "$" + cost + " (" + ChatColor.RED + "$" + baseMiningCost + "x" + spawnerCount + " spawners" + ChatColor.GRAY + ")");
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityExplode(EntityExplodeEvent e) {
        final List<Location> blocksToCheck = new ArrayList<>();
        for (Block b : e.blockList()) {
            if (b.getType() == Material.MOB_SPAWNER)
                blocksToCheck.add(b.getLocation());
        }
        Bukkit.getScheduler().runTaskLater(P.p, new Runnable() {
            public void run() {
                for (Location l : blocksToCheck) {
                    if (l.getBlock().getType() != Material.MOB_SPAWNER) {
                        String s = SpawnerFeeListener.convertBlockToString(l.getBlock());
                        Bukkit.getLogger().info("[Core (SpawnerFee)] Removing " + s + " from playerSpawners list due to explosion.");
                        SpawnerFee.playerSpawners.remove(s);
                    }
                }
            }
        },  5L);
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent e) {
        if (e.getBlock().getType() == Material.MOB_SPAWNER && SpawnerFee.playerSpawners.containsKey(convertBlockToString(e.getBlock()))) {
            if (this.blockPlaceEvent.containsKey(e.getBlock()) &&
                    System.currentTimeMillis() - ((Long)this.blockPlaceEvent.get(e.getBlock())).longValue() <= 300000L)
                return;
            Player p = e.getPlayer();
            int cost = getMiningPrice(p, e.getBlock());
            int baseMiningCost = getMiningPriceNoSpawnerCount(p, e.getBlock());
            CreatureSpawner ms = (CreatureSpawner)e.getBlock().getState();
            int spawnerCount = 1;
            e.getPlayer().sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + "(!) " + ChatColor.YELLOW + "You will be fined $" +
                    getMiningPrice(e.getPlayer(), e.getBlock()) + " ($" + baseMiningCost + "x" + spawnerCount + " spawners) for collecting this spawner.");
            if (getMiningPriceModifier(e.getPlayer()) == 1.0D)
                e.getPlayer().sendMessage(ChatColor.GRAY + "Unlock or purchase a rank at " + ChatColor.UNDERLINE + "buy.cosmicpvp.com" + ChatColor.GRAY + " for smaller fees.");
        }
    }

    private int getMiningPrice(Player p, Block b) {
        if (!SpawnerFee.spawnerFee)
            return 0;
        if (b.getType() == Material.MOB_SPAWNER) {
            CreatureSpawner ms = (CreatureSpawner)b.getState();
            EntityType spawned = ms.getSpawnedType();
            return (int)(getSpawnerFeeBase(spawned) * getMiningPriceModifier(p)) * 1;
        }
        return 0;
    }

    private int getMiningPriceNoSpawnerCount(Player p, Block b) {
        if (!SpawnerFee.spawnerFee)
            return 0;
        if (b.getType() == Material.MOB_SPAWNER) {
            CreatureSpawner ms = (CreatureSpawner)b.getState();
            EntityType spawned = ms.getSpawnedType();
            return (int)(getSpawnerFeeBase(spawned) * getMiningPriceModifier(p));
        }
        return 0;
    }

    public static int getSpawnerFeeBase(EntityType et) {
        return P.p.getConfig().getInt("patches.spawner_fee." + et.toString().toLowerCase());
    }

    private double getMiningPriceModifier(Player p) {
        if (p.hasPermission("core.patch.spawnerfee.modifier.6"))
            return 0.6D;
        if (p.hasPermission("core.patch.spawnerfee.modifier.7"))
            return 0.7D;
        if (p.hasPermission("core.patch.spawnerfee.modifier.75"))
            return 0.75D;
        if (p.hasPermission("core.patch.spawnerfee.modifier.8"))
            return 0.8D;
        if (p.hasPermission("core.patch.spawnerfee.modifier.9"))
            return 0.9D;
        return 1.0D;
    }

    public static String convertBlockToString(Block b) {
        return b.getWorld().getName() + "," + b.getX() + "," + b.getY() + "," + b.getZ();
    }

    public static SpawnerLocation convertBlockToSpawnerLocation(Block b) {
        return new SpawnerLocation(b);
    }
}
