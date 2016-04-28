package de.gmx.endermansend.supplyCrates.chest;

import de.gmx.endermansend.supplyCrates.config.ConfigHandler;
import de.gmx.endermansend.supplyCrates.main.SupplyCrates;
import org.apache.logging.log4j.core.helpers.KeyValuePair;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Spawner extends BukkitRunnable {

    private SupplyCrates main;

    private ConfigHandler config;

    private ItemHandler itemHandler;

    private SupplyDrop supplyDrop;

    private List<World> worlds;

    private List<KeyValuePair> chestOccurrences;

    /**
     * Continuously spawns one chest per loaded chunk.
     *
     * @param main
     */
    public Spawner(SupplyCrates main) {

        this.main = main;
        config = main.getConfigHandler();
        itemHandler = new ItemHandler(main);

        supplyDrop = new SupplyDrop(main, itemHandler);

        worlds = main.getServer().getWorlds();
        chestOccurrences = new ArrayList<KeyValuePair>();
        for (String chest : config.get.chests())
            chestOccurrences.add(new KeyValuePair(chest, "" + config.get.dropProbability(chest)));

    }

    /**
     * Spawns a chest at the locations. Therefore occurrences are calculated determining the chest types (e.g.
     * white, green, blue, ...)
     */
    public void run() {

        System.out.println("___________________");
        for (World world : worlds) {
            for (Location location : getRandomLocations(world)) {

                double occurrence = Math.random();
                String chest = "";

                // Compares a random value with chest probabilities defined in the config.yml
                for (KeyValuePair chestOccurrence : chestOccurrences) {
                    double currentOccurrence = Double.valueOf(chestOccurrence.getValue());
                    if (occurrence <= currentOccurrence) {
                        chest = chestOccurrence.getKey();
                        break;
                    } else
                        occurrence -= currentOccurrence;
                }

                System.out.println(location);

                supplyDrop.dropNewSupplyChest(chest, location, itemHandler.createItemStacksFor(chest));

            }
        }

    }

    /**
     * Returns one location inside each loaded chunk.
     *
     * @param world World containing the chunks and therefore the locations
     * @return A list containing one location per chunk
     */
    private List<Location> getRandomLocations(World world) {

        Chunk[] chunks = world.getLoadedChunks();

        List<Location> locations = new ArrayList<Location>(chunks.length);

        int i = 0;
        int interval = 20;
        for (Chunk chunk : chunks) {
            i += (Math.random() * interval);
            if (i++ % interval == 0) {
                Location location = new Location(world,
                        chunk.getX() * 16 + (int) (Math.random() * 16),
                        80,
                        chunk.getZ() * 16 + (int) (Math.random() * 16));
                locations.add(world.getHighestBlockAt(location.add(0, 1, 0)).getLocation());
            }
        }
        locations.add(new Location(world, 1, world.getHighestBlockYAt(1, 1), 1));

        return locations;

    }

}
