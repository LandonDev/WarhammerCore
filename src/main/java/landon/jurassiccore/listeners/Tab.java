package landon.jurassiccore.listeners;

import java.io.File;
import java.util.Iterator;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.file.FileManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

public class Tab implements Listener {
  private final JurassicCore instance;
  
  public Tab(JurassicCore instance) {
    this.instance = instance;
  }
  
  @EventHandler
  public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent event) {
    FileManager fileManager = this.instance.getFileManager();
    Player player = event.getPlayer();
    Iterator<String> iterator = fileManager.getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration().getStringList("Listeners.Command.Blacklist").iterator();
    while (iterator.hasNext()) {
      String command = iterator.next();
      if (!event.getChatMessage().toLowerCase().startsWith("/" + command.toLowerCase()))
        continue; 
      if (player.hasPermission("jurassiccore.command." + command.toLowerCase()) || 
        player.hasPermission("jurassiccore.command.*") || player.hasPermission("jurassiccore.*"))
        break; 
      event.getTabCompletions().clear();
      break;
    } 
  }
}
