package landon.core.patchapi.patches.fupgrades.struct.upgrades;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

import landon.core.patchapi.patches.fupgrades.struct.Upgrade;
import org.bukkit.ChatColor;

public class OutpostControl extends Upgrade {
    public OutpostControl(int maxUpgradeLevel, String defaultName, @Nullable List<String> defaultLore) {
        super(maxUpgradeLevel, defaultName, defaultLore);
    }

    public int getTokenCost(int tier) {
        return tier * 2;
    }

    public double getMoneyCost(int tier) {
        return (tier * 2500000);
    }

    public List<String> getPerkList(int tier) {
        return Lists.newArrayList(new String[] { ((tier >= 1) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 1) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "+10% Outpost Cap Speed", ((tier >= 2) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 2) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "+25% Outpost Cap Speed", ((tier >= 3) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 3) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "+50% Outpost Cap Speed", ((tier >= 4) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 4) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "+100% Outpost Cap Speed", ((tier >= 5) ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD
                .toString() + "* " + ((tier >= 5) ? (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : ChatColor.GRAY) + "+125% Outpost Cap Speed" });
    }
}
