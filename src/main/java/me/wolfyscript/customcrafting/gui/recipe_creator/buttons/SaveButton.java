package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators.RecipeCreator;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.IShapedCraftingRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class SaveButton extends ActionButton {

    public SaveButton() {
        super("save", new ButtonState("recipe_creator", "save", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            if(guiHandler.getCurrentInv() instanceof RecipeCreator){
                RecipeCreator recipeCreator = (RecipeCreator) guiHandler.getCurrentInv();
                WolfyUtilities api = recipeCreator.getAPI();
                CustomCrafting customCrafting = recipeCreator.getCustomCrafting();
                if (recipeCreator.validToSave(cache)) {
                    recipeCreator.openChat("recipe_creator", "save.input", guiHandler, (guiHandler1, player1, s, args) -> {
                        CustomRecipe recipe = cache.getRecipe();
                        if (args.length > 1) {
                            NamespacedKey namespacedKey = new NamespacedKey(args[0], args[1]);
                            recipe.setNamespacedKey(namespacedKey);
                            if(!recipe.save(player1)){
                                return true;
                            }
                            try {
                                Bukkit.getScheduler().runTask(customCrafting, () -> {
                                    if(recipe instanceof IShapedCraftingRecipe){
                                        ((IShapedCraftingRecipe) recipe).constructShape();
                                    }
                                    customCrafting.getRecipeHandler().injectRecipe(recipe);
                                    api.sendPlayerMessage(player, "recipe_creator", "loading.success");

                                    if (customCrafting.getConfigHandler().getConfig().isResetCreatorAfterSave()) cache.resetRecipe();
                                });
                            } catch (Exception ex) {
                                api.sendPlayerMessage(player, "recipe_creator", "loading.error", new String[]{"%REC%", recipe.getNamespacedKey().toString()});
                                ex.printStackTrace();
                                return false;
                            }
                            Bukkit.getScheduler().runTask(customCrafting, () -> guiHandler.openCluster("none"));
                            return false;
                        }
                        return false;
                    });
                } else {
                    api.sendPlayerMessage(player, "recipe_creator", "save.empty");
                }
            }
            return false;
        }));
    }
}
