/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;

public class CmdMoney
        extends FCommand {
    public CmdMoneyBalance cmdMoneyBalance = new CmdMoneyBalance();
    public CmdMoneyDeposit cmdMoneyDeposit = new CmdMoneyDeposit();
    public CmdMoneyWithdraw cmdMoneyWithdraw = new CmdMoneyWithdraw();

    public CmdMoney() {
        this.aliases.add("money");
        this.isMoneyCommand = true;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
        this.setHelpShort("faction money commands");
        this.helpLong.add(this.p.txt.parseTags("<i>The faction money commands."));
        this.addSubCommand(this.cmdMoneyBalance);
        this.addSubCommand(this.cmdMoneyDeposit);
        this.addSubCommand(this.cmdMoneyWithdraw);
    }

    @Override
    public void perform() {
        this.commandChain.add(this);
        P.p.cmdAutoHelp.execute(this.sender, this.args, this.commandChain);
    }
}

