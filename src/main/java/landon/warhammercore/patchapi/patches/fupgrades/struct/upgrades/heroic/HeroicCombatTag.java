package landon.warhammercore.patchapi.patches.fupgrades.struct.upgrades.heroic;

import com.google.common.collect.Lists;
import landon.warhammercore.patchapi.patches.fupgrades.struct.HeroicUpgrade;

import java.util.List;
import javax.annotation.Nullable;

public class HeroicCombatTag extends HeroicUpgrade {
    public HeroicCombatTag(int maxUpgradeLevel, String defaultName, @Nullable List<String> defaultLore) {
        super(maxUpgradeLevel, defaultName, defaultLore);
    }

    public long getFTopSpawnerValueRequirement() {
        return 500000000L;
    }

    public int getHeroicCrystalCost(int tier) {
        if (tier == 1)
            return 8;
        if (tier == 2)
            return 12;
        if (tier == 3)
            return 16;
        if (tier == 4)
            return 20;
        return 0;
    }

    public int getTokenCost(int tier) {
        return 0;
    }

    public double getMoneyCost(int tier) {
        if (tier == 1)
            return 2.0E7D;
        if (tier == 2)
            return 4.0E7D;
        if (tier == 3)
            return 6.0E7D;
        if (tier == 4)
            return 1.0E8D;
        return 0.0D;
    }

    public List<String> getPerkList(int currentTier) {
        return Lists.newArrayList(new String[] { getLorePrefix(currentTier, 1) + "-0.25s Combat Tag Timer",
                getLorePrefix(currentTier, 2) + "-0.5s Combat Tag Timer",
                getLorePrefix(currentTier, 3) + "-1.0s Combat Tag Timer",
                getLorePrefix(currentTier, 4) + "-2.0s Combat Tag Timer" });
    }
}
