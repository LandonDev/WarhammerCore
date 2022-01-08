package landon.core.patchapi.patches.fupgrades.listeners;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Relation;

import landon.core.patchapi.patches.fupgrades.FactionUpgradeAPI;
import landon.core.patchapi.patches.fupgrades.struct.FactionUpgrade;
import net.minecraft.server.v1_8_R3.BlockCrops;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class PerkListener implements Listener {
    public PerkListener() {
        FurnaceRecipe recipe = new FurnaceRecipe(new ItemStack(Material.TNT), Material.SULPHUR);
        Bukkit.addRecipe((Recipe)recipe);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void EXPHarvest(EntityDeathEvent e) {
        if (e.getDroppedExp() <= 0)
            return;
        FLocation fl = new FLocation(e.getEntity().getLocation());
        Faction f = Board.getFactionAt(fl);
        if (f.isNormal()) {
            int xpHarvestTier = FactionUpgradeAPI.getPerkLevel(f, FactionUpgrade.EXP_HARVEST);
            if (xpHarvestTier > 0) {
                double multi = 1.0D;
                if (xpHarvestTier == 1)
                    multi = 1.05D;
                if (xpHarvestTier == 2)
                    multi = 1.15D;
                if (xpHarvestTier == 3)
                    multi = 1.25D;
                e.setDroppedExp((int)(e.getDroppedExp() * multi));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void HomeFieldAdvantage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player)e.getEntity();
            float homeFieldAdvTier = FactionUpgradeAPI.getPerkLevel(p, FactionUpgrade.HOME_ADVANTAGE);
            if (homeFieldAdvTier > 0.0F) {
                FPlayer fp = (FPlayer)FPlayers.i.get((OfflinePlayer)p);
                Relation r = fp.getRelationToLocation();
                if (r.isAtLeast(Relation.ALLY))
                    e.setDamage(e.getDamage() * (1.0D - (homeFieldAdvTier * 0.1F)));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void MonsterFarm(CreatureSpawnEvent e) {
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER)
            return;
        Faction f = FactionChunkOwnershipCache.getCachedFactionChunkOwnership(e.getLocation());
        if (f == null) {
            FLocation fl = new FLocation(e.getEntity().getLocation());
            f = Board.getFactionAt(fl);
        }
        int monsterFarmTier = FactionUpgradeAPI.getPerkLevel(f, FactionUpgrade.MONSTER_FARM);
        if (monsterFarmTier > 0) {
            double doubleSpawnChance = 0.0D;
            if (monsterFarmTier == 1)
                doubleSpawnChance = 0.05D;
            if (monsterFarmTier == 2)
                doubleSpawnChance = 0.15D;
            if (monsterFarmTier == 3)
                doubleSpawnChance = 0.25D;
            if (monsterFarmTier == 4)
                doubleSpawnChance = 0.5D;
            if (Math.random() < doubleSpawnChance)
                e.getEntity().setMetadata("monsterAmount", (MetadataValue)new FixedMetadataValue(P.p, Integer.valueOf(e.getEntity().hasMetadata("monsterAmount") ? (((MetadataValue)e.getEntity().getMetadata("monsterAmount").get(0)).asInt() + 2) : 2)));
        }
    }

    @EventHandler
    public void NaturalGrowth(BlockGrowEvent e) {
        FLocation fl = new FLocation(e.getBlock().getLocation());
        Faction f = Board.getFactionAt(fl);
        int naturalGrowthTier = FactionUpgradeAPI.getPerkLevel(f, FactionUpgrade.NATURAL_GROWTH);
        if (naturalGrowthTier > 0) {
            double d = 0.0D;
            if (naturalGrowthTier == 1)
                d = 0.25D;
            if (naturalGrowthTier == 1)
                d = 0.5D;
            if (naturalGrowthTier == 1)
                d = 1.0D;
            naturalGrowthTier = (int)(naturalGrowthTier / 3.5D);
            if (Math.random() < naturalGrowthTier) {
                Block b = e.getBlock();
                CraftWorld cw = (CraftWorld)b.getWorld();
                IBlockData nmsBlock = cw.getHandle().getType(new BlockPosition(b.getX(), b.getY(), b.getZ()));
                if (nmsBlock instanceof BlockCrops) {
                    BlockCrops bc = (BlockCrops)nmsBlock;
                    bc.u();
                }
            }
        }
    }

    @EventHandler
    public void WellFed(FoodLevelChangeEvent e) {
        Player p = (Player)e.getEntity();
        if (e.getFoodLevel() == p.getFoodLevel() - 1) {
            int level = FactionUpgradeAPI.getPerkLevelIfAllowed(p, FactionUpgrade.HEROIC_WELL_FED);
            if (level > 0) {
                double chance = (level == 1) ? 0.3D : ((level == 2) ? 0.35D : ((level == 3) ? 0.45D : ((level == 4) ? 0.5D : 0.0D)));
                if (Math.random() < chance) {
                    e.setCancelled(true);
                    p.setSaturation(Math.max(0.0F, p.getSaturation() * (1.0F - (float)chance)));
                }
                return;
            }
            int wellFedPerk = FactionUpgradeAPI.getPerkLevel(p, FactionUpgrade.WELL_FED);
            if (wellFedPerk > 0) {
                double skipHungerChance = 0.0D;
                if (wellFedPerk == 1)
                    skipHungerChance = 0.05D;
                if (wellFedPerk == 2)
                    skipHungerChance = 0.1D;
                if (wellFedPerk == 3)
                    skipHungerChance = 0.15D;
                if (wellFedPerk == 4)
                    skipHungerChance = 0.25D;
                if (Math.random() < skipHungerChance) {
                    e.setCancelled(true);
                    p.setSaturation(Math.max(0.0F, p.getSaturation() * (1.0F - (float)skipHungerChance)));
                }
            }
        }
    }
}
