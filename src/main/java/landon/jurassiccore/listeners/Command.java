package landon.jurassiccore.listeners;

import java.io.File;
import java.util.Iterator;
import landon.jurassiccore.JurassicCore;
import landon.jurassiccore.file.FileManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Command implements Listener {
  private final JurassicCore instance;
  
  public Command(JurassicCore instance) {
    this.instance = instance;
  }
  
  @EventHandler
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    FileManager fileManager = this.instance.getFileManager();
    Player player = event.getPlayer();
    Iterator<String> iterator = fileManager.getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration().getStringList("Listeners.Command.Blacklist").iterator();
    while (iterator.hasNext()) {
      String command = iterator.next();
      if (!event.getMessage().toLowerCase().startsWith("/" + command.toLowerCase()))
        continue; 
      if (player.hasPermission("jurassiccore.command." + command.toLowerCase()) || 
        player.hasPermission("jurassiccore.command.*") || 
        player.hasPermission("jurassiccore.*"))
        break; 
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
            fileManager.getConfig(new File(this.instance.getDataFolder(), "language.yml")).getFileConfiguration()
            .getString("Listeners.Command.Permission.Message")));
      player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
      event.setCancelled(true);
      break;
    } 
  }
}
