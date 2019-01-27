package me.wolfyscript.customcrafting.configs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class MainConfig extends Config {

    public MainConfig(ConfigAPI configAPI) {
        super(configAPI, "me/wolfyscript/customcrafting/configs", CustomCrafting.getInst().getDataFolder().getPath(),"config");
    }

    @Override
    public void init() {
        loadDefaults();
        configAPI.registerConfig(this);
    }

    public int getAutosaveInterval(){
        return getInt("data.auto_save.interval");
    }
}
