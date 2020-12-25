package me.wolfyscript.customcrafting.gui.main_gui.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.lists.RecipesList;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Keyed;
import org.bukkit.Material;
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
                        ((RecipesList) guiWindow).getRecipeNamespaces().put(guiHandler, namespace);
                        ((RecipesList) guiWindow).setPage(guiHandler, 0);
                    }
                } else {
                    if (namespace.equalsIgnoreCase("minecraft")) {
                        if (clickEvent.isShiftClick() && clickEvent.isLeftClick()) {
                            for (Recipe recipe : customCrafting.getRecipeHandler().getVanillaRecipes()) {
                                if (recipe instanceof Keyed) {
                                    String id = ((Keyed) recipe).getKey().toString();
                                    if (!customCrafting.getRecipeHandler().getDisabledRecipes().contains(id)) {
                                        customCrafting.getRecipeHandler().getDisabledRecipes().add(id);
                                    }
                                }
                            }
                        } else if (clickEvent.isShiftClick() && clickEvent.isRightClick()) {
                            for (Recipe recipe : customCrafting.getRecipeHandler().getVanillaRecipes()) {
                                if (recipe instanceof Keyed) {
                                    customCrafting.getRecipeHandler().getDisabledRecipes().remove(((Keyed) recipe).getKey().toString());
                                }
                            }
                        }
                    } else {
                        if (clickEvent.isShiftClick() && clickEvent.isLeftClick()) {
                            for (ICustomRecipe<?> recipe : customCrafting.getRecipeHandler().getRecipesByNamespace(namespace)) {
                                String id = recipe.getNamespacedKey().toString();
                                if (!customCrafting.getRecipeHandler().getDisabledRecipes().contains(id)) {
                                    customCrafting.getRecipeHandler().getDisabledRecipes().add(id);
                                }
                            }
                        } else if (clickEvent.isShiftClick() && clickEvent.isRightClick()) {
                            for (ICustomRecipe<?> recipe : customCrafting.getRecipeHandler().getRecipesByNamespace(namespace)) {
                                customCrafting.getRecipeHandler().getDisabledRecipes().remove(recipe.getNamespacedKey().toString());
                            }
                        }
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
