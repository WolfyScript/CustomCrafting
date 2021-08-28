package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import org.bukkit.Material;

public class RecipeCreatorCooking extends RecipeCreator {

    public static final String XP = "xp";
    public static final String COOKING_TIME = "cooking_time";

    public RecipeCreatorCooking(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "cooking", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new ButtonRecipeIngredient(0));
        registerButton(new ButtonRecipeResult());

        registerButton(new ChatInputButton<>(XP, Material.EXPERIENCE_BOTTLE, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%XP%", cache.getRecipeCreatorCache().getCookingCache().getExp());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            float xp;
            try {
                xp = Float.parseFloat(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, getCluster(), "valid_number");
                return true;
            }
            guiHandler.getCustomCache().getRecipeCreatorCache().getCookingCache().setExp(xp);
            return false;
        }));
        registerButton(new ChatInputButton<>(COOKING_TIME, Material.COAL, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%TIME%", cache.getRecipeCreatorCache().getCookingCache().getCookingTime());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int time;
            try {
                time = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, getCluster(), "valid_number");
                return true;
            }
            guiHandler.getCustomCache().getRecipeCreatorCache().getCookingCache().setCookingTime(time);
            return false;
        }));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, BACK);
        CCCache cache = update.getGuiHandler().getCustomCache();

        CCPlayerData data = PlayerUtil.getStore(update.getPlayer());

        update.setButton(3, ClusterRecipeCreator.HIDDEN);
        update.setButton(5, ClusterRecipeCreator.CONDITIONS);
        update.setButton(20, data.getLightBackground());
        update.setButton(11, "recipe.ingredient_0");
        update.setButton(24, "recipe.result");
        update.setButton(10, data.getLightBackground());
        update.setButton(12, data.getLightBackground());
        update.setButton(22, XP);
        update.setButton(29, COOKING_TIME);

        update.setButton(42, ClusterRecipeCreator.GROUP);
        if (cache.getRecipeCreatorCache().getCookingCache().isSaved()) {
            update.setButton(43, ClusterRecipeCreator.SAVE);
        }
        update.setButton(44, ClusterRecipeCreator.SAVE_AS);
    }

}
