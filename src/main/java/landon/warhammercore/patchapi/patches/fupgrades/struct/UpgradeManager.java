package landon.warhammercore.patchapi.patches.fupgrades.struct;

import com.massivecraft.factions.Faction;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.google.common.reflect.TypeToken;
import com.massivecraft.factions.P;
import landon.warhammercore.patchapi.patches.ftop.FactionsTop;
import landon.warhammercore.patchapi.patches.ftop.struct.TopFaction;
import landon.warhammercore.patchapi.patches.fupgrades.FactionUpgrades;
import org.bukkit.Bukkit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UpgradeManager {
    private Map<String, FactionUpgradeInfo> upgradeInfo = new HashMap<>();

    private File upgradeFile;

    public Map<String, FactionUpgradeInfo> getUpgradeInfo() {
        return this.upgradeInfo;
    }

    private Gson gson = (new GsonBuilder()).create();

    private double upgradePriceMultiplier = 1.0D;

    public double getUpgradePriceMultiplier() {
        return this.upgradePriceMultiplier;
    }

    public void loadUpgrades() {
        try {
            this.upgradeFile = new File(P.p.getDataFolder(), "upgrades.json");
            if (!this.upgradeFile.exists())
                this.upgradeFile.createNewFile();
            StringBuilder string = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(this.upgradeFile));
            String line = null;
            while ((line = reader.readLine()) != null)
                string.append(line);
            this.upgradeInfo = (Map<String, FactionUpgradeInfo>)this.gson.fromJson(string.toString(), (new TypeToken<ConcurrentHashMap<String, FactionUpgradeInfo>>() {

            }).getType());
            if (this.upgradeInfo == null)
                this.upgradeInfo = new ConcurrentHashMap<>();
            if (this.upgradeInfo.size() > 0)
                Bukkit.getLogger().info("Loaded " + this.upgradeInfo.size() + " Upgrades.");
            this.upgradePriceMultiplier = P.p.getConfig().getDouble("patches.fupgrades.upgradeModifier", 1.0D);
            Bukkit.getLogger().info("[FactionUpgrades] Loaded " + this.upgradePriceMultiplier + " Upgrade Cost Multiplier.");
        } catch (Throwable $ex) {
            try {
                throw $ex;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Long canAccessHeroic(Faction faction, FactionUpgrade upgrade) {
        if (!(upgrade.getUpgrade() instanceof HeroicUpgrade))
            return Long.valueOf(-1L);
        TopFaction topFac = FactionsTop.get().getTopManager().getTopFaction(faction.getId());
        if (topFac != null && topFac.getStoredFaction().getTotalSpawnerWorth() >= ((HeroicUpgrade)upgrade.getUpgrade()).getFTopSpawnerValueRequirement())
            return null;
        if (topFac != null)
            return Long.valueOf((long)topFac.getStoredFaction().getTotalSpawnerWorth());
        return Long.valueOf(-1L);
    }

    public boolean canAccessHeroic(String factionID, FactionUpgrade upgrade) {
        if (!(upgrade.getUpgrade() instanceof HeroicUpgrade))
            return false;
        TopFaction topFac = FactionsTop.get().getTopManager().getTopFaction(factionID);
        return (topFac != null && topFac.getStoredFaction().getTotalSpawnerWorth() >= ((HeroicUpgrade)upgrade.getUpgrade()).getFTopSpawnerValueRequirement());
    }

    public void saveUpgradeInfo() {
        try {
            String map = this.gson.toJson(this.upgradeInfo);
            FileWriter writer = new FileWriter(this.upgradeFile);
            try {
                writer.write(map);
                writer.flush();
            } finally {
                if (Collections.<FileWriter>singletonList(writer).get(0) != null)
                    writer.close();
            }
        } catch (Throwable $ex) {
            try {
                throw $ex;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public FactionUpgradeInfo getFactionUpgradeInfo(Faction faction) {
        return this.upgradeInfo.computeIfAbsent(faction.getId(), u -> new FactionUpgradeInfo(faction.getId()));
    }
}

