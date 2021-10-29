/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.cosmicpvp.cosmicutils.utils.CC
 *  org.arkhamnetwork.Arkkit.patches.combatlog.CombatLog
 *  org.arkhamnetwork.Arkkit.plugins.ArkPlugin
 *  org.arkhamnetwork.Arkkit.plugins.ArkPlugins
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Chest
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 */
package com.massivecraft.factions.cmd;

import com.cosmicpvp.cosmicutils.utils.CC;
import com.google.common.collect.Sets;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.FLogType;
import com.massivecraft.factions.struct.RolePerm;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.text.DecimalFormat;

public class CmdTNT
        extends FCommand {
    private final DecimalFormat bigNumber = new DecimalFormat("#,###");

    public CmdTNT() {
        this.aliases.add("tnt");
        this.errorOnToManyArgs = false;
        this.disableOnLock = true;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction fac = null;
        if (fac == null) {
            fac = this.fme.getFaction();
        }
        if (!fac.isNormal()) {
            this.sender.sendMessage(CC.RedB + "(!) " + CC.Red + "You must be in a faction to use this command!");
            return;
        }
        if (this.args.size() == 0) {
            this.sender.sendMessage(CC.YellowB + "/f tnt: " + CC.White + this.bigNumber.format(fac.tnt / 64) + "x stacks " + CC.Gray + "(" + this.bigNumber.format(fac.tnt) + " total)");
            this.sender.sendMessage(CC.Yellow + "Usage: " + CC.Gray + "/f tnt [w/d] [#/all]");
            this.sender.sendMessage(CC.Gray + "(w = withdraw, d = deposit, # = stacks)");
            this.sender.sendMessage(CC.Gray + "The \"deposit\" option defaults to your own inventory, but will execute on any target chest you have access to.");
            return;
        }
        if (this.args.size() >= 1) {
            String opperation = this.args.get(0);
            int stacks = this.args.size() >= 2 ? (this.args.get(1).equalsIgnoreCase("all") ? -1 : Integer.parseInt(this.args.get(1))) : -1;
            int totalTNT = fac.tnt;
            int tntStacks = totalTNT / 64;
            if (opperation.equalsIgnoreCase("w")) {
                int stacksToWithdraw;
                if (totalTNT < 64) {
                    this.sender.sendMessage(CC.RedB + "(!) " + CC.Red + "You do not have any /f tnt stacks to withdraw!");
                    this.sender.sendMessage(CC.Gray + "You can only withdraw /f tnt in stacks of 64. There are less than 64x TNT in your /f tnt.");
                    return;
                }
                if (!this.checkHasPerm(this.fme, fac, RolePerm.F_TNT_WITHDRAW)) {
                    return;
                }
                int playerSlots = this.getEmptyInventorySlots(this.fme.getPlayer());
                int x = stacksToWithdraw = stacks == -1 ? Math.min(playerSlots, tntStacks) : Math.min(playerSlots, Math.min(tntStacks, stacks));
                while (x-- > 0) {
                    fac.tnt -= 64;
                    this.fme.getPlayer().getInventory().addItem(new ItemStack(Material.TNT, 64));
                }
                this.sender.sendMessage(CC.YellowB + "(!) " + CC.Yellow + "Withdrew " + stacksToWithdraw + "x stacks from /f tnt! " + CC.Gray + "[-" + stacksToWithdraw * 64 + " TNT]");
                P.p.getFlogManager().log(fac, FLogType.F_TNT, this.sender.getName(), "WITHDREW", stacks + "x TNT");
                return;
            }
            if (opperation.equalsIgnoreCase("d")) {
                if (!this.checkHasPerm(this.fme, fac, RolePerm.F_TNT_DEPOSIT)) {
                    return;
                }
                int playerTNTStacks = this.getTNTStacksInPlayerInventory(this.fme.getPlayer());
                int tntToDeposit = stacks == -1 ? playerTNTStacks : Math.min(playerTNTStacks, stacks);
                int stacksDeposited = 0;
                PlayerInventory targetInventory = this.fme.getPlayer().getInventory();
                Block targetBlock = this.fme.getPlayer().getTargetBlock(Sets.newHashSet(Material.AIR), 5);
                if (targetBlock.getState() instanceof Chest) {
                    Chest c = (Chest) this.fme.getPlayer().getTargetBlock(Sets.newHashSet(Material.AIR), 5).getState();
                    int chestTntStacks = this.getTNTStacksInInventory(c.getInventory());
                    if (chestTntStacks > 0) {
                        tntToDeposit = stacks == -1 ? chestTntStacks : Math.min(chestTntStacks, stacks);
                        targetInventory = (PlayerInventory) c.getInventory();
                    } else {
                        this.sender.sendMessage(CC.YellowB + "(!) " + CC.Yellow + "No TNT (stacks) in target chest!");
                        return;
                    }
                }
                for (int x = tntToDeposit; x > 0 && targetInventory.contains(new ItemStack(Material.TNT, 64)); --x) {
                    targetInventory.setItem(targetInventory.first(new ItemStack(Material.TNT, 64)), new ItemStack(Material.AIR));
                    fac.tnt += 64;
                    ++stacksDeposited;
                }
                this.sender.sendMessage(CC.GreenB + "(!) " + CC.Green + "Deposited " + stacksDeposited + "x stacks to /f tnt! " + CC.Gray + "[+" + stacksDeposited * 64 + " TNT]");
            }
        }
    }

    private int getEmptyInventorySlots(Player p) {
        int emptySlots = 0;
        for (int i = 0; i < 36; ++i) {
            if (p.getInventory().getItem(i) != null && p.getInventory().getItem(i).getType() != Material.AIR) continue;
            ++emptySlots;
        }
        return emptySlots;
    }

    private int getTNTStacksInPlayerInventory(Player p) {
        int playerTNT = 0;
        for (int i = 0; i < 36; ++i) {
            ItemStack is = p.getInventory().getItem(i);
            if (is == null || is.getType() != Material.TNT) continue;
            playerTNT += is.getAmount();
        }
        return Math.floorDiv(playerTNT, 64);
    }

    private int getTNTStacksInInventory(Inventory inventory) {
        int TNT = 0;
        for (int i = 0; i < inventory.getSize(); ++i) {
            ItemStack is = inventory.getItem(i);
            if (is == null || is.getType() != Material.TNT) continue;
            TNT += is.getAmount();
        }
        return Math.floorDiv(TNT, 64);
    }
}

