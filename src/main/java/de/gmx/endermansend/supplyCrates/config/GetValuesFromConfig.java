package de.gmx.endermansend.supplyCrates.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class GetValuesFromConfig {

    ConfigHandler config;

    GetValuesFromConfig(ConfigHandler config) {
        this.config = config;
    }

    /**
     * Gets the presence time for chests. Converts it from seconds to game ticks.
     *
     * @return Time interval in game ticks
     */
    public int presenceTime() {
        return config.getIntFromConfig("PRESENCE_PERIOD") * 20;
    }

    /**
     * Gets the spawn interval for chests. Converts it from seconds to game ticks.
     *
     * @return Time interval in game ticks
     */
    public int spawnInterval() {
        return config.getIntFromConfig("SPAWN_INTERVAL") * 20;
    }

    /**
     * Gets all chests types defined in the config.
     *
     * @return List of chest names
     */
    public List<String> chests() {
        Set<String> chests = config.getConfigurationSectionFromConfig("CHESTS").getKeys(false);
        return new ArrayList<String>(chests);
    }

    /**
     * Creates a list with all materials an entity can drop on death.
     *
     * @param chest Name of the entity that is supposed to drop items
     * @return A list of materials
     */
    public List<Material> itemDropsFor(String chest) {

        Set<String> materialNames = config.getConfigurationSectionFromConfig("CHESTS." + chest.toUpperCase()).getKeys(false);

        List<Material> materials = new ArrayList<Material>();

        for (String materialName : materialNames) {
            if (materialName.equalsIgnoreCase("PROBABILITY"))
                continue;
            else
                materials.add(Material.getMaterial(materialName.toUpperCase()));
        }

        return materials;

    }

    /**
     * Creates a HashMap with probabilities for dropping different numbers of the specified material when an entity
     * dies.
     *
     * @param chest    Name of the chest
     * @param material Type of the item
     * @return HashMap with the syntax <amount of itemStack, probability in percent/100>
     */
    public HashMap<Integer, Double> itemOccurrence(String chest, String material) {

        HashMap<Integer, Double> occurrences = new HashMap<Integer, Double>();
        ConfigurationSection section = config.getConfigurationSectionFromConfig("CHESTS." + chest.toUpperCase() + "." + material);

        for (String key : section.getKeys(false))
            occurrences.put(Integer.parseInt(key), (Double) section.get(key));

        return occurrences;

    }

    public double dropProbability(String chest) {

        return config.getDoubleFromConfig("CHESTS." + chest.toUpperCase() + ".PROBABILITY");

    }

}
