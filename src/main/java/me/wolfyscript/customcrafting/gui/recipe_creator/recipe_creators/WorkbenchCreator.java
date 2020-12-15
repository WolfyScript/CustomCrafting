package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.CraftingIngredientButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ExactMetaButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.PriorityButton;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapelessCraftRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;

public class WorkbenchCreator extends RecipeCreator {

    public WorkbenchCreator(GuiCluster<TestCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "workbench", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new ExactMetaButton());
        registerButton(new PriorityButton());

        for (int i = 0; i < 10; i++) {
            registerButton(new CraftingIngredientButton(i, customCrafting));
        }

        registerButton(new ToggleButton("workbench.shapeless", false, new ButtonState("recipe_creator", "workbench.shapeless.enabled", PlayerHeadUtils.getViaURL("f21d93da43863cb3759afefa9f7cc5c81f34d920ca97b7283b462f8b197f813"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setCustomRecipe(new ShapedCraftRecipe(((TestCache) guiHandler.getCustomCache()).getAdvancedCraftingRecipe()));
            return true;
        }), new ButtonState("recipe_creator", "workbench.shapeless.disabled", PlayerHeadUtils.getViaURL("1aae7e8222ddbee19d184b97e79067814b6ba3142a3bdcce8b93099a312"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setCustomRecipe(new ShapelessCraftRecipe(((TestCache) guiHandler.getCustomCache()).getAdvancedCraftingRecipe()));
            return true;
        })));

        registerButton(new ToggleButton("workbench.mirrorHorizontal", false, new ButtonState("recipe_creator", "workbench.mirrorHorizontal.enabled", PlayerHeadUtils.getViaURL("956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getAdvancedCraftingRecipe()).setMirrorHorizontal(false);
            return true;
        }), new ButtonState("recipe_creator", "workbench.mirrorHorizontal.disabled", PlayerHeadUtils.getViaURL("956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getAdvancedCraftingRecipe()).setMirrorHorizontal(true);
            return true;
        })));
        registerButton(new ToggleButton("workbench.mirrorVertical", false, new ButtonState("recipe_creator", "workbench.mirrorVertical.enabled", PlayerHeadUtils.getViaURL("882faf9a584c4d676d730b23f8942bb997fa3dad46d4f65e288c39eb471ce7"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getAdvancedCraftingRecipe()).setMirrorVertical(false);
            return true;
        }), new ButtonState("recipe_creator", "workbench.mirrorVertical.disabled", PlayerHeadUtils.getViaURL("882faf9a584c4d676d730b23f8942bb997fa3dad46d4f65e288c39eb471ce7"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getAdvancedCraftingRecipe()).setMirrorVertical(true);
            return true;
        })));
        registerButton(new ToggleButton("workbench.mirrorRotation", false, new ButtonState("recipe_creator", "workbench.mirrorRotation.enabled", PlayerHeadUtils.getViaURL("e887cc388c8dcfcf1ba8aa5c3c102dce9cf7b1b63e786b34d4f1c3796d3e9d61"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getAdvancedCraftingRecipe()).setMirrorRotation(false);
            return true;
        }), new ButtonState("recipe_creator", "workbench.mirrorRotation.disabled", PlayerHeadUtils.getViaURL("e887cc388c8dcfcf1ba8aa5c3c102dce9cf7b1b63e786b34d4f1c3796d3e9d61"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getAdvancedCraftingRecipe()).setMirrorRotation(true);
            return true;
        })));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<TestCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        TestCache cache = update.getGuiHandler().getCustomCache();
        CraftingRecipe<?> craftingRecipe = cache.getAdvancedCraftingRecipe();

        ((ToggleButton) getButton("workbench.shapeless")).setState(update.getGuiHandler(), craftingRecipe.isShapeless());
        ((ToggleButton) getButton("exact_meta")).setState(update.getGuiHandler(), craftingRecipe.isExactMeta());
        ((ToggleButton) getButton("hidden")).setState(update.getGuiHandler(), craftingRecipe.isHidden());

        if (!craftingRecipe.isShapeless()) {
            ((ToggleButton) getButton("workbench.mirrorHorizontal")).setState(update.getGuiHandler(), ((ShapedCraftRecipe) craftingRecipe).mirrorHorizontal());
            ((ToggleButton) getButton("workbench.mirrorVertical")).setState(update.getGuiHandler(), ((ShapedCraftRecipe) craftingRecipe).mirrorVertical());
            ((ToggleButton) getButton("workbench.mirrorRotation")).setState(update.getGuiHandler(), ((ShapedCraftRecipe) craftingRecipe).mirrorRotation());

            if (((ShapedCraftRecipe) craftingRecipe).mirrorHorizontal() && ((ShapedCraftRecipe) craftingRecipe).mirrorVertical()) {
                update.setButton(37, "workbench.mirrorRotation");
            }
            update.setButton(38, "workbench.mirrorHorizontal");
            update.setButton(39, "workbench.mirrorVertical");
        }

        int slot;
        for (int i = 0; i < 9; i++) {
            slot = 10 + i + (i / 3) * 6;
            update.setButton(slot, "crafting.container_" + i);
        }
        update.setButton(22, "workbench.shapeless");
        update.setButton(24, "crafting.container_9");

        update.setButton(1, "hidden");
        update.setButton(3, "recipe_creator", "conditions");
        update.setButton(5, "exact_meta");
        update.setButton(7, "priority");

        if(craftingRecipe.hasNamespacedKey()){
            update.setButton(43, "save");
        }
        update.setButton(44, "save_as");
    }

    @Override
    public boolean validToSave(TestCache cache) {
        AdvancedCraftingRecipe workbench = cache.getAdvancedCraftingRecipe();
        return workbench.getIngredients() != null && !InventoryUtils.isCustomItemsListEmpty(workbench.getResults());
    }
}
