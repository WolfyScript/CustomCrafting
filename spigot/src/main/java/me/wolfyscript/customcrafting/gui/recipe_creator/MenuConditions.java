/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

import java.util.List;

public class MenuConditions extends CCWindow {

    private static final String BACK = "back";
    private static final String PAGE_UP = "page_up";
    private static final String PAGE_DOWN = "page_down";
    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    public static final String TOGGLE_MODE = "toggle_mode";

    public MenuConditions(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "conditions", 54, customCrafting);
    }

    @Override
    public void onInit() {
        getButtonBuilder().action(BACK).state(s -> s.key(ClusterMain.BACK_BOTTOM).icon(PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c")).action((cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openPreviousWindow();
            return true;
        })).register();
        getButtonBuilder().action(ADD).state(s -> s.icon(PlayerHeadUtils.getViaURL("10c97e4b68aaaae8472e341b1d872b93b36d4eb6ea89ecec26a66e6c4e178")).action((cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openWindow(MenuConditionsAdd.KEY);
            return true;
        })).register();
        getButtonBuilder().action(PAGE_UP).state(s -> s.icon(PlayerHeadUtils.getViaURL("3f46abad924b22372bc966a6d517d2f1b8b57fdd262b4e04f48352e683fff92")).action((cache, guiHandler, player, inventory, slot, event) -> true)).register();
        getButtonBuilder().action(PAGE_DOWN).state(s -> s.icon(PlayerHeadUtils.getViaURL("be9ae7a4be65fcbaee65181389a2f7d47e2e326db59ea3eb789a92c85ea46")).action((cache, guiHandler, player, inventory, slot, event) -> true)).register();
        getButtonBuilder().action(TOGGLE_MODE).state(s -> s.icon(Material.LEVER).action((cache, guiHandler, player, inventory, slot, event) -> {
            var condition = cache.getRecipeCreatorCache().getRecipeCache().getConditions().getByKey(cache.getRecipeCreatorCache().getConditionsCache().getSelectedCondition());
            if (condition != null) {
                condition.toggleOption();
            }
            return true;
        }).render((cache, guiHandler, player, guiInventory, itemStack, slot) -> {
            var condition = cache.getRecipeCreatorCache().getRecipeCache().getConditions().getByKey(cache.getRecipeCreatorCache().getConditionsCache().getSelectedCondition());
            if (condition != null) {
                return CallbackButtonRender.UpdateResult.of(Placeholder.parsed("mode", condition.getOption().getDisplayString(api)));
            }
            return CallbackButtonRender.UpdateResult.of();
        })).register();
        getButtonBuilder().action(REMOVE).state(s -> s.icon(Material.BARRIER).action((cache, guiHandler, player, inventory, slot, event) -> {
            var condition = cache.getRecipeCreatorCache().getConditionsCache().getSelectedCondition();
            if (condition != null) {
                cache.getRecipeCreatorCache().getRecipeCache().getConditions().removeCondition(condition);
            }
            return true;
        })).register();
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

        Conditions conditions = cache.getRecipeCreatorCache().getRecipeCache().getConditions();

        List<NamespacedKey> values = List.copyOf(conditions.keySet());
        int size = values.size();
        int maxPages = (int) Math.floor(size / 16d);
        int page = cache.getRecipeCreatorCache().getConditionsCache().getPage();

        for (int i = page * 16, slot = 0; i < values.size(); i++, slot++) {
            if (slot == 7) {
                slot += 2;
            }
            var button = new ButtonConditionSelect(values.get(i));
            registerButton(button);
            update.setButton(slot, button);
        }

        NamespacedKey key = cache.getRecipeCreatorCache().getConditionsCache().getSelectedCondition();

        Condition<?> selectedCondition = conditions.getByKey(key);
        if (selectedCondition != null) {
            selectedCondition.render(update, cache, cache.getRecipeCreatorCache().getRecipeCache());
            update.setButton(53, REMOVE);
        }
    }
}
