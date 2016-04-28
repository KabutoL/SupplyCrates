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

    public SpawnedChestTracker(SupplyCrates main, ItemHandler itemHandler) {

        this.main = main;
        this.itemHandler = itemHandler;

        ConfigHandler config = main.getConfigHandler();
        presenceTime = config.get.presenceTime() * 20;

    }

    public void dropNewSupplyChest(String chest, Location location, List<ItemStack> items) {

        ParticleSpawner particleSpawner = new ParticleSpawner(location);

        (new ChestSpawner(
                location,
                itemHandler.createItemStacksFor(chest),
                particleSpawner
        )).runTaskTimer(main, presenceTime, 0L);

        particleSpawner.runTaskTimerAsynchronously(main, particleFrequency, particleFrequency);

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

    }

}
