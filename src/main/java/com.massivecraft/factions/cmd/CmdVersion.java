/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.plugin.PluginDescriptionFile
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;

public class CmdVersion
        extends FCommand {
    public CmdVersion() {
        this.aliases.add("version");
        this.aliases.add("ver");
        this.permission = Permission.VERSION.node;
        this.disableOnLock = false;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        this.msg("<i>You are running " + P.p.getDescription().getFullName());
    }
}

