package landon.warhammercore.scoreboard;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Role;
import landon.warhammercore.WarhammerCore;
import landon.warhammercore.patchapi.patches.ftop.FactionsTop;
import landon.warhammercore.util.c;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScoreboardManager {
    private static volatile ScoreboardManager inst;

    private Map<Role, Integer> rolesMap;

    private final DateFormat format = new SimpleDateFormat("MM/dd/yy");

    private ScoreboardManager() {
        this.rolesMap = new HashMap<>();
        for (Role role : Role.values()) {
            this.rolesMap.put(role, role.value);
        }
        Map<Role, Integer> sortedRoles = sortByComparator(this.rolesMap, true);
        this.rolesMap = sortedRoles;
    }

    private Map<Role, Integer> sortByComparator(Map<Role, Integer> unsortMap, final boolean order) {
        List<Map.Entry<Role, Integer>> list = new LinkedList<>(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Role, Integer>>() {
            public int compare(Map.Entry<Role, Integer> o1, Map.Entry<Role, Integer> o2) {
                if (order)
                    return ((Integer)o1.getValue()).compareTo(o2.getValue());
                return ((Integer)o2.getValue()).compareTo(o1.getValue());
            }
        });
        Map<Role, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Role, Integer> entry : list)
            sortedMap.put(entry.getKey(), entry.getValue());
        return sortedMap;
    }

    public static ScoreboardManager get() {
        if(inst == null) {
            synchronized (ScoreboardManager.class) {
                inst = new ScoreboardManager();
            }
        }
        return inst;
    }

    public void updateScoreboard(Player player) {
        if (player == null)
            return;
        FPlayer fplayer = FPlayers.i.get(player);
        if (fplayer == null)
            return;
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("factionsbaddon", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(c.c("&6&lWarhammer&9&lMC&r"));
        int index = 16;
        objective.getScore(c.c("&7" + this.format.format(new Date()))).setScore(index--);
        objective.getScore(c.c("&fLives: &6" + WarhammerCore.get().getLifeManager().getLivesFromCache(player))).setScore(index--);
        objective.getScore(c.c("&fConfig Value: &60")).setScore(index--);
        objective.getScore(c.c("&r")).setScore(index--);
        if (fplayer.hasFaction()) {
            objective.getScore(c.c("&fFaction: &b" + fplayer.getFaction().getTag())).setScore(index--);
            objective.getScore(c.c("&fFaction Rank: &6#" + FactionsTop.get().getTopManager().getTopFaction(fplayer.getFactionId()).getRank())).setScore(index--);
            objective.getScore(c.c("&f")).setScore(index--);
            objective.getScore(c.c("&fOnline Members: &7" + fplayer.getFaction().getOnlinePlayers().size() + "/" + fplayer.getFaction().getFPlayers().size())).setScore(index--);
            addPlayers(fplayer, objective, index);
        } else {
            objective.getScore(c.c("&fFaction: &bNone")).setScore(index--);
            objective.getScore(c.c(" ")).setScore(index--);
            objective.getScore(c.c("&fAccount:")).setScore(index--);
            objective.getScore(c.c("&a" + player.getName())).setScore(index--);
        }
        player.setScoreboard(board);
    }

    public String colorify(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private void addPlayers(FPlayer fplayer, Objective objective, int startingIndex) {
        Score score = objective.getScore(c.c("&a" + fplayer.getNameAndTitleAsync() + " &f(&a" + fplayer.getPowerRounded() + "&f)"));
        score.setScore(startingIndex);
        for (Role role : this.rolesMap.keySet()) {
            if(startingIndex >= 1) {
                for (Player p : fplayer.getFaction().getOnlinePlayers()) {
                    FPlayer fp = FPlayers.i.get(p);
                    if (!fp.getPlayer().getUniqueId().equals(fplayer.getPlayer().getUniqueId()) &&
                            fp.getRole().equals(role)) {
                        score = objective.getScore(c.c("&a" + fp.getNameAndTitleAsync() + " &f(&a" + fp.getPowerRounded() + "&f)"));
                        score.setScore(startingIndex--);
                    }
                }
            } else {
                return;
            }
        }
    }
}
