package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.RecipeCreatorCluster;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonRecipeIngredient;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonRecipeResult;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import org.bukkit.Material;

public class CookingCreator extends RecipeCreator {

    public CookingCreator(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "cooking", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new ButtonRecipeIngredient(0));
        registerButton(new ButtonRecipeResult());

        registerButton(new ChatInputButton<>("xp", Material.EXPERIENCE_BOTTLE, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%XP%", guiHandler.getCustomCache().getCookingRecipe().getExp());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            float xp;
            try {
                xp = Float.parseFloat(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, getCluster(), "valid_number");
                return true;
            }
            guiHandler.getCustomCache().getCookingRecipe().setExp(xp);
            return false;
        }));
        registerButton(new ChatInputButton<>("cooking_time", Material.COAL, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%TIME%", guiHandler.getCustomCache().getCookingRecipe().getCookingTime());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int time;
            try {
                time = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, getCluster(), "valid_number");
                return true;
            }
            guiHandler.getCustomCache().getCookingRecipe().setCookingTime(time);
            return false;
        }));
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, BACK);
        CCCache cache = update.getGuiHandler().getCustomCache();
        ((ToggleButton) getCluster().getButton("hidden")).setState(update.getGuiHandler(), cache.getCookingRecipe().isHidden());

        CCPlayerData data = PlayerUtil.getStore(update.getPlayer());

        update.setButton(3, RecipeCreatorCluster.HIDDEN);
        update.setButton(5, RecipeCreatorCluster.CONDITIONS);
        update.setButton(20, data.getLightBackground());
        update.setButton(11, "recipe.ingredient_0");
        update.setButton(24, "recipe.result");
        update.setButton(10, data.getLightBackground());
        update.setButton(12, data.getLightBackground());
        update.setButton(22, "xp");
        update.setButton(29, "cooking_time");

        update.setButton(42, RecipeCreatorCluster.GROUP);
        if (cache.getCookingRecipe().hasNamespacedKey()) {
            update.setButton(43, RecipeCreatorCluster.SAVE);
        }
        update.setButton(44, RecipeCreatorCluster.SAVE_AS);
    }

    public boolean validToSave(CCCache cache) {
        switch (cache.getRecipeType().getType()) {
            case BLAST_FURNACE:
            case SMOKER:
            case CAMPFIRE:
            case FURNACE:
                CustomCookingRecipe<?, ?> furnace = cache.getCookingRecipe();
                return !furnace.getSource().isEmpty() && !furnace.getResult().isEmpty();
            default:
                return false;
        }
    }
}
