package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.ConditionsMenu;
import me.wolfyscript.customcrafting.gui.recipe_creator.VariantMenu;
import me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators.*;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;

public class RecipeCreatorCluster extends CCCluster {

    public RecipeCreatorCluster(InventoryAPI<TestCache> inventoryAPI, CustomCrafting customCrafting) {
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
        registerGuiWindow(new VariantMenu(this, customCrafting));

        registerButton(new ActionButton<>("conditions", Material.CYAN_CONCRETE_POWDER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.changeToInv("conditions");
            return true;
        }));
    }
}
