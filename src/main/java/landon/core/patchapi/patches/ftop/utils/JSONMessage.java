package landon.core.patchapi.patches.ftop.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;

public class JSONMessage {
    private JsonObject json;

    public JSONMessage() {
        initiateData();
    }

    public JSONMessage(String text) {
        initiateData();
        this.json.addProperty("text", text);
    }

    public JSONMessage(String text, ChatColor color) {
        initiateData();
        this.json.addProperty("text", text);
        this.json.addProperty("color", color.name().toLowerCase());
    }

    private void initiateData() {
        this.json = new JsonObject();
        this.json.add("extra", (JsonElement)new JsonArray());
    }

    private JsonArray getExtra() {
        if (!this.json.has("extra"))
            this.json.add("extra", (JsonElement)new JsonArray());
        return (JsonArray)this.json.get("extra");
    }

    public void addText(String text) {
        addText(text, ChatColor.WHITE);
    }

    public void addText(String text, ChatColor color, ChatColor second, Player p) {
        JsonObject data = new JsonObject();
        data.addProperty("text", text);
        data.addProperty("color", second.name().toLowerCase());
        data.addProperty("color", color.name().toLowerCase());
        getExtra().add((JsonElement)data);
    }

    public void addText(String text, ChatColor color) {
        JsonObject data = new JsonObject();
        data.addProperty("text", text);
        data.addProperty("color", color.name().toLowerCase());
        getExtra().add((JsonElement)data);
    }

    public void addText(String text, ChatColor color, Player p) {
        JsonObject data = new JsonObject();
        data.addProperty("text", text);
        data.addProperty("color", color.name().toLowerCase());
        getExtra().add((JsonElement)data);
    }

    public void addInsertionText(String text, ChatColor color, String insertion) {
        JsonObject o = new JsonObject();
        o.addProperty("text", text);
        o.addProperty("color", color.name().toLowerCase());
        o.addProperty("insertion", insertion);
        getExtra().add((JsonElement)o);
    }

    public void addURL(String text, ChatColor color, String url) {
        JsonObject o = new JsonObject();
        o.addProperty("text", text);
        o.addProperty("color", color.name().toLowerCase());
        JsonObject u = new JsonObject();
        u.addProperty("action", "open_url");
        u.addProperty("value", url);
        o.add("clickEvent", (JsonElement)u);
        getExtra().add((JsonElement)o);
    }

    public void addItem(ItemStack item, String text) {
        addItem(item, text, ChatColor.WHITE);
    }

    public void addHoverData(String text, String data) {
        JsonObject o = new JsonObject();
        o.addProperty("text", text);
        o.addProperty("color", ChatColor.RESET.name().toLowerCase());
        JsonObject a = new JsonObject();
        a.addProperty("action", "show_text");
        a.addProperty("value", data);
        o.add("hoverEvent", (JsonElement)a);
        getExtra().add((JsonElement)o);
    }

    public void addItem(ItemStack item, String text, ChatColor color) {
        if (item == null)
            return;
        JsonObject o = new JsonObject();
        o.addProperty("text", text);
        o.addProperty("color", color.name().toLowerCase());
        JsonObject a = new JsonObject();
        a.addProperty("action", "show_item");
        JsonObject i = new JsonObject();
        i.addProperty("id", Integer.valueOf(item.getTypeId()));
        i.addProperty("Damage", Short.valueOf(item.getDurability()));
        if ((item.getItemMeta() != null && (item
                .getItemMeta().getDisplayName() != null || (item.getItemMeta().getLore() != null && item.getItemMeta().getLore().size() > 0))) || item
                .getEnchantments() != null) {
            JsonObject x = new JsonObject();
            JsonObject v = new JsonObject();
            ItemMeta m = item.getItemMeta();
            if (m.hasEnchants());
            if (m.getDisplayName() != null)
                v.addProperty("Name", m.getDisplayName());
            if (m.getLore() != null || item.getEnchantments() != null)
                v.addProperty("Lore", "%LORE%");
            x.add("display", (JsonElement)v);
            i.add("tag", (JsonElement)x);
        }
        String is = i.toString();
        is = is.replace("\"", "");
        if (is.contains("%LORE%")) {
            List<String> lor = new ArrayList<>();
            if (item.getItemMeta().hasEnchants())
                lor.addAll(getEnchantsAsList(item.getEnchantments()));
            if (item.getItemMeta().getLore() != null)
                lor.addAll(item.getItemMeta().getLore());
            String lore = JSONArray.toJSONString(lor);
            lore = lore.replace(":", "|");
            lore = lore.replace("\\", "");
            is = is.replace("%LORE%", lore);
        }
        a.addProperty("value", is);
        o.add("hoverEvent", (JsonElement)a);
        getExtra().add((JsonElement)o);
    }

