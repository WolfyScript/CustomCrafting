package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
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
        return getCustomResult().getItemStack();
    }

    List<CustomItem> getCustomResults();

    RecipePriority getPriority();

    void load();

    void save();

    RecipeConfig getConfig();

    boolean isExactMeta();

    Conditions getConditions();

}
