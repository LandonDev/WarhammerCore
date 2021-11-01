package landon.warhammercore.patchapi.patches.fupgrades.struct.upgrades.heroic;

import com.google.common.collect.Lists;
import landon.warhammercore.patchapi.patches.fupgrades.struct.HeroicUpgrade;

import java.util.List;
import javax.annotation.Nullable;

public class HeroicWellFed extends HeroicUpgrade {
    public HeroicWellFed(int maxUpgradeLevel, String defaultName, @Nullable List<String> defaultLore) {
        super(maxUpgradeLevel, defaultName, defaultLore);
    }

    public int getHeroicCrystalCost(int tier) {
        if (tier == 2)
            return 5;
        if (tier == 3)
            return 5;
        if (tier == 4)
            return 10;
        return 0;
    }

    public int getTokenCost(int tier) {
        return 0;
    }

    public long getFTopSpawnerValueRequirement() {
        return 50000000L;
    }

    public double getMoneyCost(int tier) {
        if (tier == 1)
            return 5000000.0D;
        if (tier == 2)
            return 1.0E7D;
        if (tier == 3)
            return 2.0E7D;
        if (tier == 4)
            return 3.0E7D;
        return 0.0D;
    }

    public List<String> getPerkList(int tier) {
        return Lists.newArrayList(new String[] { getLorePrefix(tier, 1) + "-30% Hunger increase rate",
                getLorePrefix(tier, 2) + "-35% Hunger increase rate",
                getLorePrefix(tier, 3) + "-45% Hunger increase rate",
                getLorePrefix(tier, 4) + "-50% Hunger increase rate" });
    }
}
