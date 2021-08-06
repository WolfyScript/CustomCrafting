package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;

import java.util.List;

public class ConditionsAddMenu extends CCWindow {

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
        var recipe = cache.getRecipe();

        List<Condition<?>> conditions = Registry.RECIPE_CONDITIONS.values().stream().filter(condition -> condition.isApplicable(recipe) && recipe.getConditions().getByKey(condition.getNamespacedKey()) == null).toList();
        if (!conditions.isEmpty()) {
            int size = conditions.size();
            int maxPages = (int) Math.floor(size / CONDITIONS_PER_PAGE);
            int page = cache.getConditionsCache().getSelectNewPage();

            for (int i = page * CONDITIONS_PER_PAGE, slot = 0; i < size && slot < CONDITIONS_PER_PAGE; i++, slot++) {
                Condition<?> condition = conditions.get(i);
                condition.getGuiComponent()


            }
        }
    }
}
