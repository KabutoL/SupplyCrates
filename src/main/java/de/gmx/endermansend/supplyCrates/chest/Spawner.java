package de.gmx.endermansend.supplyCrates.chest;

import de.gmx.endermansend.supplyCrates.config.ConfigHandler;
import de.gmx.endermansend.supplyCrates.main.SupplyCrates;
import org.apache.logging.log4j.core.helpers.KeyValuePair;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Spawner extends BukkitRunnable {

    private SupplyCrates main;

    private ConfigHandler config;

    private ItemHandler itemHandler;

    private List<World> worlds;

    private List<KeyValuePair> chestOccurrences;

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
                world.spawnParticle(Particle.EXPLOSION_HUGE, location, 5);
                world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            }
        }
    }

    private void spawnChestAt(Location location) {

        double occurrence = Math.random();
        String chest = "";

        for (KeyValuePair chestOccurrence : chestOccurrences) {
            double currentOccurrence = Double.valueOf(chestOccurrence.getValue());
            if (occurrence <= currentOccurrence) {
                chest = chestOccurrence.getKey();
                break;
            } else
                occurrence -= currentOccurrence;
        }

        System.out.println(location);
        (new ChestSpawner(location, itemHandler.createItemStacksFor(chest))).runTaskTimerAsynchronously(main, 18000L, 0L);

    }

    private List<Location> getRandomLocations(World world) {

        Chunk[] chunks = world.getLoadedChunks();

        List<Location> locations = new ArrayList<Location>(chunks.length);

        for (Chunk chunk : chunks) {
            Location location = new Location(world,
                    chunk.getX() + (int) (Math.random() * 16),
                    0,
                    chunk.getZ() + (int) (Math.random() * 16));
            locations.add(world.getHighestBlockAt(location.add(0, 1, 0)).getLocation());
        }

        return locations;

    }

    class ChestSpawner extends BukkitRunnable {

        private Material oldBlock;

        private Location location;

        public ChestSpawner(Location location, List<ItemStack> items) {
            this.location = location;
            Block block = location.getBlock();
            this.oldBlock = block.getType();

            block.setType(Material.CHEST);
            Chest chest = (Chest) block.getState();
            Inventory chestInventory = chest.getInventory();

            for (ItemStack item : items)
                chestInventory.addItem(item);

        }

        public void run() {
            location.getBlock().setType(oldBlock);
            this.cancel();
        }
    }

}
