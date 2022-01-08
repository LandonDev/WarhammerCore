package net.minelink.ctplus.listener;

import com.massivecraft.factions.P;
import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.Npc;
import net.minelink.ctplus.event.CombatLogEvent;
import net.minelink.ctplus.event.NpcDespawnEvent;
import net.minelink.ctplus.event.NpcDespawnReason;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public final class NpcListener implements Listener {
    private final CombatTagPlus plugin;

    public NpcListener(CombatTagPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCombatLog(CombatLogEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.getSettings().instantlyKill())
            return;
        this.plugin.getNpcManager().spawn(player);
    }

    @EventHandler
    public void despawnNpc(PlayerJoinEvent event) {
        Npc npc = this.plugin.getNpcManager().getSpawnedNpc(event.getPlayer().getUniqueId());
        if (npc != null)
            this.plugin.getNpcManager().despawn(npc);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void despawnNpc(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!this.plugin.getNpcPlayerHelper().isNpc(player))
            return;
        UUID id = this.plugin.getNpcPlayerHelper().getIdentity(player).getId();
        final Npc npc = this.plugin.getNpcManager().getSpawnedNpc(id);
        if (npc == null)
            return;
        this.plugin.getTagManager().untag(id);
        Bukkit.getScheduler().scheduleSyncDelayedTask(P.p, new Runnable() {
            public void run() {
                NpcListener.this.plugin.getNpcManager().despawn(npc, NpcDespawnReason.DEATH);
            }
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onNPCDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player))
            return;
        if (!(event.getEntity() instanceof Player) || !this.plugin.getNpcPlayerHelper().isNpc((Player) event.getEntity()))
            return;
        Player attacker = (Player) event.getDamager();
        Player npc = (Player) event.getEntity();
        UUID npcPlayerId = this.plugin.getNpcPlayerHelper().getIdentity(npc).getId();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void updateDespawnTime(EntityDamageByEntityEvent event) {
        if (!this.plugin.getSettings().resetDespawnTimeOnHit())
            return;
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        if (!this.plugin.getNpcPlayerHelper().isNpc(player))
            return;
        UUID npcId = this.plugin.getNpcPlayerHelper().getIdentity(player).getId();
        Npc npc = this.plugin.getNpcManager().getSpawnedNpc(npcId);
        if (this.plugin.getNpcManager().hasDespawnTask(npc)) {
            long despawnTime = System.currentTimeMillis() + this.plugin.getSettings().getNpcDespawnMillis();
            this.plugin.getNpcManager().getDespawnTask(npc).setTime(despawnTime);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void syncOffline(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if (!this.plugin.getNpcPlayerHelper().isNpc(player))
            return;
        this.plugin.getTagManager().untag(player.getUniqueId());
        Bukkit.getScheduler().scheduleSyncDelayedTask(P.p, new Runnable() {
            public void run() {
                NpcListener.this.plugin.getNpcPlayerHelper().syncOffline(player);
            }
        });
    }

    @EventHandler
    public void syncOffline(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED)
            return;
        final UUID playerId = event.getUniqueId();
        if (!this.plugin.getNpcManager().npcExists(playerId))
            return;
        Future<?> future = Bukkit.getScheduler().callSyncMethod(P.p, new Callable<Void>() {
            public Void call() throws Exception {
                Npc npc = NpcListener.this.plugin.getNpcManager().getSpawnedNpc(playerId);
                if (npc == null)
                    return null;
                NpcListener.this.plugin.getNpcPlayerHelper().syncOffline(npc.getEntity());
                return null;
            }
        });
        try {
            future.get();
        } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void syncOffline(NpcDespawnEvent event) {
        Npc npc = event.getNpc();
        Player player = this.plugin.getPlayerCache().getPlayer(npc.getIdentity().getId());
        if (player == null) {
            this.plugin.getNpcPlayerHelper().syncOffline(npc.getEntity());
            return;
        }
        Player npcPlayer = npc.getEntity();
        player.setMaximumAir(npcPlayer.getMaximumAir());
        player.setRemainingAir(npcPlayer.getRemainingAir());
        player.setHealthScale(npcPlayer.getHealthScale());
        player.setMaxHealth(getRealMaxHealth(npcPlayer));
        player.setHealth(npcPlayer.getHealth());
        player.setTotalExperience(npcPlayer.getTotalExperience());
        player.setFoodLevel(npcPlayer.getFoodLevel());
        player.setExhaustion(npcPlayer.getExhaustion());
        player.setSaturation(npcPlayer.getSaturation());
        player.setFireTicks(npcPlayer.getFireTicks());
        player.getInventory().setContents(npcPlayer.getInventory().getContents());
        player.getInventory().setArmorContents(npcPlayer.getInventory().getArmorContents());
        player.addPotionEffects(npcPlayer.getActivePotionEffects());
    }

    private static double getRealMaxHealth(Player npcPlayer) {
        double health = npcPlayer.getMaxHealth();
        for (PotionEffect p : npcPlayer.getActivePotionEffects()) {
            if (p.getType().equals(PotionEffectType.HEALTH_BOOST))
                health -= ((p.getAmplifier() + 1) * 4);
        }
        return health;
    }
}
