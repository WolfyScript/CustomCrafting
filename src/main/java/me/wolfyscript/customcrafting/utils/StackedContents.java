package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class StackedContents {

    private final Map<ItemStack, Integer> contents = new HashMap<>();

    public StackedContents() {
    }

    public StackedContents(Inventory inventory) {
        inventory.forEach(this::accountItemStack);
    }

    public void accountItemStack(ItemStack itemStack) {
        accountItemStack(itemStack, 64);
    }

    public void accountItemStack(ItemStack itemStack, int amount) {
        if (!ItemUtils.isAirOrNull(itemStack)) {
            put(itemStack, Math.min(amount, itemStack.getAmount()));
        }
    }

    void put(ItemStack itemStack, int amount) {
        var clonedItem = itemStack.clone();
        clonedItem.setAmount(1);
        int currentAmount = contents.getOrDefault(clonedItem, 0);
        contents.put(clonedItem, currentAmount + amount);
    }

    ItemStack take(ItemStack itemStack, int amount) {
        var clonedItem = itemStack.clone();
        clonedItem.setAmount(1);
        int currentAmount = contents.getOrDefault(clonedItem, 0);
        if (currentAmount >= amount) {
            this.contents.put(clonedItem, currentAmount - amount);
            return itemStack;
        } else {
            return new ItemStack(Material.AIR);
        }
    }

    @Override
    public String toString() {
        return "StackedContents{" +
                "contents=" + contents +
                '}';
    }
}
