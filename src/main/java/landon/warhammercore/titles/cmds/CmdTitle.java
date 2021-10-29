package landon.warhammercore.titles.cmds;

import landon.warhammercore.titles.mongo.TitleManager;
import landon.warhammercore.titles.utils.TitleGUI;
import landon.warhammercore.titles.utils.TitleVoucher;
import landon.warhammercore.util.GiveUtil;
import landon.warhammercore.util.c;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdTitle implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender instanceof Player) {
            Player player = (Player)commandSender;
            if(player.isOp() || player.hasPermission("title.create")) {
                if(args.length > 0 && args[0].equalsIgnoreCase("create")) {
                    try {
                        String title = "";
                        for(int i = 0; i < args.length; i++) {
                            if(i != 0) {
                                title = title + args[i];
                            }
                        }
                        TitleManager.get().createTitle(title);
                        player.sendMessage(c.c("&aSuccessfully created title: " + c.c(title)));
                        player.sendMessage(c.c("&7Use /title give <player> <title> to give titles."));
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                    } catch (Exception e) {
                        e.printStackTrace();
                        player.sendMessage(c.c("&cSome error occurred, follow exactly: /title create <title with colors>"));
                        player.sendMessage(c.c("&7If this still isn't working, contact developers."));
                    }
                } else if(args.length > 0 && args[0].equalsIgnoreCase("give")) {
                    try {
                        Player toGive = Bukkit.getPlayer(args[1]);
                        String title = TitleManager.get().getTitleFromStrippedText(args[2]);
                        if(title == null) {
                            player.sendMessage(c.c("&cNo title found for query '&7" + args[2] + "&c'"));
                            return false;
                        }
                        player.sendMessage(c.c("&aSuccessfully given title: " + c.c(title)));
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                        GiveUtil.giveOrDropItem(toGive, new TitleVoucher(title).build());
                    } catch (Exception e) {
                        player.sendMessage(c.c("&cSome error occurred, follow exactly: /title give <player> <stripped title>"));
                        player.sendMessage(c.c("&7Common errors are invalid player or invalid title."));
                        e.printStackTrace();
                    }
                } else {
                    if(TitleManager.get().getUnlockedTitles(player).size() >= 1) {
                        TitleGUI.open(player).open(player);
                    } else {
                        player.sendMessage(c.c("&c&l(!) &cYou do not have any &n/titles&c currently unlocked!"));
                        player.sendMessage(c.c("&7Titles can be unlocked by redeeming a title voucher."));
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                    }
                }
            } else {
                if(TitleManager.get().getUnlockedTitles(player).size() >= 1) {
                    TitleGUI.open(player).open(player);
                } else {
                    player.sendMessage(c.c("&c&l(!) &cYou do not have any &n/titles&c currently unlocked!"));
                    player.sendMessage(c.c("&7Titles can be unlocked by redeeming a title voucher."));
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                }
            }
        }
        return false;
    }
}
