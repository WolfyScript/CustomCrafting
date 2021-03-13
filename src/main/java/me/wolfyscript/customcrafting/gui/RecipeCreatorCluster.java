package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.ConditionsMenu;
import me.wolfyscript.customcrafting.gui.recipe_creator.IngredientMenu;
import me.wolfyscript.customcrafting.gui.recipe_creator.ResultMenu;
import me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators.*;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;

public class RecipeCreatorCluster extends CCCluster {

    public RecipeCreatorCluster(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, "recipe_creator", customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new WorkbenchCreator(this, customCrafting));
        registerGuiWindow(new CookingCreator(this, customCrafting));
        registerGuiWindow(new AnvilCreator(this, customCrafting));
        registerGuiWindow(new CauldronCreator(this, customCrafting));
        registerGuiWindow(new StonecutterCreator(this, customCrafting));
        registerGuiWindow(new GrindstoneCreator(this, customCrafting));
        registerGuiWindow(new EliteWorkbenchCreator(this, customCrafting));
        registerGuiWindow(new BrewingCreator(this, customCrafting));
        registerGuiWindow(new SmithingCreator(this, customCrafting));
        registerGuiWindow(new ConditionsMenu(this, customCrafting));
        registerGuiWindow(new ResultMenu(this, customCrafting));
        registerGuiWindow(new IngredientMenu(this, customCrafting));

        registerButton(new ActionButton<>("conditions", Material.CYAN_CONCRETE_POWDER, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openWindow("conditions");
            return true;
        }));
    }
}
