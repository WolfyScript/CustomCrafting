package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.*;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapedEliteCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapelessEliteCraftRecipe;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import org.bukkit.event.EventHandler;

public class EliteWorkbenchCreator extends RecipeCreator {

    public EliteWorkbenchCreator(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("elite_workbench", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openCluster("none");
            return true;
        })));
        registerButton(new SaveButton());

        registerButton(new ExactMetaButton());
        registerButton(new PriorityButton());
        registerButton(new HiddenButton());

        for (int i = 0; i < 37; i++) {
            registerButton(new CraftingIngredientButton(i, customCrafting));
        }

        registerButton(new ToggleButton("workbench.shapeless", false, new ButtonState("recipe_creator", "workbench.shapeless.enabled", PlayerHeadUtils.getViaURL("f21d93da43863cb3759afefa9f7cc5c81f34d920ca97b7283b462f8b197f813"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setCustomRecipe(EliteCraftingRecipe.class, new ShapedEliteCraftRecipe(((TestCache) guiHandler.getCustomCache()).getEliteCraftingRecipe()));
            return true;
        }), new ButtonState("recipe_creator", "workbench.shapeless.disabled", PlayerHeadUtils.getViaURL("1aae7e8222ddbee19d184b97e79067814b6ba3142a3bdcce8b93099a312"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setCustomRecipe(EliteCraftingRecipe.class, new ShapelessEliteCraftRecipe(((TestCache) guiHandler.getCustomCache()).getEliteCraftingRecipe()));
            return true;
        })));

        registerButton(new ToggleButton("workbench.mirrorHorizontal", false, new ButtonState("recipe_creator", "workbench.mirrorHorizontal.enabled", PlayerHeadUtils.getViaURL("956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedEliteCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getEliteCraftingRecipe()).setMirrorHorizontal(false);
            return true;
        }), new ButtonState("recipe_creator", "workbench.mirrorHorizontal.disabled", PlayerHeadUtils.getViaURL("956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedEliteCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getEliteCraftingRecipe()).setMirrorHorizontal(true);
            return true;
        })));
        registerButton(new ToggleButton("workbench.mirrorVertical", false, new ButtonState("recipe_creator", "workbench.mirrorVertical.enabled", PlayerHeadUtils.getViaURL("882faf9a584c4d676d730b23f8942bb997fa3dad46d4f65e288c39eb471ce7"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedEliteCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getEliteCraftingRecipe()).setMirrorVertical(false);
            return true;
        }), new ButtonState("recipe_creator", "workbench.mirrorVertical.disabled", PlayerHeadUtils.getViaURL("882faf9a584c4d676d730b23f8942bb997fa3dad46d4f65e288c39eb471ce7"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedEliteCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getEliteCraftingRecipe()).setMirrorVertical(true);
            return true;
        })));
        registerButton(new ToggleButton("workbench.mirrorRotation", false, new ButtonState("recipe_creator", "workbench.mirrorRotation.enabled", PlayerHeadUtils.getViaURL("e887cc388c8dcfcf1ba8aa5c3c102dce9cf7b1b63e786b34d4f1c3796d3e9d61"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedEliteCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getEliteCraftingRecipe()).setMirrorRotation(false);
            return true;
        }), new ButtonState("recipe_creator", "workbench.mirrorRotation.disabled", PlayerHeadUtils.getViaURL("e887cc388c8dcfcf1ba8aa5c3c102dce9cf7b1b63e786b34d4f1c3796d3e9d61"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedEliteCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getEliteCraftingRecipe()).setMirrorRotation(true);
            return true;
        })));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(6, "back");
            TestCache cache = (TestCache) event.getGuiHandler().getCustomCache();
            EliteCraftingRecipe workbench = cache.getEliteCraftingRecipe();

            ((ToggleButton) event.getGuiWindow().getButton("workbench.shapeless")).setState(event.getGuiHandler(), workbench.isShapeless());
            ((ToggleButton) event.getGuiWindow().getButton("exact_meta")).setState(event.getGuiHandler(), workbench.isExactMeta());
            ((ToggleButton) event.getGuiWindow().getButton("hidden")).setState(event.getGuiHandler(), workbench.isHidden());

            if(!workbench.isShapeless()){
                ((ToggleButton) event.getGuiWindow().getButton("workbench.mirrorHorizontal")).setState(event.getGuiHandler(), ((ShapedEliteCraftRecipe) workbench).mirrorHorizontal());
                ((ToggleButton) event.getGuiWindow().getButton("workbench.mirrorVertical")).setState(event.getGuiHandler(), ((ShapedEliteCraftRecipe) workbench).mirrorVertical());
                ((ToggleButton) event.getGuiWindow().getButton("workbench.mirrorRotation")).setState(event.getGuiHandler(), ((ShapedEliteCraftRecipe) workbench).mirrorRotation());

                if (((ShapedEliteCraftRecipe) workbench).mirrorHorizontal() && ((ShapedEliteCraftRecipe) workbench).mirrorVertical()) {
                    event.setButton(33, "workbench.mirrorRotation");
                }
                event.setButton(42, "workbench.mirrorHorizontal");
                event.setButton(43, "workbench.mirrorVertical");
            }

            int slot;
            for (int i = 0; i < 36; i++) {
                slot = i + (i / 6) * 3;
                event.setButton(slot, "crafting.container_" + i);
            }
            event.setButton(25, "crafting.container_36");
            event.setButton(24, "workbench.shapeless");

            event.setButton(35, "hidden");
            event.setButton(44, "recipe_creator", "conditions");
            event.setButton(51, "exact_meta");
            event.setButton(52, "priority");
            event.setButton(53, "save");
        }
    }

    @Override
    public boolean validToSave(TestCache cache) {
        EliteCraftingRecipe workbench = cache.getEliteCraftingRecipe();
        return workbench.getIngredients() != null && !InventoryUtils.isCustomItemsListEmpty(workbench.getCustomResults());
    }
}
