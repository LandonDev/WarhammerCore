/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.listeners.menu.faccess.FAccessChunkList;
import com.massivecraft.factions.listeners.menu.faccess.FAccessFactionMenu;
import com.massivecraft.factions.listeners.menu.faccess.FAccessMainMenu;
import com.massivecraft.factions.listeners.menu.faccess.FAccessPlayerMenu;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.FactionPermissions;
import com.massivecraft.factions.struct.PermissionManager;
import com.massivecraft.factions.struct.Relation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class CmdAccess
        extends FCommand {
    public CmdAccess() {
        this.aliases.add("access");
        this.optionalArgs.put("f/p/list/clear", "p");
        this.optionalArgs.put("name", "");
        this.optionalArgs.put("yes/no/all/none", "");
        this.setHelpShort(ChatColor.GRAY + "Gives a specific faction or player access to a chunk.");
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = true;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (this.args.size() <= 0 && this.myFaction != null) {
            new FAccessMainMenu(this.myFaction, this.fme).open((Player) this.sender);
            return;
        }
        String access = this.argAsString(0, "p");
        if (this.args.size() == 1) {
            if (access.equals("p")) {
                new FAccessPlayerMenu((Player) this.sender, this.myFaction).open((Player) this.sender);
                return;
            }
            if (access.equals("f")) {
                new FAccessFactionMenu((Player) this.sender, this.myFaction).open((Player) this.sender);
                return;
            }
        }
        String name = this.argAsString(1, "");
        if (this.args.size() == 2) {
            if (access.equalsIgnoreCase("f")) {
                Faction fac = this.argAsFaction(1);
                if (fac != null && fac.isNormal()) {
                    if (this.myFaction.getRelationTo(fac) == Relation.ENEMY) {
                        this.sender.sendMessage(ChatColor.RED + "You cannot grant enemy factions access!");
                        return;
                    }
                    FactionPermissions permissions = PermissionManager.get().getPermissions(this.myFaction);
                    if (permissions.getFactionDefaultPermissions().containsKey(fac.getId())) {
                        this.sender.sendMessage(ChatColor.RED + fac.getTag() + " already has a default permission list!");
                        this.sender.sendMessage(ChatColor.GRAY + "Use '/f access f' to view their permissions!");
                    } else {
                        permissions.getFactionDefaultPermissions().put(fac.getId(), permissions.getDefaultPermissions());
                        this.sender.sendMessage(ChatColor.RED + fac.getTag() + " has been granted a default permission set!");
                    }
                    return;
                }
                this.sender.sendMessage(ChatColor.RED + "No valid faction found with that name!");
            } else if (access.equalsIgnoreCase("p")) {
                FPlayer fplayer = FPlayers.i.get(name);
                if (fplayer != null) {
                    UUID uuid = fplayer.getCachedUUID();
                    if (uuid == null) {
                        this.sender.sendMessage(ChatColor.RED + "Unable to find proper data for that player!");
                        return;
                    }
                    if (this.myFaction.getRelationTo(fplayer) == Relation.ENEMY) {
                        this.sender.sendMessage(ChatColor.RED + "You cannot grant enemy factions access!");
                        return;
                    }
                    FactionPermissions permissions = PermissionManager.get().getPermissions(this.myFaction);
                    if (permissions.getDefaultPlayerPermissions().containsKey(uuid)) {
                        this.sender.sendMessage(ChatColor.RED + fplayer.getNameAsync() + " already has a default permission list!");
                        this.sender.sendMessage(ChatColor.GRAY + "Use '/f perm' to view their permissions!");
                    } else {
                        permissions.getDefaultPlayerPermissions().put(uuid, permissions.getDefaultPermissions());
                        this.sender.sendMessage(ChatColor.RED + fplayer.getNameAsync() + " has been granted a default permission set!");
                    }
                    return;
                }
                this.sender.sendMessage(ChatColor.RED + "No valid player found with that name!");
            }
        }
        String action = this.argAsString(2, "");
        if (this.sender instanceof Player) {
            Player p = (Player) this.sender;
            FPlayer fp = FPlayers.i.get(p);
            if (ChatColor.stripColor(fp.getFaction().getTag().toLowerCase()).equals("wilderness")) {
                p.sendMessage(ChatColor.RED + "You are not in a faction.");
                return;
            }
            Faction f = fp.getFaction();
            FactionWrapper wrapper = FactionWrappers.get(f.getId());
            FLocation fl = new FLocation(p.getLocation());
            if (Board.getFactionAt(fl) != f) {
                p.sendMessage(ChatColor.RED + "You must own the chunk you are trying to edit.");
                return;
            }
            if (Board.getAllClaimedLand(f).size() <= 0) {
                p.sendMessage(ChatColor.RED + "Your faction does not have any claimed land.");
                return;
            }
            if (access.equalsIgnoreCase("list")) {
                if (wrapper.isLocationTracked(fl)) {
                    List<String> factions = wrapper.getFactionAccess(fl);
                    List<String> players = wrapper.getPlayerAccess(fl);
                    if (factions.size() <= 0 && players.size() <= 0) {
                        p.sendMessage(ChatColor.RED + "Nobody has access to this chunk.");
                        return;
                    }
                    new FAccessChunkList(fp, this.myFaction, fl).open(p);
                } else {
                    p.sendMessage(ChatColor.RED + "Nobody has access to this chunk.");
                }
                return;
            }
            if (access.equalsIgnoreCase("clear")) {
                if (wrapper.isLocationTracked(fl) && wrapper.chunkClaims.get(fl).size() > 0) {
                    wrapper.chunkClaims.remove(fl);
                    FactionPermissions perms = PermissionManager.get().getPermissions(f);
                    perms.cleanupPermissions(fl);
                    p.sendMessage(ChatColor.RED + "All access has been cleared for this chunk.");
                    P.p.logFactionEvent(f, FLogType.PERM_EDIT_CHUNK_ACCESS, this.fme.getNameAsync(), ChatColor.RED + ChatColor.BOLD.toString() + "CLEARED", fl.getCoordString(), "ALL");
                } else {
                    p.sendMessage(ChatColor.RED + "Nobody has access to this chunk.");
                }
                return;
            }
            if (name.isEmpty() || access.isEmpty()) {
                p.sendMessage(ChatColor.RED + "Invalid arguments.");
                p.sendMessage(ChatColor.RED + "/f access <p/f/list/clear> <name> <yes/no/none/all>");
                return;
            }
            if (access.equalsIgnoreCase("f") || access.equalsIgnoreCase("factions")) {
                Faction otherFac = this.argAsFaction(1);
                if (this.myFaction.getRelationTo(otherFac) == Relation.ENEMY) {
                    this.sender.sendMessage(ChatColor.RED + "You cannot grant enemy factions access!");
                    return;
                }
                this.attemptFactionAccess(p, f, otherFac, wrapper, fl, action);
            } else if (action.isEmpty()) {
                this.sender.sendMessage(ChatColor.RED + "Invalid arguments.");
                this.sender.sendMessage(ChatColor.RED + "/f access <p/f/list/clear> <name> <yes/no/none/all>");
            } else {
                this.attemptFPlayerAccess(p, f, name, wrapper, fl, action);
            }
        }
    }

    public void attemptFactionAccess(Player p, Faction f, Faction otherFac, FactionWrapper wrapper, FLocation fl, String action) {
        if (otherFac == null || otherFac.isWarZone() || otherFac.isSafeZone()) {
            p.sendMessage(ChatColor.RED + "That is " + ChatColor.RED + ChatColor.UNDERLINE.toString() + "not" + ChatColor.RED + " a valid faction.");
            return;
        }
        if (action.equalsIgnoreCase("yes")) {
            if (wrapper.addFactionAccess(fl, otherFac)) {
                f.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "(!) " + ChatColor.GREEN + "The faction " + otherFac.getTag() + " now has access to the claim " + fl.formatXAndZ("") + "! Use /f perm to customize what they can edit in this chunk.");
                otherFac.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "(!) " + ChatColor.GREEN + "Your faction now has access to " + f.getTag() + "'s claimed chunk at " + fl.formatXAndZ(",!"));
                FactionPermissions perms = PermissionManager.get().getPermissions(f);
                perms.createDefaultPermissions(fl, otherFac);
                P.p.logFactionEvent(f, FLogType.PERM_EDIT_CHUNK_ACCESS, this.fme.getNameAsync(), ChatColor.GREEN + ChatColor.BOLD.toString() + "GRANTED", fl.formatXAndZ(","), otherFac.getTag());
            } else {
                p.sendMessage(ChatColor.RED + "That faction already has access to this chunk.");
            }
        } else if (action.equalsIgnoreCase("no")) {
            if (wrapper.removeFactionAccess(fl, otherFac)) {
                f.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "The faction " + otherFac.getTag() + " no longer has access to the chunk x" + (int) fl.getX() * 16 + ", z" + (int) fl.getZ() * 16 + "!");
                otherFac.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "Your faction no longer has access to " + f.getTag() + "'s claimed chunk at x" + (int) fl.getX() * 16 + ", z" + (int) fl.getZ() * 16 + "!");
                FactionPermissions perms = PermissionManager.get().getPermissions(f);
                perms.removePermissions(fl, otherFac);
                P.p.logFactionEvent(f, FLogType.PERM_EDIT_CHUNK_ACCESS, this.fme.getNameAsync(), ChatColor.RED + ChatColor.BOLD.toString() + "REMOVED", fl.formatXAndZ(","), otherFac.getTag());
            } else {
                p.sendMessage(ChatColor.RED + "That faction does not have access to this chunk.");
            }
        } else if (action.equalsIgnoreCase("all")) {
            FactionPermissions perms = PermissionManager.get().getPermissions(f);
            for (FLocation all : Board.getAllClaimedLand(f)) {
                wrapper.addFactionAccess(all, otherFac);
                perms.createDefaultPermissions(all, otherFac);
            }
            f.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "(!) " + ChatColor.YELLOW + "The faction " + otherFac.getTag() + " now has access to all of your land.");
            otherFac.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "(!) " + ChatColor.YELLOW + "Your faction now has access to all of " + f.getTag() + "'s land.");
            P.p.logFactionEvent(f, FLogType.PERM_EDIT_CHUNK_ACCESS, this.fme.getNameAsync(), ChatColor.GREEN + ChatColor.BOLD.toString() + "GRANTED", "ALL CHUNKS", otherFac.getTag());
        } else if (action.equalsIgnoreCase("none")) {
            int removed = 0;
            FactionPermissions perms = PermissionManager.get().getPermissions(f);
            for (FLocation l : wrapper.chunkClaims.keySet()) {
                if (wrapper.doesFactionHaveAccess(l, otherFac)) {
                    ++removed;
                    wrapper.removeFactionAccess(l, otherFac);
                }
                perms.removePermissions(l, otherFac);
            }
            if (removed > 0) {
                f.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + ChatColor.UNDERLINE.toString() + otherFac.getTag() + ChatColor.RED + " no longer has access to any of your land.");
                otherFac.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "Your faction no longer has access to " + ChatColor.RED + ChatColor.UNDERLINE.toString() + "any" + ChatColor.RED + " of " + f.getTag() + "'s land!");
                P.p.logFactionEvent(f, FLogType.PERM_EDIT_CHUNK_ACCESS, this.fme.getNameAsync(), ChatColor.RED + ChatColor.BOLD.toString() + "REMOVED", "ALL CHUNKS", otherFac.getTag());
            } else {
                p.sendMessage(ChatColor.RED + "That faction did not have access to any of your land.");
            }
        }
    }

    public void attemptFPlayerAccess(Player p, Faction f, String name, FactionWrapper wrapper, FLocation fl, String action) {
        String showname;
        FPlayer otherplayer = this.argAsBestFPlayerMatch(1);
        if (otherplayer == null) {
            p.sendMessage(ChatColor.RED + "That player does not exist.");
            return;
        }
        if (name != null && name.length() > 16) {
            p.sendMessage(ChatColor.RED + "Player names can not be longer then 16 characters.");
            return;
        }
        if (otherplayer.getRelationTo(f) == Relation.ENEMY) {
            p.sendMessage(ChatColor.RED + "You cannot grant enemy factions access!");
            return;
        }
        String string = showname = otherplayer.getNameAsync().contains("-") ? name : otherplayer.getNameAsync();
        if (p.getName().equalsIgnoreCase(showname)) {
            p.sendMessage(ChatColor.RED + "You can not add yourself to a faction chunk.");
            return;
        }
        if (action.equalsIgnoreCase("yes")) {
            if (wrapper.addPlayerAccess(fl, otherplayer)) {
                f.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "(!) " + ChatColor.GREEN + "The player " + showname + " now has access to the claim " + fl.formatXAndZ(",") + "! Use /f perm to customize what they can edit in this chunk.");
                otherplayer.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "(!) " + ChatColor.GREEN + "You now have access to " + f.getTag() + "'s claimed chunk at x" + (int) fl.getX() * 16 + ", z" + (int) fl.getZ() * 16 + "!");
                FactionPermissions perms = PermissionManager.get().getPermissions(f);
                perms.createDefaultPermissions(fl, otherplayer);
                P.p.logFactionEvent(f, FLogType.PERM_EDIT_CHUNK_ACCESS, this.fme.getNameAsync(), ChatColor.GREEN + ChatColor.BOLD.toString() + "GRANTED", fl.formatXAndZ(","), showname);
            } else {
                p.sendMessage(ChatColor.RED + "That player already has access to this chunk.");
            }
        } else if (action.equalsIgnoreCase("no")) {
            if (wrapper.removePlayerAccess(fl, otherplayer)) {
                p.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "The player " + showname + " no longer has access to the chunk x" + (int) fl.getX() * 16 + ", z" + (int) fl.getZ() * 16 + "!");
                otherplayer.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "You no longer have access to " + f.getTag() + "'s claimed chunk at x" + (int) fl.getX() * 16 + ", z" + (int) fl.getZ() * 16 + "!");
                FactionPermissions perms = PermissionManager.get().getPermissions(f);
                perms.removePermissions(fl, otherplayer);
                P.p.logFactionEvent(f, FLogType.PERM_EDIT_CHUNK_ACCESS, this.fme.getNameAsync(), ChatColor.RED + ChatColor.BOLD.toString() + "REMOVED", fl.formatXAndZ(","), showname);
            } else {
                p.sendMessage(ChatColor.RED + "That player does not have access to this chunk.");
            }
        } else if (action.equalsIgnoreCase("all")) {
            FactionPermissions perms = PermissionManager.get().getPermissions(f);
            for (FLocation all : Board.getAllClaimedLand(f)) {
                wrapper.addPlayerAccess(all, otherplayer);
                perms.createDefaultPermissions(all, otherplayer);
            }
            f.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "(!) " + ChatColor.GREEN + "The player " + showname + " now has access all of your claimed land!");
            otherplayer.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "(!) " + ChatColor.YELLOW + "You now have access to all of " + f.getTag() + "'s claimed land.");
            P.p.logFactionEvent(f, FLogType.PERM_EDIT_CHUNK_ACCESS, this.fme.getNameAsync(), ChatColor.GREEN.toString() + ChatColor.BOLD + "GRANTED", "ALL CHUNKS", showname);
        } else if (action.equalsIgnoreCase("none")) {
            int removed = 0;
            FactionPermissions perms = PermissionManager.get().getPermissions(f);
            for (FLocation l : wrapper.chunkClaims.keySet()) {
                if (wrapper.doesPlayerHaveChunkAccess(l, otherplayer, false)) {
                    ++removed;
                    wrapper.removePlayerAccess(l, otherplayer);
                }
                perms.removePermissions(l, otherplayer);
            }
            if (removed > 0) {
                f.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + ChatColor.UNDERLINE.toString() + showname + ChatColor.RED + " no longer has access to any of your land.");
                otherplayer.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "(!) " + ChatColor.RED + "You no longer have access to " + ChatColor.RED + ChatColor.UNDERLINE.toString() + "any" + ChatColor.RED + " of " + f.getTag() + "'s land!");
                P.p.logFactionEvent(f, FLogType.PERM_EDIT_CHUNK_ACCESS, this.fme.getNameAsync(), ChatColor.RED.toString() + ChatColor.BOLD + "REMOVED", "ALL CHUNKS", showname);
            } else {
                p.sendMessage(ChatColor.RED + "That player did not have access to any of your land.");
            }
        }
    }
}

