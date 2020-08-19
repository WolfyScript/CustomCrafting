package me.wolfyscript.customcrafting.gui.main_gui.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RecipeTypeButton extends ActionButton {

    public RecipeTypeButton(RecipeType recipeType, ItemStack icon) {
        super(recipeType.getId(), new ButtonState(recipeType.getId(), icon, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSetting(Setting.RECIPE_CREATOR);
            ((TestCache) guiHandler.getCustomCache()).setRecipeType(recipeType);
            guiHandler.changeToInv("recipe_editor");
            return true;
        }));
    }

    public RecipeTypeButton(RecipeType recipeType, Material icon) {
        this(recipeType, new ItemStack(icon));
    }
}
