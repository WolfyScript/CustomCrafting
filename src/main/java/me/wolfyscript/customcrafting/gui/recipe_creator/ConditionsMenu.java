package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ConditionSelectButton;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

import java.util.List;

public class ConditionsMenu extends CCWindow {

    private static final String BACK = "back";
    private static final String PAGE_UP = "page_up";
    private static final String PAGE_DOWN = "page_down";
    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String TOGGLE_MODE = "toggle_mode";

    public ConditionsMenu(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "conditions", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>(BACK, new ButtonState<>(MainCluster.BACK_BOTTOM, PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openPreviousWindow();
            return true;
        })));
        registerButton(new ActionButton<>(ADD, PlayerHeadUtils.getViaURL("10c97e4b68aaaae8472e341b1d872b93b36d4eb6ea89ecec26a66e6c4e178"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openWindow(ConditionsAddMenu.KEY);
            return true;
        }));
        registerButton(new ActionButton<>(PAGE_UP, PlayerHeadUtils.getViaURL("3f46abad924b22372bc966a6d517d2f1b8b57fdd262b4e04f48352e683fff92"), (cache, guiHandler, player, inventory, slot, event) -> {
            return true;
        }));
        registerButton(new ActionButton<>(PAGE_DOWN, PlayerHeadUtils.getViaURL("be9ae7a4be65fcbaee65181389a2f7d47e2e326db59ea3eb789a92c85ea46"), (cache, guiHandler, player, inventory, slot, event) -> {
            return true;
        }));
        registerButton(new ActionButton<>(TOGGLE_MODE, Material.LEVER, (cache, guiHandler, player, inventory, slot, event) -> {
            var condition = cache.getRecipe().getConditions().getByKey(cache.getConditionsCache().getSelectedCondition());
            if (condition != null) {
                condition.toggleOption();
            }
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            var condition = cache.getRecipe().getConditions().getByKey(cache.getConditionsCache().getSelectedCondition());
            if (condition != null) {
                hashMap.put("%MODE%", condition.getOption().getDisplayString(api));
            }
            return itemStack;
        }));

        registerButton(new ActionButton<>(REMOVE, Material.BARRIER, (cache, guiHandler, player, inventory, slot, event) -> {
            var condition = cache.getConditionsCache().getSelectedCondition();
            if (condition != null) {
                cache.getRecipe().getConditions().removeCondition(condition);
            }
            return true;
        }));
        Condition.getGuiComponents().forEach((key, abstractGUIComponent) -> abstractGUIComponent.init(this, api));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        CCCache cache = update.getGuiHandler().getCustomCache();
        NamespacedKey background = PlayerUtil.getStore(update.getPlayer()).getLightBackground();
        for (int i = 8; i < 16; i++) {
            update.setButton(i, background);
        }
        update.setButton(45, BACK);
        update.setButton(7, PAGE_UP);
        update.setButton(16, PAGE_DOWN);
        update.setButton(17, ADD);

        Conditions conditions = cache.getRecipe().getConditions();

        List<Condition<?>> values = List.copyOf(conditions.getValues());
        int size = values.size();
        int maxPages = (int) Math.floor(size / 16d);
        int page = cache.getConditionsCache().getPage();

        for (int i = page * 16, slot = 0; i < values.size(); i++, slot++) {
            if (slot == 7) {
                slot += 2;
            }
            var button = new ConditionSelectButton(values.get(i));
            registerButton(button);
            update.setButton(slot, button);
        }

        NamespacedKey key = cache.getConditionsCache().getSelectedCondition();

        Condition<?> selectedCondition = conditions.getByKey(key);
        if (selectedCondition != null) {
            selectedCondition.render(update, cache, cache.getRecipe());
            update.setButton(53, REMOVE);
        }
    }
}
