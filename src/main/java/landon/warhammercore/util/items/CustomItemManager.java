package landon.warhammercore.util.items;

import landon.warhammercore.util.GiveUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class CustomItemManager {
    private JavaPlugin plugin;
    private List<CustomItem> oasisItems = new ArrayList<>();
    public CustomItemManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerItem(CustomItem item) {
        this.oasisItems.add(item);
    }

    public void registerItems(CustomItem[] items) {
        for (CustomItem oasisItem : Arrays.asList(items)) {
            this.registerItem(oasisItem);
        }
    }

    public CustomItem getOasisItem(String id) {
        for(CustomItem item : this.oasisItems) {
            if(item.getName().equalsIgnoreCase(id)) {
                return item;
            }
        }
        return null;
    }

    public boolean isOasisItem(ItemStack is) {
        for(CustomItem item : this.oasisItems) {
            if(item.getItem().isSimilar(is)) {
                return true;
            }
        }
        return false;
    }

    public CustomItem getOasisItem(ItemStack is) {
        for(CustomItem item : this.oasisItems) {
            if (item.getItem().isSimilar(is)) {
                return item;
            }
        }
        return null;
    }

    public void giveOasisItem(Player player, String id) {
        GiveUtil.giveOrDropItem(player, this.getOasisItem(id).getItem());
    }
}
