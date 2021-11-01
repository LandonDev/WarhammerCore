package landon.warhammercore.commands;

import com.massivecraft.factions.P;
import landon.warhammercore.WarhammerCore;
import landon.warhammercore.patchapi.UHCFPatch;
import landon.warhammercore.util.c;
import landon.warhammercore.util.customcommand.StructuredCommand;
import landon.warhammercore.util.customcommand.SubCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class CmdACore extends StructuredCommand {
    public CmdACore() {
        super("acore", "Admin command for WarhammerCore.", false, true, "*", new String[0],
                new SubCommand("patches", "Check loaded patches.", false, null, null),
                new SubCommand("patch", "Enable/disable a patch.", false, null, "<enable/disable> <patch_name>")
        );
    }

    @Override
    public void executeNoArgs(Player player, Plugin plugin) {
        sendNoArgMessage(player);
    }

    @Override
    public void execute(Player player, SubCommand subCommand, List<String> args, Plugin plugin2) {
        String sub = subCommand.getSubCommand();
        P plugin = (P) plugin2;
        if(sub.equalsIgnoreCase("patches")) {
            try {
                StringBuilder builder = new StringBuilder();
                builder.append(c.c("&eUHCFPatches (&f" + WarhammerCore.get().getAllPatches().size() + "&e): "));
                for(int i = 0; i < WarhammerCore.get().getAllPatches().size(); i++) {
                    UHCFPatch patch = WarhammerCore.get().getAllPatches().get(i);
                    if(WarhammerCore.get().getEnabledPatches().contains(patch)) {
                        if(i != 0) {
                            builder.append(c.c(", &a" + patch.getClass().getSimpleName()));
                        } else {
                            builder.append(c.c("&a" + patch.getClass().getSimpleName()));
                        }
                    } else {
                        if(i != 0) {
                            builder.append(c.c(", &c" + patch.getClass().getSimpleName()));
                        } else {
                            builder.append(c.c("&c" + patch.getClass().getSimpleName()));
                        }
                    }
                }
                player.sendMessage(builder.toString());
            } catch(Exception e) {
                sendFailedSubCommand(player, subCommand, e);
            }
        }
        if(sub.equalsIgnoreCase("patch")) {
            try {
                if(args.get(0).equalsIgnoreCase("enable")) {
                    if(WarhammerCore.get().getPatch(args.get(1)) != null) {
                        UHCFPatch patch = WarhammerCore.get().getPatch(args.get(1));
                        if(WarhammerCore.get().getEnabledPatches().contains(patch)) {
                            player.sendMessage(c.c("&aPatch &f" + patch.getClass().getSimpleName() + " &ais already enabled!"));
                            return;
                        }

                        patch.enable();
                        patch.inject();
                        player.sendMessage(c.c("&aEnabling patch &f" + patch.getClass().getSimpleName()));
                        return;
                    }
                    player.sendMessage(c.c("&cNo patch found for query '&7" + args.get(1) + "&c'."));
                    player.sendMessage(c.c("&7You can use /acore patches to see all loaded patches."));
                }
                if(args.get(0).equalsIgnoreCase("disable")) {
                    if(WarhammerCore.get().getPatch(args.get(1)) != null) {
                        UHCFPatch patch = WarhammerCore.get().getPatch(args.get(1));
                        if(!WarhammerCore.get().getEnabledPatches().contains(patch)) {
                            player.sendMessage(c.c("&cPatch &f" + patch.getClass().getSimpleName() + " &cis already disabled!"));
                            return;
                        }
                        WarhammerCore.get().disablePatch(patch);
                        player.sendMessage(c.c("&cDisabling patch &f" + patch.getClass().getSimpleName()));
                        return;
                    }
                    player.sendMessage(c.c("&cNo patch found for query '&7" + args.get(1) + "&c'."));
                    player.sendMessage(c.c("&7You can use /acore patches to see all loaded patches."));
                }
                if(!args.get(0).equalsIgnoreCase("enable") && !args.get(0).equalsIgnoreCase("disable")) {
                    player.sendMessage(c.c("&e/acore patch <enable/disable> <patch>"));
                }
            } catch(Exception e) {
                sendFailedSubCommand(player, subCommand, e);
            }
        }
    }

    @Override
    public void fail(Player player, List<String> args, Plugin plugin) {
        player.sendMessage(getFailMessage());
    }
}
