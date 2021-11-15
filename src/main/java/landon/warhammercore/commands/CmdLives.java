package landon.warhammercore.commands;

import landon.warhammercore.WarhammerCore;
import landon.warhammercore.deathbans.lives.LifeManager;
import landon.warhammercore.util.c;
import landon.warhammercore.util.customcommand.CustomCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdLives extends CustomCommand {
    public CmdLives() {
        super("lives", false, false, null);
    }

    @Override
    public void execute(Player player, List<String> args) {
        if(!player.hasPermission("core.command.lives.admin")) {
            WarhammerCore.get().getLifeManager().getLives(player.getUniqueId(), lives -> {
                player.sendMessage(c.c("&e&lYour Lives: &f" + lives));
                player.sendMessage(c.c("&7Lives allow you to continue playing on the server. Each time you die, you lose one of your lives. Once you run out of lives you will be death-banned (meaning you cannot connect to the server for 30 minutes). You can redeem more lives by purchasing or earning them!"));
            });
        } else {
            if(args.size() > 1) {
                try {
                    WarhammerCore.get().getLifeManager().giveLifeItem(Bukkit.getPlayer(args.get(0)), Integer.parseInt(args.get(1)));
                    player.sendMessage(c.c("&aYou have successfully given them a life item!"));
                } catch (Exception e) {
                    player.sendMessage(c.c("&c/lives <player> <amount>"));
                }
            } else {
                WarhammerCore.get().getLifeManager().getLives(player.getUniqueId(), lives -> {
                    player.sendMessage(c.c("&e&lYour Lives: &f" + lives));
                    player.sendMessage(c.c("&7Lives allow you to continue playing on the server. Each time you die, you lose one of your lives. Once you run out of lives you will be death-banned (meaning you cannot connect to the server for 30 minutes). You can redeem more lives by purchasing or earning them!"));
                });
            }
        }
    }

    @Override
    public void onFail(Player player, List<String> args) {

    }
}
