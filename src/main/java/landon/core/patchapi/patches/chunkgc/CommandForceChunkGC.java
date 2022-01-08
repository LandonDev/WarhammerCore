package landon.core.patchapi.patches.chunkgc;

import landon.core.util.customcommand.CustomCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

final class CommandForceChunkGC extends CustomCommand {
    public CommandForceChunkGC() {
        super("chunkgc", true, false, null);
    }

    @Override
    public void execute(Player player, List<String> args) {
        if (!player.isOp())
            return;
        ChunkGCTask.doChunkGC();
        player.sendMessage("doChunkGC() running!");
        return;
    }

    @Override
    public void onFail(Player player, List<String> args) {
        player.sendMessage(getFailMessage());
    }
}
