package me.wolfyscript.customcrafting.configs.custom_configs;

import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;

public class CustomConfig extends Config {

    private String folder;
    private String name;
    private String id;
    private String type;

    public CustomConfig(ConfigAPI configAPI, String defaultName, String folder, String type, String name) {
        super(configAPI, "me/wolfyscript/customcrafting/configs/custom_configs", defaultName, configAPI.getPlugin().getDataFolder().getPath()+"/recipes/"+folder+"/"+type, name);
        this.folder = folder;
        this.name = name;
        this.id = folder+":"+name;
        this.type = type;
    }

    @Override
    public void init() {
        saveAfterSet(true);
        loadDefaults();
    }

    public String getFolder() {
        return folder;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
