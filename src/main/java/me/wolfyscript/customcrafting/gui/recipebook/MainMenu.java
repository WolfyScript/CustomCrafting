package me.wolfyscript.customcrafting.gui.recipebook;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Categories;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.MainCategoryButton;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import org.bukkit.event.EventHandler;

public class MainMenu extends ExtendedGuiWindow {

    public MainMenu(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("main_menu", inventoryAPI, 18, customCrafting);
    }

    @Override
    public void onInit() {
        RecipeHandler recipeHandler = customCrafting.getRecipeHandler();
        Categories categories = recipeHandler.getCategories();

        for (String categoryId : categories.getSortedMainCategories()) {
            registerButton(new MainCategoryButton(categoryId, customCrafting));
        }
    }

    @EventHandler
    private void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            PlayerStatistics playerStatistics = CustomCrafting.getPlayerStatistics(event.getPlayer());
            event.setButton(8, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");

            RecipeHandler recipeHandler = customCrafting.getRecipeHandler();
            Categories categories = recipeHandler.getCategories();

            int slot = 0;
            for (String categoryId : categories.getSortedMainCategories()) {
                event.setButton(slot, "mainCategory." + categoryId);
                slot++;
            }
        }
    }
}
