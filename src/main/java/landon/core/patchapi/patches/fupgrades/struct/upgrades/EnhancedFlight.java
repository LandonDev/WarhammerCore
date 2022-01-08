package landon.core.patchapi.patches.fupgrades.struct.upgrades;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

import landon.core.patchapi.patches.fupgrades.struct.Upgrade;
import org.bukkit.ChatColor;

public class EnhancedFlight extends Upgrade {
    public EnhancedFlight(int maxUpgradeLevel, String defaultName, @Nullable List<String> defaultLore) {
        super(maxUpgradeLevel, defaultName, defaultLore);
    }

    public int getTokenCost(int tier) {
        if (tier == 1)
            return 15;
        if (tier == 2)
            return 25;
        if (tier == 3)
            return 50;
        return Integer.MAX_VALUE;
    }

    public double getMoneyCost(int tier) {
        if (tier == 1)
            return 1000000.0D;
        if (tier == 2)
            return 1.0E7D;
        if (tier == 3)
            return 5.0E7D;
        return Double.MAX_VALUE;
    }

    public List<String> getPerkList(int tier) {
        return Lists.newArrayList(new String[] { ((tier >= 1) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 1) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "Faction Members can fly.", ((tier >= 2) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 2) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "All allies can fly.", ((tier >= 3) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 3) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "All truces can fly." });
    }
}
