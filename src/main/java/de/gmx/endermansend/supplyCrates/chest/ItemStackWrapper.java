package de.gmx.endermansend.supplyCrates.chest;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Wrapper class for ItemStacks. Sees ItemStacks with the same material type as the same item.
 */
public class ItemStackWrapper {

    private final ItemStack itemStack;

    public ItemStackWrapper(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public int hashCode() {
        return itemStack.getType().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ItemStackWrapper) {
            return itemStack.getType() == ((ItemStackWrapper) o).getItemStack().getType();
        }
        return false;
    }

    /**
     * Gets the amount of items in this stack.
     *
     * @return Amount of items in this stick
     */
    public int getAmount() {
        return itemStack.getAmount();
    }

    /**
     * Gets the item stack of this wrapper
     *
     * @return Item in this wrapper
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Gets the type of this item.
     *
     * @return Type of the items in this stack
     */
    public Material getType() {
        return itemStack.getType();
    }

}
