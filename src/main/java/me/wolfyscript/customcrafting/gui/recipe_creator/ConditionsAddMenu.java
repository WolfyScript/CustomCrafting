package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ConditionAddButton;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;

import java.util.List;

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
        var recipe = cache.getRecipe();
        update.setButton(8, PlayerUtil.getStore(update.getPlayer()).getLightBackground());

        List<Condition<?>> conditions = Registry.RECIPE_CONDITIONS.values().stream().filter(condition -> condition.isApplicable(recipe) && recipe.getConditions().getByKey(condition.getNamespacedKey()) == null).toList();
        if (!conditions.isEmpty()) {
            int size = conditions.size();
            int maxPages = (int) Math.floor(size / CONDITIONS_PER_PAGE);
            int page = cache.getConditionsCache().getSelectNewPage();

            for (int i = page * CONDITIONS_PER_PAGE, slot = 0; i < size && slot < CONDITIONS_PER_PAGE; i++, slot++) {
                Condition<?> condition = conditions.get(i);
                var button = new ConditionAddButton(condition);
                registerButton(button);
                update.setButton(slot, button);
            }
        }
        update.setButton(49, MainCluster.BACK_BOTTOM);
    }
}
