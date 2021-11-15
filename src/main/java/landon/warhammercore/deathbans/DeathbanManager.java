package landon.warhammercore.deathbans;

import com.massivecraft.factions.P;
import com.mongodb.client.model.Filters;
import landon.warhammercore.WarhammerCore;
import landon.warhammercore.util.mongo.MongoDB;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class DeathbanManager {
    private Set<Deathban> activeDeathbans;
    private Map<UUID, Integer> killMap;
    private static DeathbanManager inst;
    private HashMap<UUID, Long> reviveCooldowns;

    private DeathbanManager() {
        this.activeDeathbans = new HashSet<>();
        this.killMap = new HashMap<>();
        this.reviveCooldowns = new HashMap<>();
    }

    public static DeathbanManager get() {
        if(inst == null) {
            synchronized (DeathbanManager.class) {
                inst = new DeathbanManager();
            }
        }
        return inst;
    }

    public void loadDeathbans() {
        Bukkit.getScheduler().runTaskAsynchronously(P.p, () -> {
            for (Document doc : MongoDB.get().getDeathbans().find()) {
                if(doc.containsKey("uniqueid")) {
                    Deathban deathban = new Deathban(doc.getString("bannedatname"), UUID.fromString(doc.getString("uniqueid")), doc.getLong("started-on"), doc.getLong("ends-on"), doc.getString("death-message"));
                    this.activeDeathbans.add(deathban);
                }
                if(doc.containsKey("kill-uuid")) {
                    this.killMap.put(UUID.fromString(doc.getString("kill-uuid")), doc.getInteger("kills"));
                }
            }
        });
    }

    public Deathban findDeathban(Player player) {
        for (Deathban deathban : this.activeDeathbans) {
            if(deathban.getUuid().toString().equals(player.getUniqueId().toString())) {
                return deathban;
            }
        }
        return null;
    }

    public Deathban findDeathban(String username) {
        for (Deathban deathban : this.activeDeathbans) {
            if(deathban.getBannedAtName().toLowerCase().equals(username)) {
                return deathban;
            }
        }
        return null;
    }

    public void createDeathban(String name, UUID uuid, long duration, String deathMessage) {
        Deathban db = new Deathban(name, uuid, duration, deathMessage);
        this.activeDeathbans.add(db);
        this.storeDeathban(db);
    }

    public void storeDeathban(Deathban deathban) {
        Bukkit.getScheduler().runTaskAsynchronously(P.p, () -> {
            Document doc = new Document();
            doc.append("bannedatname", deathban.getBannedAtName());
            doc.append("uniqueid", deathban.getUuid().toString());
            doc.append("started-on", deathban.getStartedOn());
            doc.append("ends-on", deathban.getEndsOn());
            doc.append("death-message", deathban.getDeathMessage());
            if(MongoDB.get().getDeathbans().findOneAndReplace(Filters.eq("uniqueid", deathban.getUuid().toString()), doc) == null) {
                MongoDB.get().getDeathbans().insertOne(doc);
            }
        });
    }

    public void deleteDeathban(Deathban ban) {
        this.activeDeathbans.remove(ban);
        Bukkit.getScheduler().runTaskAsynchronously(P.p, () -> {
            MongoDB.get().getDeathbans().findOneAndDelete(Filters.eq("uniqueid", ban.getUuid()));
        });
    }

    public int getTotalKills(UUID uuid) {
        if(this.killMap.containsKey(uuid)) {
            return this.killMap.get(uuid);
        }
        this.killMap.put(uuid, 0);
        return 0;
    }

    public void addKill(Player player) {
        UUID uuid = player.getUniqueId();
        if(this.killMap.containsKey(uuid)) {
            this.killMap.put(uuid, this.killMap.get(uuid) + 1);
        } else {
            this.killMap.put(uuid, 1);
        }
    }

    public boolean isDeathbanned(Player player) {
        for (Deathban db : this.activeDeathbans) {
            if(db.getUuid().toString().equals(player.getUniqueId().toString())) {
                if(db.checkExpired()) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    public void storeKills(UUID uuid, boolean sync) {
        if(!sync) {
            Bukkit.getScheduler().runTaskAsynchronously(P.p, () -> {
                Document doc = new Document();
                doc.append("kill-uuid", uuid.toString());
                doc.append("kills", this.getTotalKills(uuid));
                if(MongoDB.get().getDeathbans().findOneAndReplace(Filters.eq("kill-uuid", uuid.toString()), doc) == null) {
                    MongoDB.get().getDeathbans().insertOne(doc);
                }
            });
        } else {
            Document doc = new Document();
            doc.append("kill-uuid", uuid.toString());
            doc.append("kills", this.getTotalKills(uuid));
            if(MongoDB.get().getDeathbans().findOneAndReplace(Filters.eq("kill-uuid", uuid.toString()), doc) == null) {
                MongoDB.get().getDeathbans().insertOne(doc);
            }
        }
    }
}
