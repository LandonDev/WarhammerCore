/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.ChatColor;

public class CmdMoneyTransferPf
        extends FCommand {
    public CmdMoneyTransferPf() {
        this.aliases.add("pf");
        this.requiredArgs.add("amount");
        this.requiredArgs.add("player");
        this.requiredArgs.add("faction");
        this.permission = Permission.MONEY_P2F.node;
        this.setHelpShort("transfer p -> f");
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        double amount = this.argAsDouble(0, 0.0);
        FPlayer from = this.argAsBestFPlayerMatch(1);
        if (from == null) {
            return;
        }
        Faction to = this.argAsFaction(2);
        if (to == null) {
            return;
        }
        boolean success = Econ.transferMoney(this.fme, from, to, amount);
        if (success && Conf.logMoneyTransactions) {
            P.p.log(ChatColor.stripColor(P.p.txt.parse("%s transferred %s from the player \"%s\" to the faction \"%s\"", this.fme.getNameAsync(), Econ.moneyString(amount), from.describeToAsync(null), to.describeToAsync(null))));
        }
    }
}

