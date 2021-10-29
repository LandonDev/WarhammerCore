/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonArray
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializationContext
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonDeserializer
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonElement
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonObject
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonParseException
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonPrimitive
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializationContext
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonSerializer
 */
package com.massivecraft.factions.util;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.P;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class MapFLocToStringSetTypeAdapter
        implements JsonDeserializer<Map<FLocation, Set<String>>>,
        JsonSerializer<Map<FLocation, Set<String>>> {
    public Map<FLocation, Set<String>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject obj = json.getAsJsonObject();
            if (obj == null) {
                return null;
            }
            ConcurrentHashMap<FLocation, Set<String>> locationMap = new ConcurrentHashMap<FLocation, Set<String>>();
            for (Map.Entry entry : obj.entrySet()) {
                String worldName = (String) entry.getKey();
                for (Map.Entry entry2 : ((JsonElement) entry.getValue()).getAsJsonObject().entrySet()) {
                    String[] coords = ((String) entry2.getKey()).trim().split("[,\\s]+");
                    int x = Integer.parseInt(coords[0]);
                    int z = Integer.parseInt(coords[1]);
                    HashSet<String> nameSet = new HashSet<String>();
                    for (JsonElement jsonElement : ((JsonElement) entry2.getValue()).getAsJsonArray()) {
                        nameSet.add((jsonElement).getAsString());
                    }
                    locationMap.put(new FLocation(worldName, x, z), nameSet);
                }
            }
            return locationMap;
        } catch (Exception ex) {
            ex.printStackTrace();
            P.p.log(Level.WARNING, "Error encountered while deserializing a Map of FLocations to String Sets.");
            return null;
        }
    }

    public JsonElement serialize(Map<FLocation, Set<String>> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        try {
            if (src != null) {
                for (Map.Entry<FLocation, Set<String>> entry : src.entrySet()) {
                    FLocation loc = entry.getKey();
                    String locWorld = loc.getWorldName();
                    Set<String> nameSet = entry.getValue();
                    if (nameSet == null || nameSet.isEmpty()) continue;
                    JsonArray nameArray = new JsonArray();
                    for (String s : nameSet) {
                        JsonPrimitive nameElement = new JsonPrimitive(s);
                        nameArray.add(nameElement);
                    }
                    if (!obj.has(locWorld)) {
                        obj.add(locWorld, new JsonObject());
                    }
                    obj.get(locWorld).getAsJsonObject().add(loc.getCoordString(), nameArray);
                }
            }
            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
            P.p.log(Level.WARNING, "Error encountered while serializing a Map of FLocations to String Sets.");
            return obj;
        }
    }
}

