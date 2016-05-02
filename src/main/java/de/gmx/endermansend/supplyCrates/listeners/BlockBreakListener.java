package de.gmx.endermansend.supplyCrates.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

/**
 * Prevents players from breaking a supply chests.
 */
public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {

        Block block = e.getBlock();

        if (block.getType() != Material.CHEST)
            return;

        List<MetadataValue> metaData = block.getMetadata("SupplyCrate");

        for (MetadataValue metadataValue : metaData) {
            if (metadataValue.asBoolean()) {
                e.setCancelled(true);
            }
        }
    }

}
