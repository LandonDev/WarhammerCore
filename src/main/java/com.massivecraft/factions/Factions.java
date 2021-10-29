/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.craftbukkit.libs.com.google.gson.Gson
 *  org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken
 */
package com.massivecraft.factions;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.zcore.persist.EntityCollection;
import com.massivecraft.factions.zcore.util.TextUtil;
import javafx.beans.binding.MapExpression;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class Factions
        extends EntityCollection<Faction> {
    public static Factions i = new Factions();
    public static transient List<String> nullFactions = new ArrayList<>();
    P p = P.p;
    private Random random = new Random();

    private Factions() {
        super(Faction.class, new CopyOnWriteArrayList<>(), new ConcurrentHashMap<>(), new File(P.p.getDataFolder(), "factions.json"), P.p.gson);
    }

    public static ArrayList<String> validateTag(String str) {
        ArrayList<String> errors = new ArrayList<String>();
        if (MiscUtil.getComparisonString(str).length() < Conf.factionTagLengthMin) {
            errors.add(P.p.txt.parse("<i>The faction tag can't be shorter than <h>%s<i> chars.", Conf.factionTagLengthMin));
        }
        if (str.length() > Conf.factionTagLengthMax) {
            errors.add(P.p.txt.parse("<i>The faction tag can't be longer than <h>%s<i> chars.", Conf.factionTagLengthMax));
        }
        for (char c : str.toCharArray()) {
            if (MiscUtil.substanceChars.contains(String.valueOf(c))) continue;
            errors.add(P.p.txt.parse("<i>Faction tag must be alphanumeric. \"<h>%s<i>\" is not allowed.", c));
        }
        return errors;
    }




    @Override
    public Type getMapType() {
        return new TypeToken<Map<String, Faction>>() {
        }.getType();
    }



    @Override
    public boolean loadFromDisc() {
        Faction faction;
        if (!super.loadFromDisc()) {
            return false;
        }
        if (!this.exists("0")) {
            faction = this.create("0");
            faction.setTag(ChatColor.DARK_GREEN + "Wilderness");
            faction.setDescription("");
        } else {
            faction = this.get("0");
            if (!faction.getTag().equals(ChatColor.DARK_GREEN.toString() + "Wilderness")) {
                faction.setTag(ChatColor.DARK_GREEN.toString() + "Wilderness");
            }
        }
        if (!this.exists("-1")) {
            faction = this.create("-1");
            faction.setTag("SafeZone");
            faction.setDescription("Free from PVP and monsters");
        } else {
            faction = this.getSafeZone();
            if (faction.getTag().contains(" ")) {
                faction.setTag("SafeZone");
            }
        }
        if (!this.exists("-2")) {
            faction = this.create("-2");
            faction.setTag("WarZone");
            faction.setDescription("Not the safest place to be");
        } else {
            faction = this.getWarZone();
            if (faction.getTag().contains(" ")) {
                faction.setTag("WarZone");
            }
        }
        HashMap<String, List> duplicateFactions = new HashMap<>();
        ArrayList<String> deadFacs = new ArrayList<String>();
        for (Faction faction2 : i.get()) {
            String tag = faction2.getTag().toLowerCase();
            if (tag.equals(ChatColor.DARK_GREEN.toString() + "Wilderness") && !faction2.getId().equals("0")) {
                Bukkit.getLogger().info("FOUND FACTION THAT HAD WILDERNESS TAG: " + faction2.getTag() + " ID: " + faction2.getId());
                String newTag = "ChangeMe" + this.random.nextInt(100000);
                while (this.getByTag(newTag) != null) {
                    newTag = "ChangeMe" + this.random.nextInt(100000);
                }
                faction2.setTag(newTag);
                continue;
            }
            this.pruneRelations(faction2);
            List clones = duplicateFactions.containsKey(tag) ? duplicateFactions.get(tag) : new ArrayList();
            clones.add(faction2.getId());
            duplicateFactions.put(tag, clones);
            faction2.refreshFPlayers();
            if (!faction2.isNormal() || faction2.getFPlayers().size() > 0 || faction2.isPermanent()) continue;
            deadFacs.add(faction2.getId());
        }
        for (Map.Entry factionList : duplicateFactions.entrySet()) {
            if (((List) factionList.getValue()).size() <= 1) continue;
            List<String> factionIds = (List) factionList.getValue();
            Bukkit.getLogger().info("Found " + factionIds.size() + " factions with the same tag: " + factionList.getKey() + " List: " + factionIds.toString());
            for (String id : factionIds) {
                Faction faction3 = this.get(id);
                if (faction3 == null || faction3.getFPlayers().size() > 0) continue;
                Bukkit.getLogger().severe("Removing ghost faction " + faction3.getId() + " due to same tag: " + faction3.getTag());
                faction3.detach();
            }
        }
        for (String fac : deadFacs) {
            Faction faction4 = this.get(fac);
            if (faction4 == null) continue;
            Bukkit.getLogger().severe("Removing ghost faction " + faction4.getId() + " due to no players: " + faction4.getTag());
            faction4.detach();
        }
        for (Faction faction2 : i.get()) {
            faction2.refreshFPlayers();
        }
        duplicateFactions.clear();
        deadFacs.clear();
        return true;
    }

    private void pruneRelations(Faction faction) {
        Lists.newArrayList(faction.relationWish.keySet()).forEach(id -> {
            Faction relationedFaction = this.get(id);
            if (relationedFaction == null || relationedFaction.isNone()) {
                Bukkit.getLogger().info("Pruning ID: " + id + " from " + faction.getTag() + " due to no longer existing.");
                faction.relationWish.remove(id);
            }
        });
    }

    @Override
    public Faction get(String id) {
        if (nullFactions.contains(id)) {
            this.p.log(Level.INFO, ("Returning null for factionId " + id + " (already cleaned!)"));
            return null;
        }
        if (!this.exists(id)) {
            this.p.log(Level.WARNING, ("Non existing factionId " + id + " requested! Issuing cleaning!: " + new Exception().getStackTrace()[1].getClassName()));
            nullFactions.add(id);
            Board.clean();
            FPlayers.i.cleanSync();
            return null;
        }
        return super.get(id);
    }

    public Faction getNone() {
        return this.get("0");
    }

    public Faction getSafeZone() {
        return this.get("-1");
    }

    public Faction getWarZone() {
        return this.get("-2");
    }

    public Faction getByTag(String str) {
        for (Faction faction : this.get()) {
            if (!faction.getTag().equalsIgnoreCase(str)) continue;
            return faction;
        }
        String compStr = MiscUtil.getComparisonString(str);
        Bukkit.getLogger().info("[Factions] Could not find Faction associated with tag '" + str + "', attempting comparisonString search with: '" + compStr + "'");
        for (Faction faction : this.get()) {
            if (!faction.getComparisonTag().equals(compStr)) continue;
            return faction;
        }
        return null;
    }

    public Faction getBestTagMatch(String searchFor) {
        HashMap<String, Faction> tag2faction = new HashMap<String, Faction>();
        for (Faction faction : this.get()) {
            tag2faction.put(ChatColor.stripColor(faction.getTag()), faction);
        }
        String tag = TextUtil.getBestStartWithCI(tag2faction.keySet(), searchFor);
        if (tag == null) {
            return null;
        }
        return tag2faction.get(tag);
    }

    public boolean isTagTaken(String str) {
        return this.getByTag(str) != null;
    }

}

