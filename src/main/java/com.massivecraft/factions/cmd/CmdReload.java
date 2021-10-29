/*
 * Decompiled with CFR 0.145.
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Permission;

public class CmdReload
        extends FCommand {
    public CmdReload() {
        this.aliases.add("reload");
        this.optionalArgs.put("file", "all");
        this.permission = Permission.RELOAD.node;
        this.disableOnLock = false;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        long timeInitStart = System.currentTimeMillis();
        Conf.load();
        long timeReload = System.currentTimeMillis() - timeInitStart;
        this.msg("<i>Reloaded <h>conf.json <i>from disk, took <h>%dms<i>.", timeReload);
    }
}

