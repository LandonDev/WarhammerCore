package landon.warhammercore.patchapi.patches.chat;

import landon.warhammercore.util.customcommand.CustomCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandChatFilter extends CustomCommand {
    public CommandChatFilter() {
        super("togglefilter", false, true, "core.command.togglefilter");
    }

    @Override
    public void execute(Player player, List<String> args) {
        if (ChatFilter.disabledFilteredPlayers.add(player.getUniqueId())) {
            player.sendMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + "(!) " + ChatColor.AQUA + "Chat Filter " + ChatColor.RED + ChatColor.BOLD.toString() + "DISABLED");
            player.sendMessage(ChatColor.GRAY + "Global chat and /msg's will no longer be filtered for profanity.");
        } else {
            player.sendMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + "(!) " + ChatColor.AQUA + "Chat Filter " + ChatColor.GREEN + ChatColor.BOLD.toString() + "ENABLED");
            player.sendMessage(ChatColor.GRAY + "Global chat and /msg's will be filtered for profanity.");
            ChatFilter.disabledFilteredPlayers.remove(player.getUniqueId());
        }
    }

    @Override
    public void onFail(Player player, List<String> args) {
        player.sendMessage(getFailMessage());
    }
}
