package me.wolfyscript.customcrafting.configs.custom_configs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.inventory.ItemStack;

public class CustomConfig extends Config {

    private String folder;
    private String name;
    private String id;
    private String type;

    public CustomConfig(ConfigAPI configAPI, String defaultName, String folder, String type, String name) {
        super(configAPI, "me/wolfyscript/customcrafting/configs/custom_configs/"+type, defaultName, configAPI.getPlugin().getDataFolder().getPath()+"/recipes/"+folder+"/"+type, name);
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

    public void saveCustomItem(String path, CustomItem customItem){
        if(customItem != null){
            if(!customItem.getId().isEmpty() && !customItem.getId().equals("NULL")){
                set(path+".item_key", customItem.getId());
            }else {
                saveItem(path+".item", customItem);
            }
        }
    }

    public CustomItem getCustomItem(String path){
        String id = getString(path+".item_key");
        if(id != null && !id.isEmpty()){
            return CustomCrafting.getRecipeHandler().getCustomItem(id);
        }
        return new CustomItem(getItem(path+".item"));
    }

    public RecipePriority getPriority(){
        try {
            return RecipePriority.valueOf(getString("priority"));
        }catch (IllegalArgumentException e){
            return RecipePriority.NORMAL;
        }
    }

    public void setPriority(RecipePriority recipePriority){
        set("priority", recipePriority.name());
    }
}
