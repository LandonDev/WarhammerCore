package landon.warhammercore.patchapi.patches.fupgrades.struct.menu;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import java.text.DecimalFormat;

import com.massivecraft.factions.util.CCItemBuilder;
import com.massivecraft.factions.util.gui.CustomGUI;
import com.massivecraft.factions.util.gui.InventoryItem;
import landon.warhammercore.patchapi.patches.fpoints.FactionsPointsAPI;
import landon.warhammercore.patchapi.patches.fpoints.utils.FactionUtils;
import landon.warhammercore.patchapi.patches.fpoints.utils.NumberUtils;
import landon.warhammercore.patchapi.patches.ftop.utils.JSONMessage;
import landon.warhammercore.patchapi.patches.fupgrades.FactionUpgradeAPI;
import landon.warhammercore.patchapi.patches.fupgrades.FactionUpgrades;
import landon.warhammercore.patchapi.patches.fupgrades.struct.*;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FUpgradeMenu extends CustomGUI {
    private static DecimalFormat format = new DecimalFormat("#,###");

    private FPlayer fPlayer;

    private Faction overrideToSee;

    public FUpgradeMenu(Player player, Faction factionToSee) {
        super(player, "Faction Upgrades", 45);
        this.fPlayer = FactionUtils.getFPlayer(player);
        this.overrideToSee = factionToSee;
    }

    private boolean canChangeSettings() {
        return (this.overrideToSee.getRelationTo((RelationParticipator)this.fPlayer) == Relation.MEMBER && this.fPlayer.getRole().isAtLeast(Role.COLEADER));
    }

    public void redraw() {
        UpgradeManager manager = FactionUpgrades.get().getUpgradeManager();
        FactionUpgradeInfo upgradeInfo = manager.getFactionUpgradeInfo(this.overrideToSee);
        int slot = 0;
        for (FactionUpgrade upgrade : FactionUpgrade.values()) {
            Upgrade upgr = upgrade.getUpgrade();
            ItemStack display = (upgradeInfo.getUpgradeTier(upgrade) == upgr.getMaxUpgradeLevel() || upgrade.isHeroic()) ? addGlow(upgrade.createUpgradeItem(this.overrideToSee.getId(), upgradeInfo)) : upgrade.createUpgradeItem(this.overrideToSee.getId(), upgradeInfo);
            boolean heroic = upgrade.isHeroic();
            if (heroic && slot < 29)
                slot = 29;
            if (slot == 34)
                slot = 38;
            setItem(slot++, (new InventoryItem(display)).click(() -> {
                if (!canChangeSettings()) {
                    this.player.sendMessage(CC.YellowB + "(!)" + CC.Yellow + " Only faction (co)leaders can edit /f upgrades!");
                    return;
                }
                int currentTier = upgradeInfo.getUpgradeTier(upgrade);
                int nextTier = currentTier + 1;
                double cash = upgrade.getMoneyCost(nextTier);
                int tokenCost = upgrade.getTokenCost(nextTier);
                int heroicTokenCost = upgrade.getHeroicTokenCost(nextTier);
                long valueRequired = (heroic && upgr instanceof HeroicUpgrade) ? ((HeroicUpgrade)upgr).getFTopSpawnerValueRequirement() : 0L;
                if (currentTier >= upgr.getMaxUpgradeLevel()) {
                    this.player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "(!) " + ChatColor.RED + "That upgrade is already max level!");
                    return;
                }
                if (!canUnlock(upgrade, upgradeInfo)) {
                    FactionUpgrade required = upgrade.getChildUpgrade();
                    if (required != null) {
                        this.player.sendMessage(CC.RedB + "(!) " + CC.Red + "You must have " + CC.GoldB + required.getUpgrade().getDisplayName(0) + " " + JSONMessage.getNumber(required.getUpgrade().getMaxUpgradeLevel()) + CC.Red + " to unlock this Upgrade!");
                        return;
                    }
                }
                if (tokenCost > 0 && !FactionUpgradeAPI.hasTokensInInventory(this.player, tokenCost)) {
                    this.player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "(!) " + ChatColor.RED + "You do not have " + tokenCost + "x Faction Crystals!");
                    return;
                }
                if (cash > 0.0D && !FactionUpgrades.getEconomy().has((OfflinePlayer)this.player, cash)) {
                    this.player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "(!) " + ChatColor.RED + "You do not have enough /bal $ to purchase this upgrade!");
                    this.player.sendMessage(ChatColor.GRAY + "Upgrade Cost: " + ChatColor.UNDERLINE + "$" + format.format(cash));
                    return;
                }
                if (heroicTokenCost > 0 && !FactionUpgradeAPI.hasHeroicTokensInInventory(this.player, heroicTokenCost)) {
                    this.player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "(!) " + ChatColor.RED + "You do not have " + heroicTokenCost + "x Heroic Faction Crystals!");
                    return;
                }
                Long spawnerValue;
                if (heroic && (spawnerValue = manager.canAccessHeroic(this.overrideToSee, upgrade)) != null) {
                    this.player.sendMessage(CC.RedB + "(!) " + CC.Red + "Your faction must have a /f top Spawner value of at least " + CC.RedB + "$" + CC.Red + format.format(valueRequired) + " to use this /f upgrade!");
                    this.player.sendMessage(CC.GrayB + "Current Spawner Value: $" + CC.Gray + ((spawnerValue.longValue() == -1L) ? "0" : format.format(spawnerValue)));
                    return;
                }
                if (heroicTokenCost > 0) {
                    this.player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "- " + format.format(heroicTokenCost) + ChatColor.RED + "x Heroic Faction Crystals");
                    FactionUpgradeAPI.removeHeroicTokensFromInventory(this.player, heroicTokenCost);
                }
                if (tokenCost > 0) {
                    this.player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "- " + format.format(tokenCost) + ChatColor.RED + "x Faction Crystals");
                    FactionUpgradeAPI.removeTokensFromInventory(this.player, tokenCost);
                }
                if (cash > 0.0D) {
                    this.player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "- $" + ChatColor.RED + format.format(cash));
                    FactionUpgrades.getEconomy().withdrawPlayer((OfflinePlayer)this.player, cash);
                }
                for (FPlayer fplayers : this.overrideToSee.getFPlayers()) {
                    if (fplayers.isOnline()) {
                        String color = heroic ? CC.GoldB : CC.AquaB;
                        if (currentTier == 0) {
                            fplayers.sendMessage("");
                            fplayers.sendMessage(color + "/f upgrade Unlock");
                            fplayers.sendMessage(ChatColor.GRAY.toString() + ChatColor.BOLD + "** " + ChatColor.GRAY + this.player.getName() + " has unlocked Tier 1 of the " + color + upgr.getDisplayName(0) + ChatColor.GREEN + " /f upgrade!");
                            fplayers.sendMessage("");
                        } else {
                            fplayers.sendMessage("");
                            fplayers.sendMessage(color + "/f upgrade Upgrade");
                            fplayers.sendMessage(ChatColor.GRAY.toString() + this.player.getName() + " has increased your faction's " + color + upgr.getDisplayName(currentTier) + ChatColor.GRAY + " /f upgrade to Tier " + nextTier + "!");
                            fplayers.sendMessage("");
                        }
                        fplayers.getPlayer().playSound(fplayers.getPlayer().getLocation(), Sound.LEVEL_UP, 1.0F, 0.8F);
                    }
                }
                upgradeInfo.setUpgradeTier(upgrade, nextTier);
                FactionUpgradeAPI.processPerkPurchase(this.overrideToSee, upgrade, nextTier);
                    double rewardPoints = upgrade.getFactionPointsForTier(nextTier);
                    if (rewardPoints > 0.0D) {
                        FactionsPointsAPI.giveFactionPoints(this.overrideToSee, this.player, "f_upgrade", (int)rewardPoints, null);
                        this.overrideToSee.sendMessage(CC.GreenB + "+ " + CC.Green + NumberUtils.formatMoney(rewardPoints) + " Faction Points");
                }
                this.player.playSound(this.player.getLocation(), Sound.LEVEL_UP, 1.0F, 0.9F);
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "fquests add F_UPGRADES " + this.overrideToSee.getTag());
                redraw();
            }));
        }
        for (int i = 18; i < this.size; i++) {
            ItemStack is = this.inventory.getItem(i);
            if (is == null || is.getType() == Material.AIR)
                setItem(i, (new CCItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)DyeColor.BLACK.getWoolData())))
                        .name(" ").build(), null);
        }
    }

    private boolean canUnlock(FactionUpgrade upgrade, FactionUpgradeInfo info) {
        FactionUpgrade childRequired = upgrade.getChildUpgrade();
        if (childRequired == null)
            return true;
        int childLevel = info.getUpgradeTier(childRequired);
        return (childLevel >= childRequired.getUpgrade().getMaxUpgradeLevel());
    }

    private ItemStack addGlow(ItemStack item) {
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
}
