/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.BlockIterator
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CmdClaimLine
        extends FCommand {
    HashSet<Byte> seeThrough = new HashSet<>();

    public CmdClaimLine() {
        for (Material mat : Material.values()) {
            this.seeThrough.add((byte) mat.getId());
        }
        this.setHelpShort("Claim land in the direction you are looking.");
        this.aliases.add("claimline");
        this.optionalArgs.put("faction", "your");
        this.requiredArgs.add("amount");
        this.permission = Permission.CLAIM.node;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction forFaction = this.argAsFaction(1, this.myFaction);
        int line = this.argAsInt(0, 1);
        if (line < 1) {
            this.msg("<b>If you specify a chunk line, it must be at least 1.");
            return;
        }
        if (line > 1 && this.sender instanceof Player) {
            Player p = (Player) this.sender;
            int blocks = line * 16;
            if (line > 20 && !p.isOp()) {
                this.msg("<b>You can only claim 20 chunks in your line of sight at a time.");
                return;
            }
            List<Block> b = this.getLineOfSight(p, this.seeThrough, blocks);
            ArrayList<Chunk> toClaim = new ArrayList<Chunk>();
            for (Block bl : b) {
                if (toClaim.contains(bl.getChunk())) continue;
                if (CmdClaim.chunkCoordsPendingClaim.contains(bl.getChunk().getX() + "," + bl.getChunk().getZ())) {
                    this.msg("<b>A chunk in your line already has an /f claim pending on it.");
                    return;
                }
                CmdClaim.chunkCoordsPendingClaim.add(bl.getChunk().getX() + "," + bl.getChunk().getZ());
                toClaim.add(bl.getChunk());
            }
            int claimed = 0;
            for (Chunk c : toClaim) {
                try {
                    if (this.fme.attemptClaimAsync(forFaction, new Location(c.getWorld(), c.getX() * 16, 0.0, c.getZ() * 16), true)) {
                        ++claimed;
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }
                CmdClaim.chunkCoordsPendingClaim.remove(c.getX() + "," + c.getZ());
            }
            P.p.logFactionEvent(forFaction, FLogType.CHUNK_CLAIMS, this.fme.getNameAsync(), CC.GreenB + "CLAIMED", String.valueOf(claimed), new FLocation(this.me.getLocation()).formatXAndZ(","));
            return;
        }
    }

    private List<Block> getLineOfSight(Player p, HashSet<Byte> transparent, int maxDistance) {
        ArrayList<Block> blocks = new ArrayList<Block>();
        BlockIterator itr = new BlockIterator(p, maxDistance);
        while (itr.hasNext()) {
            Block block = itr.next();
            blocks.add(block);
        }
        return blocks;
    }
}

