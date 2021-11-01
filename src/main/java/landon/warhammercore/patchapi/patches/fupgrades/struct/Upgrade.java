package landon.warhammercore.patchapi.patches.fupgrades.struct;

import java.util.List;
import javax.annotation.Nullable;

public abstract class Upgrade {
    protected String defaultName;

    protected List<String> defaultLore;

    private int maxUpgradeLevel;

    public int getMaxUpgradeLevel() {
        return this.maxUpgradeLevel;
    }

    public Upgrade(int maxUpgradeLevel, String defaultName, @Nullable List<String> defaultLore) {
        this.defaultLore = defaultLore;
        this.defaultName = defaultName;
        this.maxUpgradeLevel = maxUpgradeLevel;
    }

    public String getDisplayName(int tier) {
        return this.defaultName;
    }

    public List<String> getLore(int tier) {
        return this.defaultLore;
    }

    public abstract int getTokenCost(int paramInt);

    public abstract double getMoneyCost(int paramInt);

    public abstract List<String> getPerkList(int paramInt);
}
