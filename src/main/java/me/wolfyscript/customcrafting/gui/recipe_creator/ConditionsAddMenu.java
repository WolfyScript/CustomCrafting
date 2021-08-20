package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ConditionAddButton;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.List;
import java.util.Map;

public class ConditionsAddMenu extends CCWindow {

    private static final String BACK = "back";
    public static final String KEY = "conditions_add";
    private static final int CONDITIONS_PER_PAGE = 44;

    public ConditionsAddMenu(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, KEY, 54, customCrafting);
    }

    @Override
    public void onInit() {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        var cache = update.getGuiHandler().getCustomCache();
        var recipe = cache.getRecipeCreatorCache().getRecipeCache();
        update.setButton(8, PlayerUtil.getStore(update.getPlayer()).getLightBackground());
        var recipeType = cache.getRecipeCreatorCache().getRecipeType();
        List<Map.Entry<NamespacedKey, Condition.AbstractGUIComponent<?>>> conditions = Condition.getGuiComponents().entrySet().stream().filter(entry -> entry.getValue().shouldRender(recipeType) && !recipe.getConditions().has(entry.getKey())).toList();
        if (!conditions.isEmpty()) {
            int size = conditions.size();
            int page = cache.getRecipeCreatorCache().getConditionsCache().getSelectNewPage();
            for (int i = page * CONDITIONS_PER_PAGE, slot = 0; i < size && slot < CONDITIONS_PER_PAGE; i++, slot++) {
                Map.Entry<NamespacedKey, Condition.AbstractGUIComponent<?>> entry = conditions.get(i);
                var button = new ConditionAddButton(entry.getKey(), entry.getValue());
                registerButton(button);
                update.setButton(slot, button);
            }

            int maxPages = (int) Math.floor(size / (double) CONDITIONS_PER_PAGE);
        }

        update.setButton(49, MainCluster.BACK_BOTTOM);
    }
}
