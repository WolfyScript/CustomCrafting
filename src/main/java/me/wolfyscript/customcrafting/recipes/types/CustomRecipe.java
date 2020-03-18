package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import javax.annotation.Nullable;
import java.util.List;

public interface CustomRecipe<T extends RecipeConfig> extends Recipe {

    String getId();

    String getGroup();

    @Nullable
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

    CustomRecipe save(ConfigAPI configAPI, String namespace, String key);

    CustomRecipe save(T config);

    T getConfig();

    boolean isExactMeta();

    Conditions getConditions();

    boolean isHidden();

}
