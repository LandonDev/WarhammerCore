package landon.warhammercore.patchapi.patches.fupgrades;

import com.google.common.collect.Lists;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import landon.warhammercore.patchapi.patches.fupgrades.struct.FactionUpgrade;
import landon.warhammercore.patchapi.patches.fupgrades.struct.FactionUpgradeInfo;
import landon.warhammercore.patchapi.patches.fupgrades.utils.FUItemBuilder;
import landon.warhammercore.util.items.ItemBuilder;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.ListIterator;

public class FactionUpgradeAPI {
    private static List<String> lore = Lists.newArrayList(new String[] { ChatColor.GRAY + "This crystal can be used to purchase", ChatColor.AQUA + "/f upgrades" + ChatColor.GRAY + " for your Faction." });

    private static List<String> heroicLore = Lists.newArrayList(new String[] { ChatColor.GRAY + "This heroic crystal can be used to purchase", ChatColor.AQUA + "/f upgrades" + ChatColor.GRAY + " for your Faction." });

    private static ItemStack clone = addGlow(FUItemBuilder.buildItem(Material.QUARTZ, 1, (short)0, ChatColor.AQUA
            .toString() + ChatColor.BOLD + "Faction Crystal", lore));

    private static ItemStack heroicCrystal = addGlow(FUItemBuilder.buildItem(Material.INK_SACK, 1, (short)14, ChatColor.GOLD
            .toString() + ChatColor.BOLD + "Heroic Faction Crystal", heroicLore));
    public static int getPerkLevelIfAllowed(Faction faction, FactionUpgrade upgrade) {
        return getPerkLevel(faction.getId(), upgrade);
    }

    public static int getPerkLevelIfAllowed(String factionID, FactionUpgrade upgrade) {
        FactionUpgradeInfo info = (FactionUpgradeInfo)FactionUpgrades.get().getUpgradeManager().getUpgradeInfo().get(factionID);
        if (info == null)
            return 0;
        int retr = info.getUpgradeTier(upgrade);
        if (retr > 0 && upgrade.isHeroic()) {
            if (FactionUpgrades.get().getUpgradeManager().canAccessHeroic(factionID, upgrade))
                return retr;
            return 0;
        }
        return retr;
    }

    public static int getPerkLevelIfAllowed(Player player, FactionUpgrade upgrade) {
        FPlayer fplayer = (FPlayer)FPlayers.i.get((OfflinePlayer)player);
        if (fplayer == null || !fplayer.hasFaction() || !fplayer.getFaction().isNormal())
            return 0;
        return getPerkLevelIfAllowed(fplayer.getFaction().getId(), upgrade);
    }

    public static int getPerkLevel(Faction faction, FactionUpgrade upgrade) {
        return getPerkLevel(faction.getId(), upgrade);
    }

    public static int getPerkLevel(String factionID, FactionUpgrade upgrade) {
        FactionUpgradeInfo info = (FactionUpgradeInfo)FactionUpgrades.get().getUpgradeManager().getUpgradeInfo().get(factionID);
        if (info == null)
            return 0;
        return info.getUpgradeTier(upgrade);
    }

    public static int getPerkLevel(Player player, FactionUpgrade upgrade) {
        FPlayer fplayer = (FPlayer)FPlayers.i.get((OfflinePlayer)player);
        if (fplayer == null || !fplayer.hasFaction() || !fplayer.getFaction().isNormal())
            return 0;
        return getPerkLevel(fplayer.getFaction().getId(), upgrade);
    }

    public static void processPerkPurchase(Faction f, FactionUpgrade upgrade, int newTier) {
        if (upgrade == FactionUpgrade.FACTION_POWER_BOOST)
            f.setPowerBoost(f.getPowerBoost() + 20.0D);
    }

    public static boolean isFactionToken(ItemStack item) {
        if (item == null)
            return false;
        return (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore() && item
                .getItemMeta().getLore().equals(lore) && item.getItemMeta().getDisplayName().contains("Faction Crystal"));
    }

    public static boolean isHeroicFactionToken(ItemStack item) {
        if (item == null)
            return false;
        return (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore() && item
                .getItemMeta().getLore().equals(heroicLore) && item.getItemMeta().getDisplayName().contains("Heroic Faction Crystal"));
    }

    private static ItemStack addGlow(ItemStack item) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null)
            tag = nmsStack.getTag();
        NBTTagList ench = new NBTTagList();
        tag.set("ench", (NBTBase)ench);
        nmsStack.setTag(tag);
        return (ItemStack)CraftItemStack.asCraftMirror(nmsStack);
    }

    public static ItemStack createHeroicToken(int amount) {
        ItemStack item = heroicCrystal.clone();
        item.setAmount(amount);
        return item;
    }

    public static ItemStack createFactionToken(int amount) {
        ItemStack item = clone.clone();
        item.setAmount(amount);
        return item;
    }

    public static int getHeroicTokensInInventory(Player player) {
        int retr = 0;
        for (ListIterator<ItemStack> listIterator = player.getInventory().iterator(); listIterator.hasNext(); ) {
            ItemStack item = listIterator.next();
            if (item != null && item.getType() != Material.AIR && isHeroicFactionToken(item))
                retr += item.getAmount();
        }
        return retr;
    }

    public static boolean hasHeroicTokensInInventory(Player player, int tokenCount) {
        return (getHeroicTokensInInventory(player) >= tokenCount);
    }

    public static void removeHeroicTokensFromInventory(Player player, int toRemove) {
        int leftToRemove = toRemove;
        for (ListIterator<ItemStack> listIterator = player.getInventory().iterator(); listIterator.hasNext(); ) {
            ItemStack item = listIterator.next();
            if (leftToRemove <= 0)
                break;
            if (item != null && item.getType() != Material.AIR && isHeroicFactionToken(item)) {
                if (item.getAmount() <= leftToRemove) {
                    leftToRemove -= item.getAmount();
                    player.getInventory().removeItem(new ItemStack[] { item });
                    continue;
                }
                item.setAmount(item.getAmount() - leftToRemove);
                leftToRemove = 0;
            }
        }
        player.updateInventory();
    }

    public static int getTokensInInventory(Player player) {
        int retr = 0;
        for (ListIterator<ItemStack> listIterator = player.getInventory().iterator(); listIterator.hasNext(); ) {
            ItemStack item = listIterator.next();
            if (item != null && isFactionToken(item))
                retr += item.getAmount();
        }
        return retr;
    }

    public static boolean hasTokensInInventory(Player player, int tokenCount) {
        return (getTokensInInventory(player) >= tokenCount);
    }

    public static void removeTokensFromInventory(Player player, int toRemove) {
        int leftToRemove = toRemove;
        for (ListIterator<ItemStack> listIterator = player.getInventory().iterator(); listIterator.hasNext(); ) {
            ItemStack item = listIterator.next();
            if (leftToRemove <= 0)
                break;
            if (item != null && isFactionToken(item)) {
                if (item.getAmount() <= leftToRemove) {
                    leftToRemove -= item.getAmount();
                    player.getInventory().removeItem(new ItemStack[] { item });
                    continue;
                }
                item.setAmount(item.getAmount() - leftToRemove);
                leftToRemove = 0;
            }
        }
        player.updateInventory();
    }
}

