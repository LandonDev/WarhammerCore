package landon.jurassiccore.tasks;

import com.massivecraft.factions.P;
import landon.jurassiccore.playerdata.Item;
import landon.jurassiccore.playerdata.PlayerData;
import landon.jurassiccore.playerdata.PlayerDataManager;
import landon.jurassiccore.JurassicCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Iterator;

public class ItemProtectTask extends BukkitRunnable {
    private final JurassicCore instance;

    public ItemProtectTask(JurassicCore instance) {
        this.instance = instance;
    }

    public void run() {
        if (!this.instance.getFileManager().getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("ItemProtect.Enable"))
            return;
        PlayerDataManager playerDataManager = this.instance.getPlayerDataManager();
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (!playerDataManager.hasPlayerData(all))
                continue;
            PlayerData playerData = playerDataManager.getPlayerData(all);
            Iterator<Item> itemIterator = playerData.getItems().iterator();
            while (itemIterator.hasNext()) {
                Item item = itemIterator.next();
                if (!item.getItem().isDead() && System.currentTimeMillis() - item.getTime() < 0L)
                    continue;
                item.getItem().removeMetadata("ItemProtect", P.p);
                itemIterator.remove();
            }
        }
    }
}
