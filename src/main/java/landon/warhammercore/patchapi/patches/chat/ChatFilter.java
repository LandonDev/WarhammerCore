package landon.warhammercore.patchapi.patches.chat;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import com.google.common.reflect.TypeToken;
import com.massivecraft.factions.P;
import com.massivecraft.factions.util.JSONUtils;
import io.netty.util.internal.ConcurrentSet;
import landon.warhammercore.patchapi.UHCFPatch;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ChatFilter extends UHCFPatch {

    public static String defaultChatFormat;
    protected static List<String> bad_words = new ArrayList<>();

    protected static List<String> permanent_bad_words = new ArrayList<>();

    protected static List<UUID> hasntMoved = new ArrayList<>();

    protected static HashMap<UUID, String> lastMessage = new HashMap<>();

    protected static HashMap<UUID, Long> lastMessage_time = new HashMap<>();

    public static Set<UUID> disabledFilteredPlayers = (Set<UUID>)new ConcurrentSet();

    protected static boolean block_chat_until_move = true;

    protected static boolean block_duplicate_messages = true;

    protected static boolean remove_chat_colors = true;

    protected static final Pattern ipPattern = Pattern.compile("((?<![0-9])(?:(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[.,-:; ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2}))(?![0-9]))");

    protected static final Pattern webpattern = Pattern.compile("[-a-zA-Z0-9@:%_\\+.~#?&//=]{2,256}\\.[a-z]{2,4}\\b(\\/[-a-zA-Z0-9@:%_\\+.~#?&//=]*)?");

    private static boolean chat_haulted = false;

    private Type token;

    public ChatFilter(Plugin p) {
        super(p);
        this
                .token = (new TypeToken<ConcurrentSet<UUID>>() {

        }).getType();
    }

    public void enable() {
        this.defaultChatFormat = P.p.getConfig().getString("patches.chat_format.default");
        bad_words = P.p.getConfig().getStringList("patches.chat_filter.bad_words");
        permanent_bad_words = P.p.getConfig().getStringList("patches.chat_filter.permanent_bad_words");
        block_chat_until_move = P.p.getConfig().getBoolean("patches.chat_filter.block_chat_until_move");
        block_duplicate_messages = P.p.getConfig().getBoolean("patches.chat_filter.no_duplicates");
        remove_chat_colors = P.p.getConfig().getBoolean("patches.chat_filter.remove_chat_colors");
        for (String s : P.p.getConfig().getDefaults().getStringList("patches.chat_filter.bad_words")) {
            if (!bad_words.contains(s)) {
                bad_words.add(s);
                Bukkit.getLogger().info("(CommandBlocker) Added command '" + s + "' to blocked commands list (default).");
            }
        }
        bad_words.remove("fu");
        bad_words.remove("fk");
        bad_words.remove("muff");
        registerCommand(new CommandChatFilter());
        registerCommand(new CommandChat());
        registerListener(new ChatFilterListener());
        try {
            disabledFilteredPlayers = (Set<UUID>) JSONUtils.fromJson("plugins/Factions/filterToggles.json", this.token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (disabledFilteredPlayers == null)
            disabledFilteredPlayers = (Set<UUID>)new ConcurrentSet();
    }

    public void disable() {
        chat_haulted = false;
        try {
            JSONUtils.saveJSONToFile("plugins/Factions/filterToggles.json", disabledFilteredPlayers, this.token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static boolean isChatHalted() {
        return chat_haulted;
    }

    protected static void setChatHalted(boolean b) {
        chat_haulted = b;
    }
}

