package landon.warhammercore.patchapi.patches.combattag;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.Relation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import landon.warhammercore.patchapi.patches.anticrash.AntiCrash;
import landon.warhammercore.patchapi.patches.fpoints.utils.FactionUtils;
import landon.warhammercore.util.MinecraftThreadMonitor;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class CombatLogLogoutListener implements Listener {
    private PlayerLogoutMonitorTask logoutMonitor;

    private CombatLog cl = null;

    public static boolean spawnNPC = false;

    public static ConcurrentHashMap<String, NPC> combatNPCs;

    public static HashMap<String, ItemStack[]> combatNPCs_loot;

    protected static HashSet<String> playersToKill = new HashSet<>();

    CombatLogLogoutListener(CombatLog cl) {
        this.logoutMonitor = new PlayerLogoutMonitorTask();
        combatNPCs = new ConcurrentHashMap<>();
        combatNPCs_loot = (HashMap)new HashMap<>();
        this.cl = cl;
        this.cl.registerTask(this.logoutMonitor.runTaskTimer(P.p, 5L, 5L), CombatLogLogoutListener.class);
        spawnNPC = cl.getBukkitPlugin().getConfig().getBoolean("settings.combat_log.spawnNPC");
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (this.logoutMonitor.logouts++ > 2) {
            Bukkit.getLogger().info("Skipping combatLog check for " + e.getPlayer().getName() + " due to >2 logouts in 5 ticks.");
            return;
        }
        if (e.getPlayer().isDead() || e.getPlayer().getHealth() <= 0.0D) {
            Bukkit.getLogger().info("Skipping combatLog check for " + e.getPlayer().getName() + " because their entity is dead.");
            return;
        }
        if (e.getPlayer().hasMetadata("lastDeathEvent") && MinecraftServer.currentTick - ((MetadataValue)e.getPlayer().getMetadata("lastDeathEvent").get(0)).asLong() < 20L) {
            Bukkit.getLogger().info("Skipping combatLog check for " + e.getPlayer().getName() + " because they had a death event in last 20 ticks.");
            return;
        }
        if (e.getPlayer().getWorld().getName().startsWith("dungeon_")) {
            Bukkit.getLogger().info("Skipping combatLog check for " + e.getPlayer().getName() + " due to worldName: " + e.getPlayer().getWorld().getName());
            return;
        }
        if (!AntiCrash.shutting_down && !AntiCrash.crash && MinecraftThreadMonitor.non_response <= 0 && CombatLog.inCombat(e.getPlayer()))
            if (!CombatLog.isPvPDisabled(e.getPlayer().getLocation())) {
                if (e.getPlayer().hasMetadata("noCombatLog")) {
                    e.getPlayer().removeMetadata("noCombatLog", P.p);
                    return;
                }
                if (spawnNPC && e.getPlayer().getHealth() >= 2.0D) {
                    if (e.getPlayer().getWorld().getName().equals("world_duels2"))
                        return;
                    if (combatNPCs.containsKey(e.getPlayer().getUniqueId().toString())) {
                        Bukkit.getLogger().info("[Arkkit (CombatLog)] Skipping Combat NPC spawn for " + e.getPlayer() + " due to existing NPC: " + combatNPCs.get(e.getPlayer().getUniqueId().toString()));
                        return;
                    }
                    String playerName = e.getPlayer().getName();
                    String playerUUID = e.getPlayer().getUniqueId().toString();
                    NPC n = CitizensAPI.getNPCRegistry().createNPC(EntityType.ZOMBIE, ChatColor.RED.toString() + ChatColor.BOLD + "*OFFLINE* " + ChatColor.RESET + playerName);
                    Player p = e.getPlayer();
                    Bukkit.getLogger().info("[Arkkit (CombatLog)] Attempting to spawn combatNPC for player " + p.getName() + " at " + p
                            .getLocation().getBlockX() + "," + p.getLocation().getBlockY() + "," + p.getLocation().getBlockZ() + " with HP: " + p
                            .getHealth());
                    Location npcLocation = p.getLocation();
                    if (e.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END && e
                            .getPlayer().getLocation().getY() < 35.0D &&
                            p.hasMetadata("lastPlayerDamagerUUID")) {
                        Player pKiller = Bukkit.getPlayer((UUID)((MetadataValue)p.getMetadata("lastPlayerDamagerUUID").get(0)).value());
                        if (pKiller != null && pKiller.isOnline())
                            npcLocation = pKiller.getLocation().add(0.0D, 1.0D, 0.0D);
                    }
                    n.spawn(npcLocation);
                    Zombie z = (Zombie)n.getEntity();
                    z.setMaxHealth(p.getMaxHealth());
                    z.setHealth(p.getHealth());
                    z.setCustomNameVisible(true);
                    z.setBaby(false);
                    z.setVillager(true);
                    z.setCanPickupItems(false);
                    z.setRemoveWhenFarAway(false);
                    z.setNoDamageTicks(0);
                    z.setFireTicks(0);
                    if (e.getPlayer().hasMetadata("resilienceEnchant"))
                        z.setMetadata("resilienceEnchant", (MetadataValue)new FixedMetadataValue(P.p, Integer.valueOf(((MetadataValue)e.getPlayer().getMetadata("resilienceEnchant").get(0)).asInt())));
                    List<ItemStack> loot = new ArrayList<>();
                    for (ItemStack i : (ItemStack[])p.getInventory().getContents().clone()) {
                        if (i != null && i.getTypeId() != 0)
                            loot.add(i);
                    }
                    for (ItemStack i : (ItemStack[])p.getEquipment().getArmorContents().clone()) {
                        if (i != null && i.getTypeId() != 0)
                            loot.add(i);
                    }
                    n.getEntity().setMetadata("combatNPC_PlayerName", (MetadataValue)new FixedMetadataValue(P.p, playerName));
                    n.getEntity().setMetadata("combatNPC_PlayerUUID", (MetadataValue)new FixedMetadataValue(P.p, e.getPlayer().getUniqueId().toString()));
                    n.getEntity().setMetadata("combatNPC_ID", (MetadataValue)new FixedMetadataValue(P.p, Integer.valueOf(n.getId())));
                    n.getEntity().setMetadata("combatNPC_TimeoutTime", (MetadataValue)new FixedMetadataValue(P.p, Long.valueOf(System.currentTimeMillis() + CombatLog.getTimeLeftInCombat(p))));
                    n.getEntity().setMetadata("combatNPC_Faction", (MetadataValue)new FixedMetadataValue(P.p, FactionUtils.getFaction(p, true)));
                    n.setFlyable(false);
                    n.setProtected(false);
                    combatNPCs.put(playerUUID, n);
                    combatNPCs_loot.put(playerUUID, loot.toArray(new ItemStack[loot.size()]));
                    for (Entity ent : e.getPlayer().getNearbyEntities(48.0D, 48.0D, 48.0D)) {
                        if (ent instanceof Player)
                            ((Player)ent).sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + e
                                    .getPlayer().getName() + " has logged out while in combat, kill their NPC before they reconnect!");
                    }
                    Bukkit.getLogger().info("[Arkkit (CombatLog)] Spawned combatNPC for player " + p.getName() + " at " + p
                            .getLocation().getBlockX() + "," + p.getLocation().getBlockY() + "," + p.getLocation().getBlockZ() + " with HP: " + p
                            .getHealth() + " / Inventory: " + combatNPCs_loot.get(playerUUID));
                } else {
                    Bukkit.getLogger().info("[Arkkit (CombatLog)] Killing " + e.getPlayer().getName() + " due to logging out while still in cl_combat!");
                    e.getPlayer().setHealth(0.0D);
                    for (Entity ent : e.getPlayer().getNearbyEntities(64.0D, 64.0D, 64.0D)) {
                        if (ent instanceof Player)
                            ((Player)ent).sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + e
                                    .getPlayer().getName() + " was killed due to logging out in combat.");
                    }
                }
            }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player) {
            ((Player)e.getEntity()).setMetadata("lastDeathEvent", (MetadataValue)new FixedMetadataValue(P.p, Integer.valueOf(MinecraftServer.currentTick)));
            if (!e.getEntity().hasMetadata("NPC")) {
                Player p = (Player)e.getEntity();
                if (combatNPCs.containsKey(p.getUniqueId().toString())) {
                    String pName = p.getName();
                    Bukkit.getLogger().info("[Arkkit (CombatLog)] Adding Player:" + p.getName() + ", UUID:" + p.getUniqueId().toString() + " to playersToKill due to NPC EntityDeathEvent.");
                    playersToKill.add(p.getUniqueId().toString());
                    combatNPCs.remove(p.getUniqueId().toString());
                    combatNPCs_loot.remove(p.getUniqueId().toString());
                    int id = e.getEntity().hasMetadata("combatNPC_ID") ? ((MetadataValue)e.getEntity().getMetadata("combatNPC_ID").get(0)).asInt() : -1;
                    NPC n = (id != -1) ? CitizensAPI.getNPCRegistry().getById(id) : CitizensAPI.getNPCRegistry().getNPC((Entity)e.getEntity());
                    if (n != null) {
                        n.despawn();
                        n.destroy();
                        CitizensAPI.getNPCRegistry().deregister(n);
                    } else {
                        Bukkit.getLogger().info("[Arkkit (CombatLog)] Null NPC object for: " + pName + " onEntityDeath.");
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityCombust(final EntityCombustEvent e) {
        if (e.getEntity().hasMetadata("combatNPC_PlayerName")) {
            e.setCancelled(true);
            e.setDuration(0);
            Bukkit.getScheduler().runTaskLater(P.p, new Runnable() {
                public void run() {
                    e.getEntity().setFireTicks(0);
                }
            },  1L);
        }
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent e) {
        if (e.getEntity().hasMetadata("combatNPC_PlayerName") && e.getAmount() > 0.0D) {
            LivingEntity le = (LivingEntity)e.getEntity();
            e.setAmount(le.getMaxHealth() * 0.1D);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPotionSplashEvent(PotionSplashEvent e) {
        for (Object le2 : new ArrayList(e.getAffectedEntities())) {
            LivingEntity le = (LivingEntity) le2;
            if (le.hasMetadata("combatNPC_PlayerName")) {
                for (PotionEffect pe : e.getPotion().getEffects()) {
                    if (pe.getType().equals(PotionEffectType.HEAL))
                        le.setHealth(Math.min(le.getMaxHealth(), le.getHealth() + le.getMaxHealth() * 0.1D));
                }
                e.getAffectedEntities().remove(le);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNPCDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && !e.getEntity().hasMetadata("NPC")) {
            Player p = (Player)e.getEntity();
            if (combatNPCs.containsKey(p.getUniqueId().toString())) {
                e.setCancelled(true);
                e.setDamage(0.0D);
                return;
            }
        }
        if (e.getEntity().hasMetadata("combatNPC_PlayerName") && e.getCause() != EntityDamageEvent.DamageCause.CUSTOM) {
            LivingEntity le = (LivingEntity)e.getEntity();
            double dmgMod = 0.1D;
            if (le.hasMetadata("resilienceEnchant")) {
                int resilienceLevel = ((MetadataValue)le.getMetadata("resilienceEnchant").get(0)).asInt();
                dmgMod -= resilienceLevel * 0.015D;
            }
            double dmg = le.getMaxHealth() * dmgMod;
            e.setCancelled(true);
            e.setDamage(0.0D);
            if (e.getCause() == EntityDamageEvent.DamageCause.MAGIC)
                return;
            Faction f = (Faction)((MetadataValue)e.getEntity().getMetadata("combatNPC_Faction").get(0)).value();
            String pName = ((MetadataValue)e.getEntity().getMetadata("combatNPC_PlayerName").get(0)).asString();
            if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent)e;
                if (edbee.getDamager() instanceof Player) {
                    Player p = (Player)edbee.getDamager();
                    Faction faction = FactionUtils.getFaction(p);
                    if (FactionUtils.getRelation((RelationParticipator)faction, (RelationParticipator)f).isAtLeast(Relation.TRUCE)) {
                        p.sendMessage(ChatColor.RED + "** " + ChatColor.GRAY + "You cannot hurt " + ChatColor.RED + pName + "'s NPC" + ChatColor.GRAY + " due to your faction relationship.");
                        return;
                    }
                }
            }
            if (le.getHealth() > 0.0D) {
                double newHealth = Math.max(0.0D, le.getHealth() - dmg);
                le.setHealth(newHealth);
                le.playEffect(EntityEffect.HURT);
                le.setMetadata("combatNPC_TimeoutTime", (MetadataValue)new FixedMetadataValue(P.p, Long.valueOf(System.currentTimeMillis() + 10000L)));
                if (newHealth <= 0.0D &&
                        le.hasMetadata("combatNPC_PlayerName")) {
                    String playerUUID = ((MetadataValue)le.getMetadata("combatNPC_PlayerUUID").get(0)).asString();
                    ItemStack[] storedItems = combatNPCs_loot.get(playerUUID);
                    CombatLoggerDeathEvent combatLoggerDeathEvent = new CombatLoggerDeathEvent(le, playerUUID, pName, e, storedItems);
                    Bukkit.getPluginManager().callEvent(combatLoggerDeathEvent);
                    if (combatLoggerDeathEvent.getInventoryItems() != null) {
                        for (ItemStack i : combatLoggerDeathEvent.getInventoryItems()) {
                            if (i == null || i.getType() == Material.AIR)
                                continue;
                            e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), i);
                        }
                        combatLoggerDeathEvent.getInventoryItems().clear();
                    }
                    if (!playersToKill.contains(playerUUID)) {
                        Bukkit.getLogger().info("[Arkkit (CombatLog)] Adding UUID:" + playerUUID + " to playersToKill due to NPC death.");
                        playersToKill.add(playerUUID);
                    }
                    combatNPCs.remove(playerUUID);
                    combatNPCs_loot.remove(playerUUID);
                    int id = ((MetadataValue)e.getEntity().getMetadata("combatNPC_ID").get(0)).asInt();
                    NPC n = CitizensAPI.getNPCRegistry().getById(id);
                    if (n != null) {
                        n.despawn();
                        n.destroy();
                        CitizensAPI.getNPCRegistry().deregister(n);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerVelocity(PlayerVelocityEvent e) {
        if (e.getPlayer().hasMetadata("combatNPC_PlayerName")) {
            e.setCancelled(true);
            e.setVelocity(new Vector(0, 0, 0));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().isOnline())
            return;
        if (combatNPCs.containsKey(e.getPlayer().getUniqueId().toString())) {
            Bukkit.getLogger().info("[Arkkit (CombatLog)] Removing " + e.getPlayer().getName() + "'s NPC due to relog.");
            NPC n = combatNPCs.get(e.getPlayer().getUniqueId().toString());
            if (n.getEntity() instanceof LivingEntity) {
                LivingEntity le = (LivingEntity)n.getEntity();
                e.getPlayer().setHealth(Math.max(0.0D, Math.min(20.0D, le.getHealth())));
                if (!n.getEntity().getWorld().getName().startsWith("dungeon_"))
                    e.getPlayer().teleport(n.getEntity().getLocation(), PlayerTeleportEvent.TeleportCause.UNKNOWN);
            }
            n.despawn();
            n.destroy();
            CitizensAPI.getNPCRegistry().deregister(n);
            combatNPCs.remove(e.getPlayer().getUniqueId().toString());
            CombatLog.flagCombat(e.getPlayer());
        }
        String uuid = e.getPlayer().getUniqueId().toString();
        if (playersToKill.contains(uuid)) {
            Bukkit.getLogger().info("[Arkkit (CombatLog)] Killing " + e.getPlayer().getName() + " due to their combat NPC dieing.");
            e.getPlayer().getInventory().clear();
            e.getPlayer().getEquipment().setHelmet(null);
            e.getPlayer().getEquipment().setChestplate(null);
            e.getPlayer().getEquipment().setLeggings(null);
            e.getPlayer().getEquipment().setBoots(null);
            if (!e.getPlayer().isDead())
                e.getPlayer().setHealth(0.0D);
            playersToKill.remove(uuid);
            if (playersToKill.contains(uuid)) {
                Bukkit.getLogger().info("[Arkkit (CombatLog] playersToKill still contains uuid: " + uuid + " despite .remove() call?");
                playersToKill.remove(uuid);
            }
            final Player p = e.getPlayer();
            CombatLog.unflagCombat(p);
            Bukkit.getScheduler().runTaskLater(P.p, new Runnable() {
                public void run() {
                    if (p.isOnline())
                        p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "(!) " + ChatColor.RED + "You logged out in combat and your NPC was killed while you were away.");
                }
            },  60L);
            Iterator<NPC> it = CitizensAPI.getNPCRegistry().iterator();
            while (it.hasNext()) {
                NPC n = it.next();
                if (n.isSpawned() && n.getEntity() != null && n.getEntity().hasMetadata("combatNPC_PlayerUUID") && ((MetadataValue)n
                        .getEntity().getMetadata("combatNPC_PlayerUUID").get(0)).asString().equals(e.getPlayer().getUniqueId().toString())) {
                    Bukkit.getLogger().info("[Arkkit (CombatLog)] Invalid NPC found for UUID (" + e.getPlayer().getUniqueId() + "): " + n.getFullName() + "(" + n.getId() + ") / " + n.getEntity());
                    n.despawn();
                    n.destroy();
                }
            }
        }
    }
}
