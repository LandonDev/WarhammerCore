/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.RolePerm;
import com.massivecraft.factions.util.MiscUtil;
import org.bukkit.ChatColor;

public class CmdMoneyDeposit
        extends FCommand {
    public CmdMoneyDeposit() {
        this.aliases.add("d");
        this.aliases.add("deposit");
        this.requiredArgs.add("amount");
        this.permission = Permission.MONEY_DEPOSIT.node;
        this.requiredRolePermission = RolePerm.DEPOSIT;
        this.setHelpShort("deposit money");
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        int amount = this.argAsInt(0, 0);
        Faction faction = this.argAsFaction(1, this.myFaction);
        if (faction == null) {
            return;
        }
        Faction fac = faction;
        if (!fac.isNormal()) {
            this.fme.sendMessage(ChatColor.RED + "You cannot deposit into this faction.");
            return;
        }
        try {
            long attempt = MiscUtil.getMoneyValue(this.argAsString(0));
            if (attempt < 0L || attempt > Integer.MAX_VALUE) {
                throw new Exception();
            }
            amount = (int) attempt;
        } catch (Exception e) {
            this.fme.sendMessage(ChatColor.RED + "Please enter a valid amount!");
            return;
        }
        boolean success = Econ.transferMoney(this.fme, this.fme, faction, amount, false, true);
        if (success) {
            this.fme.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "- $" + ChatColor.RED + Econ.insertCommas(amount));
            faction.msg(ChatColor.YELLOW + this.fme.getNameAsync() + " deposited " + ChatColor.YELLOW + ChatColor.BOLD.toString() + "$" + ChatColor.YELLOW + "%s into the faction bank!", Econ.insertCommas(amount));
            P.p.logFactionEvent(fac, FLogType.BANK_EDIT, this.fme.getNameAsync(), ChatColor.GREEN + ChatColor.BOLD.toString() + "DEPOSITED", Econ.insertCommas(amount));
        }
        if (success && Conf.logMoneyTransactions) {
            P.p.log(ChatColor.stripColor(P.p.txt.parse("%s deposited %s in the faction bank: %s", this.fme.getNameAsync(), Econ.moneyString(amount), faction.describeToAsync(null))));
        }
    }
}