    public List<String> getEnchantsAsList(Map<Enchantment, Integer> map) {
        List<String> lore = new ArrayList<>();
        for (Map.Entry<Enchantment, Integer> entry : map.entrySet())
            lore.add(ChatColor.GRAY + StringUtils.capitalize(getDisplayEnchantName(((Enchantment)entry.getKey()).getName())) + " " + getNumber(((Integer)entry.getValue()).intValue()));
        return lore;
    }

    public String getDisplayEnchantName(String name) {
        switch (name.toLowerCase()) {
            case "protection_environmental":
                return "Protection";
            case "protection_explosions":
                return "Blast Protection";
            case "protection_fall":
                return "Feathing Falling";
            case "protection_fire":
                return "Fire Protection";
            case "fire_aspect":
                return "Fire Aspect";
            case "durability":
                return "Unbreaking";
            case "dig_speed":
                return "Efficiency";
            case "arrow_infinite":
                return "Infinity";
            case "arrow_damage":
                return "Power";
            case "arrow_fire":
                return "Flame";
            case "loot_bonus_blocks":
                return "Fortune";
            case "silk_touch":
                return "Silk Touch";
            case "knockback":
                return "Knockback";
            case "damage_all":
                return "Sharpness";
            case "damage_undead":
                return "Smite";
            case "damage_arthropods":
                return "Bane of Arthropods";
            case "protection_projectile":
                return "Projectile Protection";
            case "oxygen":
                return "Resperation";
            case "water_worker":
                return "Aqua Affinity";
            case "thorns":
                return "Thorns";
        }
        return "";
    }

    public static String getNumber(int i) {
        if (i > 10)
            return i + "";
        if (i == 1)
            return "I";
        if (i == 2)
            return "II";
        if (i == 3)
            return "III";
        if (i == 4)
            return "IV";
        if (i == 5)
            return "V";
        if (i == 6)
            return "VI";
        if (i == 7)
            return "VII";
        if (i == 8)
            return "VIII";
        if (i == 9)
            return "IX";
        if (i == 10)
            return "X";
        return i + "";
    }

    public void addSuggestCommand(String text, ChatColor color, String cmd) {
        JsonObject o = new JsonObject();
        o.addProperty("text", text);
        o.addProperty("color", color.name().toLowerCase());
        JsonObject u = new JsonObject();
        u.addProperty("action", "suggest_command");
        u.addProperty("value", cmd);
        o.add("clickEvent", (JsonElement)u);
        getExtra().add((JsonElement)o);
    }

    public void addRunCommand(String text, ChatColor color, String cmd, String hoverData) {
        JsonObject o = new JsonObject();
        o.addProperty("text", text);
        o.addProperty("color", color.name().toLowerCase());
        JsonObject u = new JsonObject();
        u.addProperty("action", "run_command");
        u.addProperty("value", cmd);
        o.add("clickEvent", (JsonElement)u);
        JsonObject a = new JsonObject();
        a.addProperty("action", "show_text");
        a.addProperty("value", hoverData);
        o.add("hoverEvent", (JsonElement)a);
        getExtra().add((JsonElement)o);
    }

    public String toString() {
        return this.json.toString();
    }

    public void sendToPlayer(Player p) {
        (((CraftPlayer)p).getHandle()).playerConnection.sendPacket((Packet)new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(this.json.toString())));
    }

    public void setColor(ChatColor color) {
        this.json.addProperty("color", color.name().toLowerCase());
    }

    public void setText(String text) {
        this.json.addProperty("text", text);
    }
}
