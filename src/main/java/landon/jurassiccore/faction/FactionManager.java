package landon.jurassiccore.faction;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.iface.RelationParticipator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FactionManager {
    public ChatColor getRelationColor(Faction faction1, Faction faction2) {
        switch (faction1.getRelationTo((RelationParticipator) faction2)) {
          case ALLY:
                return ChatColor.LIGHT_PURPLE;
            case ENEMY:
                return ChatColor.RED;
            case MEMBER:
                return ChatColor.GREEN;
            case NEUTRAL:
                return ChatColor.WHITE;
            case TRUCE:
                return ChatColor.AQUA;
        }
        return ChatColor.WHITE;
    }

    public String getRoleSymbol(Player player) {
        switch (FPlayers.i.get(player).getRole()) {
          case ADMIN:
                return "***";
            case COLEADER:
                return "**";
            case MODERATOR:
                return "*";
            case NORMAL:
                return "";
            case RECRUIT:
                return "-";
        }
        return "";
    }
}
