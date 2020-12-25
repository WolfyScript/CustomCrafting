package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators.RecipeCreator;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.IShapedCraftingRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SaveButton extends ActionButton<CCCache> {

    public SaveButton(boolean saveAs) {
        super(saveAs ? "save_as" : "save", new ButtonState<>("recipe_creator", saveAs ? "save_as" : "save", Material.WRITABLE_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            if (guiHandler.getWindow() instanceof RecipeCreator) {
                RecipeCreator recipeCreator = (RecipeCreator) guiHandler.getWindow();
                WolfyUtilities api = guiHandler.getApi();
                CustomCrafting customCrafting = recipeCreator.getCustomCrafting();
                if (recipeCreator.validToSave(cache)) {
                    if (saveAs) {
                        recipeCreator.openChat(guiHandler.getInvAPI().getGuiCluster("recipe_creator"), "save.input", guiHandler, (guiHandler1, player1, s, args) -> {
                            NamespacedKey namespacedKey = ChatUtils.getNamespacedKey(player1, s, args);
                            if (namespacedKey != null) {
                                ICustomRecipe<?> recipe = cache.getRecipe();
                                recipe.setNamespacedKey(namespacedKey);
                                return saveRecipe(cache, recipe, player1, api, guiHandler, customCrafting);
                            }
                            return true;
                        });
                    } else {
                        return saveRecipe(cache, cache.getRecipe(), player, api, guiHandler, customCrafting);
                    }
                } else {
                    api.getChat().sendPlayerMessage(player, "recipe_creator", "save.empty");
                }
            }
            return true;
        }));
    }

    private static boolean saveRecipe(CCCache cache, ICustomRecipe<?> recipe, Player player, WolfyUtilities api, GuiHandler<CCCache> guiHandler, CustomCrafting customCrafting) {
        if (!recipe.save(player)) {
            return true;
        }
        try {
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                if (recipe instanceof IShapedCraftingRecipe) {
                    ((IShapedCraftingRecipe) recipe).constructShape();
                }
                customCrafting.getRecipeHandler().injectRecipe(recipe);
                api.getChat().sendPlayerMessage(player, "recipe_creator", "loading.success");

                if (customCrafting.getConfigHandler().getConfig().isResetCreatorAfterSave()) cache.resetRecipe();
            });
        } catch (Exception ex) {
            api.getChat().sendPlayerMessage(player, guiHandler.getInvAPI().getGuiCluster("recipe_creator"), "loading.error", new Pair<>("%REC%", recipe.getNamespacedKey().toString()));
            ex.printStackTrace();
            return true;
        }
        Bukkit.getScheduler().runTask(customCrafting, () -> guiHandler.openPreviousWindow("none", 2));
        return true;
    }
}
