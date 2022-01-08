package net.minelink.ctplus;

import com.massivecraft.factions.P;
import net.minelink.ctplus.event.NpcDespawnEvent;
import net.minelink.ctplus.event.NpcDespawnReason;
import net.minelink.ctplus.task.NpcDespawnTask;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class NpcManager {
    private final CombatTagPlus plugin;

    private final Map<UUID, Npc> spawnedNpcs = new HashMap<>();

    private final Map<Npc, NpcDespawnTask> despawnTasks = new HashMap<>();

    private static final Sound EXPLODE_SOUND;

    NpcManager(CombatTagPlus plugin) {
        this.plugin = plugin;
    }

    public Npc spawn(Player player) {
        Npc npc = getSpawnedNpc(player.getUniqueId());
        if (npc != null)
            return null;
        npc = new Npc(this.plugin.getNpcPlayerHelper(), this.plugin.getNpcPlayerHelper().spawn(player));
        this.spawnedNpcs.put(player.getUniqueId(), npc);
        Player entity = npc.getEntity();
        entity.setCanPickupItems(false);
        entity.setNoDamageTicks(0);
        entity.setHealthScale(player.getHealthScale());
        entity.setMaxHealth(player.getMaxHealth());
        entity.setHealth(player.getHealth());
        entity.setTotalExperience(player.getTotalExperience());
        entity.setFoodLevel(player.getFoodLevel());
        entity.setExhaustion(player.getExhaustion());
        entity.setSaturation(player.getSaturation());
        entity.setFireTicks(player.getFireTicks());
        entity.getInventory().setContents(player.getInventory().getContents());
        entity.getInventory().setArmorContents(player.getInventory().getArmorContents());
        entity.addPotionEffects(player.getActivePotionEffects());
        entity.teleport((Entity) player, PlayerTeleportEvent.TeleportCause.PLUGIN);
        this.plugin.getNpcPlayerHelper().updateEquipment(entity);
        entity.setMetadata("NPC", (MetadataValue) new FixedMetadataValue(P.p, Boolean.valueOf(true)));
        if (this.plugin.getSettings().playEffect()) {
            Location l = entity.getLocation();
            l.getWorld().playEffect(l, Effect.MOBSPAWNER_FLAMES, 0, 64);
            l.getWorld().playSound(l, EXPLODE_SOUND, 0.9F, 0.0F);
        }
        long despawnTime = System.currentTimeMillis() + this.plugin.getSettings().getNpcDespawnMillis();
        NpcDespawnTask despawnTask = new NpcDespawnTask(this.plugin, npc, despawnTime);
        despawnTask.start();
        this.despawnTasks.put(npc, despawnTask);
        return npc;
    }

    public void despawn(Npc npc) {
        despawn(npc, NpcDespawnReason.DESPAWN);
    }

    public void despawn(Npc npc, NpcDespawnReason reason) {
        Npc other = getSpawnedNpc(npc.getIdentity().getId());
        if (other == null || other != npc)
            return;
        NpcDespawnEvent event = new NpcDespawnEvent(npc, reason);
        Bukkit.getPluginManager().callEvent((Event) event);
        if (hasDespawnTask(npc)) {
            NpcDespawnTask despawnTask = getDespawnTask(npc);
            despawnTask.stop();
            this.despawnTasks.remove(npc);
        }
        this.plugin.getNpcPlayerHelper().despawn(npc.getEntity());
        this.spawnedNpcs.remove(npc.getIdentity().getId());
        npc.getEntity().removeMetadata("NPC", P.p);
    }

    public Npc getSpawnedNpc(UUID playerId) {
        return this.spawnedNpcs.get(playerId);
    }

    public boolean npcExists(UUID playerId) {
        return this.spawnedNpcs.containsKey(playerId);
    }

    public NpcDespawnTask getDespawnTask(Npc npc) {
        return this.despawnTasks.get(npc);
    }

    public boolean hasDespawnTask(Npc npc) {
        return this.despawnTasks.containsKey(npc);
    }

    static {
        Sound sound;
        try {
            sound = Sound.valueOf("ENTITY_GENERIC_EXPLODE");
        } catch (IllegalArgumentException e) {
            try {
                sound = Sound.valueOf("EXPLODE");
            } catch (IllegalArgumentException e2) {
                throw new AssertionError("Unable to find explosion sound");
            }
        }
        EXPLODE_SOUND = sound;
    }
}
