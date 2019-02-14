package me.wolfyscript.customcrafting.items;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.ItemConfig;
import me.wolfyscript.customcrafting.gui.PlayerCache;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemUtils {

    public static String getCustomItemID(ItemStack itemStack){
        ItemStack item = itemStack.clone();
        ItemMeta meta = item.getItemMeta();
        if(meta.hasLore()){
            List<String> lore = meta.getLore();
            if(meta.getLore().get(meta.getLore().size()-2).equals("§7Item ID:")){
                return meta.getLore().get(lore.size()-1).substring("§7".length());
            }
        }
        return "";
    }

    public static void saveItem(String id, ItemStack customItem){
        ItemConfig config = new ItemConfig(CustomCrafting.getApi().getConfigAPI(), id.split(":")[0], id.split(":")[1]);
        config.setCustomItem(customItem);
        if(CustomCrafting.getRecipeHandler().getCustomItem(id) != null){
            CustomCrafting.getRecipeHandler().removeCustomItem(id);
        }
        CustomCrafting.getRecipeHandler().addCustomItem(new CustomItem(config));
    }

    public static void applyItem(ItemStack item, PlayerCache cache){
        switch (cache.getSetting()){
            case CRAFT_RECIPE:
                if(cache.getItemTag(0).equals("result")){
                    cache.setCraftResult(item);
                }else{
                    cache.getCraftIngredients().set(Integer.parseInt(cache.getItemTag(0).split(":")[1]), item);
                }
            case FURNACE_RECIPE:
                if(cache.getItemTag(0).equals("result")){
                    cache.setFurnaceResult(item);
                }else{
                    cache.setFurnaceSource(item);
                }

        }
    }


}
