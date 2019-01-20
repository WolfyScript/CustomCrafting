package me.wolfyscript.customcrafting;

import me.wolfyscript.utilities.api.config.Config;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    public static void saveItem(String name, Config config, ItemStack itemStack){
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(itemMeta.getDisplayName().replace('§','&'));
        List<String> newLore = new ArrayList<>();
        for(String row : itemMeta.getLore()){
            newLore.add(row.replace('§','&'));
        }
        itemMeta.setLore(newLore);
        itemStack.setItemMeta(itemMeta);
        config.set(name+".ItemMeta", itemStack.serialize());
    }

    public static ItemStack getItem(Config config, String path){
        ItemStack itemStack = ItemStack.deserialize(config.getConfig().getConfigurationSection(path).getValues(false));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(itemMeta.getDisplayName().replace('&','§'));
        List<String> newLore = new ArrayList<>();
        for(String row : itemMeta.getLore()){
            newLore.add(row.replace('&','§'));
        }
        itemMeta.setLore(newLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
