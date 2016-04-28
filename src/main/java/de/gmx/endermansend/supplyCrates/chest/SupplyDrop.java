package de.gmx.endermansend.supplyCrates.chest;

import de.gmx.endermansend.supplyCrates.config.ConfigHandler;
import de.gmx.endermansend.supplyCrates.main.SupplyCrates;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class SupplyDrop {

    private SupplyCrates main;

    private ItemHandler itemHandler;

    private long presenceTime;

    private long particleFrequency = 20L;

    public SupplyDrop(SupplyCrates main, ItemHandler itemHandler) {

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

        private Material oldBlock;

        private Location location;

        private ParticleSpawner particleSpawner;

        public ChestSpawner(Location location, List<ItemStack> items, ParticleSpawner particleSpawner) {
            this.location = location;
            Block block = location.getBlock();
            this.oldBlock = block.getType();

            this.particleSpawner = particleSpawner;

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
                List<MetadataValue> meta = chest.getMetadata("SupplyCrate");

                if (meta != null && !meta.isEmpty()) {
                    for (MetadataValue s : meta) {
                        if (s.asBoolean()) {

                            chest.getInventory().clear();
                            location.getWorld().playEffect(location, Effect.EXTINGUISH, null);
                            location.getBlock().setType(oldBlock);
                            
                        }
                    }
                }


            }
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
            int r = 1;

            for (double y = location.getY(); y <= maxHeight; y += 2.0) {
                double x = r * Math.cos(y);
                double z = r * Math.sin(y);
                r += 0.1;

                world.spawnParticle(
                        Particle.REDSTONE,
                        new Location(
                                world,
                                location.getX() + x,
                                y,
                                location.getZ() + z
                        ),
                        1,
                        1.5, 0, 1.5);
                world.spawnParticle(
                        Particle.REDSTONE,
                        new Location(
                                world,
                                location.getX() - x,
                                y,
                                location.getZ() - z
                        ),
                        1,
                        1.5, 0, 1.5);
            }

        }

    }

}
