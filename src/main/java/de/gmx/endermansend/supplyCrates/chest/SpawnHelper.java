package de.gmx.endermansend.supplyCrates.chest;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class SpawnHelper {

    /**
     * Checks if a block was spawned by this plugin.
     *
     * @param block The block that should be checked
     * @return
     */
    private static Material getOriginalBlock(Block block) {

        List<MetadataValue> meta = block.getMetadata("SupplyCrate");
        Material material = null;
        if (meta != null && !meta.isEmpty()) {
            for (MetadataValue m : meta) {
                try {
                    material = Material.valueOf(m.asString());
                } catch (IllegalArgumentException e) {
                    return null;
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
        block.setType(oldMaterial);

    }

}
