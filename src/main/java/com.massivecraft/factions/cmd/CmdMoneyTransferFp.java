/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.concurrent.TimeUnit;

public class CmdMoneyTransferFp
        extends FCommand {
    public CmdMoneyTransferFp() {
        this.aliases.add("fp");
        this.requiredArgs.add("amount");
        this.requiredArgs.add("faction");
        this.requiredArgs.add("player");
        this.permission = Permission.MONEY_F2P.node;
        this.setHelpShort("transfer f -> p");
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (this.sender instanceof Player) {
            long time;
            Player p = (Player) this.sender;
            if (p.hasMetadata("ff_faction_cooldown") && p.getMetadata("ff_faction_coooldown").size() > 0 && (time = p.getMetadata("ff_faction_cooldown").get(0).asLong()) > System.currentTimeMillis()) {
                p.sendMessage(ChatColor.RED + "You must wait " + (time - System.currentTimeMillis()) + "ms to use this command again.");
                return;
            }
            p.setMetadata("ff_faction_cooldown", new FixedMetadataValue(P.getP(), (System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10L))));
        }
        double amount = this.argAsDouble(0, 0.0);
        Faction from = this.argAsFaction(1);
        if (from == null) {
            return;
        }
        FPlayer to = this.argAsBestFPlayerMatch(2);
        if (to == null) {
            return;
        }
        boolean success = Econ.transferMoney(this.fme, from, to, amount);
        if (success && Conf.logMoneyTransactions) {
            P.p.log(ChatColor.stripColor(P.p.txt.parse("%s transferred %s from the faction \"%s\" to the player \"%s\"", this.fme.getNameAsync(), Econ.moneyString(amount), from.describeToAsync(null), to.describeToAsync(null))));
        }
    }
}

