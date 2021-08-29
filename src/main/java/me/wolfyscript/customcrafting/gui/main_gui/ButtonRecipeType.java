package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

class ButtonRecipeType extends ActionButton<CCCache> {

    ButtonRecipeType(String key, RecipeType<?> recipeType, ItemStack icon) {
        super(key, icon, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().setRecipeType(recipeType);
            cache.setSetting(Setting.RECIPE_CREATOR);
            guiHandler.openWindow(new NamespacedKey("recipe_creator", recipeType.getCreatorID()));
            return true;
        });
    }

    ButtonRecipeType(String key, RecipeType<?> recipeType, Material icon) {
        this(key, recipeType, new ItemStack(icon));
    }
}
