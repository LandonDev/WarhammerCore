/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.CommandVisibility;
import com.massivecraft.factions.zcore.MCommand;

import java.util.ArrayList;
import java.util.Iterator;

public class CmdAutoHelp
        extends MCommand<P> {
    public CmdAutoHelp() {
        super(P.p);
        this.aliases.add("?");
        this.aliases.add("h");
        this.aliases.add("help");
        this.setHelpShort("");
        this.optionalArgs.put("page", "1");
    }

    @Override
    public void perform() {
        if (this.commandChain.size() != 0) {
            MCommand<?> pcmd = this.commandChain.get(this.commandChain.size() - 1);
            ArrayList<String> lines = new ArrayList<>();
            lines.addAll(pcmd.helpLong);
            Iterator var3 = pcmd.subCommands.iterator();

            while (true) {
                MCommand scmd;
                do {
                    if (!var3.hasNext()) {
                        this.sendMessage(this.p.txt.getPage(lines, this.argAsInt(0, 1), "Help for command \"" + pcmd.aliases.get(0) + "\""));
                        return;
                    }

                    scmd = (MCommand) var3.next();
                } while (scmd.visibility != CommandVisibility.VISIBLE && (scmd.visibility != CommandVisibility.SECRET || !scmd.validSenderPermissions(this.sender, false)));

                lines.add(scmd.getUseageTemplate(this.commandChain, true));
            }
        }
    }
}


