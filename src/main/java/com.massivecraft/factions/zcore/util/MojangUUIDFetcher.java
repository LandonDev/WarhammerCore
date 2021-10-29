/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.json.simple.JSONArray
 *  org.json.simple.JSONObject
 *  org.json.simple.parser.JSONParser
 */
package com.massivecraft.factions.zcore.util;

import com.google.common.collect.ImmutableList;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Callable;

public class MojangUUIDFetcher
        implements Callable<Map<String, UUID>> {
    private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
    private static final double PROFILES_PER_REQUEST = 100.0;
    public static HashMap<String, UUID> uuidCache = new HashMap<>();
    private final JSONParser jsonParser = new JSONParser();
    private final List<String> names;
    private final boolean rateLimiting;

    public MojangUUIDFetcher(List<String> names, boolean rateLimiting) {
        this.names = ImmutableList.copyOf(names);
        this.rateLimiting = rateLimiting;
    }

    public MojangUUIDFetcher(List<String> names) {
        this(names, true);
    }

    private static void writeBody(HttpURLConnection connection, String body) throws Exception {
        OutputStream stream = connection.getOutputStream();
        stream.write(body.getBytes());
        stream.flush();
        stream.close();
    }

    private static HttpURLConnection createConnection() throws Exception {
        URL url = new URL(PROFILE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    private static UUID getUUID(String id) {
        return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
    }

    public static byte[] toBytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    public static UUID fromBytes(byte[] array) {
        if (array.length != 16) {
            throw new IllegalArgumentException("Illegal byte array length: " + array.length);
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        long mostSignificant = byteBuffer.getLong();
        long leastSignificant = byteBuffer.getLong();
        return new UUID(mostSignificant, leastSignificant);
    }

    public static UUID getUUIDOf(String name) throws Exception {
        return uuidCache.containsKey(name) ? uuidCache.get(name) : new MojangUUIDFetcher(Arrays.asList(name), false).call().get(name);
    }

    public static void updateCachedUUID(Player p) {
        uuidCache.put(p.getName(), p.getUniqueId());
    }

    public static void loadFactionPlayerUUIDs() {
        int c = 0;
        for (FPlayer fp : FPlayers.i.get()) {
            if (fp.getId().length() <= 16) continue;
            ++c;
            try {
                uuidCache.put(fp.getNameAsync(), UUID.fromString(fp.getId()));
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }
        Bukkit.getLogger().info("(Factions) Cached " + c + " players.json UUIDs!");
    }

    @Override
    public Map<String, UUID> call() throws Exception {
        HashMap<String, UUID> uuidMap = new HashMap<String, UUID>();
        int requests = (int) Math.ceil((double) this.names.size() / 100.0);
        for (int i = 0; i < requests; ++i) {
            HttpURLConnection connection = MojangUUIDFetcher.createConnection();
            connection.setConnectTimeout(4000);
            connection.setReadTimeout(4000);
            String body = JSONArray.toJSONString(this.names.subList(i * 100, Math.min((i + 1) * 100, this.names.size())));
            MojangUUIDFetcher.writeBody(connection, body);
            JSONArray array = (JSONArray) this.jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            for (Object profile : array) {
                JSONObject jsonProfile = (JSONObject) profile;
                String id = (String) jsonProfile.get("id");
                String name = (String) jsonProfile.get("name");
                UUID uuid = MojangUUIDFetcher.getUUID(id);
                uuidMap.put(name, uuid);
                uuidCache.put(name, uuid);
            }
            if (!this.rateLimiting || i == requests - 1) continue;
            Thread.sleep(100L);
        }
        return uuidMap;
    }
}

