package de.gmx.endermansend.supplyCrates.chest;

import de.gmx.endermansend.supplyCrates.config.ConfigHandler;
import de.gmx.endermansend.supplyCrates.main.SupplyCrates;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class SpawnedChestTracker {

    private SupplyCrates main;

    private ItemHandler itemHandler;

    private long presenceTime;

    private long particleFrequency = 20L;

    public SpawnedChestTracker() {

        this.main = SupplyCrates.getInstance();
        this.itemHandler = new ItemHandler();

        ConfigHandler config = main.getConfigHandler();
        presenceTime = config.get.presenceTime() * 20;

    }

    public void spawnDescendingChest(String chest, Location location) {

        (new DescendingChest(chest, location)).runTaskTimer(main, 0L, 60L);

    }

    private void deployNewSupplyChest(String chest, Location location) {

        ParticleSpawner particleSpawner = new ParticleSpawner(location);

        (new ChestSpawner(
                location,
                itemHandler.createItemStacksFor(chest),
                particleSpawner
        )).runTaskTimer(main, presenceTime, 0L);

        particleSpawner.runTaskTimerAsynchronously(main, particleFrequency, particleFrequency);

    }

    /**
     * Spawns a beam of light at the given location to mark the chest.
     *
     * @param location Location of the beam
     */
    private void spawnParticleBeamAt(Location location) {

        World world = location.getWorld();
        int maxHeight = world.getMaxHeight();
        double r = 1;

        for (double y = location.getY(); y <= maxHeight; y += 0.5) {
            double x = r * Math.cos(y);
            double z = r * Math.sin(y);
            r += 0.05;

            world.spawnParticle(
                    Particle.PORTAL,
                    new Location(
                            world,
                            location.getX() + x,
                            y,
                            location.getZ() + z
                    ),
                    1);
            world.spawnParticle(
                    Particle.PORTAL,
                    new Location(
                            world,
                            location.getX() - x,
                            y,
                            location.getZ() - z
                    ),
                    1);
        }

    }

    /**
     * Spawns a chest at the highest possible block and lets it descend.
     */
    class DescendingChest extends BukkitRunnable {

        private Location location;
        private World world;

        private double highestBlock;

        private Material previousMaterial;

        private String chest;

        public DescendingChest(String chest, Location location) {

            this.location = location;
            this.world = location.getWorld();
            this.location.setY(world.getMaxHeight());
            previousMaterial = location.getBlock().getType();
            highestBlock = world.getHighestBlockYAt(location);

            this.chest = chest;

        }

        public void run() {

            if (location.getY() <= highestBlock) {
                deployNewSupplyChest(chest, location);
            } else {

                Block block = location.getBlock();
                block.setType(previousMaterial);
                block.removeMetadata("SupplyCrate", main);
                location.add(0, -1, 0);

                block = location.getBlock();
                previousMaterial = block.getType();
                block.setType(Material.CHEST);
                block.setMetadata("SupplyCrate", new FixedMetadataValue(main, true));
                spawnParticleBeamAt(location);

            }
        }
    }

    /**
     * Spawns a chest in the constructor and removes it again when the run method is called (-> needs to be executed
     * with a delay).
     */
    class ChestSpawner extends BukkitRunnable {

        private Location location;

        private ParticleSpawner particleSpawner;

        public ChestSpawner(Location location, List<ItemStack> items, ParticleSpawner particleSpawner) {

            // Process chest
            this.location = location;
            Block block = location.getBlock();
            this.particleSpawner = particleSpawner;

            block.setMetadata("SupplyCrate", new FixedMetadataValue(main, block.getType().toString()));
            block.setType(Material.CHEST);
            Chest chest = (Chest) block.getState();
            Inventory chestInventory = chest.getInventory();

            for (ItemStack item : items)
                chestInventory.addItem(item);

            // Process glowstone
            Block ground = (new Location(location.getWorld(), location.getX(), location.getY() - 1, location.getZ())).getBlock();
            ground.setMetadata("SupplyCrate", new FixedMetadataValue(main, ground.getType().toString()));
            ground.setType(Material.GLOWSTONE);

        }

        public void run() {
            SpawnHelper.resetBlock(location.getBlock());
            SpawnHelper.resetBlock((location.add(0, -1, 0).getBlock()));
            particleSpawner.cancel();
            this.cancel();
        }

    }

    /**
     * Spawns particle beams at the given location.
     */
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
