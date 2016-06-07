package de.gmx.endermansend.supplyCrates.chest;

import de.gmx.endermansend.supplyCrates.main.SupplyCrates;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

import static de.gmx.endermansend.supplyCrates.chest.SpawnedChestTracker.getMetaKeyMaterial;
import static de.gmx.endermansend.supplyCrates.chest.SpawnedChestTracker.getMetaKeyWasSpawnedByThisPlugin;

public class SpawnHelper {

    /**
     * Checks if a block was spawned by this plugin.
     *
     * @param block The block that should be checked
     * @return
     */
    private static Material getOriginalBlock(Block block) {

        List<MetadataValue> meta = block.getMetadata("Material");
        Material material = null;
        if (meta != null && !meta.isEmpty()) {
            for (MetadataValue m : meta) {
                try {
                    material = Material.valueOf(m.asString());
                } catch (IllegalArgumentException e) {
                }
            }
        }
        return material;

    }

    /**
     * Checks if the block was spawned by this plugin and then replaces it with the oldBlock.
     *
     * @param block The block that should be reset
     */
    public static void resetBlock(Block block) {

        Material oldMaterial = SpawnHelper.getOriginalBlock(block);

        if (block.getState() instanceof Chest) {
            Location location = block.getLocation();
            ((Chest) block.getState()).getInventory().clear();
            location.getWorld().playEffect(location, Effect.EXTINGUISH, null);
        } else if (block.getType() != Material.GLOWSTONE)
            return;

        if (oldMaterial != null)
            block.setType(oldMaterial);
        block.removeMetadata(getMetaKeyWasSpawnedByThisPlugin(), SupplyCrates.getInstance());
        block.removeMetadata(getMetaKeyMaterial(), SupplyCrates.getInstance());

    }

}
