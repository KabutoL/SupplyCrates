package de.gmx.endermansend.supplyCrates.chest;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class EmptyChestListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {

        InventoryHolder holder = e.getInventory().getHolder();
        if (!(holder instanceof Chest))
            return;

        if (isEmpty(e.getInventory())) {
            SpawnHelper.resetBlock(((Chest) holder).getBlock());
            Location location = ((Chest) holder).getLocation();
            location.add(0, -1, 0);
            SpawnHelper.resetBlock(location.getBlock());
        }

    }

    private boolean isEmpty(Inventory inventory) {

        for (ItemStack itemStack : inventory)
            if (itemStack != null)
                return false;
        return true;

    }

}
