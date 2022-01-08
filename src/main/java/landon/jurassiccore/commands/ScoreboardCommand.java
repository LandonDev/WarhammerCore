package landon.jurassiccore.commands;

import landon.jurassiccore.JurassicCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ScoreboardCommand implements CommandExecutor {
    private JurassicCore instance;

    public ScoreboardCommand(JurassicCore instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }
}
