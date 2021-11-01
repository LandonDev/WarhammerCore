package landon.warhammercore.patchapi.patches.fupgrades.struct.upgrades;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

import landon.warhammercore.patchapi.patches.fupgrades.struct.Upgrade;
import org.bukkit.ChatColor;

public class WellFed extends Upgrade {
    public WellFed(int maxUpgradeLevel, String defaultName, @Nullable List<String> defaultLore) {
        super(maxUpgradeLevel, defaultName, defaultLore);
    }

    public List<String> getLore(int tier) {
        return super.getLore(tier);
    }

    public int getTokenCost(int tier) {
        return tier * 2;
    }

    public double getMoneyCost(int tier) {
        if (tier == 1)
            return 500000.0D;
        if (tier == 2)
            return 2000000.0D;
        if (tier == 3)
            return 4000000.0D;
        if (tier == 4)
            return 1.0E7D;
        return Double.MAX_VALUE;
    }

    public List<String> getPerkList(int tier) {
        return Lists.newArrayList(new String[] { ((tier >= 1) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 1) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "-5% Hunger increase rate", ((tier >= 2) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 2) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "-10% Hunger increase rate", ((tier >= 3) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 3) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "-15% Hunger increase rate", ((tier >= 4) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 4) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "-25% Hunger increase rate" });
    }
}
