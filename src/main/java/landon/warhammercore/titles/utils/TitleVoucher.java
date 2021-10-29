package landon.warhammercore.titles.utils;

import de.tr7zw.changeme.nbtapi.NBTItem;
import landon.warhammercore.titles.mongo.TitleManager;
import landon.warhammercore.util.c;
import landon.warhammercore.util.items.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TitleVoucher {
    private String title;

    public TitleVoucher(String title) {
        this.title = title;
    }

    public ItemStack build() {
        NBTItem item = new NBTItem(ItemBuilder.createItem(Material.PAPER, "&b&lTITLE &r" + c.c(this.title), "&7Right-Click this item to unlock", "&7this title into your unlocked &n/titles&7."));
        item.setString("titleVoucher", TitleManager.get().getUUIDFromTitle(this.title).toString());
        return item.getItem();
    }
}
