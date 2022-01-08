package landon.jurassiccore.vault;

import com.massivecraft.factions.P;
import landon.jurassiccore.JurassicCore;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import java.io.File;

public class VaultManager {
    private final JurassicCore instance;

    private Economy economy;

    private Permission permission;

    private Chat chat;

    public VaultManager(JurassicCore instance) {
        this.instance = instance;
        ServicesManager servicesManager = Bukkit.getServer().getServicesManager();
        if (instance.getFileManager().getConfig(new File(instance.getDataFolder(), "config.yml")).getFileConfiguration()
                .getBoolean("Economy.Enable")) {
            this.economy = new EconomyImplementer(instance);
            servicesManager.register(Economy.class, this.economy, P.p, ServicePriority.Normal);
        } else {
            RegisteredServiceProvider<Economy> registeredServiceProviderEconomy = servicesManager
                    .getRegistration(Economy.class);
            if (registeredServiceProviderEconomy != null)
                this.economy = (Economy) registeredServiceProviderEconomy.getProvider();
        }
        RegisteredServiceProvider<Permission> registeredServiceProviderPermission = servicesManager
                .getRegistration(Permission.class);
        if (registeredServiceProviderPermission != null)
            this.permission = (Permission) registeredServiceProviderPermission.getProvider();
        RegisteredServiceProvider<Chat> registeredServiceProviderChat = servicesManager.getRegistration(Chat.class);
        if (registeredServiceProviderChat != null)
            this.chat = (Chat) registeredServiceProviderChat.getProvider();
    }

    public void onDisable() {
        if (this.instance.getFileManager().getConfig(new File(this.instance.getDataFolder(), "config.yml")).getFileConfiguration()
                .getBoolean("Economy.Enable"))
            Bukkit.getServer().getServicesManager().unregister(Economy.class, this.economy);
    }

    public Economy getEconomy() {
        return this.economy;
    }

    public Permission getPermission() {
        return this.permission;
    }

    public Chat getChat() {
        return this.chat;
    }
}
