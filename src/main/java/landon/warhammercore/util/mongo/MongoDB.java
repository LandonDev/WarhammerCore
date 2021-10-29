package landon.warhammercore.util.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import javax.print.Doc;

@Getter
@Setter
public class MongoDB {
    private static volatile MongoDB inst;
    private MongoClient client;
    private MongoDatabase db;
    private MongoCollection<Document> deathbans;
    private MongoCollection<Document> titles;

    private MongoDB() {
        inst = this;
    }

    public static MongoDB get() {
        if(inst == null) {
            synchronized (MongoDB.class) {
                inst = new MongoDB();
            }
        }
        return inst;
    }

    public void connect(String uri) {
        MongoClientURI uri2 = new MongoClientURI(uri);
        this.client = new MongoClient(uri2);
        this.db = this.client.getDatabase("warhammer-core");
        this.deathbans = this.db.getCollection("deathbans");
        this.titles = this.db.getCollection("titles");
    }

    public void connect(String ip, int port, String username, char[] password, String database) {
            MongoCredential credential = MongoCredential.createCredential(username, database, password);
            MongoClientOptions options = MongoClientOptions.builder().sslEnabled(false).build();
            this.client = new MongoClient(new ServerAddress(ip, port), credential, options);
        this.db = this.client.getDatabase("warhammer-core");
        this.deathbans = this.db.getCollection("deathbans");
        this.titles = this.db.getCollection("titles");
    }
}
