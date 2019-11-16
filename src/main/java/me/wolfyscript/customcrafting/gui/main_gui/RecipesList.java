package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.RecipeListContainerButton;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import org.bukkit.Keyed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;
import java.util.List;

public class RecipesList extends ExtendedGuiWindow {

    private HashMap<GuiHandler, Integer> pages = new HashMap<>();

    public RecipesList(InventoryAPI inventoryAPI) {
        super("recipe_list", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openPreviousInv();
            return true;
        })));
        registerButton(new ActionButton("next_page", new ButtonState("next_page", WolfyUtilities.getSkullViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            if (pages.getOrDefault(guiHandler, 0) + 1 < getMaxPages()) {
                pages.put(guiHandler, pages.getOrDefault(guiHandler, 0) + 1);
            }
            return true;
        })));
        registerButton(new ActionButton("previous_page", new ButtonState("previous_page", WolfyUtilities.getSkullViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            if (pages.getOrDefault(guiHandler, 0) > 0) {
                pages.put(guiHandler, pages.getOrDefault(guiHandler, 0) - 1);
            }
            return true;
        })));

        for (int i = 0; i < 45; i++) {
            registerButton(new RecipeListContainerButton(i));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(0, "back");
            event.setButton(2, "previous_page");
            event.setButton(6, "next_page");
            List<Recipe> recipes = CustomCrafting.getRecipeHandler().getAllRecipes();
            if (!pages.containsKey(event.getGuiHandler())) {
                pages.put(event.getGuiHandler(), 0);
            }
            int item = 0;
            for (int i = 45 * pages.get(event.getGuiHandler()); item < 45 && i < recipes.size(); i++) {
                Recipe recipe = recipes.get(i);
                if (recipe instanceof Keyed) {
                    RecipeListContainerButton button = (RecipeListContainerButton) event.getGuiWindow().getButton("recipe_list.container_" + item);
                    button.setRecipe(event.getGuiHandler(), recipe);
                    event.setButton(9 + item, button);
                }
                item++;
            }
        }
    }

    private int getMaxPages() {
        return CustomCrafting.getRecipeHandler().getAllRecipes().size() / 45 + (CustomCrafting.getRecipeHandler().getAllRecipes().size() % 45 > 0 ? 1 : 0);
    }
}
