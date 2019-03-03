package me.wolfyscript.customcrafting.items;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.ItemConfig;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Material;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemUtils {

    public static CustomItem getCustomItem(ItemStack itemStack) {
        String id = "";
        List<String> data = new ArrayList<>();
        ItemStack clearedItem = itemStack.clone();
        if (isIDItem(itemStack) && itemStack.getItemMeta().hasLore()) {
            ItemMeta clearedMeta = clearedItem.getItemMeta();
            List<String> clearedLore = clearedMeta.getLore();
            List<String> lore = itemStack.getItemMeta().getLore();
            for (int i = 0; i < lore.size(); i++) {
                String row = lore.get(i);
                if (row.startsWith("§7[§3§lID_ITEM§r§7]")) {
                    id = lore.get(i+1).substring("§3".length());
                    clearedLore.remove(i-1);
                    clearedLore.remove((int)i);
                    clearedLore.remove(row);
                }
            }
            clearedMeta.setLore(clearedLore);
            if(WolfyUtilities.unhideString(clearedMeta.getDisplayName()).contains("%NO_NAME%")){
                clearedMeta.setDisplayName(null);
            }else{
                clearedMeta.setDisplayName(clearedMeta.getDisplayName().replace(WolfyUtilities.hideString(":id_item"),""));
            }
            clearedItem.setItemMeta(clearedMeta);
            if (id.equals("NULL")) {
                CustomItem customItem = new CustomItem(clearedItem);
                customItem.setCustomData(data);
                return customItem;
            }
        }
        if(id.isEmpty()){
            CustomItem customItem = new CustomItem(itemStack.clone());
            customItem.setCustomData(data);
            return customItem;
        }
        CustomItem customItem = CustomCrafting.getRecipeHandler().getCustomItem(id);
        customItem.setCustomData(data);
        return customItem;
    }

    public static boolean isIDItem(ItemStack itemStack) {
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            String name = WolfyUtilities.unhideString(itemStack.getItemMeta().getDisplayName());
            return name.endsWith(":id_item");
        }
        return false;
    }


    public static void saveItem(PlayerCache cache, String id, CustomItem customItem) {
        //TODO: CHECK if ID exists!
        ItemConfig config = new ItemConfig(CustomCrafting.getApi().getConfigAPI(), id.split(":")[0], id.split(":")[1]);
        config.setCustomItem(customItem);
        if (CustomCrafting.getRecipeHandler().getCustomItem(id) != null) {
            CustomCrafting.getRecipeHandler().removeCustomItem(id);
        }
        CustomItem customItem1 = new CustomItem(config);
        cache.setCustomItem(customItem1);
        CustomCrafting.getRecipeHandler().addCustomItem(customItem1);
    }

    public static void applyItem(CustomItem item, PlayerCache cache) {
        switch (cache.getSetting()) {
            case CRAFT_RECIPE:
                if (cache.getItemTag(0).equals("result")) {
                    cache.setCraftResult(item);
                } else {
                    cache.setCraftIngredient(Integer.parseInt(cache.getItemTag(0).split(":")[1]), item);
                }
            case FURNACE_RECIPE:
                if (cache.getItemTag(0).equals("result")) {
                    cache.setFurnaceResult(item);
                } else {
                    cache.setFurnaceSource(item);
                }

        }
    }

    public static boolean isEmpty(List<CustomItem> list){
        for(CustomItem customItem : list){
            if(!customItem.getType().equals(Material.AIR)){
                return false;
            }
        }
        return true;
    }


}
