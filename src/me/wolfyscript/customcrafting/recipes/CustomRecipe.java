package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import org.bukkit.inventory.Recipe;

public interface CustomRecipe extends Recipe {

    String getId();
    String getGroup();

    CustomItem getCustomResult();
    RecipePriority getPriority();

    void load();
    void save();

    CustomConfig getConfig();

    boolean isExactMeta();

}
