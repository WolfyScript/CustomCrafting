package me.wolfyscript.customcrafting.items;

import me.wolfyscript.customcrafting.configs.custom_configs.ItemConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CustomItem extends ItemStack{

    private ItemConfig config;
    private String id;

    public CustomItem(ItemConfig config){
        super(config.getCustomItem());
        this.config = config;
        this.id = config.getId();
    }

    public CustomItem(Material material){
        super(new ItemStack(material));
        this.config = null;
        this.id = config.getId();
    }

    public String getId() {
        return id;
    }

    public ItemConfig getConfig() {
        return config;
    }
}
