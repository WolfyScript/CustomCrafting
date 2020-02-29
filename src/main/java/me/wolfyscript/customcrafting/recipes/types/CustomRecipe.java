package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.GuiWindow;

import javax.annotation.Nullable;
import java.util.List;

public interface CustomRecipe<T extends RecipeConfig> {

    String getId();

    RecipeType getRecipeType();

    String getGroup();

    @Nullable
    default CustomItem getCustomResult() {
        return getCustomResults().get(0).getRealItem();
    }

    List<CustomItem> getCustomResults();

    RecipePriority getPriority();

    T getConfig();

    boolean isExactMeta();

    Conditions getConditions();

    boolean isHidden();

    void renderMenu(GuiWindow guiWindow, GuiUpdateEvent event);

}
