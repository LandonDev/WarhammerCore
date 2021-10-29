/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.Material
 */
package com.massivecraft.factions.struct;

import com.massivecraft.factions.util.HeadFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum FLogType {
    INVITES("Roster Edits", Material.BOOK_AND_QUILL, "&e%s&7 &a%s&7 &e%s", 3),
    BANS("Player Bans", Material.ANVIL, "&e%s&7 &e%s&6 &e%s", 3),
    CHUNK_CLAIMS("Claim Edits", Material.WOOD_AXE, "&e%s&7 %s&7 &e%s&7 near &e%s", 3),
    PERM_EDIT_DEFAULTS("Default Perm Edits", Material.WRITTEN_BOOK, "&e%s&7 %s&7 &e%s&7 for &e%s", 4),
    PERM_EDIT_CHUNK("Chunk Perm Edits", Material.BOOK, "&e%s&7 %s&7 &e%s&7 for &e%s &7at &e%s", 5),
    PERM_EDIT_CHUNK_ACCESS("Chunk Access Edits", Material.WOOD_DOOR, "&e%s&7 %s&7 at &e%s&7 for &e%s", 4),
    BANK_EDIT("/f money Edits", Material.GOLD_INGOT, "&e%s&7 %s &e&l$&e%s", 3),
    FCHEST_EDIT("/f chest Edits", Material.CHEST, "&e%s&7 %s &f%s", 3),
    RELATION_CHANGE("Relation Edits", Material.GOLD_SWORD, "&e%s %s&e'd %s", 3),
    FTAG_EDIT("/f tag Edits", Material.NAME_TAG, "&e%s&7 set to &e'%s'", 2),
    FDESC_EDIT("/f desc Edits", Material.PAPER, "&e%s&7 set to &e'%s'", 2),
    SELLING("/sell Uses", Material.DIAMOND, "&e%s&7 sold &e%s&7 for &a&l$&a%s", 3),
    ROLE_PERM_EDIT("/f role Edits", Material.WRITTEN_BOOK, "&e%s&7&e %s &e%s", 3),
    SPAWNER_EDIT("Spawner Edits", Material.MOB_SPAWNER, "&e%s&7 %s &e%s&7 %s", 4),
    RANK_EDIT("Rank Edits", Material.GOLD_HELMET, "&e%s&7 set &e%s&7 to %s", 3),
    F_POINTS("/f point Edits", Material.BOOK, "&e%s&7 %s &e%s", 3),
    F_TNT("/f tnt Edits", Material.TNT, "&e%s&7 %s &e%s", 3);


    private String displayName;
    private Material displayMaterial;
    private String msg;
    private int requiredArgs;

    FLogType(String displayName, Material displayMaterial, String msg, int requiredArgs) {
        this.displayName = displayName;
        this.displayMaterial = displayMaterial;
        this.msg = msg;
        this.requiredArgs = requiredArgs;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Material getDisplayMaterial() {
        return this.displayMaterial;
    }

    public String getMsg() {
        return this.msg;
    }

    public int getRequiredArgs() {
        return this.requiredArgs;
    }
}

