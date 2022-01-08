package landon.core.patchapi.patches.fupgrades.listeners;

import com.massivecraft.factions.event.FactionDisbandEvent;
import landon.core.patchapi.patches.fupgrades.FactionUpgrades;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FactionListener implements Listener {
    @EventHandler
    public void onFactionDisband(FactionDisbandEvent e) {
        String factionId = e.getFaction().getId();
        FactionUpgrades.get().getUpgradeManager().getUpgradeInfo().remove(factionId);
    }
}
