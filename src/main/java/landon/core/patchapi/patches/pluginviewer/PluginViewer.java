package landon.core.patchapi.patches.pluginviewer;

import landon.core.patchapi.UHCFPatch;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PluginViewer extends UHCFPatch {
    protected static PluginViewer inst;
    private String[] blacklisted = new String[] {"Verus"};
    private List<Plugin> plugins = new ArrayList<>();

    public PluginViewer(Plugin p) {
        super(p);
    }

    @Override
    public void enable() {
        inst = this;
        super.enable();
        this.plugins = Arrays.asList(Bukkit.getPluginManager().getPlugins());
        this.registerListener(new PluginCommandListener());
    }

    @Override
    public void disable() {
        super.disable();
    }

    public List<Plugin> getRestPlugins() {
        return this.plugins.stream().filter(pl -> !this.isBlacklisted(pl)).filter(pl -> pl.isEnabled()).filter(pl -> !pl.getDescription().getName().replace("Factions", "JurassicCore").contains("Jurassic")).collect(Collectors.toList());
    }

    public List<Plugin> getJurassicPlugins() {
        return this.plugins.stream().filter(pl -> !this.isBlacklisted(pl)).filter(pl -> pl.getDescription().getName().replace("Factions", "JurassicCore").contains("Jurassic")).collect(Collectors.toList());
    }

    public List<Plugin> getDisabledPlugins() {
        return this.plugins.stream().filter(pl -> !this.isBlacklisted(pl)).filter(pl -> !pl.isEnabled()).filter(pl -> !pl.getDescription().getName().replace("Factions", "JurassicCore").contains("Jurassic")).collect(Collectors.toList());
    }

    private boolean isBlacklisted(Plugin plugin) {
        for (String s : this.blacklisted) {
            if(s.equalsIgnoreCase(plugin.getDescription().getName())) {
                return true;
            }
        }
        return false;
    }
}
