package de.gmx.endermansend.supplyCrates.chest;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class EmptyChestListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {

        InventoryHolder holder = e.getInventory().getHolder();
        if (!(holder instanceof Chest))
            return;

        Chest chest = (Chest) holder;

        List<MetadataValue> meta = chest.getMetadata("SupplyCrate");

        if (meta != null && !meta.isEmpty()) {
            for (MetadataValue s : meta) {
                if (s.asBoolean() && isEmpty(e.getInventory())) {
                    chest.getBlock().setType(Material.AIR);
                }
            }
        }

    }

    private boolean isEmpty(Inventory inventory) {

        for (ItemStack itemStack : inventory)
            if (itemStack != null)
                return false;
        return true;

    }

}
