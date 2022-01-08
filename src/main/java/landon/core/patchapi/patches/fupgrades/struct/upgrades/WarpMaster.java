package landon.core.patchapi.patches.fupgrades.struct.upgrades;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

import landon.core.patchapi.patches.fupgrades.struct.Upgrade;
import org.bukkit.ChatColor;

public class WarpMaster extends Upgrade {
    public WarpMaster(int maxUpgradeLevel, String defaultName, @Nullable List<String> defaultLore) {
        super(maxUpgradeLevel, defaultName, defaultLore);
    }

    public int getTokenCost(int tier) {
        if (tier == 1)
            return 0;
        return tier;
    }

    public double getMoneyCost(int tier) {
        return (150000 * tier);
    }

    public List<String> getPerkList(int tier) {
        return Lists.newArrayList(new String[] {
                ((tier >= 1) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                        .toString() + "* " + ((tier >= 1) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "+1 Max /f warps", ((tier >= 2) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 2) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "+2 Max /f warps", ((tier >= 3) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 3) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "+3 Max /f warps", ((tier >= 4) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 4) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "+4 Max /f warps", ((tier >= 5) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 5) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "+5 Max /f warps", ((tier >= 6) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 6) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "+6 Max /f warps", ((tier >= 7) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 7) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "+7 Max /f warps", ((tier >= 8) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 8) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "+8 Max /f warps", ((tier >= 9) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 9) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "+9 Max /f warps", ((tier >= 10) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 10) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "+10 Max /f warps",
                ((tier >= 11) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                        .toString() + "* " + ((tier >= 11) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "+11 Max /f warps", ((tier >= 12) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 12) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "+12 Max /f warps" });
    }
}
