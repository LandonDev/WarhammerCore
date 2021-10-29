package landon.warhammercore.util.cooldown;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.P;
import landon.warhammercore.WarhammerCore;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

public final class Cooldowns {
    private CooldownManager cooldownManager;

    private File dataFile = new File(P.p.getDataFolder(), "cooldowns.json");

    private Gson gson = (new GsonBuilder()).setPrettyPrinting().create();

    private Type type = (new TypeToken<HashMap<UUID, HashMap<String, Long>>>() {

    }).getType();

    public Cooldowns(JavaPlugin plugin) {
        this.cooldownManager = new CooldownManager(plugin);
    }

    public CooldownManager getCooldownManager() {
        return this.cooldownManager;
    }

    public void loadCooldowns() {
        if (!this.dataFile.getParentFile().exists())
            this.dataFile.getParentFile().mkdirs();
        if (!this.dataFile.exists())
            try {
                this.dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        try {
            HashMap<UUID, HashMap<String, Long>> json = (HashMap<UUID, HashMap<String, Long>>)this.gson.fromJson(new FileReader(this.dataFile), this.type);
            if (json != null)
                this.cooldownManager.setCooldowns(json);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveCooldowns() {
        try (FileWriter writer = new FileWriter(this.dataFile)) {
            this.gson.toJson(this.cooldownManager.getCooldowns(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
