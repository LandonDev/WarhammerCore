package net.minelink.ctplus.factions.v1_6;

import com.google.common.base.Preconditions;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import net.minelink.ctplus.hook.Hook;
import org.bukkit.Location;

public class FactionsHook implements Hook {
  public boolean isPvpEnabledAt(Location location) {
    FLocation fLocation = new FLocation((Location)Preconditions.checkNotNull(location, "Null location"));
    Faction faction = Board.getInstance().getFactionAt(fLocation);
    return !faction.isSafeZone();
  }
}
