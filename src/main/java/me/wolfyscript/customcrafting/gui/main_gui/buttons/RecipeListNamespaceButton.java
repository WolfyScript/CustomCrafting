package me.wolfyscript.customcrafting.gui.main_gui.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.main_gui.RecipesList;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;

public class RecipeListNamespaceButton extends ActionButton {

    private final CustomCrafting customCrafting;
    private HashMap<GuiHandler, String> namespaces = new HashMap<>();


    public RecipeListNamespaceButton(int slot, CustomCrafting customCrafting) {
        super("recipe_list.namespace_" + slot, new ButtonState("namespace", Material.CHEST, null));
        this.customCrafting = customCrafting;
    }

    @Override
    public void init(GuiWindow guiWindow) {
        getState().setAction((guiHandler, player, inventory, i, event) -> {
            String namespace = getNamespace(guiHandler);
            if (!namespace.isEmpty()) {
                if (!event.isShiftClick()) {
                    if (guiWindow instanceof RecipesList) {
                        ((RecipesList) guiWindow).getRecipeNamespaces().put(guiHandler, namespace);
                        ((RecipesList) guiWindow).setPage(guiHandler, 0);
                    }
                } else {
                    if (namespace.equalsIgnoreCase("minecraft")) {
                        if (event.isShiftClick() && event.isLeftClick()) {
                            for (Recipe recipe : customCrafting.getRecipeHandler().getVanillaRecipes()) {
                                if (recipe instanceof Keyed) {
                                    String id = ((Keyed) recipe).getKey().toString();
                                    if (!customCrafting.getRecipeHandler().getDisabledRecipes().contains(id)) {
                                        customCrafting.getRecipeHandler().getDisabledRecipes().add(id);
                                    }
                                }
                            }
                        } else if (event.isShiftClick() && event.isRightClick()) {
                            for (Recipe recipe : customCrafting.getRecipeHandler().getVanillaRecipes()) {
                                if (recipe instanceof Keyed) {
                                    customCrafting.getRecipeHandler().getDisabledRecipes().remove(((Keyed) recipe).getKey().toString());
                                }
                            }
                        }
                    } else {
                        if (event.isShiftClick() && event.isLeftClick()) {
                            for (CustomRecipe recipe : customCrafting.getRecipeHandler().getRecipesByNamespace(namespace)) {
                                String id = recipe.getNamespacedKey().toString();
                                if (!customCrafting.getRecipeHandler().getDisabledRecipes().contains(id)) {
                                    customCrafting.getRecipeHandler().getDisabledRecipes().add(id);
                                }
                            }
                        } else if (event.isShiftClick() && event.isRightClick()) {
                            for (CustomRecipe recipe : customCrafting.getRecipeHandler().getRecipesByNamespace(namespace)) {
                                customCrafting.getRecipeHandler().getDisabledRecipes().remove(recipe.getNamespacedKey().toString());
                            }
                        }
                    }
                }
            }
            return true;
        });
        getState().setRenderAction((hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%namespace%", getNamespace(guiHandler));
            return itemStack;
        });
        super.init(guiWindow);
    }

    public String getNamespace(GuiHandler guiHandler) {
        return namespaces.getOrDefault(guiHandler, "");
    }

    public void setNamespace(GuiHandler guiHandler, String namespace) {
        namespaces.put(guiHandler, namespace);
    }
}
