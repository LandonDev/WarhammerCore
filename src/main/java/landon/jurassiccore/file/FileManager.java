package landon.jurassiccore.file;

import com.massivecraft.factions.P;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public class FileManager {
    private final P instance;

    private Map<String, Config> loadedConfigs = new HashMap<>();

    public FileManager(P instance) {
        this.instance = instance;
        loadConfigs();
    }

    public void loadConfigs() {
        if (!this.instance.getDataFolder().exists())
            this.instance.getDataFolder().mkdir();
        Map<String, File> configFiles = new LinkedHashMap<>();
        configFiles.put("config.yml", new File(this.instance.getDataFolder(), "config.yml"));
        configFiles.put("language.yml", new File(this.instance.getDataFolder(), "language.yml"));
        for (String configFileList : configFiles.keySet()) {
            File configFile = configFiles.get(configFileList);
            if (!configFile.exists())
                try {
                    Exception exception2;
                    configFile.createNewFile();
                    Exception exception1 = null;
                } catch (IOException ex) {
                    Bukkit.getServer().getLogger().log(Level.WARNING,
                            String.valueOf(this.instance.getDescription().getName()) + " | Error: Unable to create configuration file.");
                }
        }
    }

    public boolean isFileExist(File configPath) {
        return configPath.exists();
    }

    public String getFileExtension(File configFile) {
        String name = configFile.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1)
            return "";
        return name.substring(lastIndexOf);
    }

    public void unloadConfig(File configPath) {
        this.loadedConfigs.remove(configPath.getPath());
    }

    public void deleteConfig(File configPath) {
        Config config = getConfig(configPath);
        config.getFile().delete();
        this.loadedConfigs.remove(configPath.getPath());
    }

    public Config getConfig(File configPath) {
        if (this.loadedConfigs.containsKey(configPath.getPath()))
            return this.loadedConfigs.get(configPath.getPath());
        Config config = new Config(this, configPath);
        this.loadedConfigs.put(configPath.getPath(), config);
        return config;
    }

    public Map<String, Config> getConfigs() {
        return this.loadedConfigs;
    }

    public boolean isConfigLoaded(File configPath) {
        return this.loadedConfigs.containsKey(configPath.getPath());
    }

    public static class Config {
        private File configFile;

        private FileConfiguration configLoad;

        public Config(FileManager fileManager, File configPath) {
            this.configFile = configPath;
            this.configLoad = (FileConfiguration) YamlConfiguration.loadConfiguration(configPath);
        }

        public File getFile() {
            return this.configFile;
        }

        public FileConfiguration getFileConfiguration() {
            return this.configLoad;
        }

        public FileConfiguration loadFile() {
            this.configLoad = (FileConfiguration) YamlConfiguration.loadConfiguration(this.configFile);
            return this.configLoad;
        }

        public void saveFile() {
            try {
                this.configLoad.save(this.configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
