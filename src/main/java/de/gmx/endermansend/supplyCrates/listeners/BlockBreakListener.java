package de.gmx.endermansend.supplyCrates.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

import static de.gmx.endermansend.supplyCrates.chest.SpawnedChestTracker.getMetaKeyWasSpawnedByThisPlugin;

/**
 * Prevents players from breaking a supply chests.
 */
public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {

        Block block = e.getBlock();

        if (block.getType() != Material.CHEST && block.getType() != Material.GLOWSTONE)
            return;

        List<MetadataValue> metaData = block.getMetadata(getMetaKeyWasSpawnedByThisPlugin());

        for (MetadataValue metadataValue : metaData) {
            if (metadataValue.asBoolean()) {
                e.setCancelled(true);
            }
        }
    }

}
