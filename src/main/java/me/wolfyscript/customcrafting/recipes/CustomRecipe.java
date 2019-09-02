package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;

public interface CustomRecipe extends Recipe {

    String getId();

    String getGroup();

    default CustomItem getCustomResult() {
        return getCustomResults().get(0);
    }

    @Override
    default ItemStack getResult() {
        return getCustomResult().getAsItemStack();
    }

    List<CustomItem> getCustomResults();

    RecipePriority getPriority();

    void load();

    void save();

    CustomConfig getConfig();

    boolean isExactMeta();

}
