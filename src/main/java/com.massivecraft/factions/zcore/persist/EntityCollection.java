package com.massivecraft.factions.zcore.persist;

import com.google.gson.Gson;
import com.massivecraft.factions.*;
import com.massivecraft.factions.zcore.util.DiscUtil;
import com.massivecraft.factions.zcore.util.MojangUUIDFetcher;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;

public abstract class EntityCollection<E extends Entity> {
    protected Map<String, E> id2entity;
    private Collection<E> entities;
    private boolean creative;
    private int nextId;
    private Class<E> entityClass;
    private Gson gson;
    private File file;
    private boolean saveIsRunning;

    public EntityCollection(final Class<E> entityClass, final Collection<E> entities, final Map<String, E> id2entity, final File file, final Gson gson, final boolean creative) {
        this.saveIsRunning = false;
        this.entityClass = entityClass;
        this.entities = entities;
        this.id2entity = id2entity;
        this.file = file;
        this.gson = gson;
        this.creative = creative;
        this.nextId = 1;
        EM.setEntitiesCollectionForEntityClass(this.entityClass, this);
    }

    public EntityCollection(final Class<E> entityClass, final Collection<E> entities, final Map<String, E> id2entity, final File file, final Gson gson) {
        this(entityClass, entities, id2entity, file, gson, false);
    }

    public boolean isCreative() {
        return this.creative;
    }

    public void setCreative(final boolean creative) {
        this.creative = creative;
    }

    public abstract Type getMapType();

    public Gson getGson() {
        return this.gson;
    }

