package landon.core.patchapi.patches.fupgrades.struct;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Lists;
import java.text.DecimalFormat;
import java.util.List;

import landon.core.patchapi.patches.fpoints.menus.PointPurchaseMenu;
import landon.core.patchapi.patches.ftop.utils.JSONMessage;
import landon.core.patchapi.patches.fupgrades.FactionUpgrades;
import landon.core.patchapi.patches.fupgrades.struct.upgrades.*;
import landon.core.patchapi.patches.fupgrades.struct.upgrades.heroic.HeroicCombatTag;
import landon.core.patchapi.patches.fupgrades.struct.upgrades.heroic.HeroicEnderPearlCooldown;
import landon.core.patchapi.patches.fupgrades.struct.upgrades.heroic.HeroicWellFed;
import landon.core.patchapi.patches.fupgrades.utils.FUItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public enum FactionUpgrade {
    ENHANCED_FLIGHT(Material.FEATHER, (Upgrade)new EnhancedFlight(3, "Enhanced Flight",
            Lists.newArrayList(new String[] { ChatColor.GRAY + "Increased flight abilities in your claims!" }))),
    WARP_MASTER(Material.ENDER_PEARL, (Upgrade)new WarpMaster(12, "Warp Master",
            Lists.newArrayList(new String[] { "Create more /f warps." }))),
    MAX_FACTION_SIZE(Material.EMERALD, (Upgrade)new MaxFactionSize(10, "Max Faction Size",
            Lists.newArrayList(new String[] { "Increases max faction size." }))),
    FACTION_POWER_BOOST(Material.DIAMOND, (Upgrade)new FactionPowerBoost(5, "Faction Power Boost",
            Lists.newArrayList(new String[] { "Adds passive faction power boosts." }))),
    MONSTER_FARM(Material.BLAZE_ROD, (Upgrade)new MonsterFarm(4, "Monster Farm",
            Lists.newArrayList(new String[] { "Increased mob spawn rates in claims." }))),
    EXP_HARVEST(Material.EXP_BOTTLE, (Upgrade)new EXPHarvest(3, "XP Harvest",
            Lists.newArrayList(new String[] { "Increases Vanilla XP gained from monsters." }))),
    TP_MASTERY(Material.BOOK, (Upgrade)new TPMastery(4, "TP Mastery",
            Lists.newArrayList(new String[] { "Decreases teleportation casting time." }))),
    WELL_FED(Material.COOKED_BEEF, (Upgrade)new WellFed(4, "Well Fed",
            Lists.newArrayList(new String[] { "Reduces rate at which faction members", "get hungry and need to /feed." }))),
    NATURAL_GROWTH(Material.WHEAT, (Upgrade)new NaturalGrowth(5, "Natural Growth",
            Lists.newArrayList(new String[] { "Increase growth speed of crops in claims." }))),
    HOME_ADVANTAGE(Material.GOLD_CHESTPLATE, (Upgrade)new HomeAdvantage(3, "Home Advantage",
            Lists.newArrayList(new String[] { "Decrease enemy damage dealt to", "friendly players in your faction claims." }))),
    WARZONE_CONTROL(Material.DIAMOND_SWORD, (Upgrade)new WarzoneControl(3, "Warzone Control",
            Lists.newArrayList(new String[] { "Increases /warzone \"Last Man Standing\"", "$ and XP payouts." }))),
    OUTPOST_CONTROL(Material.DIAMOND_CHESTPLATE, (Upgrade)new OutpostControl(5, "Outpost Control",
            Lists.newArrayList(new String[] { "Capture and Assault /outposts faster." }))),
    ENDER_FARMING(Material.ENDER_PORTAL_FRAME, (Upgrade)new EnderFarming(3, "Ender Farming",
            Lists.newArrayList(new String[] { "Multiplies Ender Monster killstreak rewards." }))),
    HEROIC_WELL_FED(Material.COOKED_BEEF, (Upgrade)new HeroicWellFed(4, "Heroic Well Fed", Lists.newArrayList(new String[] { "Reduces rate at which faction members", "get hungry and need to /feed." })), true),
    HEROIC_COMBAT_TAG(Material.WATCH, (Upgrade)new HeroicCombatTag(4, "Escape Artist", Lists.newArrayList(new String[] { "Reduces Combat Tag Timer" })), true),
    HEROIC_ENDERPEARL_COOLDOWN(Material.ENDER_PEARL, (Upgrade)new HeroicEnderPearlCooldown(4, "Fast Enderpearl", Lists.newArrayList(new String[] { "Reduces Enderpearl Cooldown" })), true);

    private MaterialData displayData;

    private Upgrade upgrade;

    private boolean heroicUpgrade;

    private static DecimalFormat format;

    public MaterialData getDisplayData() {
        return this.displayData;
    }

    public Upgrade getUpgrade() {
        return this.upgrade;
    }

    FactionUpgrade(MaterialData data, Upgrade upgrade) {
        this.displayData = data;
        this.upgrade = upgrade;
    }

    FactionUpgrade(Material data, Upgrade upgrade) {
        this.displayData = new MaterialData(data);
        this.upgrade = upgrade;
    }

    FactionUpgrade(Material data, Upgrade upgrade, boolean heroic) {
        this.displayData = new MaterialData(data);
        this.upgrade = upgrade;
        this.heroicUpgrade = heroic;
    }

    static {
        format = new DecimalFormat("#,###");
    }

    public boolean isHeroic() {
        return this.heroicUpgrade;
    }

    public FactionUpgrade getChildUpgrade() {
        if (this == HEROIC_WELL_FED)
            return WELL_FED;
        return null;
    }

    public double getFactionPointsForTier(int tier) {
        double costPerPoint = PointPurchaseMenu.COST_PER_POINT;
        if (costPerPoint <= 0.0D)
            return 0.0D;
        double pointsToGive = 0.0D;
        double money = getMoneyCost(tier);
        int heroicToken = getHeroicTokenCost(tier);
        int tokenCost = getTokenCost(tier);
        pointsToGive += (heroicToken * 10);
        pointsToGive += (tokenCost * 2);
        pointsToGive += Math.max(0.0D, money / costPerPoint / 2.0D);
        return Math.max(1.0D, Math.floor(pointsToGive));
    }

    public int getHeroicTokenCost(int tier) {
        Upgrade upgrade = getUpgrade();
        if (upgrade instanceof HeroicUpgrade) {
            int retr = ((HeroicUpgrade)upgrade).getHeroicCrystalCost(tier);
            return (int)Math.max(0.0D, Math.ceil(retr * FactionUpgrades.get().getUpgradeManager().getUpgradePriceMultiplier()));
        }
        return 0;
    }

    public int getTokenCost(int tier) {
        int cost = getUpgrade().getTokenCost(tier);
        return (int)Math.floor(cost * FactionUpgrades.get().getUpgradeManager().getUpgradePriceMultiplier()) * 8;
    }

    public double getMoneyCost(int tier) {
        double cost = getUpgrade().getMoneyCost(tier);
        return Math.floor(cost * FactionUpgrades.get().getUpgradeManager().getUpgradePriceMultiplier());
    }

    public ItemStack createUpgradeItem(String factionId, FactionUpgradeInfo upgradeInfo) {
        int tier = (upgradeInfo != null) ? upgradeInfo.getUpgradeTier(this) : 0;
        Upgrade upgrade = getUpgrade();
        List<String> lore = Lists.newArrayList();
        upgrade.getLore(tier).forEach(line -> lore.add(ChatColor.GRAY + line));
        ChatColor tierColor = isHeroic() ? ChatColor.YELLOW : ChatColor.LIGHT_PURPLE;
        lore.add("");
        lore.add(tierColor + ChatColor.BOLD.toString() + "Tier");
        lore.add(ChatColor.GRAY + " " + ((tier == 0) ? (ChatColor.RED.toString() + ChatColor.BOLD + "LOCKED") : (tier + " / " + upgrade.getMaxUpgradeLevel())));
        lore.add("");
        lore.add(tierColor + ChatColor.BOLD.toString() + "Perks");
        List<String> perkLore = upgrade.getPerkList(tier);
        if (perkLore == null)
            perkLore = Lists.newArrayList(new String[] { ChatColor.GRAY
                    .toString() + ChatColor.BOLD + "* " + ChatColor.GRAY + "Vaq pls fill me in.", ChatColor.GRAY
                    .toString() + ChatColor.BOLD + "* " + ChatColor.GRAY + "Good faction upgrades.", ChatColor.GRAY
                    .toString() + ChatColor.BOLD + "* " + ChatColor.GRAY + "All allies can fly." });
        lore.addAll(perkLore);
        lore.add("");
        int heroicTokenCost = getHeroicTokenCost(tier + 1);
        long balanceRequired = 0L;
        boolean hasAccess = true;
        if (upgrade instanceof HeroicUpgrade) {
            balanceRequired = ((HeroicUpgrade)upgrade).getFTopSpawnerValueRequirement();
            hasAccess = FactionUpgrades.get().getUpgradeManager().canAccessHeroic(factionId, this);
        }
        ChatColor secondaryColor = isHeroic() ? ChatColor.GOLD : ChatColor.AQUA;
        if (upgrade.getMaxUpgradeLevel() > tier) {
            lore.add(tierColor + ChatColor.BOLD.toString() + (this.heroicUpgrade ? "Requirements" : "Price"));
            int tokenCost = getTokenCost(tier + 1);
            double money = getMoneyCost(tier + 1);
            FactionUpgrade childRequired = getChildUpgrade();
            if (childRequired != null)
                lore.add(secondaryColor + CC.Bold + " * " + CC.Gold + childRequired.getUpgrade().getDisplayName(0) + " " + JSONMessage.getNumber(childRequired.getUpgrade().getMaxUpgradeLevel()) + " /f upgrade");
            if (balanceRequired > 0L)
                lore.add(secondaryColor + ChatColor.BOLD.toString() + " * " + secondaryColor + ChatColor.BOLD.toString() + (hasAccess ? CC.Strike : (secondaryColor + ChatColor.BOLD.toString())) + "$" + (hasAccess ? (secondaryColor + CC.Strike) : secondaryColor) + format.format(balanceRequired) + " " + (!hasAccess ? CC.GoldU : (CC.GoldU + CC.Strike)) + "Faction Spawner Value");
            if (heroicTokenCost > 0)
                lore.add(secondaryColor + ChatColor.BOLD.toString() + " * " + CC.Gold + heroicTokenCost + "x " + CC.Gold + "Heroic Faction Crystal" + ((tokenCost > 1) ? "s" : ""));
            if (tokenCost > 0)
                lore.add(secondaryColor + ChatColor.BOLD.toString() + " * " + secondaryColor + tokenCost + "x Faction Crystal" + ((tokenCost > 1) ? "s" : ""));
            if (money > 0.0D)
                lore.add(secondaryColor + ChatColor.BOLD.toString() + " * $" + secondaryColor + format.format(money));
            lore.add("");
            if (tier == 0) {
                lore.add(secondaryColor.toString() + ChatColor.BOLD + "Click to " + ChatColor.UNDERLINE + "Unlock");
            } else {
                lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "Click to " + ChatColor.UNDERLINE + "Upgrade");
            }
        } else {
            if (balanceRequired > 0L) {
                lore.add(tierColor + ChatColor.BOLD.toString() + "Requires");
                lore.add(secondaryColor + ChatColor.BOLD.toString() + " * " + secondaryColor + ChatColor.BOLD.toString() + (hasAccess ? CC.Strike : (secondaryColor + ChatColor.BOLD.toString())) + "$" + (hasAccess ? (secondaryColor + CC.Strike) : secondaryColor) + format.format(balanceRequired) + " /f top Spawner Value");
                lore.add("");
            }
            lore.add(ChatColor.AQUA + ChatColor.BOLD.toString() + "Upgrade is " + ChatColor.AQUA + ChatColor.BOLD + ChatColor.UNDERLINE.toString() + "MAXED!");
        }
        return FUItemBuilder.buildItem((tier == 0 || !hasAccess) ? Material.STAINED_GLASS_PANE : getDisplayData().getItemType(), (tier == 0) ? 1 : tier, (tier == 0) ?
                (short)DyeColor.RED.getWoolData() : (!hasAccess ? (short)DyeColor.YELLOW.getWoolData() : (short)getDisplayData().getData()), (this.heroicUpgrade ? ChatColor.GOLD : ChatColor.AQUA) + ChatColor.BOLD
                .toString() + upgrade.getDisplayName(tier), lore);
    }
}
