package de.gmx.endermansend.supplyCrates.chest;

import de.gmx.endermansend.supplyCrates.config.ConfigHandler;
import de.gmx.endermansend.supplyCrates.main.SupplyCrates;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemHandler {

    ConfigHandler config;

    public ItemHandler(SupplyCrates main) {
        config = main.getConfigHandler();
    }

    public List<ItemStack> createItemStacksFor(String chest) {

        List<ItemStackWrapper> itemStacks = new ArrayList<ItemStackWrapper>();

        List<Material> materials = config.get.itemDropsFor(chest);
        for (Material material : materials) {
            if (material == null) {
                System.out.println("Wrong material name");
                continue;
            }
            HashMap<Integer, Double> occurrences = config.get.itemOccurrence(chest, material.name());
            if (occurrences == null)
                continue;
            for (ItemStack item : getItemStacksFor(material, occurrences))
                itemStacks.add(new ItemStackWrapper(item));
        }

        return packItemStacks(itemStacks);

    }

    /**
     * Stacks up a list of ItemStacks by combining items from the same material to one stack.
     *
     * @param itemStacks Stacks of items that are going to be sorted
     * @return A stacked up version of the input
     */
    private List<ItemStack> packItemStacks(List<ItemStackWrapper> itemStacks) {

        Map<ItemStackWrapper, Integer> itemMap = new HashMap<ItemStackWrapper, Integer>();

        Integer amount;
        for (ItemStackWrapper itemStack : itemStacks) {

            amount = itemMap.get(itemStack);
            if (amount != null) {
                itemMap.put(itemStack, amount + itemStack.getAmount());
            } else {
                itemMap.put(itemStack, itemStack.getAmount());
            }
        }

        List<ItemStack> ret = new ArrayList<ItemStack>();
        for (Map.Entry<ItemStackWrapper, Integer> entry : itemMap.entrySet())
            ret.add(new ItemStack(entry.getKey().getType(), entry.getValue()));

        return ret;

    }

    private List<ItemStack> getItemStacksFor(Material material, HashMap<Integer, Double> occurrences) {

        List<ItemStack> itemStacks = new ArrayList<ItemStack>();

        for (Integer key : occurrences.keySet()) {
            if (Math.random() < occurrences.get(key)) {
                ItemStack item = new ItemStack(material);
                item.setAmount(key);
                itemStacks.add(item);
            }
        }

        return itemStacks;

    }

}
