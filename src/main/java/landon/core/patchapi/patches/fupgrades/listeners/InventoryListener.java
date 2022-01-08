package landon.core.patchapi.patches.fupgrades.listeners;


import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Role;
import java.text.DecimalFormat;
import java.util.Arrays;

import landon.core.patchapi.patches.fupgrades.FactionUpgradeAPI;
import landon.core.patchapi.patches.fupgrades.FactionUpgrades;
import landon.core.patchapi.patches.fupgrades.struct.FactionUpgrade;
import landon.core.patchapi.patches.fupgrades.struct.FactionUpgradeInfo;
import landon.core.patchapi.patches.fupgrades.struct.Upgrade;
import landon.core.patchapi.patches.fupgrades.struct.UpgradeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
    private DecimalFormat format = new DecimalFormat("#,###");

    @EventHandler
    public void onInventoryListener(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        if (event.getInventory().getName().equals("Faction Upgrades")) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
                return;
            if (event.getRawSlot() > event.getInventory().getSize() - 1)
                return;
            FPlayer fplayer = (FPlayer)FPlayers.i.get((OfflinePlayer)player);
            boolean canChange = fplayer.getRole().isAtLeast(Role.COLEADER);
            if (!fplayer.hasFaction() || !fplayer.getRole().isAtLeast(Role.COLEADER)) {
                player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "(!)" + ChatColor.YELLOW + " Only faction (co)leaders can edit /f upgrades!");
                return;
            }
            Faction faction = fplayer.getFaction();
            String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            UpgradeManager upgradeManager = FactionUpgrades.get().getUpgradeManager();
            FactionUpgrade upgrade = Arrays.<FactionUpgrade>stream(FactionUpgrade.values()).filter(upgr -> upgr.getUpgrade().getDisplayName(0).equalsIgnoreCase(name)).findFirst().orElse(null);
            if (upgrade == null)
                return;
            Upgrade upgr = upgrade.getUpgrade();
            FactionUpgradeInfo info = upgradeManager.getFactionUpgradeInfo(faction);
            if (info == null)
                if (canChange) {
                    info = new FactionUpgradeInfo(faction.getId());
                    upgradeManager.getUpgradeInfo().put(faction.getId(), info);
                } else {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "(!)" + ChatColor.RED + " Only faction (co)leaders can edit /f upgrades!");
                    return;
                }
            int currentTier = info.getUpgradeTier(upgrade);
            int nextTier = currentTier + 1;
            double cash = upgrade.getTokenCost(nextTier);
            int tokenCost = upgrade.getTokenCost(nextTier);
            if (currentTier >= upgr.getMaxUpgradeLevel()) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "(!) " + ChatColor.RED + "That upgrade is already max level!");
                return;
            }
            if (tokenCost > 0 && !FactionUpgradeAPI.hasTokensInInventory(player, tokenCost)) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "(!) " + ChatColor.RED + "You do not have " + tokenCost + "x Faction Crystals!");
                return;
            }
            if (cash > 0.0D && !FactionUpgrades.getEconomy().has((OfflinePlayer)player, cash)) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "(!) " + ChatColor.RED + "You do not have enough /bal $ to purchase this upgrade!");
                player.sendMessage(ChatColor.GRAY + "Upgrade Cost: " + ChatColor.UNDERLINE + "$" + this.format.format(cash));
                return;
            }
            if (tokenCost > 0) {
                player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "- " + this.format.format(tokenCost) + ChatColor.RED + "x Faction Crystals");
                FactionUpgradeAPI.removeTokensFromInventory(player, tokenCost);
            }
            if (cash > 0.0D) {
                player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "- $" + ChatColor.RED + this.format.format(cash));
                FactionUpgrades.getEconomy().withdrawPlayer((OfflinePlayer)player, cash);
            }
            for (FPlayer fplayers : faction.getFPlayers()) {
                if (fplayers.isOnline()) {
                    if (currentTier == 0) {
                        fplayers.sendMessage("");
                        fplayers.sendMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + "/f upgrade Unlock");
                        fplayers.sendMessage(ChatColor.GRAY.toString() + ChatColor.BOLD + "** " + ChatColor.GRAY + player.getName() + " has unlocked Tier 1 of the " + ChatColor.AQUA + ChatColor.BOLD + upgr
                                .getDisplayName(0) + ChatColor.GREEN + " /f upgrade!");
                        fplayers.sendMessage("");
                    } else {
                        fplayers.sendMessage("");
                        fplayers.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "/f upgrade Upgrade");
                        fplayers.sendMessage(ChatColor.GRAY.toString() + player.getName() + " has increased your faction's " + ChatColor.GREEN + ChatColor.BOLD + upgr
                                .getDisplayName(currentTier) + ChatColor.GRAY + " /f upgrade to Tier " + nextTier + "!");
                        fplayers.sendMessage("");
                    }
                    fplayers.getPlayer().playSound(fplayers.getPlayer().getLocation(), Sound.LEVEL_UP, 1.0F, 0.8F);
                }
            }
            info.setUpgradeTier(upgrade, nextTier);
            FactionUpgradeAPI.processPerkPurchase(faction, upgrade, nextTier);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 0.9F);
            player.updateInventory();
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "fquests add F_UPGRADES " + faction.getTag());
        }
    }
}
