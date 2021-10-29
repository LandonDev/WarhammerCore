package com.massivecraft.factions.cmd;

/**
 * Created by FactionsUUID Team
 */

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.Permission;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.*;

public class CmdClaimFill extends FCommand {

    public CmdClaimFill() {

        // Aliases
        this.aliases.add("claimfill");
        this.optionalArgs.put("faction", "your");
        this.permission = Permission.CLAIM.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
        // Args
        this.optionalArgs.put("faction", "you");

    }

    @Override
    public void perform() {
        // Args
        final int limit = this.argAsInt(0, 25);

        if (limit > 25) {
            this.msg(ChatColor.translateAlternateColorCodes('&', "&cThe maximum limit for claim fill is %s."), 25);
            return;
        }

        final Faction forFaction = this.argAsFaction(2, fme.getFaction());
        Location location = fme.getPlayer().getLocation();
        FLocation loc = new FLocation(location);

        Faction currentFaction = Board.getInstance().getFactionAt(loc);

        if (currentFaction.equals(forFaction)) {
            this.msg(ChatColor.translateAlternateColorCodes('&', "%s already own this land."), forFaction.describeTo(fme, true));
            return;
        }

        if (!currentFaction.isWilderness()) {
            this.msg(ChatColor.translateAlternateColorCodes('&', "&cCannot claim fill using already claimed land!."));
            return;
        }

        if (!fme.isAdminBypassing()) {
            this.msg(ChatColor.translateAlternateColorCodes('&', "You can't claim land for %s."), forFaction.describeTo(fme));
            return;
        }

        final double distance = 5;
        long startX = loc.getX();
        long startZ = loc.getZ();

        Set<FLocation> toClaim = new HashSet<>();
        Queue<FLocation> queue = new LinkedList<>();
        FLocation currentHead;
        queue.add(loc);
        toClaim.add(loc);
        while (!queue.isEmpty() && toClaim.size() <= limit) {
            currentHead = queue.poll();

            if (Math.abs(currentHead.getX() - startX) > distance || Math.abs(currentHead.getZ() - startZ) > distance) {
                this.msg(ChatColor.translateAlternateColorCodes('&', "&cThis fill would exceed the maximum distance of %.2f"), distance);
                return;
            }

            addIf(toClaim, queue, currentHead.getRelative(0, 1));
            addIf(toClaim, queue, currentHead.getRelative(0, -1));
            addIf(toClaim, queue, currentHead.getRelative(1, 0));
            addIf(toClaim, queue, currentHead.getRelative(-1, 0));
        }

        if (toClaim.size() > limit) {
            this.msg(ChatColor.translateAlternateColorCodes('&',"&cThis claim would exceed the limit!"));
            return;
        }

        if (toClaim.size() > fme.getFaction().getPowerRounded() - fme.getFaction().getLandRounded()) {
            this.msg(ChatColor.translateAlternateColorCodes('&', "%s &cdoes not have enough land left to make %d claims"), forFaction.describeTo(fme), toClaim.size());
            return;
        }

        final int limFail = 9;
        FPlayer fPlayer = this.fme;
        Player player = this.me;
        int fails = 0;
        for (FLocation currentLocation : toClaim) {
            if (!fPlayer.attemptClaimAsync(forFaction, this.me.getLocation(), true)) {
                fails++;
            }
            if (fails >= limFail) {
                this.msg(ChatColor.translateAlternateColorCodes('&', "&cAborting claim fill after %d failures"), fails);
                return;
            }
        }
    }

    private void addIf(Set<FLocation> toClaim, Queue<FLocation> queue, FLocation examine) {
        if (Board.getInstance().getFactionAt(examine).isWilderness() && !toClaim.contains(examine)) {
            toClaim.add(examine);
            queue.add(examine);
        }
    }

}