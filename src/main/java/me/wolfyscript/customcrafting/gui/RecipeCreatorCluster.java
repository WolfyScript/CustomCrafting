package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.*;
import me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators.*;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
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
        registerGuiWindow(new EliteWorkbenchCreatorSettings(this, customCrafting));
        registerGuiWindow(new BrewingCreator(this, customCrafting));
        registerGuiWindow(new SmithingCreator(this, customCrafting));
        //Other Menus
        registerGuiWindow(new ConditionsMenu(this, customCrafting));
        registerGuiWindow(new ResultMenu(this, customCrafting));
        registerGuiWindow(new IngredientMenu(this, customCrafting));
        //Tags
        registerGuiWindow(new TagSettings(this, customCrafting));
        registerGuiWindow(new TagChooseList(this, customCrafting));

        registerButton(new ActionButton<>("conditions", Material.CYAN_CONCRETE_POWDER, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openWindow("conditions");
            return true;
        }));

        registerButton(new ActionButton<>("tags", Material.NAME_TAG, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            guiHandler.openWindow("tag_settings");
            return true;
        }));

        registerButton(new ChatInputButton<>("group", Material.BOOKSHELF, (values, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
            values.put("%group%", cache.getRecipe().getGroup());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            if (args.length > 0) {
                guiHandler.getCustomCache().getRecipe().setGroup(args[0]);
            }
            return false;
        }));
    }
}
