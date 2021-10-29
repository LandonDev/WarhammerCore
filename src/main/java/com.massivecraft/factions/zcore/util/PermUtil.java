/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.permissions.Permission
 *  org.bukkit.plugin.PluginDescriptionFile
 */
package com.massivecraft.factions.zcore.util;

import com.massivecraft.factions.zcore.MPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.HashMap;
import java.util.Map;

public class PermUtil {
    public Map<String, String> permissionDescriptions = new HashMap<String, String>();
    protected MPlugin p;

    public PermUtil(MPlugin p) {
        this.p = p;
        this.setup();
    }

    public String getForbiddenMessage(String perm) {
        return this.p.txt.parse("<b>You don't have permission to %s.", this.getPermissionDescription(perm));
    }

    public final void setup() {
        for (Permission permission : this.p.getDescription().getPermissions()) {
            this.permissionDescriptions.put(permission.getName(), permission.getDescription());
        }
    }

    public String getPermissionDescription(String perm) {
        String desc = this.permissionDescriptions.get(perm);
        if (desc == null) {
            return "do that";
        }
        return desc;
    }

    public boolean has(CommandSender me, String perm) {
        if (me == null) {
            return false;
        }
        return me.hasPermission(perm);
    }

    public boolean has(CommandSender me, String perm, boolean informSenderIfNot) {
        if (this.has(me, perm)) {
            return true;
        }
        if (informSenderIfNot && me != null) {
            me.sendMessage(this.getForbiddenMessage(perm));
        }
        return false;
    }

    public <T> T pickFirstVal(CommandSender me, Map<String, T> perm2val) {
        if (perm2val == null) {
            return null;
        }
        T ret = null;
        for (Map.Entry<String, T> entry : perm2val.entrySet()) {
            ret = entry.getValue();
            if (!this.has(me, entry.getKey())) continue;
            break;
        }
        return ret;
    }
}

