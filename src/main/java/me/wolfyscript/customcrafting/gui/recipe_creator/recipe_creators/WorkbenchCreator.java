package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.RecipeCreatorCluster;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonRecipeIngredient;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonRecipeResult;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapelessCraftRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;

public class WorkbenchCreator extends RecipeCreator {

    public WorkbenchCreator(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "workbench", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        for (int i = 0; i < 9; i++) {
            registerButton(new ButtonRecipeIngredient(i));
        }

        registerButton(new ButtonRecipeResult());

        registerButton(new ToggleButton<>("workbench.shapeless", false, new ButtonState<>("recipe_creator", "workbench.shapeless.enabled", PlayerHeadUtils.getViaURL("f21d93da43863cb3759afefa9f7cc5c81f34d920ca97b7283b462f8b197f813"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().setCustomRecipe(new ShapedCraftRecipe(guiHandler.getCustomCache().getAdvancedCraftingRecipe()));
            return true;
        }), new ButtonState<>("recipe_creator", "workbench.shapeless.disabled", PlayerHeadUtils.getViaURL("1aae7e8222ddbee19d184b97e79067814b6ba3142a3bdcce8b93099a312"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().setCustomRecipe(new ShapelessCraftRecipe(guiHandler.getCustomCache().getAdvancedCraftingRecipe()));
            return true;
        })));

        registerButton(new ToggleButton<>("workbench.mirrorHorizontal", false, new ButtonState<>("recipe_creator", "workbench.mirrorHorizontal.enabled", PlayerHeadUtils.getViaURL("956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311"), (cache, guiHandler, player, inventory, slot, event) -> {
            ((ShapedCraftRecipe) guiHandler.getCustomCache().getAdvancedCraftingRecipe()).setMirrorHorizontal(false);
            return true;
        }), new ButtonState<>("recipe_creator", "workbench.mirrorHorizontal.disabled", PlayerHeadUtils.getViaURL("956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311"), (cache, guiHandler, player, inventory, slot, event) -> {
            ((ShapedCraftRecipe) guiHandler.getCustomCache().getAdvancedCraftingRecipe()).setMirrorHorizontal(true);
            return true;
        })));
        registerButton(new ToggleButton<>("workbench.mirrorVertical", false, new ButtonState<>("recipe_creator", "workbench.mirrorVertical.enabled", PlayerHeadUtils.getViaURL("882faf9a584c4d676d730b23f8942bb997fa3dad46d4f65e288c39eb471ce7"), (cache, guiHandler, player, inventory, slot, event) -> {
            ((ShapedCraftRecipe) guiHandler.getCustomCache().getAdvancedCraftingRecipe()).setMirrorVertical(false);
            return true;
        }), new ButtonState<>("recipe_creator", "workbench.mirrorVertical.disabled", PlayerHeadUtils.getViaURL("882faf9a584c4d676d730b23f8942bb997fa3dad46d4f65e288c39eb471ce7"), (cache, guiHandler, player, inventory, slot, event) -> {
            ((ShapedCraftRecipe) guiHandler.getCustomCache().getAdvancedCraftingRecipe()).setMirrorVertical(true);
            return true;
        })));
        registerButton(new ToggleButton<>("workbench.mirrorRotation", false, new ButtonState<>("recipe_creator", "workbench.mirrorRotation.enabled", PlayerHeadUtils.getViaURL("e887cc388c8dcfcf1ba8aa5c3c102dce9cf7b1b63e786b34d4f1c3796d3e9d61"), (cache, guiHandler, player, inventory, slot, event) -> {
            ((ShapedCraftRecipe) guiHandler.getCustomCache().getAdvancedCraftingRecipe()).setMirrorRotation(false);
            return true;
        }), new ButtonState<>("recipe_creator", "workbench.mirrorRotation.disabled", PlayerHeadUtils.getViaURL("e887cc388c8dcfcf1ba8aa5c3c102dce9cf7b1b63e786b34d4f1c3796d3e9d61"), (cache, guiHandler, player, inventory, slot, event) -> {
            ((ShapedCraftRecipe) guiHandler.getCustomCache().getAdvancedCraftingRecipe()).setMirrorRotation(true);
            return true;
        })));
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, BACK);
        CCCache cache = update.getGuiHandler().getCustomCache();
        CraftingRecipe<?> craftingRecipe = cache.getAdvancedCraftingRecipe();

        ((ToggleButton<CCCache>) getButton("workbench.shapeless")).setState(update.getGuiHandler(), craftingRecipe.isShapeless());
        ((ToggleButton<CCCache>) getCluster().getButton("exact_meta")).setState(update.getGuiHandler(), craftingRecipe.isExactMeta());
        ((ToggleButton<CCCache>) getCluster().getButton("hidden")).setState(update.getGuiHandler(), craftingRecipe.isHidden());

        if (!craftingRecipe.isShapeless()) {
            ((ToggleButton<CCCache>) getButton("workbench.mirrorHorizontal")).setState(update.getGuiHandler(), ((ShapedCraftRecipe) craftingRecipe).mirrorHorizontal());
            ((ToggleButton<CCCache>) getButton("workbench.mirrorVertical")).setState(update.getGuiHandler(), ((ShapedCraftRecipe) craftingRecipe).mirrorVertical());
            ((ToggleButton<CCCache>) getButton("workbench.mirrorRotation")).setState(update.getGuiHandler(), ((ShapedCraftRecipe) craftingRecipe).mirrorRotation());

            if (((ShapedCraftRecipe) craftingRecipe).mirrorHorizontal() && ((ShapedCraftRecipe) craftingRecipe).mirrorVertical()) {
                update.setButton(37, "workbench.mirrorRotation");
            }
            update.setButton(38, "workbench.mirrorHorizontal");
            update.setButton(39, "workbench.mirrorVertical");
        }

        for (int i = 0; i < 9; i++) {
            update.setButton(10 + i + (i / 3) * 6, "recipe.ingredient_" + i);
        }
        update.setButton(22, "workbench.shapeless");
        update.setButton(24, "recipe.result");

        update.setButton(1, RecipeCreatorCluster.HIDDEN);
        update.setButton(3, RecipeCreatorCluster.CONDITIONS);
        update.setButton(5, RecipeCreatorCluster.EXACT_META);
        update.setButton(7, RecipeCreatorCluster.PRIORITY);

        update.setButton(42, RecipeCreatorCluster.GROUP);
        if (craftingRecipe.hasNamespacedKey()) {
            update.setButton(43, RecipeCreatorCluster.SAVE);
        }
        update.setButton(44, RecipeCreatorCluster.SAVE_AS);
    }

    @Override
    public boolean validToSave(CCCache cache) {
        AdvancedCraftingRecipe workbench = cache.getAdvancedCraftingRecipe();
        return workbench.getIngredients() != null && !workbench.getIngredients().isEmpty() && !workbench.getResult().isEmpty();
    }
}
