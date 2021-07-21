package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.RecipeCreatorCluster;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonRecipeIngredient;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonRecipeResult;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapedEliteCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapelessEliteCraftRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

public class EliteWorkbenchCreator extends RecipeCreator {

    private static final String SETTINGS = "settings";

    public EliteWorkbenchCreator(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "elite_workbench", 54, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        for (int i = 0; i < 37; i++) {
            registerButton(new ButtonRecipeIngredient(i));
        }
        registerButton(new ButtonRecipeResult());

        registerButton(new ActionButton<>(SETTINGS, Material.REDSTONE, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            guiHandler.openWindow("elite_workbench_settings");
            return true;
        }));

        registerButton(new ToggleButton<>(RecipeCreatorCluster.SHAPELESS, (cache, g, p, gui, i) -> cache.getEliteCraftingRecipe().isShapeless(), new ButtonState<>(RecipeCreatorCluster.SHAPELESS_ENABLED, PlayerHeadUtils.getViaURL("f21d93da43863cb3759afefa9f7cc5c81f34d920ca97b7283b462f8b197f813"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.setCustomRecipe(Types.ELITE_WORKBENCH, new ShapedEliteCraftRecipe(guiHandler.getCustomCache().getEliteCraftingRecipe()));
            return true;
        }), new ButtonState<>(RecipeCreatorCluster.SHAPELESS_DISABLED, PlayerHeadUtils.getViaURL("1aae7e8222ddbee19d184b97e79067814b6ba3142a3bdcce8b93099a312"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.setCustomRecipe(Types.ELITE_WORKBENCH, new ShapelessEliteCraftRecipe(guiHandler.getCustomCache().getEliteCraftingRecipe()));
            return true;
        })));

        registerButton(new ToggleButton<>(RecipeCreatorCluster.MIRROR_HORIZONTAL, (cache, g, p, gui, i) -> ((ShapedEliteCraftRecipe) cache.getEliteCraftingRecipe()).mirrorHorizontal(), new ButtonState<>(RecipeCreatorCluster.MIRROR_HORIZONTAL_ENABLED, PlayerHeadUtils.getViaURL("956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311"), (cache, guiHandler, player, inventory, slot, event) -> {
            ((ShapedEliteCraftRecipe) cache.getEliteCraftingRecipe()).setMirrorHorizontal(false);
            return true;
        }), new ButtonState<>(RecipeCreatorCluster.MIRROR_HORIZONTAL_DISABLED, PlayerHeadUtils.getViaURL("956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311"), (cache, guiHandler, player, inventory, slot, event) -> {
            ((ShapedEliteCraftRecipe) cache.getEliteCraftingRecipe()).setMirrorHorizontal(true);
            return true;
        })));
        registerButton(new ToggleButton<>(RecipeCreatorCluster.MIRROR_VERTICAL, (cache, g, p, gui, i) -> ((ShapedEliteCraftRecipe) cache.getEliteCraftingRecipe()).mirrorVertical(), new ButtonState<>(RecipeCreatorCluster.MIRROR_VERTICAL_ENABLED, PlayerHeadUtils.getViaURL("882faf9a584c4d676d730b23f8942bb997fa3dad46d4f65e288c39eb471ce7"), (cache, guiHandler, player, inventory, slot, event) -> {
            ((ShapedEliteCraftRecipe) cache.getEliteCraftingRecipe()).setMirrorVertical(false);
            return true;
        }), new ButtonState<>(RecipeCreatorCluster.MIRROR_VERTICAL_DISABLED, PlayerHeadUtils.getViaURL("882faf9a584c4d676d730b23f8942bb997fa3dad46d4f65e288c39eb471ce7"), (cache, guiHandler, player, inventory, slot, event) -> {
            ((ShapedEliteCraftRecipe) cache.getEliteCraftingRecipe()).setMirrorVertical(true);
            return true;
        })));
        registerButton(new ToggleButton<>(RecipeCreatorCluster.MIRROR_ROTATION, (cache, g, p, gui, i) -> ((ShapedEliteCraftRecipe) cache.getEliteCraftingRecipe()).mirrorRotation(), new ButtonState<>(RecipeCreatorCluster.MIRROR_ROTATION_ENABLED, PlayerHeadUtils.getViaURL("e887cc388c8dcfcf1ba8aa5c3c102dce9cf7b1b63e786b34d4f1c3796d3e9d61"), (cache, guiHandler, player, inventory, slot, event) -> {
            ((ShapedEliteCraftRecipe) cache.getEliteCraftingRecipe()).setMirrorRotation(false);
            return true;
        }), new ButtonState<>(RecipeCreatorCluster.MIRROR_ROTATION_DISABLED, PlayerHeadUtils.getViaURL("e887cc388c8dcfcf1ba8aa5c3c102dce9cf7b1b63e786b34d4f1c3796d3e9d61"), (cache, guiHandler, player, inventory, slot, event) -> {
            ((ShapedEliteCraftRecipe) cache.getEliteCraftingRecipe()).setMirrorRotation(true);
            return true;
        })));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(6, BACK);
        CCCache cache = update.getGuiHandler().getCustomCache();
        var workbench = cache.getEliteCraftingRecipe();

        if (!workbench.isShapeless()) {
            if (((ShapedEliteCraftRecipe) workbench).mirrorHorizontal() && ((ShapedEliteCraftRecipe) workbench).mirrorVertical()) {
                update.setButton(33, RecipeCreatorCluster.MIRROR_ROTATION);
            }
            update.setButton(42, RecipeCreatorCluster.MIRROR_HORIZONTAL);
            update.setButton(51, RecipeCreatorCluster.MIRROR_VERTICAL);
        }

        int slot;
        for (int i = 0; i < 36; i++) {
            slot = i + (i / 6) * 3;
            update.setButton(slot, "recipe.ingredient_" + i);
        }
        update.setButton(25, "recipe.result");
        update.setButton(24, RecipeCreatorCluster.SHAPELESS);

        update.setButton(44, SETTINGS);

        if(workbench.hasNamespacedKey()){
            update.setButton(52, RecipeCreatorCluster.SAVE);
        }
        update.setButton(53, RecipeCreatorCluster.SAVE_AS);
    }

    @Override
    public boolean validToSave(CCCache cache) {
        var workbench = cache.getEliteCraftingRecipe();
        return workbench.getIngredients() != null && !workbench.getResult().isEmpty();
    }
}
