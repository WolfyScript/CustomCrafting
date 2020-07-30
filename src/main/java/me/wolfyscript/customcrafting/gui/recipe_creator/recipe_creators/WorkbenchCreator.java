package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.*;
import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapelessCraftRecipe;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import org.bukkit.event.EventHandler;

public class WorkbenchCreator extends RecipeCreator {

    public WorkbenchCreator(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("workbench", inventoryAPI, 45, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openCluster("none");
            return true;
        })));

        registerButton(new SaveButton());

        for (int i = 0; i < 10; i++) {
            registerButton(new CraftingIngredientButton(i, customCrafting));
        }

        registerButton(new ExactMetaButton());
        registerButton(new PriorityButton());
        registerButton(new HiddenButton());

        registerButton(new ToggleButton("workbench.shapeless", false, new ButtonState("recipe_creator", "workbench.shapeless.enabled", PlayerHeadUtils.getViaURL("f21d93da43863cb3759afefa9f7cc5c81f34d920ca97b7283b462f8b197f813"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setCustomRecipe(CraftingRecipe.class, new ShapedCraftRecipe(((TestCache) guiHandler.getCustomCache()).getCraftingRecipe()));
            return true;
        }), new ButtonState("recipe_creator", "workbench.shapeless.disabled", PlayerHeadUtils.getViaURL("1aae7e8222ddbee19d184b97e79067814b6ba3142a3bdcce8b93099a312"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setCustomRecipe(CraftingRecipe.class, new ShapelessCraftRecipe(((TestCache) guiHandler.getCustomCache()).getCraftingRecipe()));
            return true;
        })));

        registerButton(new ToggleButton("workbench.mirrorHorizontal", false, new ButtonState("recipe_creator", "workbench.mirrorHorizontal.enabled", PlayerHeadUtils.getViaURL("956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getCraftingRecipe()).setMirrorHorizontal(false);
            return true;
        }), new ButtonState("recipe_creator", "workbench.mirrorHorizontal.disabled", PlayerHeadUtils.getViaURL("956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getCraftingRecipe()).setMirrorHorizontal(true);
            return true;
        })));
        registerButton(new ToggleButton("workbench.mirrorVertical", false, new ButtonState("recipe_creator", "workbench.mirrorVertical.enabled", PlayerHeadUtils.getViaURL("882faf9a584c4d676d730b23f8942bb997fa3dad46d4f65e288c39eb471ce7"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getCraftingRecipe()).setMirrorVertical(false);
            return true;
        }), new ButtonState("recipe_creator", "workbench.mirrorVertical.disabled", PlayerHeadUtils.getViaURL("882faf9a584c4d676d730b23f8942bb997fa3dad46d4f65e288c39eb471ce7"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getCraftingRecipe()).setMirrorVertical(true);
            return true;
        })));
        registerButton(new ToggleButton("workbench.mirrorRotation", false, new ButtonState("recipe_creator", "workbench.mirrorRotation.enabled", PlayerHeadUtils.getViaURL("e887cc388c8dcfcf1ba8aa5c3c102dce9cf7b1b63e786b34d4f1c3796d3e9d61"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getCraftingRecipe()).setMirrorRotation(false);
            return true;
        }), new ButtonState("recipe_creator", "workbench.mirrorRotation.disabled", PlayerHeadUtils.getViaURL("e887cc388c8dcfcf1ba8aa5c3c102dce9cf7b1b63e786b34d4f1c3796d3e9d61"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((ShapedCraftRecipe) ((TestCache) guiHandler.getCustomCache()).getCraftingRecipe()).setMirrorRotation(true);
            return true;
        })));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(0, "back");
            TestCache cache = (TestCache) event.getGuiHandler().getCustomCache();
            CraftingRecipe craftingRecipe = cache.getCraftingRecipe();

            ((ToggleButton) event.getGuiWindow().getButton("workbench.shapeless")).setState(event.getGuiHandler(), craftingRecipe.isShapeless());
            ((ToggleButton) event.getGuiWindow().getButton("exact_meta")).setState(event.getGuiHandler(), craftingRecipe.isExactMeta());
            ((ToggleButton) event.getGuiWindow().getButton("hidden")).setState(event.getGuiHandler(), craftingRecipe.isHidden());

            if(!craftingRecipe.isShapeless()){
                ((ToggleButton) event.getGuiWindow().getButton("workbench.mirrorHorizontal")).setState(event.getGuiHandler(), ((ShapedCraftRecipe)craftingRecipe).mirrorHorizontal());
                ((ToggleButton) event.getGuiWindow().getButton("workbench.mirrorVertical")).setState(event.getGuiHandler(), ((ShapedCraftRecipe)craftingRecipe).mirrorVertical());
                ((ToggleButton) event.getGuiWindow().getButton("workbench.mirrorRotation")).setState(event.getGuiHandler(), ((ShapedCraftRecipe)craftingRecipe).mirrorRotation());

                if (((ShapedCraftRecipe)craftingRecipe).mirrorHorizontal() && ((ShapedCraftRecipe)craftingRecipe).mirrorVertical()) {
                    event.setButton(37, "workbench.mirrorRotation");
                }
                event.setButton(38, "workbench.mirrorHorizontal");
                event.setButton(39, "workbench.mirrorVertical");
            }

            int slot;
            for (int i = 0; i < 9; i++) {
                slot = 10 + i + (i / 3) * 6;
                event.setButton(slot, "crafting.container_" + i);
            }
            event.setButton(22, "workbench.shapeless");
            event.setButton(24, "crafting.container_9");

            event.setButton(1, "hidden");
            event.setButton(3, "recipe_creator", "conditions");
            event.setButton(5, "exact_meta");
            event.setButton(7, "priority");
            event.setButton(44, "save");
        }
    }

    @Override
    public boolean validToSave(TestCache cache) {
        CraftingRecipe workbench = cache.getCraftingRecipe();
        return workbench.getIngredients() != null && !InventoryUtils.isCustomItemsListEmpty(workbench.getCustomResults());
    }
}
