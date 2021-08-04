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

        List<Condition> conditions = Registry.RECIPE_CONDITIONS.values().stream().filter(condition -> condition.isApplicable(recipe) && recipe.getConditions().getByKey(condition.getNamespacedKey()) == null).toList();
        if (!conditions.isEmpty()) {
            int maxPages = (int) Math.floor(conditions.size() / 44);
            int currentPage = cache.getConditionsCache().getSelectNewPage();


        }
    }
}
