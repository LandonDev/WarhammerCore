package landon.warhammercore.titles.mongo;

import com.google.common.collect.Lists;
import com.massivecraft.factions.P;
import com.mongodb.client.model.Filters;
import landon.warhammercore.WarhammerCore;
import landon.warhammercore.util.c;
import landon.warhammercore.util.mongo.MongoDB;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TitleManager {
    private static volatile TitleManager inst;
    private HashMap<UUID, String> loadedTitles;
    private HashMap<UUID, List<UUID>> ownedTitles;
    private HashMap<UUID, String> equippedTitles;

    private TitleManager() {
        this.loadedTitles = new HashMap<>();
        this.ownedTitles = new HashMap<>();
        this.equippedTitles = new HashMap<>();
    }

    public static TitleManager get() {
        if(inst == null) {
            synchronized (TitleManager.class) {
                inst = new TitleManager();
            }
        }
        return inst;
    }

    public void load() {
        for (Document doc : MongoDB.get().getTitles().find()) {
            if(doc.containsKey("uuid")) {
                this.loadedTitles.put(UUID.fromString(doc.getString("uuid")), c.c(doc.getString("title")));
            }
            if(doc.containsKey("playerid")) {
                List<UUID> ownedTitles = new ArrayList<>();
                for (Object o : ((List) doc.get("owned-titles"))) {
                    ownedTitles.add(UUID.fromString((String)o));
                }
                this.ownedTitles.put(UUID.fromString(doc.getString("playerid")), ownedTitles);
            }
        }
    }

    public void createTitle(String title) {
        UUID toCreate = UUID.randomUUID();
        this.loadedTitles.put(toCreate, c.c(title));
        Bukkit.getScheduler().runTaskAsynchronously(P.p, () -> {
            Document doc = new Document();
            doc.append("uuid", toCreate.toString());
            doc.append("title", title);
            MongoDB.get().getTitles().insertOne(doc);
        });
    }

    public UUID getUUIDFromTitle(String title) {
        for (UUID uuid : this.loadedTitles.keySet()) {
            if(this.loadedTitles.get(uuid).equals(title)) {
                return uuid;
            }
        }
        return null;
    }

    public void unlockTitle(Player player, UUID title) {
        if(this.ownedTitles.containsKey(player.getUniqueId())) {
            List<UUID> titles = this.ownedTitles.get(player.getUniqueId());
            titles.add(title);
            this.ownedTitles.put(player.getUniqueId(), titles);
        } else {
            List<UUID> titles = new ArrayList<>();
            titles.add(title);
            this.ownedTitles.put(player.getUniqueId(), titles);
        }
        Bukkit.getScheduler().runTaskAsynchronously(P.p, () -> {
            List<String> stringUUIDs = new ArrayList<>();
            for (UUID uuid : this.ownedTitles.get(player.getUniqueId())) {
                stringUUIDs.add(uuid.toString());
            }
            Document doc = new Document();
            doc.append("playerid", player.getUniqueId().toString());
            doc.append("owned-titles", stringUUIDs);
            if(MongoDB.get().getTitles().findOneAndReplace(Filters.eq("playerid", player.getUniqueId().toString()), doc) == null) {
                MongoDB.get().getTitles().insertOne(doc);
            }
        });
    }

    public boolean hasTitle(Player player, UUID title) {
        if(this.ownedTitles.containsKey(player.getUniqueId())) {
            return this.ownedTitles.get(player.getUniqueId()).contains(title);
        }
        return false;
    }

    public String getTitle(String uuid) {
        return this.loadedTitles.get(UUID.fromString(uuid));
    }

    public String getEquippedTitle(UUID uuid) {
        if(this.equippedTitles.containsKey(uuid)) {
            return this.equippedTitles.get(uuid);
        }
        return null;
    }

    public String getTitleFromStrippedText(String strippedText) {
        for (String title : this.loadedTitles.values()) {
            if(strippedText.toLowerCase().equals(ChatColor.stripColor(title.toLowerCase()))) {
                return title;
            }
        }
        return null;
    }

    public boolean matches(String title1, String title2) {
        UUID uuid1 = null;
        UUID uuid2 = null;
        for (UUID uuid : this.loadedTitles.keySet()) {
            if(this.loadedTitles.get(uuid).equals(title1)) {
                uuid1 = uuid;
            }
            if(this.loadedTitles.get(uuid).equals(title2)) {
                uuid2 = uuid;
            }
        }
        if(uuid1 == null || uuid2 == null) {
            return false;
        }
        if(uuid1.toString().equals(uuid2.toString())) {
            return true;
        }
        return false;
    }

    public List<String> getUnlockedTitles(Player player) {
        List<String> titlesToReturn = new ArrayList<>();
        if(!this.ownedTitles.containsKey(player.getUniqueId())) {
            return Lists.newArrayList();
        }
        for (UUID uuid : this.ownedTitles.get(player.getUniqueId())) {
            titlesToReturn.add(this.getTitle(uuid.toString()));
        }
        return titlesToReturn;
    }

    public void setEquippedTitle(Player player, String title) {
        this.equippedTitles.put(player.getUniqueId(), title);
    }
}
