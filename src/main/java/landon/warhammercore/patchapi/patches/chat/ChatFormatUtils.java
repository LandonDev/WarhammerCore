package landon.warhammercore.patchapi.patches.chat;

import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.util.RelationUtil;
import landon.warhammercore.patchapi.patches.fpoints.utils.FactionUtils;
import landon.warhammercore.util.c;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.entity.Player;

public class ChatFormatUtils {
    /*public static String getChatFormat(Player playerSending, Player playerReceiving) {
        return ChatFilter.defaultChatFormat
                .replace("%faction%", FactionUtils.getFaction(playerSending) != null ? RelationUtil.getColorOfThatToMe((RelationParticipator)playerReceiving, (RelationParticipator) playerSending) + FactionUtils.getFaction(playerSending).getTag() : "")
                .replace("%rank%", LuckPermsProvider.get().getUserManager().getUser(playerSending.getUniqueId()).getCachedData().getMetaData().getPrefix())
                .replace("%rank_color%", (LuckPermsProvider.get().getUserManager().getUser(playerSending.getUniqueId()).getCachedData().getMetaData().getPrefix().contains(c.c("&l")) ? LuckPermsProvider.get().getUserManager().getUser(playerSending.getUniqueId()).getCachedData().getMetaData().getPrefix().substring(0, 4) : LuckPermsProvider.get().getUserManager().getUser(playerSending.getUniqueId()).getCachedData().getMetaData().getPrefix().substring(0, 2)))
                .replace("%name%", playerSending.getName())
                .replace("%colon_color%", (playerSending.hasPermission("chat")));
    }*/
}