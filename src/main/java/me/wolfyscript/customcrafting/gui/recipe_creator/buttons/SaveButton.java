package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators.RecipeCreator;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.IShapedCraftingRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SaveButton extends ActionButton {

    public SaveButton(boolean saveAs) {
        super(saveAs ? "save_as" : "save", new ButtonState("recipe_creator", saveAs ? "save_as" : "save", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            if (guiHandler.getCurrentInv() instanceof RecipeCreator) {
                RecipeCreator recipeCreator = (RecipeCreator) guiHandler.getCurrentInv();
                WolfyUtilities api = recipeCreator.getAPI();
                CustomCrafting customCrafting = recipeCreator.getCustomCrafting();
                if (recipeCreator.validToSave(cache)) {
                    if(saveAs){
                        recipeCreator.openChat("recipe_creator", "save.input", guiHandler, (guiHandler1, player1, s, args) -> {
                            if (args.length > 1) {
                                ICustomRecipe<?> recipe = cache.getRecipe();
                                NamespacedKey namespacedKey = new NamespacedKey(args[0], args[1]);
                                recipe.setNamespacedKey(namespacedKey);
                                return saveRecipe(cache, recipe, player1, api, guiHandler, customCrafting);
                            }
                            return true;
                        });
                    }else{
                        return saveRecipe(cache, cache.getRecipe(), player, api, guiHandler, customCrafting);
                    }
                } else {
                    api.sendPlayerMessage(player, "recipe_creator", "save.empty");
                }
            }
            return true;
        }));
    }

    private static boolean saveRecipe(TestCache cache, ICustomRecipe<?> recipe, Player player, WolfyUtilities api, GuiHandler<?> guiHandler, CustomCrafting customCrafting) {
        if (!recipe.save(player)) {
            return true;
        }
        try {
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                if (recipe instanceof IShapedCraftingRecipe) {
                    ((IShapedCraftingRecipe) recipe).constructShape();
                }
                customCrafting.getRecipeHandler().injectRecipe(recipe);
                api.sendPlayerMessage(player, "recipe_creator", "loading.success");

                if (customCrafting.getConfigHandler().getConfig().isResetCreatorAfterSave()) cache.resetRecipe();
            });
        } catch (Exception ex) {
            api.sendPlayerMessage(player, "recipe_creator", "loading.error", new String[]{"%REC%", recipe.getNamespacedKey().toString()});
            ex.printStackTrace();
            return true;
        }
        Bukkit.getScheduler().runTask(customCrafting, () -> guiHandler.openCluster("none"));
        return true;
    }
}
