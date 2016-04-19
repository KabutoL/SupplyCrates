package de.gmx.endermansend.supplyCrates.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class GetValuesFromConfig {

    ConfigHandler config;
    // TODO: Add cache for config values

    GetValuesFromConfig(ConfigHandler config) {
        this.config = config;
    }

    /**
     * Gets all chests types defined in the config.
     *
     * @return List of chest names
     */
    public List<String> chests() {
        Set<String> chests = config.getConfigurationSectionFromConfig("").getKeys(false);
        chests.remove("GENERAL_PROBABILITY");
        return new ArrayList<String>(chests);
    }

    /**
     * Creates a list with all materials an entity can drop on death.
     *
     * @param chest Name of the entity that is supposed to drop items
     * @return A list of materials
     */
    public List<Material> itemDropsFor(String chest) {

        Set<String> materialNames = config.getConfigurationSectionFromConfig(chest.toUpperCase()).getKeys(false);

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
        ConfigurationSection section = config.getConfigurationSectionFromConfig(chest.toUpperCase() + "." + material);

        for (String key : section.getKeys(false))
            occurrences.put(Integer.parseInt(key), (Double) section.get(key));

        return occurrences;

    }

    public double dropProbability(String chest) {

        return config.getDoubleFromConfig(chest.toUpperCase() + ".PROBABILITY");

    }

}
