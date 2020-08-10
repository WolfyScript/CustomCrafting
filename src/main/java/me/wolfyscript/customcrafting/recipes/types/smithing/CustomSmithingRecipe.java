package me.wolfyscript.customcrafting.recipes.types.smithing;

import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import org.bukkit.inventory.SmithingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomSmithingRecipe extends CustomRecipe implements ICustomVanillaRecipe<SmithingRecipe> {


    @Override
    public SmithingRecipe getVanillaRecipe() {
        return null;
    }

    @Override
    public RecipeType getRecipeType() {
        return null;
    }

    @Nullable
    @Override
    public CustomItem getCustomResult() {
        return null;
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return null;
    }

    @Override
    public void setResult(List<CustomItem> result) {

    }

    @Override
    public void renderMenu(GuiWindow guiWindow, GuiUpdateEvent event) {

    }
}
