package landon.warhammercore.deathbans.lives;

import com.massivecraft.factions.P;
import com.mongodb.client.model.Filters;
import de.tr7zw.changeme.nbtapi.NBTItem;
import landon.warhammercore.WarhammerCore;
import landon.warhammercore.util.GiveUtil;
import landon.warhammercore.util.items.ItemBuilder;
import landon.warhammercore.util.mongo.MongoDB;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.print.Doc;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
@Setter
public class LifeManager {
    private MongoDB mongo;
    private HashMap<UUID, Integer> lifeCache;
    public LifeManager() {
        this.mongo = MongoDB.get();
        this.lifeCache = new HashMap<>();
    }

    public void giveLifeItem(Player player, int amount) {
        NBTItem nbtItem = new NBTItem(ItemBuilder.createItem(Material.DOUBLE_PLANT, "&a&l" + amount + "x Live(s) &7(Right Click)", 1, 0, "&7Right-Click to redeem this item that will", "&7grant you " + amount + " live(s) towards your /lives count."));
        nbtItem.setInteger("lifeCount", amount);
        GiveUtil.giveOrDropItem(player, nbtItem.getItem());
    }

    public int getLivesFromCache(Player player) {
        if(this.lifeCache.containsKey(player.getUniqueId())) {
            return this.lifeCache.get(player.getUniqueId());
        }
        this.getLives(player.getUniqueId(), callback -> {});
        return 0;
    }

    public void getLives(UUID uuid, Consumer<Integer> callback) {
        if(this.lifeCache.containsKey(uuid)) {
            callback.accept(this.lifeCache.get(uuid));
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(P.p, () -> {
            for (Document doc : this.mongo.getDeathbans().find(Filters.eq("uuid", uuid.toString()))) {
                if(doc.containsKey("lives")) {
                    callback.accept(doc.getInteger("lives"));
                    this.lifeCache.put(uuid, doc.getInteger("lives"));
                    return;
                }
            }
            callback.accept(2);
            this.setLives(uuid, 2);
            this.lifeCache.put(uuid, 2);
        });
    }

    public void getLivesNoCache(UUID uuid, Consumer<Integer> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(P.p, () -> {
            for (Document doc : this.mongo.getDeathbans().find(Filters.eq("uuid", uuid.toString()))) {
                if(doc.containsKey("lives")) {
                    callback.accept(doc.getInteger("lives"));
                    return;
                }
            }
            callback.accept(2);
            this.setLives(uuid, 2);
        });
    }

    public void setLives(UUID uuid, int amount) {
        this.lifeCache.remove(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(P.p, () -> {
            Document doc = new Document();
            doc.append("uuid", uuid.toString());
            doc.append("lives", amount);
            if(this.mongo.getDeathbans().findOneAndReplace(Filters.eq("uuid", uuid.toString()), doc) == null) {
                this.mongo.getDeathbans().insertOne(doc);
            }
        });
    }

    public void removeLives(UUID uuid, int amount) {
        this.lifeCache.remove(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(P.p, () -> {
            this.getLivesNoCache(uuid, lives -> {
                Document doc = new Document();
                doc.append("uuid", uuid.toString());
                doc.append("lives", (lives-amount));
                if(this.mongo.getDeathbans().findOneAndReplace(Filters.eq("uuid", uuid.toString()), doc) == null) {
                    this.mongo.getDeathbans().insertOne(doc);
                }
            });
        });
    }

    public void addLives(UUID uuid, int amount) {
        this.lifeCache.remove(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(P.p, () -> {
            this.getLivesNoCache(uuid, lives -> {
                Document doc = new Document();
                doc.append("uuid", uuid.toString());
                doc.append("lives", (lives+amount));
                if(this.mongo.getDeathbans().findOneAndReplace(Filters.eq("uuid", uuid.toString()), doc) == null) {
                    this.mongo.getDeathbans().insertOne(doc);
                }
            });
        });
    }
}
