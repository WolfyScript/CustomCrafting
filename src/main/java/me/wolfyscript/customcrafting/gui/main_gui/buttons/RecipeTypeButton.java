package me.wolfyscript.customcrafting.gui.main_gui.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RecipeTypeButton extends ActionButton<CCCache> {

    public RecipeTypeButton(RecipeType<?> recipeType, ItemStack icon) {
        super(recipeType.getId(), icon, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().setRecipeType(recipeType);
            guiHandler.getCustomCache().setSetting(Setting.RECIPE_CREATOR);
            guiHandler.openWindow(new NamespacedKey("recipe_creator", guiHandler.getCustomCache().getRecipeType().getCreatorID()));
            return true;
        });
    }

    public RecipeTypeButton(RecipeType<?> recipeType, Material icon) {
        this(recipeType, new ItemStack(icon));
    }
}