    public void setGson(final Gson gson) {
        this.gson = gson;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(final File file) {
        this.file = file;
    }

    public Collection<E> get() {
        return this.entities;
    }

    public Map<String, E> getMap() {
        return this.id2entity;
    }

    public E get(String id) {
        if (this.entityClass != null && this.entityClass.equals(FPlayer.class) && id.length() <= 16 && MojangUUIDFetcher.uuidCache.containsKey(id)) {
            final String uuid = MojangUUIDFetcher.uuidCache.get(id).toString();
            Bukkit.getLogger().info("[Factions] id:" + id + " -> id:" + uuid);
            id = uuid;
        }
        if (this.creative) {
            return this.getCreative(id);
        }
        return this.id2entity.get(id);
    }

    public E getCreative(final String id) {
        final E e = this.id2entity.get(id);
        if (e != null) {
            return e;
        }
        return this.create(id);
    }

    public boolean exists(final String id) {
        return id != null && this.id2entity.containsKey(id);
    }

    public E getBestIdMatch(final String pattern) {
        final String id = TextUtil.getBestStartWithCI(this.id2entity.keySet(), pattern);
        if (id == null) {
            return null;
        }
        return this.id2entity.get(id);
    }

    public synchronized E create() {
        return this.create(this.getNextId());
    }

    public synchronized E create(final String id) {
        if (!this.isIdFree(id)) {
            return null;
        }
        Factions.nullFactions.remove(id);
        E e = null;
        try {
            e = this.entityClass.newInstance();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        e.setId(id);
        this.entities.add(e);
        this.id2entity.put(e.getId(), e);
        this.updateNextIdForId(id);
        return e;
    }

    public void attach(final E entity) {
        if (entity.getId() != null) {
            return;
        }
        entity.setId(this.getNextId());
        this.entities.add(entity);
        this.id2entity.put(entity.getId(), entity);
    }

    public void detach(final E entity) {
        entity.preDetach();
        this.entities.remove(entity);
        this.id2entity.remove(entity.getId());
        entity.postDetach();
    }

    public void detach(final String id) {
        final E entity = this.id2entity.get(id);
        if (entity == null) {
            return;
        }
        this.detach(entity);
    }

    public boolean attached(final E entity) {
        return this.entities.contains(entity);
    }

    public boolean detached(final E entity) {
        return !this.attached(entity);
    }

    public boolean saveToDisc() {
        return this.saveToDisc(false);
    }

    public boolean saveToDisc(final boolean force) {
        if (this.saveIsRunning && !force) {
            P.p.log("Skipping saveToDisc because saveIsRunning == true!");
            return true;
        }
        this.saveIsRunning = true;
        final Map<String, E> entitiesThatShouldBeSaved = new HashMap<String, E>();
        for (final E entity : this.entities) {
            if (entity.shouldBeSaved()) {
                if (entity instanceof PlayerEntity && entity.getId().length() <= 16) {
                    Bukkit.getLogger().info("[Faction] Entity " + entity.getId() + " does not have a valid UUID. Fetching...");
                    try {
                        final String uuid = MojangUUIDFetcher.getUUIDOf(entity.getId()).toString();
                        entity.setId(uuid);
                        Bukkit.getLogger().info("[Faction] Entity " + entity.getId() + " assigned " + uuid + "!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                entitiesThatShouldBeSaved.put(entity.getId(), entity);
            }
        }
        this.saveIsRunning = false;
        return this.saveCore(this.file, entitiesThatShouldBeSaved);
    }

    private boolean saveCore(final File target, final Map<String, E> entities) {
        return DiscUtil.writeCatch(target, this.gson.toJson(entities));
    }

    public boolean loadFromDisc() {
        final Map<String, E> id2entity = this.loadCore();
        if (id2entity == null) {
            return false;
        }
        this.entities.clear();
        this.entities.addAll(id2entity.values());
        this.id2entity.clear();
        this.id2entity.putAll(id2entity);
        this.fillIds();
        return true;
    }

    private Map<String, E> loadCore() {
        if (!this.file.exists()) {
            return new HashMap<>();
        }
        final String content = DiscUtil.readCatch(this.file);
        if (content == null) {
            return null;
        }
        final Type type = this.getMapType();
        if (type.toString().contains("FPlayer")) {
            final Map<String, FPlayer> data = this.gson.fromJson(content, type);
            final Set<String> list = this.whichKeysNeedMigration(data.keySet());
            final Set<String> invalidList = this.whichKeysAreInvalid(list);
            list.removeAll(invalidList);
            if (list.size() > 0) {
                Bukkit.getLogger().log(Level.INFO, "Factions is now updating players.json");
                final File file = new File(this.file.getParentFile(), "players.json.old");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.saveCore(file, (Map<String, E>) data);
                Bukkit.getLogger().log(Level.INFO, "Backed up your old data at " + file.getAbsolutePath());
                Bukkit.getLogger().log(Level.INFO, "Please wait while Factions converts " + list.size() + " old player names to UUID. This may take a while.");
                final MojangUUIDFetcher fetcher = new MojangUUIDFetcher(new ArrayList<String>(list));
                try {
                    final Map<String, UUID> response = fetcher.call();
                    for (final String s : list) {
                        if (!response.containsKey(s)) {
                            invalidList.add(s);
                        }
                    }
                    for (final String value : response.keySet()) {
                        final String id = response.get(value).toString();
                        if (data.get(id) != null) {
                            Bukkit.getLogger().info("[Factions] Skipping conversion of " + value + " becaues their UUID is already registered. (" + id + ")");
                            data.remove(value);
                        } else {
                            final FPlayer player = data.get(value);
                            if (player == null) {
                                invalidList.add(value);
                            } else {
                                player.setId(id);
                                data.remove(value);
                                data.put(id, player);
                            }
                        }
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                if (invalidList.size() > 0) {
                    for (final String name : invalidList) {
                        data.remove(name);
                    }
                    Bukkit.getLogger().log(Level.INFO, "While converting we found names that either don't have a UUID or aren't players and removed them from storage.");
                    Bukkit.getLogger().log(Level.INFO, "The following names were detected as being invalid: " + StringUtils.join(invalidList, ", "));
                }
                this.saveCore(this.file, (Map<String, E>) data);
                Bukkit.getLogger().log(Level.INFO, "Done converting players.json to UUID.");
            }
            return (Map<String, E>) data;
        }
        final Map<String, Faction> data2 = this.gson.fromJson(content, type);
        int needsUpdate = 0;
        for (final String string : data2.keySet()) {
            final Faction f = data2.get(string);
            needsUpdate += this.whichKeysNeedMigration(f.getPendingInvites()).size();
            final Map<FLocation, Set<String>> claims = f.getClaimOwnership();
            for (final FLocation key : f.getClaimOwnership().keySet()) {
                needsUpdate += this.whichKeysNeedMigration(claims.get(key)).size();
            }
        }
        if (needsUpdate > 0) {
            Bukkit.getLogger().log(Level.INFO, "Factions is now updating factions.json");
            final File file2 = new File(this.file.getParentFile(), "factions.json.old");
            try {
                file2.createNewFile();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            this.saveCore(file2, (Map<String, E>) data2);
            Bukkit.getLogger().log(Level.INFO, "Backed up your old data at " + file2.getAbsolutePath());
            Bukkit.getLogger().log(Level.INFO, "Please wait while Factions converts " + needsUpdate + " old player names to UUID. This may take a while.");
            for (final String string2 : data2.keySet()) {
                final Faction f2 = data2.get(string2);
                final Map<FLocation, Set<String>> claims2 = f2.getClaimOwnership();
                for (final FLocation key2 : claims2.keySet()) {
                    final Set<String> set = claims2.get(key2);
                    final Set<String> list2 = this.whichKeysNeedMigration(set);
                    if (list2.size() > 0) {
                        final MojangUUIDFetcher fetcher2 = new MojangUUIDFetcher(new ArrayList<String>(list2));
                        try {
                            final Map<String, UUID> response2 = fetcher2.call();
                            for (final String value2 : response2.keySet()) {
                                final String id2 = response2.get(value2).toString();
                                set.remove(value2.toLowerCase());
                                set.add(id2);
                            }
                        } catch (Exception e4) {
                            e4.printStackTrace();
                        }
                        claims2.put(key2, set);
                    }
                }
            }
            for (final String string2 : data2.keySet()) {
                final Faction f2 = data2.get(string2);
                final Set<String> invites = f2.getPendingInvites();
                final Set<String> list3 = this.whichKeysNeedMigration(invites);
                if (list3.size() > 0) {
                    final MojangUUIDFetcher fetcher3 = new MojangUUIDFetcher(new ArrayList<String>(list3));
                    try {
                        final Map<String, UUID> response3 = fetcher3.call();
                        for (final String value3 : response3.keySet()) {
                            final String id3 = response3.get(value3).toString();
                            invites.remove(value3.toLowerCase());
                            invites.add(id3);
                        }
                    } catch (Exception e5) {
                        e5.printStackTrace();
                    }
                }
            }
            this.saveCore(this.file, (Map<String, E>) data2);
            Bukkit.getLogger().log(Level.INFO, "Done converting factions.json to UUID.");
        }
        return (Map<String, E>) data2;
    }

    private Set<String> whichKeysNeedMigration(final Set<String> keys) {
        final HashSet<String> list = new HashSet<String>();
        for (final String value : keys) {
            if (!value.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}") && value.matches("[a-zA-Z0-9_]{2,16}")) {
                list.add(value);
            }
        }
        return list;
    }

    private Set<String> whichKeysAreInvalid(final Set<String> keys) {
        final Set<String> list = new HashSet<String>();
        for (final String value : keys) {
            if (!value.matches("[a-zA-Z0-9_]{2,16}")) {
                list.add(value);
            }
        }
        return list;
    }

    public String getNextId() {
        while (!this.isIdFree(this.nextId)) {
            ++this.nextId;
        }
        return Integer.toString(this.nextId);
    }

    public boolean isIdFree(final String id) {
        return !this.id2entity.containsKey(id);
    }

    public boolean isIdFree(final int id) {
        return this.isIdFree(Integer.toString(id));
    }

    protected synchronized void fillIds() {
        this.nextId = 1;
        for (final Map.Entry<String, E> entry : this.id2entity.entrySet()) {
            final String id = entry.getKey();
            final E entity = entry.getValue();
            entity.setId(id);
            this.updateNextIdForId(id);
        }
    }

    protected synchronized void updateNextIdForId(final int id) {
        if (this.nextId < id) {
            this.nextId = id + 1;
        }
    }

    protected void updateNextIdForId(final String id) {
        try {
            final int idAsInt = Integer.parseInt(id);
            this.updateNextIdForId(idAsInt);
        } catch (Exception ex) {
        }
    }
}
