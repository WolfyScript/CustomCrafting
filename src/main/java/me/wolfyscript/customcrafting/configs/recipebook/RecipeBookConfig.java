package me.wolfyscript.customcrafting.configs.recipebook;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.JsonConfiguration;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import org.bukkit.Material;

import java.util.List;

public class RecipeBookConfig extends JsonConfiguration {

    private WolfyUtilities api;

    public RecipeBookConfig(CustomCrafting customCrafting) {
        super(WolfyUtilities.getAPI(customCrafting).getConfigAPI(), customCrafting.getDataFolder().getPath(), "recipe_book", "me/wolfyscript/customcrafting/configs/recipebook", "recipebook", false);
        this.api = WolfyUtilities.getAPI(customCrafting);
    }

    public List<String> getSwitchCategoriesOrder() {
        return getStringList("categories.orders.switchConditions");
    }

    public List<String> getMainCategoriesOrder() {
        return getStringList("categories.orders.mainConditions");
    }

    public Categories getCategories() {
        return get(Categories.class, "categories");
    }

    public void setCategories(Categories categories) {
        set("categories.categories", categories);
    }

    public void saveCustomItem(String path, CustomItem customItem) {
        if (customItem != null) {
            if (!customItem.getId().isEmpty() && !customItem.getId().equals("NULL")) {
                this.set(path + ".item_key", customItem.getId());
                this.set(path + ".custom_amount", customItem.getAmount() != CustomItems.getCustomItem(customItem.getId()).getAmount() ? customItem.getAmount() : 0);
            } else {
                this.setItem(path + ".item", customItem.getItemStack());
            }
        } else {
            this.setItem(path + ".item", null);
        }

    }

    public CustomItem getCustomItem(String path) {
        String id = this.getString(path + ".item_key");
        if (id != null && !id.isEmpty()) {
            CustomItem customItem = CustomItems.getCustomItem(id);
            int i = this.getInt(path + ".custom_amount");
            if (i != 0) {
                customItem.setAmount(i);
            }

            return customItem;
        } else {
            return this.getItem(path + ".item") != null ? new CustomItem(this.getItem(path + ".item")) : new CustomItem(Material.AIR);
        }
    }

}
