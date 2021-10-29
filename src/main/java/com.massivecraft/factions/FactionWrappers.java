/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package com.massivecraft.factions;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FactionWrappers {
    public static HashMap<String, FactionWrapper> factionWrappers = new HashMap<>();
    private static YamlConfiguration config;
    private static File file;
    private static Random random = new Random();


    P p = P.getP();

    public static void removeLocation(FactionWrapper wrapper, FLocation fl) {
        String key = fl.getWorldName() + ":" + fl.getX() + ":" + fl.getZ();
        config.set("factionwrappers." + wrapper.getFactionId() + ".chunkClaims." + key, null);
    }

    public static void loadFactionWrappers() {
        try {
            file = new File(P.p.getDataFolder(), "factionWrappers.yml");
            if (!file.exists()) {
                file.createNewFile();
            }
            if ((config = YamlConfiguration.loadConfiguration(file)).contains("factionwrappers")) {
                for (String id : config.getConfigurationSection("factionwrappers").getKeys(false)) {
                    Location l;
                    ConcurrentHashMap<FLocation, Set<String>> set = new ConcurrentHashMap<FLocation, Set<String>>();
                    ConcurrentHashMap<String, Location> allyWarps = new ConcurrentHashMap<String, Location>();
                    ConcurrentHashMap<String, Location> allAllyWarps = new ConcurrentHashMap<String, Location>();
                    if (config.contains("factionwrappers." + id + ".chunkClaims")) {
                        for (String chunkClaims : config.getConfigurationSection("factionwrappers." + id + ".chunkClaims").getKeys(false)) {
                            String[] args = chunkClaims.split(":");
                            FLocation fl = new FLocation(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                            HashSet<String> locs = new HashSet<>(config.getStringList("factionwrappers." + id + ".chunkClaims." + chunkClaims));
                            set.put(fl, locs);
                        }
                    }
                    for (String s : config.getStringList("factionwrappers." + id + ".allyWarps")) {
                        String ally = s.split(",")[0];
                        l = FactionWrappers.getLocationFromString(s.split(",")[1].split(":"));
                        allyWarps.put(ally.toLowerCase(), l);
                    }
                    for (String s : config.getStringList("factionwrappers." + id + ".allallywarps")) {
                        String name = s.contains("=") ? s.split("=")[0] : "none" + random.nextInt(100000);
                        l = FactionWrappers.getLocationFromString(s.contains("=") ? s.split("=")[1].split(":") : s.split(":"));
                        allAllyWarps.put(name.toLowerCase(), l);
                    }
                    FactionWrapper wrapper = new FactionWrapper();
                    wrapper.setFactionId(id);
                    wrapper.setAllyWarps(allyWarps);
                    wrapper.setAllAllyWarps(allAllyWarps);
                    wrapper.setChunkClaims(set);
                    factionWrappers.put(id, wrapper);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Location getLocationFromString(String[] s) {
        Location loc = new Location(Bukkit.getWorld(s[0]), Double.parseDouble(s[1]), Double.parseDouble(s[2]), Double.parseDouble(s[3]));
        loc.setYaw(Float.parseFloat(s[4]));
        loc.setPitch(Float.parseFloat(s[5]));
        return loc;
    }

    public static void saveToDisc() {
        for (Map.Entry<String, FactionWrapper> wrapper : factionWrappers.entrySet()) {
            Location l;
            for (Map.Entry<FLocation, Set<String>> entry : wrapper.getValue().chunkClaims.entrySet()) {
                Iterator<Map.Entry<String, Location>> list = (Iterator<Map.Entry<String, Location>>) Lists.newArrayList(entry.getValue());
                String key = entry.getKey().getWorldName() + ":" + entry.getKey().getX() + ":" + entry.getKey().getZ();
                config.set("factionwrappers." + wrapper.getKey() + ".chunkClaims." + key, list);
            }
            ArrayList<String> locs = new ArrayList<String>();
            ArrayList<String> allAllies = new ArrayList<String>();
            for (Map.Entry<String, Location> entry : wrapper.getValue().getAllyWarps().entrySet()) {
                l = entry.getValue();
                String s = entry.getKey() + "," + l.getWorld().getName() + ":" + l.getX() + ":" + l.getY() + ":" + l.getZ() + ":" + l.getYaw() + ":" + l.getPitch();
                locs.add(s);
            }
            for (Map.Entry<String, Location> entry : wrapper.getValue().getAllAllyWarps().entrySet()) {
                l = entry.getValue();
                String name = entry.getKey();
                allAllies.add(name + "=" + l.getWorld().getName() + ":" + l.getX() + ":" + l.getY() + ":" + l.getZ() + ":" + l.getYaw() + ":" + l.getPitch());
            }
            config.set("factionwrappers." + wrapper.getKey() + ".allallywarps", allAllies);
            config.set("factionwrappers." + wrapper.getKey() + ".allyWarps", locs);
        }
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FactionWrapper get(Faction f) {
        return FactionWrappers.get(f.getId());
    }

    public static FactionWrapper get(String id) {
        if (factionWrappers.containsKey(id)) {
            return factionWrappers.get(id);
        }
        FactionWrapper wrapper = new FactionWrapper();
        wrapper.setFactionId(id);
        factionWrappers.put(id, wrapper);
        return wrapper;
    }
}

