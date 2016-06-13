package de.gmx.endermansend.supplyCrates.config;

import de.gmx.endermansend.supplyCrates.main.SupplyCrates;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class ConfigHandler {

    public GetValuesFromConfig get;

    private SupplyCrates main;
    private Logger logger;
    private CustomConfig chestConfigFile;
    private CustomConfig locationsConfigFile;

    protected FileConfiguration config;
    protected FileConfiguration chestConfig;
    protected FileConfiguration locationsConfig;

    private String configName = "config.yml";
    private String chestConfigName = "chests.yml";
    private String locationsConfigName = "locations.yml";

    public ConfigHandler(SupplyCrates main) {

        chestConfigFile = new CustomConfig(chestConfigName);
        locationsConfigFile = new CustomConfig(locationsConfigName);

        this.main = main;
        this.logger = this.main.getLogger();
        if (!loadConfig())
            createDefaultConfig();

        get = new GetValuesFromConfig(this);

    }

    /**
     * Saves the config files after values have been change. This method is not being used and hasn't been tested yet.
     */
    public void saveConfig() {
        logger.info("Saving configs");
        main.saveConfig();
        chestConfigFile.saveConfig();
        locationsConfigFile.saveConfig();
        logger.info("Configs saved");
    }

    /**
     * Tries to convert the value found under the given path to an int. If it cannot be found in the .yml file, an error
     * message will be printed and a default one will be used.
     *
     * @param config .yml file
     * @param path   Path to the variable
     * @return Value found in the .yml file (default value if none is found)
     */
    protected int getIntFromConfig(FileConfiguration config, String path) {
        if (!config.isSet(path) || !config.isInt(path))
            noValueFound(config.getName(), path);
        return config.getInt(path);
    }

    /**
     * Tries to convert the value found under the given path to a double. If it cannot be found in the .yml file, an error
     * message will be printed and a default one will be used.
     *
     * @param config .yml file
     * @param path   Path to the variable
     * @return Value found in the .yml file (default value if none is found)
     */
    protected double getDoubleFromConfig(FileConfiguration config, String path) {
        if (!config.isSet(path) || !config.isDouble(path))
            noValueFound(config.getName(), path);
        return config.getDouble(path);
    }

    /**
     * Tries to convert the value found under the given path to a String. If it cannot be found in the .yml file, an error
     * message will be printed and a default one will be used.
     *
     * @param config .yml file
     * @param path   Path to the variable
     * @return Value found in the .yml file (default value if none is found)
     */
    protected String getStringFromConfig(FileConfiguration config, String path) {
        if (!config.isSet(path) || !config.isString(path))
            noValueFound(config.getName(), path);
        return config.getString(path);
    }

    /**
     * Tries to convert the values found under the given path to a list of Strings. If it cannot be found in
     * the .yml file, an error message will be printed and a default one is be used.
     *
     * @param config .yml file
     * @param path   Path to the variable
     * @return Value found in the .yml file (default value if none is found)
     */
    protected List getListFromConfig(FileConfiguration config, String path) {
        List list = config.getList(path);
        if (!config.isSet(path) || !config.isList(path))
            noListFound(config.getName(), path);
        else if (list.isEmpty())
            noListFound(config.getName(), path);
        return list;
    }

    /**
     * Tries to convert the values found under the given path to a ConfigurationSection. If it cannot be found in
     * the .yml file, an error message will be printed and a default one is used.
     *
     * @param config .yml file
     * @param path   Path to the variable
     * @return ConfigurationSection found in the .yml file (default value if none is found)
     */
    protected ConfigurationSection getConfigurationSectionFromConfig(FileConfiguration config, String path) {
        if (!config.isSet(path) || !config.isConfigurationSection(path))
            noValueFound(config.getName(), path);
        return config.getConfigurationSection(path);
    }

    /**
     * Checks if the given section exists.
     *
     * @param config  .yml file
     * @param section Name of the section in the the .yml file file
     * @return True if the section exists
     */
    protected boolean SectionExists(FileConfiguration config, String section) {
        return config.isSet(section);
    }

    /**
     * Saves the default configuration file and stores it's content to configHandler.
     */
    private void createDefaultConfig() {

        logger.info("Creating default configs");
        if (!configExists(configName)) {
            main.saveDefaultConfig();
            config = main.getConfig();
        }
        if (!configExists(chestConfigName)) {
            chestConfigFile.saveDefaultConfig();
            chestConfig = chestConfigFile.getConfig();
        }
        if (!configExists(locationsConfigName)) {
            locationsConfigFile.saveDefaultConfig();
            locationsConfig = locationsConfigFile.getConfig();
        }

        logger.info("Configs loaded");

    }

    /**
     * Tries to load the the .yml files from disk. This method returns false if no matching file was found. In this
     * case default files should be loaded.
     *
     * @return True if the configHandler could be loaded
     */
    private boolean loadConfig() {

        logger.info("Loading configs");

        if (this.configExists(configName))
            config = main.getConfig();
        else
            return false;
        if (this.configExists(chestConfigName))
            chestConfig = chestConfigFile.getConfig();
        else
            return false;
        if (this.configExists(locationsConfigName))
            locationsConfig = locationsConfigFile.getConfig();
        else
            return false;

        logger.info("Configs loaded");
        return false;

    }

    /**
     * Prints a warning to the log if a value was not found under the specified path.
     *
     * @param configName Name of the .yml file
     * @param path       Path to the missing value
     */
    private void noValueFound(String configName, String path) {
        logger.warning("Value is missing or of wrong type: " + path);
        logger.warning("Using default value");
        logger.warning("Delete " + configName + " to get a default one");
    }

    /**
     * Prints a warning to the log if a list was not found under the specified path.
     *
     * @param configName Name of the .yml file
     * @param path       Path to the missing value
     */
    private void noListFound(String configName, String path) {
        logger.warning("Could not find list in " + configName + ": " + path);
        logger.warning("Delete " + configName + " to get a default one");
    }

    /**
     * Checks if a the .yml file exists in the plugin folder.
     *
     * @param configName Name of the .yml file
     * @return True if a the .yml file was found
     */
    private boolean configExists(String configName) {

        File[] files = main.getDataFolder().listFiles();
        if (files == null)
            return false;

        for (File file : files) {
            if (file.getName().equals(configName)) {
                return true;
            }
        }

        return false;

    }

}
