package de.gmx.endermansend.supplyCrates.config;

import de.gmx.endermansend.supplyCrates.main.SupplyCrates;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.logging.Level;

public class CustomConfig {

    private FileConfiguration config;
    private File file;
    private String fileName;

    public CustomConfig(String fileName) {
        this.fileName = fileName;
        File dataFolder = SupplyCrates.getInstance().getDataFolder();
        if (dataFolder == null)
            throw new IllegalStateException();
        file = new File(dataFolder, fileName);
    }

    public void reloadConfig() {

        config = YamlConfiguration.loadConfiguration(file);
        InputStream defaultsStream = SupplyCrates.getInstance().getResource(fileName);
        if (defaultsStream != null) {
            Reader defaultsStreamReader = new InputStreamReader(defaultsStream);
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultsStreamReader);
            config.setDefaults(defaultConfig);
            try {
                defaultsStreamReader.close();
            } catch (IOException e) {
                SupplyCrates.getInstance().getLogger().log(Level.SEVERE, "Could not save configHandler to " + file, e);
            }
        }

    }

    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public void saveConfig() {
        if (config == null || file == null)
            return;
        try {
            getConfig().save(file);
        } catch (IOException e) {
            SupplyCrates.getInstance().getLogger().log(Level.SEVERE, "Could not save configHandler to " + file, e);
        }
    }

    public void saveDefaultConfig() {
        if (!file.exists())
            SupplyCrates.getInstance().saveResource(fileName, false);
    }

}
