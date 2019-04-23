package me.wolfyscript.customcrafting.configs.custom_configs.fuel;

import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class FurnaceFuelConfig extends CustomConfig {

    public FurnaceFuelConfig(ConfigAPI configAPI, String folder, String name) {
        super(configAPI, "furnace_fuel", folder, "fuel", name);
    }

    public CustomItem getFuel(){
        return getCustomItem("fuel");
    }

    public int getBurnTime(){
        return getInt("burn_time");
    }

}
