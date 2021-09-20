package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import org.bukkit.Material;

public class RecipeCreatorSmithing extends RecipeCreator {

    private static final String CHANGE_MATERIAL = "change_material";

    public RecipeCreatorSmithing(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "smithing", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new ButtonRecipeIngredient(0));
        registerButton(new ButtonRecipeIngredient(1));
        registerButton(new ButtonRecipeResult());
        registerButton(new ToggleButton<>(CHANGE_MATERIAL, (cache, guiHandler, player, guiInventory, i) -> cache.getRecipeCreatorCache().getSmithingCache().isOnlyChangeMaterial(), new ButtonState<>(CHANGE_MATERIAL + ".enabled", Material.LIME_CONCRETE, (customCache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            customCache.getRecipeCreatorCache().getSmithingCache().setOnlyChangeMaterial(false);
            return true;
        }), new ButtonState<>(CHANGE_MATERIAL + ".disabled", Material.RED_CONCRETE, (customCache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            customCache.getRecipeCreatorCache().getSmithingCache().setOnlyChangeMaterial(true);
            return true;
        })));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        CCCache cache = event.getGuiHandler().getCustomCache();
        event.setButton(0, BACK);
        var smithingRecipe = cache.getRecipeCreatorCache().getSmithingCache();
        event.setButton(1, ClusterRecipeCreator.HIDDEN);
        event.setButton(3, ClusterRecipeCreator.CONDITIONS);
        event.setButton(5, ClusterRecipeCreator.PRIORITY);
        event.setButton(7, ClusterRecipeCreator.EXACT_META);
        event.setButton(19, "recipe.ingredient_0");
        event.setButton(22, "recipe.ingredient_1");
        event.setButton(25, "recipe.result");

        event.setButton(39, CHANGE_MATERIAL);

        event.setButton(42, ClusterRecipeCreator.GROUP);
        if (smithingRecipe.isSaved()) {
            event.setButton(43, ClusterRecipeCreator.SAVE);
        }
        event.setButton(44, ClusterRecipeCreator.SAVE_AS);
    }

}
