package landon.warhammercore.patchapi.patches.combattag;

import com.massivecraft.factions.P;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import landon.warhammercore.WarhammerCore;
import landon.warhammercore.patchapi.UHCFPatch;
import landon.warhammercore.patchapi.patches.fpoints.utils.DecimalFormatType;
import landon.warhammercore.patchapi.patches.fpoints.utils.NumberUtils;
import landon.warhammercore.patchapi.patches.fupgrades.FactionUpgradeAPI;
import landon.warhammercore.patchapi.patches.fupgrades.struct.FactionUpgrade;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class CombatLog extends UHCFPatch {
    private static WorldGuardPlugin wg = null;

    protected static boolean duel_plugin = false;

    protected static boolean mask_plugin = false;

    protected static boolean block_enderpearls = false;

    protected static long tagDuration;

    protected static boolean cosmicPVP = false, displayOnScoreboard = false;

    public CombatLog(Plugin p) {
        super(p);
    }

    public void enable() {
        wg = hookWorldGuard();
        duel_plugin = (new File("plugins/MCLegendsDuels.jar")).exists();
        mask_plugin = (new File("plugins/CosmicMasks.jar")).exists();
        block_enderpearls = P.p.getConfig().getBoolean("patches.combat_log.block_enderpearls");
        tagDuration = P.p.getConfig().getLong("patches.combat_log.combat_duration");
        displayOnScoreboard = getBukkitPlugin().getConfig().getBoolean("patches.combat_log.show_on_scoreboard", false);
        cosmicPVP = (new File("plugins/CosmicCustomTNT.jar")).exists();
        for (String pName : getBukkitPlugin().getConfig().getStringList("settings.combatLog.playersToKill"))
            CombatLogLogoutListener.playersToKill.add(pName);
        registerListener(new CombatLogCombatListener());
        registerListener(new CombatLogLogoutListener(this));
        P.p.getCommand("logout").setExecutor(new CommandLogout());
        registerTask((new DespawnNPCTask()).runTaskTimer((Plugin)P.p, 20L, 20L), DespawnNPCTask.class);
        registerTask((new CombatExpireTask()).runTaskTimer((Plugin)P.p, 10L, 10L), CombatExpireTask.class);
    }

    public void disable() {
        for (Player pl : Bukkit.getOnlinePlayers())
            pl.removeMetadata("cl_combat", (Plugin)P.p);
        cleanNPCs();
        getBukkitPlugin().getConfig().set("settings.combatLog.playersToKill", new ArrayList<>(CombatLogLogoutListener.playersToKill));
        getBukkitPlugin().saveConfig();
        wg = null;
    }

    private void cleanNPCs() {
        for (NPC n : CombatLogLogoutListener.combatNPCs.values()) {
            n.despawn();
            n.destroy();
            CitizensAPI.getNPCRegistry().deregister(n);
        }
        Iterator<NPC> i = CitizensAPI.getNPCRegistry().iterator();
        while (i.hasNext()) {
            NPC n = i.next();
            if (n.getEntity() != null && n.getEntity().hasMetadata("combatNPC_PlayerName")) {
                n.despawn();
                n.destroy();
                CitizensAPI.getNPCRegistry().deregister(n);
            }
        }
    }

    protected static WorldGuardPlugin getWorldGuard() {
        return wg;
    }

    private static WorldGuardPlugin hookWorldGuard() {
        Plugin plugin = P.p.getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin == null || !(plugin instanceof WorldGuardPlugin))
            return null;
        return (WorldGuardPlugin)plugin;
    }

    protected static boolean isPvPDisabled(Location l) {
        try {
            Class<?> bukkitUtil = getWorldGuard().getClass().getClassLoader().loadClass("com.sk89q.worldguard.bukkit.BukkitUtil");
            Method toVector = bukkitUtil.getMethod("toVector", new Class[] { Block.class });
            Vector blockVector = (Vector)toVector.invoke(null, new Object[] { l.getBlock() });
            List<String> regionSet = getWorldGuard().getGlobalRegionManager().get(l.getWorld()).getApplicableRegionsIDs(blockVector);
            if (regionSet == null || regionSet.size() < 1)
                try {
                    return ((StateFlag.State)getWorldGuard().getGlobalRegionManager().get(l.getWorld()).getRegion("__global__").getFlags().get(DefaultFlag.PVP) == StateFlag.State.DENY);
                } catch (Exception err) {
                    return false;
                }
            boolean return_flag = false;
            int return_priority = -1;
            for (String region : regionSet) {
                if (getWorldGuard().getGlobalRegionManager().get(l.getWorld()).getRegion(region).getFlags().containsKey(DefaultFlag.PVP)) {
                    StateFlag.State pvp_flag = (StateFlag.State)getWorldGuard().getGlobalRegionManager().get(l.getWorld()).getRegion(region).getFlags().get(DefaultFlag.PVP);
                    int region_priority = getWorldGuard().getGlobalRegionManager().get(l.getWorld()).getRegion(region).getPriority();
                    if (return_priority == -1) {
                        return_flag = (pvp_flag == StateFlag.State.DENY);
                        return_priority = region_priority;
                        continue;
                    }
                    if (region_priority > return_priority) {
                        return_flag = (pvp_flag == StateFlag.State.DENY);
                        return_priority = region_priority;
                    }
                }
            }
            return return_flag;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean inCombat(Player pl) {
        try {
            List<MetadataValue> val = pl.getMetadata("cl_combat");
            if (val != null && !val.isEmpty() &&
                    System.currentTimeMillis() - ((MetadataValue)val.get(0)).asLong() <= getCombatTagDuration(pl))
                return true;
        } catch (Exception err) {
            err.printStackTrace();
            pl.setMetadata("cl_combat", (MetadataValue)new FixedMetadataValue((Plugin)P.p, Long.valueOf(System.currentTimeMillis())));
        }
        return false;
    }

    public static long getTimeLeftInCombat(Player pl) {
        try {
            List<MetadataValue> values = pl.getMetadata("cl_combat");
            if (values != null && values.size() > 0) {
                Long time = Long.valueOf(((MetadataValue)values.get(0)).asLong());
                long duration = getCombatTagDuration(pl);
                if (System.currentTimeMillis() - time.longValue() <= duration)
                    return duration - System.currentTimeMillis() - time.longValue();
            }
        } catch (Exception err) {
            err.printStackTrace();
            pl.setMetadata("cl_combat", (MetadataValue)new FixedMetadataValue((Plugin)P.p, Long.valueOf(System.currentTimeMillis())));
        }
        return 0L;
    }

    public static long getCombatTagDuration(Player player) {
        int combatDuration = FactionUpgradeAPI.getPerkLevel(player, FactionUpgrade.HEROIC_COMBAT_TAG);
        long toRetr = tagDuration;
        if (combatDuration == 1) {
            toRetr -= 250L;
        } else if (combatDuration == 2) {
            toRetr -= 500L;
        } else if (combatDuration == 3) {
            toRetr -= 1000L;
        } else if (combatDuration == 4) {
            toRetr -= 2000L;
        }
        if (player.hasMetadata("affectedByJokerMask") && (
                (MetadataValue)player.getMetadata("affectedByJokerMask").get(0)).asLong() > MinecraftServer.currentTick)
            toRetr += 4000L;
        return toRetr;
    }

    public static synchronized void flagCombat(Player pl) {
        boolean wasInCombat = inCombat(pl);
        pl.setMetadata("cl_combat", (MetadataValue)new FixedMetadataValue(P.p, Long.valueOf(System.currentTimeMillis())));
        if (!wasInCombat) {
            pl.setMetadata("cl_combat_monitor_task", (MetadataValue)new FixedMetadataValue((Plugin)P.p, Boolean.valueOf(CombatExpireTask.combatTagged.add(new WeakReference<>(pl)))));
            pl.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "You have entered combat. Do not log out for " + NumberUtils.format(getCombatTagDuration(pl) / 1000.0D, DecimalFormatType.SECONDS) + "s.");
        }
        pl.setAllowFlight(false);
        pl.setFlying(false);
        if (pl.getOpenInventory() != null && (pl
                .getOpenInventory().getTopInventory().getName().startsWith("Shop") || pl.getOpenInventory().getTopInventory().getName().equals("Auction House")))
            pl.closeInventory();
        CombatTagEvent event = new CombatTagEvent(pl, wasInCombat);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public static void unflagCombat(Player pl) {
        if (pl.hasMetadata("cl_combat_monitor_task")) {
            pl.removeMetadata("cl_combat_monitor_task", (Plugin)P.p);
            pl.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "(!)" + ChatColor.GREEN + " You have left combat. You may now safely logout.");
            if (pl.isOp())
                pl.setAllowFlight(true);
            pl.updateInventory();
        }
    }
}
