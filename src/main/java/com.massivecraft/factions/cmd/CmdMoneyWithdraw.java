/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.RolePerm;
import com.massivecraft.factions.util.MiscUtil;
import org.bukkit.ChatColor;

public class CmdMoneyWithdraw extends FCommand {
    public CmdMoneyWithdraw() {
        this.aliases.add("w");
        this.aliases.add("withdraw");
        this.requiredArgs.add("amount");
        this.permission = Permission.MONEY_WITHDRAW.node;
        this.requiredRolePermission = RolePerm.WITHDRAW;
        setHelpShort("withdraw money");
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    public void perform() {
        int amount = argAsInt(0, 0);
        Faction faction = argAsFaction(1, this.myFaction);
        if (faction == null)
            return;
        if (amount < 1)
            try {
                long attempt = MiscUtil.getMoneyValue(argAsString(0));
                if (attempt < 0L || attempt > 2147483647L)
                    throw new Exception();
                amount = (int)attempt;
            } catch (Exception e) {
                this.fme.sendMessage(ChatColor.RED + "Please enter a valid amount!");
                return;
            }
        boolean success = Econ.transferMoney(this.fme, faction, this.fme, amount, false, true);
        if (success) {
            this.fme.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "+ $" + ChatColor.GREEN + Econ.insertCommas(amount));
            faction.msg("<b>" + this.fme
                    .getNameAsync() + " withdrew " + ChatColor.RED + ChatColor.BOLD.toString() + "$" + ChatColor.RED + "%s from the faction bank!", Econ.insertCommas(amount));
            P.p.logFactionEvent(faction, FLogType.BANK_EDIT, this.fme.getNameAsync(), CC.RedB + "WITHDREW", Econ.insertCommas(amount));
        }
        if (success && Conf.logMoneyTransactions)
            P.p.log(ChatColor.stripColor(P.p.txt
                    .parse("%s withdrew %s from the faction bank: %s", this.fme.getNameAsync(), Econ.moneyString(amount), faction.describeToAsync(null))));
    }
}


