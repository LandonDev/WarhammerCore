package landon.warhammercore.patchapi.patches.fupgrades.struct.upgrades;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

import landon.warhammercore.patchapi.patches.fupgrades.struct.Upgrade;
import org.bukkit.ChatColor;

public class HomeAdvantage extends Upgrade {
    public HomeAdvantage(int maxUpgradeLevel, String defaultName, @Nullable List<String> defaultLore) {
        super(maxUpgradeLevel, defaultName, defaultLore);
    }

    public int getTokenCost(int tier) {
        if (tier == 1)
            return 10;
        if (tier == 2)
            return 15;
        if (tier == 3)
            return 25;
        return Integer.MAX_VALUE;
    }

    public double getMoneyCost(int tier) {
        if (tier == 1)
            return 1.0E7D;
        if (tier == 2)
            return 2.5E7D;
        if (tier == 3)
            return 5.0E7D;
        return Double.MAX_VALUE;
    }

    public List<String> getPerkList(int tier) {
        return Lists.newArrayList(new String[] { ((tier >= 1) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 1) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "10% Damage Reduction", ((tier >= 2) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 2) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "20% Damage Reduction", ((tier >= 3) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 3) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "30% Damage Reduction" });
    }
}
