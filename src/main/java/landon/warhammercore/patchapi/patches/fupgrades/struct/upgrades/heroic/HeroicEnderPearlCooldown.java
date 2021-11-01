package landon.warhammercore.patchapi.patches.fupgrades.struct.upgrades.heroic;

import com.google.common.collect.Lists;
import landon.warhammercore.patchapi.patches.fupgrades.struct.HeroicUpgrade;

import java.util.List;
import javax.annotation.Nullable;

public class HeroicEnderPearlCooldown extends HeroicUpgrade {
    public HeroicEnderPearlCooldown(int maxUpgradeLevel, String defaultName, @Nullable List<String> defaultLore) {
        super(maxUpgradeLevel, defaultName, defaultLore);
    }

    public int getHeroicCrystalCost(int tier) {
        if (tier == 1)
            return 5;
        if (tier == 2)
            return 10;
        if (tier == 3)
            return 12;
        if (tier == 4)
            return 20;
        return 0;
    }

    public int getTokenCost(int tier) {
        return 0;
    }

    public double getMoneyCost(int tier) {
        if (tier == 1)
            return 2.5E7D;
        if (tier == 2)
            return 5.0E7D;
        if (tier == 3)
            return 7.5E7D;
        if (tier == 4)
            return 1.25E8D;
        return 0.0D;
    }

    public long getFTopSpawnerValueRequirement() {
        return 650000000L;
    }

    public List<String> getPerkList(int currentTier) {
        return Lists.newArrayList(new String[] { getLorePrefix(currentTier, 1) + "-0.5s Enderpearl Cooldown",
                getLorePrefix(currentTier, 2) + "-1.0s Enderpearl Cooldown",
                getLorePrefix(currentTier, 3) + "-1.25s Enderpearl Cooldown",
                getLorePrefix(currentTier, 4) + "-2.0s Enderpearl Cooldown" });
    }
}
