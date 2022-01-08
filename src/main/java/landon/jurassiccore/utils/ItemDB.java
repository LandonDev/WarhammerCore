package landon.jurassiccore.utils;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemDB {
    public ItemDB() {
        this.items = new HashMap<>();
        this.names = new HashMap<>();
        this.primaryName = new HashMap<>();
        this.durabilities = new HashMap<>();
        this.splitPattern = Pattern.compile("((.*)[:+',;.](\\d+))");
        this.file = new ManagedFile("items.csv");
        this.reloadConfig();
    }

    private final Map<String, Integer> items;

    public void reloadConfig() {
        List<String> lines = this.file.getLines();
        if (lines.isEmpty()) {
            return;
        }
        this.durabilities.clear();
        this.items.clear();
        this.names.clear();
        this.primaryName.clear();
        for (String line : lines) {
            line = line.trim().toLowerCase(Locale.ENGLISH);
            if (line.length() > 0 && line.charAt(0) == '#') {
                continue;
            }
            String[] parts = line.split("[^a-z0-9]");
            if (parts.length < 2) {
                continue;
            }
            int numeric = Integer.parseInt(parts[1]);
            short data = (parts.length > 2 && !parts[2].equals("0")) ? Short.parseShort(parts[2]) : 0;
            String itemName = parts[0].toLowerCase(Locale.ENGLISH);
            this.durabilities.put(itemName, Short.valueOf(data));
            this.items.put(itemName, Integer.valueOf(numeric));
            ItemData itemData = new ItemData(numeric, data);
            if (this.names.containsKey(itemData)) {
                List<String> nameList = (List) this.names.get(itemData);
                nameList.add(itemName);
                Collections.sort(nameList, new LengthCompare());
                continue;
            }
            List<String> nameList = new ArrayList<String>();
            nameList.add(itemName);
            this.names.put(itemData, nameList);
            this.primaryName.put(itemData, itemName);
        }
    }

    private final Map<ItemData, List<String>> names;
    private final Map<ItemData, String> primaryName;
    private final Map<String, Short> durabilities;

    public ItemStack get(String id, int quantity) throws Exception {
        ItemStack retval = get(id.toLowerCase(Locale.ENGLISH));
        retval.setAmount(quantity);
        return retval;
    }

    private final ManagedFile file;
    private final Pattern splitPattern;

    public ItemStack getByName(String name) {
        if(this.items.containsKey(name.toLowerCase())) {
            return new ItemStack(Material.getMaterial(this.items.get(name)), 64);
        }
        return null;
    }

    public ItemStack getByName(String name, int quantity) {
        if(this.items.containsKey(name.toLowerCase())) {
            return new ItemStack(Material.getMaterial(this.items.get(name)), quantity);
        }
        return null;
    }

    public ItemStack get(String id) throws Exception {
        int itemid = 0;
        String itemname = null;
        short metaData = 0;
        Matcher parts = this.splitPattern.matcher(id);
        if (parts.matches()) {
            itemname = parts.group(2);
            metaData = Short.parseShort(parts.group(3));
        } else {

            itemname = id;
        }
        if (NumberUtils.isNumber(itemname)) {
            itemid = Integer.parseInt(itemname);
        } else if (NumberUtils.isNumber(id)) {
            itemid = Integer.parseInt(id);
        } else {

            itemname = itemname.toLowerCase(Locale.ENGLISH);
        }
        if (itemid < 1) {
            if (this.items.containsKey(itemname)) {
                itemid = ((Integer) this.items.get(itemname)).intValue();
                if (this.durabilities.containsKey(itemname) && metaData == 0) {
                    metaData = ((Short) this.durabilities.get(itemname)).shortValue();
                }
            } else if (Material.getMaterial(itemname.toUpperCase(Locale.ENGLISH)) != null) {
                Material bMaterial = Material.getMaterial(itemname.toUpperCase(Locale.ENGLISH));
                itemid = bMaterial.getId();
            } else {

                try {
                    Material bMaterial = Bukkit.getUnsafe().getMaterialFromInternalName(itemname.toLowerCase(Locale.ENGLISH));
                    itemid = bMaterial.getId();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (itemid < 1) {
            Bukkit.getLogger().log(Level.SEVERE, "Unknown item name!");
        }
        Material mat = Material.getMaterial(itemid);
        if (mat == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Unknown item ID!");
        }
        ItemStack retval = new ItemStack(mat);
        retval.setAmount(mat.getMaxStackSize());
        retval.setDurability(metaData);
        return retval;
    }

    public List<ItemStack> getMatching(Player player, String[] args) throws Exception {
        List<ItemStack> is = new ArrayList<ItemStack>();
        if (args.length < 1) {
            is.add(player.getItemInHand());
        } else if (args[0].equalsIgnoreCase("hand")) {
            is.add(player.getItemInHand());
        } else if (args[0].equalsIgnoreCase("inventory") || args[0].equalsIgnoreCase("invent") || args[0].equalsIgnoreCase("all")) {
            for (ItemStack stack : player.getInventory().getContents()) {
                if (stack != null &&
                        stack.getType() != Material.AIR) {
                    is.add(stack);
                }
            }

        } else if (args[0].equalsIgnoreCase("blocks")) {
            for (ItemStack stack : player.getInventory().getContents()) {
                if (stack != null && stack.getTypeId() <= 255 &&
                        stack.getType() != Material.AIR) {
                    is.add(stack);
                }
            }

        } else {

            is.add(get(args[0]));
        }
        if (is.isEmpty() || ((ItemStack) is.get(0)).getType() == Material.AIR) {
            Bukkit.getLogger().log(Level.SEVERE, "Item Sell Air ERROR!");
        }
        return is;
    }

    public String names(ItemStack item) {
        ItemData itemData = new ItemData(item.getTypeId(), item.getDurability());
        List<String> nameList = (List) this.names.get(itemData);
        if (nameList == null) {
            itemData = new ItemData(item.getTypeId(), (short) 0);
            nameList = (List) this.names.get(itemData);
            if (nameList == null) {
                return null;
            }
        }
        if (nameList.size() > 15) {
            nameList = nameList.subList(0, 14);
        }
        return StringUtil.joinList(", ", new Object[]{nameList});
    }

    public String name(ItemStack item) {
        ItemData itemData = new ItemData(item.getTypeId(), item.getDurability());
        String name = (String) this.primaryName.get(itemData);
        if (name == null) {
            itemData = new ItemData(item.getTypeId(), (short)0);
            name = (String) this.primaryName.get(itemData);
            if (name == null) {
                return null;
            }
        }
        return name;
    }

    static class ItemData {
        private final int itemNo;
        private final short itemData;

        ItemData(int itemNo, short itemData) {
            this.itemNo = itemNo;
            this.itemData = itemData;
        }


        public int getItemNo() {
            return this.itemNo;
        }


        public short getItemData() {
            return this.itemData;
        }


        public int hashCode() {
            return 31 * this.itemNo ^ this.itemData;
        }


        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof ItemData)) {
                return false;
            }
            ItemData pairo = (ItemData) o;
            return (this.itemNo == pairo.getItemNo() && this.itemData == pairo.getItemData());
        }
    }


    class LengthCompare
            extends Object
            implements Comparator<String> {
        public int compare(String s1, String s2) {
            return s1.length() - s2.length();
        }
    }
}

