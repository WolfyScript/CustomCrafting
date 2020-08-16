package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.HiddenButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.SaveButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.StonecutterContainerButton;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import org.bukkit.event.EventHandler;

public class StonecutterCreator extends RecipeCreator {

    public StonecutterCreator(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("stonecutter", inventoryAPI, 45, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openCluster("none");
            return true;
        })));
        registerButton(new SaveButton());

        registerButton(new HiddenButton());

        registerButton(new StonecutterContainerButton(0, customCrafting));
        registerButton(new StonecutterContainerButton(1, customCrafting));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            ((ToggleButton) event.getGuiWindow().getButton("hidden")).setState(event.getGuiHandler(), ((TestCache) event.getGuiHandler().getCustomCache()).getStonecutterRecipe().isHidden());
            event.setButton(0, "back");
            event.setButton(4, "hidden");
            event.setButton(20, "stonecutter.container_0");
            event.setButton(24, "stonecutter.container_1");
            event.setButton(44, "save");
        }
    }

    @Override
    public boolean validToSave(TestCache cache) {
        CustomStonecutterRecipe recipe = cache.getStonecutterRecipe();
        return !InventoryUtils.isCustomItemsListEmpty(recipe.getCustomResults()) && !InventoryUtils.isCustomItemsListEmpty(recipe.getSource());
    }
}
