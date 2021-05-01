package me.wolfyscript.customcrafting.gui.main_gui.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.lists.RecipesList;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;

public class RecipeListNamespaceButton extends ActionButton<CCCache> {

    private final CustomCrafting customCrafting;
    private final HashMap<GuiHandler<CCCache>, String> namespaces = new HashMap<>();


    public RecipeListNamespaceButton(int slot, CustomCrafting customCrafting) {
        super("recipe_list.namespace_" + slot, new ButtonState<>("namespace", Material.CHEST));
        this.customCrafting = customCrafting;
    }

    @Override
    public void init(GuiWindow<CCCache> guiWindow) {
        getState().setAction((cache, guiHandler, player, inventory, slot, event) -> {
            String namespace = getNamespace(guiHandler);
            if (!namespace.isEmpty() && event instanceof InventoryClickEvent) {
                InventoryClickEvent clickEvent = (InventoryClickEvent) event;
                if (!clickEvent.isShiftClick()) {
                    if (guiWindow instanceof RecipesList) {
                        cache.getRecipeList().setNamespace(namespace);
                        cache.getRecipeList().setPage(0);
                    }
                } else {
                    DataHandler dataHandler = customCrafting.getDataHandler();
                    if (namespace.equalsIgnoreCase("minecraft")) {
                        if (((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_LEFT)) {
                            for (Recipe recipe : customCrafting.getDataHandler().getMinecraftRecipes()) {
                                if (recipe instanceof Keyed) {
                                    dataHandler.disableBukkitRecipe(((Keyed) recipe).getKey());
                                }
                            }
                        } else if (((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_RIGHT)) {
                            for (Recipe recipe : customCrafting.getDataHandler().getMinecraftRecipes()) {
                                if (recipe instanceof Keyed) {
                                    dataHandler.enableBukkitRecipe(((Keyed) recipe).getKey());
                                }
                            }
                        }
                    } else if (((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_LEFT)) {
                        Registry.RECIPES.get(namespace).forEach(dataHandler::disableRecipe);
                    } else if (((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_RIGHT)) {
                        Registry.RECIPES.get(namespace).forEach(dataHandler::enableRecipe);
                    }
                }
            }
            return true;
        });
        getState().setRenderAction((hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%namespace%", getNamespace(guiHandler));
            return itemStack;
        });
        super.init(guiWindow);
    }

    public String getNamespace(GuiHandler<CCCache> guiHandler) {
        return namespaces.getOrDefault(guiHandler, "");
    }

    public void setNamespace(GuiHandler<CCCache> guiHandler, String namespace) {
        namespaces.put(guiHandler, namespace);
    }
}
