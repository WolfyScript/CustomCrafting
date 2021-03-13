package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ExactMetaButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.PriorityButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.RecipeIngredientButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.RecipeResultButton;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import org.bukkit.Material;

public class GrindstoneCreator extends RecipeCreator {

    public GrindstoneCreator(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "grindstone", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new ExactMetaButton());
        registerButton(new PriorityButton());

        registerButton(new RecipeIngredientButton(0, customCrafting));
        registerButton(new RecipeIngredientButton(1, customCrafting));
        registerButton(new RecipeResultButton());

        registerButton(new DummyButton<>("grindstone", Material.GRINDSTONE));

        registerButton(new ChatInputButton<>("xp", Material.EXPERIENCE_BOTTLE, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%xp%", guiHandler.getCustomCache().getGrindstoneRecipe().getXp());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int xp;
            try {
                xp = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, "recipe_creator", "valid_number");
                return true;
            }
            guiHandler.getCustomCache().getGrindstoneRecipe().setXp(xp);
            return false;
        }));
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        CCCache cache = update.getGuiHandler().getCustomCache();
        GrindstoneRecipe grindstoneRecipe = cache.getGrindstoneRecipe();
        ((ToggleButton) getButton("hidden")).setState(update.getGuiHandler(), grindstoneRecipe.isHidden());

        update.setButton(1, "hidden");
        update.setButton(3, "recipe_creator", "conditions");
        update.setButton(5, "priority");
        update.setButton(7, "exact_meta");

        update.setButton(11, "recipe.ingredient_0");
        update.setButton(20, "grindstone");
        update.setButton(29, "recipe.ingredient_1");

        update.setButton(23, "xp");
        update.setButton(25, "recipe.result");

        if(grindstoneRecipe.hasNamespacedKey()){
            update.setButton(43, "save");
        }
        update.setButton(44, "save_as");

    }

    @Override
    public boolean validToSave(CCCache cache) {
        GrindstoneRecipe recipe = cache.getGrindstoneRecipe();
        if (!recipe.getInputTop().isEmpty() || !recipe.getInputBottom().isEmpty()) {
            return !recipe.getResult().isEmpty();
        }
        return false;
    }
}
