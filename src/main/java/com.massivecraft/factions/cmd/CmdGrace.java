package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;

public class CmdGrace extends FCommand {

    public CmdGrace() {
        this.aliases.add("grace");
        this.disableOnLock = false;
        this.permission = Permission.GRACE.node;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Conf.gracePeriod = !Conf.gracePeriod;
        fme.msg("&8» &7Grace period is now &c%1$s", Conf.gracePeriod ? P.color("&aEnabled") : P.color("&4Disabled"));
    }
}
