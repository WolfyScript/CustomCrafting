package me.wolfyscript.customcrafting.listeners.smelting;

import me.wolfyscript.customcrafting.CustomCrafting;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

public abstract class SmeltAPIAdapter {

    protected CustomCrafting customCrafting;

    public SmeltAPIAdapter(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    public abstract void process(FurnaceSmeltEvent event, Block block, Furnace furnace, FurnaceInventory inventory, ItemStack currentResultItem);

}
