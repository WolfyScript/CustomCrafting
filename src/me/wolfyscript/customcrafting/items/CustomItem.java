package me.wolfyscript.customcrafting.items;

import me.wolfyscript.customcrafting.configs.custom_configs.ItemConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

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

    public ItemStack getIDItem(){
        ItemStack idItem = this.clone();
        ItemMeta idItemMeta = idItem.getItemMeta();
        List<String> lore = idItemMeta.hasLore() ? idItemMeta.getLore() : new ArrayList<>();
        lore.add("&7------------------");
        lore.add("&7Item ID:");
        lore.add("&7"+this.id);
        idItemMeta.setLore(lore);
        idItem.setItemMeta(idItemMeta);
        return idItem;
    }
}
