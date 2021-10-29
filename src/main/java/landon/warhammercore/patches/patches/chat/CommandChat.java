package landon.warhammercore.patches.patches.chat;

import landon.warhammercore.util.customcommand.StructuredCommand;
import landon.warhammercore.util.customcommand.SubCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class CommandChat extends StructuredCommand {
    public CommandChat() {
        super("chat", "Chat Moderation tools", false, true, "core.command.chat", new String[]{},
                new SubCommand("clear", "Clear the chat.", false, null, null),
                new SubCommand("toggle", "Enable/disable chatting.", false, null, null)
                );
    }

    @Override
    public void executeNoArgs(Player player, Plugin plugin) {
        sendNoArgMessage(player);
    }

    @Override
    public void execute(Player player, SubCommand subCommand, List<String> args, Plugin plugin) {
        String sub = subCommand.getSubCommand();

        if(sub.equalsIgnoreCase("clear")) {
            ChatUtils.clearChat(player);
        }

        if(sub.equalsIgnoreCase("toggle")) {
            ChatUtils.toggleChat(player);
        }
    }

    @Override
    public void fail(Player player, List<String> args, Plugin plugin) {
        player.sendMessage(getFailMessage());
    }
}
