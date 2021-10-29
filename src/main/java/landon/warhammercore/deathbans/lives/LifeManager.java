package landon.warhammercore.deathbans.lives;

import com.massivecraft.factions.P;
import com.mongodb.client.model.Filters;
import landon.warhammercore.WarhammerCore;
import landon.warhammercore.util.mongo.MongoDB;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.print.Doc;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
@Setter
public class LifeManager {
    private MongoDB mongo;
    public LifeManager() {
        this.mongo = MongoDB.get();
    }

    public void getLives(UUID uuid, Consumer<Integer> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(P.p, () -> {
             if(this.mongo.getDeathbans().find(Filters.eq("uuid", uuid.toString())) == null) {
                 callback.accept(2);
                 this.setLives(uuid, 2);
                 return;
             }
            for (Document doc : this.mongo.getDeathbans().find(Filters.eq("uuid", uuid.toString()))) {
                if(doc.containsKey("lives")) {
                    callback.accept(doc.getInteger("lives"));
                    return;
                }
            }
        });
    }

    public void setLives(UUID uuid, int amount) {
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
        Bukkit.getScheduler().runTaskAsynchronously(P.p, () -> {
            this.getLives(uuid, lives -> {
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
        Bukkit.getScheduler().runTaskAsynchronously(P.p, () -> {
            this.getLives(uuid, lives -> {
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
