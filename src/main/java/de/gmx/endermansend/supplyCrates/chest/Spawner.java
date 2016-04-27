package de.gmx.endermansend.supplyCrates.chest;

import de.gmx.endermansend.supplyCrates.config.ConfigHandler;
import de.gmx.endermansend.supplyCrates.main.SupplyCrates;
import org.apache.logging.log4j.core.helpers.KeyValuePair;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Spawner extends BukkitRunnable {

    private SupplyCrates main;

    private ConfigHandler config;

    private ItemHandler itemHandler;

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
        worlds = main.getServer().getWorlds();
        chestOccurrences = new ArrayList<KeyValuePair>();
        for (String chest : config.get.chests())
            chestOccurrences.add(new KeyValuePair(chest, "" + config.get.dropProbability(chest)));

    }

    public void run() {
        System.out.println("___________________");
        for (World world : worlds) {
            for (Location location : getRandomLocations(world)) {
                spawnChestAt(location);
                (new ParticleSpawner(location)).runTaskTimerAsynchronously(main, 20L, 20L);
                spawnParticleBeamAt(location);
                world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            }
        }
    }

    /**
     * Spawns a chest at the given location. Therefore an occurrence is calculated determining the chest type (e.g.
     * white, green, blue, ...)
     *
     * @param location Location of the chest.
     */
    private void spawnChestAt(Location location) {

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
        long presenceTime = config.get.presenceTime() * 20;
        (new ChestSpawner(location, itemHandler.createItemStacksFor(chest))).runTaskTimer(main, presenceTime, 0L);

    }

    /**
     * Spawns a beam of light at the given location to mark the chest.
     *
     * @param location Location of the beam
     */
    private void spawnParticleBeamAt(Location location) {

        World world = location.getWorld();
        int r = 1;
        for (double y = 0; y <= 50; y += 0.5) {
            double x = r * Math.cos(y);
            double z = r * Math.sin(y);
            Location currentLocation = new Location(world, location.getX() + x, location.getY() + y, location.getZ() + z);
            world.spawnParticle(Particle.REDSTONE, currentLocation, 1);
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

    /**
     * Spawns a chest in the constructor and removes it again when the run method is called (-> needs to be executed
     * with a delay).
     */
    class ChestSpawner extends BukkitRunnable {

        private Material oldBlock;

        private Location location;

        public ChestSpawner(Location location, List<ItemStack> items) {
            this.location = location;
            Block block = location.getBlock();
            this.oldBlock = block.getType();

            block.setType(Material.CHEST);
            block.setMetadata("SupplyCrate", new FixedMetadataValue(main, true));
            Chest chest = (Chest) block.getState();
            Inventory chestInventory = chest.getInventory();

            for (ItemStack item : items)
                chestInventory.addItem(item);

        }

        public void run() {
            Block block = location.getBlock();
            if (block.getState() instanceof Chest) {
                Chest chest = (Chest) block.getState();
                chest.getInventory().clear();
                location.getBlock().setType(oldBlock);
            }
            this.cancel();
        }
    }

    class ParticleSpawner extends BukkitRunnable {

        Location location;

        ParticleSpawner(Location location) {
            this.location = location;
        }

        public void run() {
            spawnParticleBeamAt(location);
        }

    }

}
