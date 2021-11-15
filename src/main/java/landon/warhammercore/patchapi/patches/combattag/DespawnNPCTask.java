package landon.warhammercore.patchapi.patches.combattag;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class DespawnNPCTask extends BukkitRunnable {
    public void run() {
        for (NPC n : CombatLogLogoutListener.combatNPCs.values()) {
            Entity e = n.getEntity();
            if (e.hasMetadata("combatNPC_TimeoutTime")) {
                long timeout = ((MetadataValue)e.getMetadata("combatNPC_TimeoutTime").get(0)).asLong();
                String playerName = ((MetadataValue)e.getMetadata("combatNPC_PlayerName").get(0)).asString();
                String playerUUID = ((MetadataValue)e.getMetadata("combatNPC_PlayerUUID").get(0)).asString();
                if (System.currentTimeMillis() > timeout) {
                    if (n.isSpawned()) {
                        n.despawn();
                        CombatLogLogoutListener.combatNPCs.remove(playerUUID);
                    }
                    n.destroy();
                    CitizensAPI.getNPCRegistry().deregister(n);
                }
            }
        }
    }
}
