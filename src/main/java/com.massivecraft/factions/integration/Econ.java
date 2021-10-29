/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  net.milkbowl.vault.economy.Economy
 *  net.milkbowl.vault.economy.EconomyResponse
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.RegisteredServiceProvider
 *  org.bukkit.plugin.ServicesManager
 */
package com.massivecraft.factions.integration;

import com.massivecraft.factions.*;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.RolePerm;
import com.massivecraft.factions.util.RelationUtil;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class Econ {
    public static Economy econ = null;

    public static void setup() {
        if (isSetup())
            return;
        String integrationFail = "Economy integration is " + (Conf.econEnabled ? "enabled, but" : "disabled, and") + " the plugin \"Vault\" ";
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            P.p.log(integrationFail + "is not installed.");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            P.p.log(integrationFail + "is not hooked into an economy plugin.");
            return;
        }
        econ = rsp.getProvider();
        P.p.log("Economy integration through Vault plugin successful.");
        if (!Conf.econEnabled)
            P.p.log("NOTE: Economy is disabled. You can enable it with the command: f config econEnabled true");
        P.p.cmdBase.cmdHelp.updateHelp();
        Bukkit.getScheduler().scheduleSyncDelayedTask(P.p, Econ::oldMoneyDoTransfer, 20L);
    }

    public static boolean shouldBeUsed() {
        return (Conf.econEnabled && econ != null && econ.isEnabled());
    }

    public static boolean isSetup() {
        return (econ != null);
    }

    public static void modifyUniverseMoney(double delta) {
        if (!shouldBeUsed())
            return;
        if (Conf.econUniverseAccount == null)
            return;
        if (Conf.econUniverseAccount.length() == 0)
            return;
        if (!econ.hasAccount(Conf.econUniverseAccount))
            return;
        modifyBalance(Conf.econUniverseAccount, delta);
    }

    public static void sendFactionBalanceInfo(FPlayer to, Faction about) {
        if (!shouldBeUsed()) {
            P.p.log(Level.WARNING, "Vault does not appear to be hooked into an economy plugin.");
            return;
        }
        String name = (about instanceof Faction) ? about.getTag() : about.describeToAsync(to, true);
        to.msg(ChatColor.YELLOW + ChatColor.BOLD.toString() + "%s's Balance: " + ChatColor.YELLOW + ChatColor.BOLD.toString() + "$" + ChatColor.YELLOW +
                insertCommas(getFactionBalance(about)), name);
    }

    public static boolean canIControllYou(EconomyParticipator i, EconomyParticipator you) {
        Faction fI = RelationUtil.getFaction(i);
        Faction fYou = RelationUtil.getFaction(you);
        if (fI == null)
            return true;
        if (i instanceof FPlayer && ((FPlayer)i).isAdminBypassing())
            return true;
        if (i instanceof FPlayer && Permission.MONEY_WITHDRAW_ANY.has(((FPlayer)i).getPlayer()))
            return true;
        if (i == you)
            return true;
        if (i == fI && fI == fYou)
            return true;
        if (i instanceof FPlayer && you instanceof Faction && fI == fYou && (Conf.bankMembersCanWithdraw || fI.hasRolePerm((FPlayer) i, RolePerm.WITHDRAW)))
            return true;
        i.msg("<h>%s<i> lacks permission to control <h>%s's<i> money.", i.describeToAsync(i, true), you.describeToAsync(i));
        return false;
    }

    public static boolean transferMoney(EconomyParticipator invoker, EconomyParticipator from, EconomyParticipator to, double amount) {
        return transferMoney(invoker, from, to, amount, true, true);
    }

    private static OfflinePlayer getPlayerFromUUIDOrName(String name) {
        if (isUUID(name))
            return Bukkit.getOfflinePlayer(UUID.fromString(name));
        return Bukkit.getOfflinePlayer(name);
    }

    public static boolean transferMoney(EconomyParticipator invoker, EconomyParticipator from, EconomyParticipator to, double amount, boolean notify, boolean notifyFailure) {
        if (!shouldBeUsed())
            return false;
        if (amount < 0.0D) {
            amount *= -1.0D;
            EconomyParticipator temp = from;
            from = to;
            to = temp;
        }
        if (!canIControllYou(invoker, from))
            return false;
        OfflinePlayer fromAcc = (from instanceof Faction) ? null : getPlayerFromUUIDOrName(from.getAccountId());
        OfflinePlayer toAcc = (to instanceof Faction) ? null : getPlayerFromUUIDOrName(to.getAccountId());
        if (!hasAtLeast(from, amount, null)) {
            if (invoker != null && notifyFailure) {
                String lastMessage;
                if (from instanceof Faction) {
                    String name = ((Faction)from).getTag();
                    invoker.msg(ChatColor.RED + ChatColor.BOLD.toString() + "%s" + ChatColor.RED + " doesn't have enough money to do that!", name);
                    lastMessage = ChatColor.RED + ChatColor.BOLD.toString() + "Faction Balance: $" + ChatColor.RED + insertCommas(getFactionBalance((Faction)from));
                } else {
                    invoker.msg(ChatColor.RED + "You don't have enough money to do that!");
                    lastMessage = ChatColor.RED + ChatColor.BOLD.toString() + "Current Balance: $" + ChatColor.RED + insertCommas(econ.getBalance(fromAcc));
                }
                if (lastMessage != null)
                    if (invoker instanceof FPlayer) {
                        ((FPlayer)invoker).sendMessage(lastMessage);
                    } else {
                        invoker.msg(lastMessage);
                    }
            }
            return false;
        }
        boolean success = (from instanceof Faction) ? withdrawFactionBalance((Faction)from, amount) : econ.withdrawPlayer(fromAcc, amount).transactionSuccess();
        if (success) {
            boolean deposited = (to instanceof Faction) ? depositFactionBalance((Faction)to, amount) : econ.depositPlayer(toAcc, amount).transactionSuccess();
            if (deposited) {
                if (notify)
                    sendTransferInfo(invoker, from, to, amount);
                return true;
            }
            if (from instanceof Faction) {
                depositFactionBalance((Faction)from, amount);
            } else {
                econ.depositPlayer(fromAcc, amount);
            }
        }
        if (notifyFailure)
            invoker.msg("Unable to transfer %s<b> to <h>%s<b> from <h>%s<b>.", moneyString(amount), to.describeToAsync(invoker), from
                    .describeToAsync(invoker, true));
        return false;
    }

    public static Set<FPlayer> getFplayers(EconomyParticipator ep) {
        Set<FPlayer> fplayers = new HashSet<>();
        if (ep != null)
            if (ep instanceof FPlayer) {
                fplayers.add((FPlayer)ep);
            } else if (ep instanceof Faction) {
                fplayers.addAll(((Faction)ep).getFPlayers());
            }
        return fplayers;
    }

    public static void sendTransferInfo(EconomyParticipator invoker, EconomyParticipator from, EconomyParticipator to, double amount) {
        Set<FPlayer> recipients = new HashSet<>();
        recipients.addAll(getFplayers(invoker));
        recipients.addAll(getFplayers(from));
        recipients.addAll(getFplayers(to));
        if (invoker == null) {
            for (FPlayer recipient : recipients) {
                recipient.msg("<h>%s<i> was transfered from <h>%s<i> to <h>%s<i>.", moneyString(amount), from.describeToAsync(recipient), to
                        .describeToAsync(recipient));
            }
        } else if (invoker == from) {
            for (FPlayer recipient : recipients) {
                recipient.msg("<h>%s<i> <h>gave %s<i> to <h>%s<i>.", from.describeToAsync(recipient, true), moneyString(amount), to.describeToAsync(recipient));
            }
        } else if (invoker == to) {
            for (FPlayer recipient : recipients) {
                recipient.msg("<h>%s<i> <h>took %s<i> from <h>%s<i>.", to.describeToAsync(recipient, true), moneyString(amount), from
                        .describeToAsync(recipient));
            }
        } else {
            for (FPlayer recipient : recipients) {
                recipient.msg("<h>%s<i> transfered <h>%s<i> from <h>%s<i> to <h>%s<i>.", invoker.describeToAsync(recipient, true), moneyString(amount), from
                        .describeToAsync(recipient), to.describeToAsync(recipient));
            }
        }
    }

    public static boolean hasAtLeast(EconomyParticipator ep, double delta, String toDoThis) {
        double currentBalance;
        if (!shouldBeUsed())
            return true;
        boolean affordable = false;
        if (ep instanceof Faction) {
            currentBalance = ((Faction)ep).money;
        } else if (isUUID(ep.getAccountId())) {
            currentBalance = econ.getBalance(Bukkit.getOfflinePlayer(UUID.fromString(ep.getAccountId())));
        } else {
            currentBalance = econ.getBalance(Bukkit.getOfflinePlayer(ep.getAccountId()));
        }
        if (currentBalance >= delta)
            affordable = true;
        if (!affordable) {
            if (toDoThis != null && !toDoThis.isEmpty())
                ep.msg("<h>%s<i> can't afford <h>%s<i> %s.", ep.describeToAsync(ep, true), moneyString(delta), toDoThis);
            return false;
        }
        return true;
    }

    public static boolean modifyMoney(EconomyParticipator ep, double delta, String toDoThis, String forDoingThis) {
        if (!shouldBeUsed())
            return false;
        OfflinePlayer acc = (ep instanceof Faction) ? null : getPlayerFromUUIDOrName(ep.getAccountId());
        String You = ep.describeToAsync(ep, true);
        if (delta == 0.0D)
            return true;
        if (delta > 0.0D) {
            boolean deposited = (ep instanceof Faction) ? depositFactionBalance((Faction)ep, delta) : econ.depositPlayer(acc, delta).transactionSuccess();
            if (deposited) {
                if (forDoingThis != null && !forDoingThis.isEmpty())
                    ep.msg("<h>%s<i> gained <h>%s<i> %s.", You, moneyString(delta), forDoingThis);
                return true;
            }
            if (forDoingThis != null && !forDoingThis.isEmpty())
                ep.msg("<h>%s<i> would have gained <h>%s<i> %s, but the deposit failed.", You, moneyString(delta), forDoingThis);
            return false;
        }
        if (ep instanceof Faction) {
            if (hasAtLeast(ep, -delta, null)) {
                withdrawFactionBalance((Faction)ep, -delta);
                if (forDoingThis != null && !forDoingThis.isEmpty())
                    ep.msg("<h>%s<i> lost <h>%s<i> %s.", You, moneyString(-delta), forDoingThis);
                return true;
            }
            if (toDoThis != null && !toDoThis.isEmpty())
                ep.msg("<h>%s<i> can't afford <h>%s<i> %s.", You, moneyString(-delta), toDoThis);
            return false;
        }
        if (econ.has(acc, -delta) && econ.withdrawPlayer(acc, -delta).transactionSuccess()) {
            if (forDoingThis != null && !forDoingThis.isEmpty())
                ep.msg("<h>%s<i> lost <h>%s<i> %s.", You, moneyString(-delta), forDoingThis);
            return true;
        }
        if (toDoThis != null && !toDoThis.isEmpty())
            ep.msg("<h>%s<i> can't afford <h>%s<i> %s.", You, moneyString(-delta), toDoThis);
        return false;
    }

    public static String moneyString(double amount) {
        return econ.format(amount);
    }

    private static DecimalFormat commaFormat = new DecimalFormat("#,##0");

    public static String insertCommas(double amount) {
        return commaFormat.format(amount);
    }

    public static void oldMoneyDoTransfer() {
        if (!shouldBeUsed())
            return;
        for (Faction faction : Factions.i.get()) {
            if (faction.money <= 0.0D) {
                String accountID = faction.getAccountId();
                double foundBalance = econ.getBalance(accountID);
                if (foundBalance > 0.0D) {
                    EconomyResponse response = econ.withdrawPlayer(accountID, foundBalance);
                    if (response.transactionSuccess()) {
                        Bukkit.getLogger().info("[Factions] Converted $" + foundBalance + " for " + faction.getTag() + " id: " + faction.getId() + " accountID: " + accountID);
                        faction.money = foundBalance;
                        continue;
                    }
                    Bukkit.getLogger().info("[Factions] Unable to convert balance of " + faction.getAccountId() + " tag: " + faction.getTag());
                    continue;
                }
                Bukkit.getLogger().info("[Factions] Balance for " + faction.getTag() + " had money: " + faction.money + " found: " + foundBalance + " acc: " + accountID);
                continue;
            }
            Bukkit.getLogger().info("[Factions] Balance for " + faction.getTag() + " had money: " + faction.money);
        }
    }

    public static double calculateClaimCost(int ownedLand, boolean takingFromAnotherFaction) {
        if (!shouldBeUsed())
            return 0.0D;
        return Conf.econCostClaimWilderness + Conf.econCostClaimWilderness * Conf.econClaimAdditionalMultiplier * ownedLand - (takingFromAnotherFaction ? Conf.econCostClaimFromFactionBonus : 0.0D);
    }

    public static double calculateClaimRefund(int ownedLand) {
        return calculateClaimCost(ownedLand - 1, false) * Conf.econClaimRefundMultiplier;
    }

    public static double calculateTotalLandValue(int ownedLand) {
        double amount = 0.0D;
        for (int x = 0; x < ownedLand; x++)
            amount += calculateClaimCost(x, false);
        return amount;
    }

    public static double calculateTotalLandRefund(int ownedLand) {
        return calculateTotalLandValue(ownedLand) * Conf.econClaimRefundMultiplier;
    }

    public static double getBalance(String account) {
        if (account.startsWith("faction-"))
            return 0.0D;
        return econ.getBalance(Bukkit.getOfflinePlayer(account));
    }

    @Deprecated
    public static boolean setBalance(String account, double amount) {
        if (account.startsWith("faction-"))
            return true;
        double current = econ.getBalance(Bukkit.getOfflinePlayer(account));
        if (current > amount)
            return econ.withdrawPlayer(Bukkit.getOfflinePlayer(account), current - amount).transactionSuccess();
        return econ.depositPlayer(Bukkit.getOfflinePlayer(account), amount - current).transactionSuccess();
    }

    public static boolean modifyBalance(String account, double amount) {
        if (account.startsWith("faction-"))
            return true;
        if (amount < 0.0D)
            return econ.withdrawPlayer(Bukkit.getOfflinePlayer(account), -amount).transactionSuccess();
        return econ.depositPlayer(Bukkit.getOfflinePlayer(account), amount).transactionSuccess();
    }

    @Deprecated
    public static boolean deposit(String account, double amount) {
        if (account.startsWith("faction-"))
            return true;
        return econ.depositPlayer(Bukkit.getOfflinePlayer(account), amount).transactionSuccess();
    }

    @Deprecated
    public static boolean withdraw(String account, double amount) {
        if (account.startsWith("faction-"))
            return true;
        return econ.withdrawPlayer(Bukkit.getOfflinePlayer(account), amount).transactionSuccess();
    }

    @Deprecated
    public static void clearFactionBankIfDoesntExist(String accountId) {}

    public static void clearFactionBank(Faction faction) {
        faction.money = 0.0D;
    }

    public static double transferAllBankMoney(Faction faction, FPlayer fme) {
        double amount = getFactionBalance(faction);
        if (amount > 0.0D) {
            setFactionBalance(faction, 0.0D);
            if (fme.getPlayer() != null) {
                econ.depositPlayer(fme.getPlayer(), amount);
            } else {
                econ.depositPlayer(fme.getAccountId(), amount);
            }
        }
        return amount;
    }

    public static boolean setFactionBalance(Faction faction, double amount) {
        faction.money = amount;
        return true;
    }

    public static double getFactionBalance(Faction faction) {
        return faction.money;
    }

    public static synchronized double getFactionBalanceAsync(Faction faction) {
        return faction.money;
    }

    public static boolean depositFactionBalance(Faction faction, double amount) {
        if (amount < 0.0D)
            return withdrawFactionBalance(faction, Math.abs(amount));
        if (faction.money + amount < 0.0D) {
            Bukkit.getLogger().info("Unable to deposit money into " + faction.getTag() + " because their balance: " + faction.money + " + " + amount + " is < 0!");
            return false;
        }
        faction.money += amount;
        return true;
    }

    public static boolean withdrawFactionBalance(Faction faction, double amount) {
        if (amount > faction.money) {
            faction.money = 0.0D;
            return false;
        }
        faction.money -= amount;
        return true;
    }

    public static boolean isUUID(String uuid) {
        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        return true;
    }
}

