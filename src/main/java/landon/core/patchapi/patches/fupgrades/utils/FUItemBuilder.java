package landon.core.patchapi.patches.fupgrades.utils;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class FUItemBuilder {
    public static ItemStack buildItem(Material m, short data, String name, String... lore) {
        return buildItem(m, 1, data, name, lore);
    }

    public static ItemStack createSkull(String owner, String name, String... lore) {
        ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        SkullMeta sm = (SkullMeta)is.getItemMeta();
        sm.setOwner(owner);
        sm.setDisplayName(name);
        sm.setLore(new ArrayList(Arrays.asList((Object[])lore)));
        is.setItemMeta((ItemMeta)sm);
        return is;
    }

    public static ItemStack buildItem(Material m, short data, String name, List<String> lore) {
        return buildItem(m, 1, data, name, lore.<String>toArray(new String[lore.size()]));
    }

    public static ItemStack buildItem(Material m, int amount, short data, String name, String... lore) {
        ItemStack is = new ItemStack(m, amount, data);
        ItemMeta im = is.getItemMeta();
        if (name != null)
            im.setDisplayName(name);
        if (lore != null) {
            List<String> lores = new ArrayList<>();
            for (String s : lore) {
                if (s != null)
                    if (s.contains("//")) {
                        lores.addAll(Lists.newArrayList(StringUtils.split(s, "//")));
                    } else {
                        lores.add(s);
                    }
            }
            im.setLore(lores);
        }
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack buildItem(Material m, int amount, short data, String name, List<String> lore) {
        ItemStack is = new ItemStack(m, amount, data);
        ItemMeta im = is.getItemMeta();
        if (name != null)
            im.setDisplayName(name);
        if (lore != null)
            im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack buildItem(Material m, String name, String... lore) {
        return buildItem(m, (short)0, name, lore);
    }
}
