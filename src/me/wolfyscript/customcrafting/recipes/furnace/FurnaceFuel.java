package me.wolfyscript.customcrafting.recipes.furnace;

import me.wolfyscript.customcrafting.configs.custom_configs.fuel.FurnaceFuelConfig;
import org.bukkit.inventory.ItemStack;

public class FurnaceFuel {

    private int burnTime;
    private ItemStack fuel;

    public FurnaceFuel(FurnaceFuelConfig config){
        this.fuel = config.getFuel().clone();
        this.burnTime = config.getBurnTime();
    }

    public int getBurnTime() {
        return burnTime;
    }

    public ItemStack getFuel() {
        return fuel;
    }
}
