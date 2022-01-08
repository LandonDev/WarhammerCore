package landon.core.patchapi.patches.fupgrades.struct.upgrades;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

import landon.core.patchapi.patches.fupgrades.struct.Upgrade;
import org.bukkit.ChatColor;

public class MonsterFarm extends Upgrade {
    public MonsterFarm(int maxUpgradeLevel, String defaultName, @Nullable List<String> defaultLore) {
        super(maxUpgradeLevel, defaultName, defaultLore);
    }

    public int getTokenCost(int tier) {
        if (tier == 1)
            return 5;
        if (tier == 2)
            return 15;
        if (tier == 3)
            return 20;
        if (tier == 4)
            return 40;
        return Integer.MAX_VALUE;
    }

    public double getMoneyCost(int tier) {
        if (tier == 1)
            return 1.0E7D;
        if (tier == 2)
            return 2.5E7D;
        if (tier == 3)
            return 5.0E7D;
        if (tier == 4)
            return 1.0E8D;
        return Double.MAX_VALUE;
    }

    public List<String> getPerkList(int tier) {
        return Lists.newArrayList(new String[] { ((tier >= 1) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 1) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "1.05x Spawn Rate", ((tier >= 2) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 2) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "1.15x Spawn Rate", ((tier >= 3) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 3) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "1.25x Spawn Rate", ((tier >= 4) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 4) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "1.5x Spawn Rate" });
    }
}
