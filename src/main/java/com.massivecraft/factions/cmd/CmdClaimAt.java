package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

/**
 * @author Saser
 */
public class CmdClaimAt extends FCommand {

    /**
     * @author FactionsUUID Team
     */

    public CmdClaimAt() {
        super();
        this.aliases.add("claimat");

        this.requiredArgs.add("world");
        this.requiredArgs.add("x");
        this.requiredArgs.add("z");
        this.permission = Permission.CLAIM.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;

    }

    @Override
    public void perform() {
        int x = argAsInt(1);
        int z = argAsInt(2);
        int claimed = 0;
            try {
                if (this.fme.attemptClaimAsync(myFaction, new Location(fme.getPlayer().getWorld(), FLocation.chunkToBlock(x), 100, FLocation.chunkToBlock(z)), true)) {
                    showMap();
                    ++claimed;
                }
            } catch (Exception err) {
                err.printStackTrace();
            }

        P.p.logFactionEvent(myFaction, FLogType.CHUNK_CLAIMS, this.fme.getNameAsync(), CC.GreenB + "CLAIMED", String.valueOf(claimed), new FLocation(this.me.getLocation()).formatXAndZ(","));

    }

    public void showMap() {
        fme.sendFancyMessage(Board.getMap(this.myFaction, new FLocation(this.fme), this.fme.getPlayer()));
    }
}
