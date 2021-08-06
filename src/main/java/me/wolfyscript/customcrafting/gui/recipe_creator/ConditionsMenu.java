package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

public class ConditionsMenu extends CCWindow {

    private static final String BACK = "back";
    private static final String PAGE_UP = "page_up";
    private static final String PAGE_DOWN = "page_down";
    private static final String ADD_CONDITION = "add_condition";
    private static final String REMOVE_CONDITION = "remove_condition";
    private static final String TOGGLE_MODE = "toggle_mode";

    public ConditionsMenu(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "conditions", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>(BACK, new ButtonState<>(MainCluster.BACK, PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openPreviousWindow();
            return true;
        })));
        registerButton(new ActionButton<>(ADD_CONDITION, new ButtonState<>(MainCluster.BACK, PlayerHeadUtils.getViaURL("10c97e4b68aaaae8472e341b1d872b93b36d4eb6ea89ecec26a66e6c4e178"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openWindow(ConditionsAddMenu.KEY);
            return true;
        })));
        registerButton(new ActionButton<>(PAGE_UP, new ButtonState<>(MainCluster.BACK, PlayerHeadUtils.getViaURL("3f46abad924b22372bc966a6d517d2f1b8b57fdd262b4e04f48352e683fff92"), (cache, guiHandler, player, inventory, slot, event) -> {

            return true;
        })));
        registerButton(new ActionButton<>(PAGE_DOWN, new ButtonState<>(MainCluster.BACK, PlayerHeadUtils.getViaURL("be9ae7a4be65fcbaee65181389a2f7d47e2e326db59ea3eb789a92c85ea46"), (cache, guiHandler, player, inventory, slot, event) -> {

            return true;
        })));
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

        registerButton(new ActionButton<>(REMOVE_CONDITION, Material.BARRIER, (cache, guiHandler, player, inventory, slot, event) -> {
            var condition = cache.getConditionsCache().getSelectedCondition();
            if (condition != null) {
                cache.getRecipe().getConditions().removeCondition(condition);
            }
            return true;
        }));
        Condition.getGuiComponents().forEach((key, abstractGUIComponent) -> abstractGUIComponent.init(this, api));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        CCCache cache = event.getGuiHandler().getCustomCache();
        event.setButton(45, BACK);
        event.setButton(7, PAGE_UP);
        event.setButton(16, PAGE_DOWN);
        event.setButton(17, ADD_CONDITION);

        NamespacedKey key = cache.getConditionsCache().getSelectedCondition();

        Condition<?> selectedCondition = cache.getRecipe().getConditions().getByKey(key);
        if (selectedCondition != null) {
            selectedCondition.render(event, cache, cache.getRecipe());
            event.setButton(53, REMOVE_CONDITION);
        }
    }
}
