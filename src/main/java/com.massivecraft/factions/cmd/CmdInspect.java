package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

/**
 * Factions-0.1_source_from_Driftay - Developed by Driftay.
 * All rights reserved 2020.
 * Creation Date: 2/25/2020
 */
public class CmdInspect extends FCommand {

    public CmdInspect() {
        this.aliases.add("inspect");
        this.disableOnLock = false;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (fme.isInspectMode()) {
            fme.setInspectMode(false);
            fme.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[!]&7 Inspect mode is now &cDisabled."));
        } else {
            fme.setInspectMode(true);
            fme.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l[!]&7 Inspect mode is now &aEnabled."));
        }
    }
}
