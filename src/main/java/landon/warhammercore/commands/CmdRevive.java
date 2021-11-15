package landon.warhammercore.commands;

import landon.warhammercore.deathbans.Deathban;
import landon.warhammercore.deathbans.DeathbanManager;
import landon.warhammercore.util.c;
import landon.warhammercore.util.customcommand.CustomCommand;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdRevive extends CustomCommand {
    public CmdRevive() {
        super("revive", false, true, "core.command.revive");
    }

    @Override
    public void execute(Player player, List<String> args) {
        if(DeathbanManager.get().getReviveCooldowns().containsKey(player.getUniqueId()) && (System.currentTimeMillis() - DeathbanManager.get().getReviveCooldowns().get(player.getUniqueId())) < 300000L) {
            player.sendMessage(c.c("&c&l(!) &cYou are currently on a /revive cooldown. You may execute /revive again in &n" + (300 - ((System.currentTimeMillis() - DeathbanManager.get().getReviveCooldowns().get(player.getUniqueId())) / 1000)) + "&c seconds!"));
            return;
        }
        if(args.size() == 0) {
            player.sendMessage(c.c("&c/revive <player>"));
            return;
        }
        String usernameToSearch = args.get(0).toLowerCase();
        Deathban deathban = DeathbanManager.get().findDeathban(usernameToSearch);
        if(deathban == null) {
            player.sendMessage(c.c("&cThere is no deathban active for a player with the username '&7" + args.get(0) + "&c'"));
            player.sendMessage(c.c("&7Keep in mind that in order to revive a player, you must use their username they had at the time of their deathban!"));
            return;
        }
        deathban.setEndsOn(System.currentTimeMillis());
        DeathbanManager.get().getReviveCooldowns().put(player.getUniqueId(), System.currentTimeMillis());
        player.sendMessage(c.c("&a&l(!) &aYou have successfully revived &n" + args.get(0) + "&a!"));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
    }

    @Override
    public void onFail(Player player, List<String> args) {
        player.sendMessage(getFailMessage());
    }
}
