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

import com.wolfyscript.utilities.bukkit.TagResolverUtil;
import com.wolfyscript.utilities.bukkit.gui.button.Button;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;

import java.util.Map;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class MenuConditionsAdd extends CCWindow {

    private static final String BACK = "back";
    public static final String KEY = "conditions_add";
    private static final int CONDITIONS_PER_PAGE = 44;

    public MenuConditionsAdd(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, KEY, 54, customCrafting);
    }

    @Override
    public void onInit() {

    }

    private void registerConditionAddBtn(NamespacedKey key, Condition.AbstractGUIComponent<?> condition) {
        getButtonBuilder().action("icon_" + key.toString("_")).state(state -> state.key("icon").icon(condition.getIcon()).action((cache, guiHandler, player, guiInventory, button, i, inventoryInteractEvent) -> {
            cache.getRecipeCreatorCache().getRecipeCache().getConditions().setCondition(customCrafting.getRegistries().getRecipeConditions().create(key));
            return true;
        }).render((cache, guiHandler, player, guiInventory, button, itemStack, i) -> {
            return CallbackButtonRender.UpdateResult.of(Placeholder.component("name", condition.getDisplayName()), TagResolverUtil.entries(condition.getDescriptionComponents()));
        })).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        var cache = update.getGuiHandler().getCustomCache();
        var recipe = cache.getRecipeCreatorCache().getRecipeCache();
        update.setButton(8, PlayerUtil.getStore(update.getPlayer()).getLightBackground());
        var recipeType = cache.getRecipeCreatorCache().getRecipeType();
        var conditions = Condition.getGuiComponents().entrySet().stream().filter(entry -> entry.getValue().shouldRender(recipeType) && !recipe.getConditions().has(entry.getKey())).toList();
        if (!conditions.isEmpty()) {
            int size = conditions.size();
            int page = cache.getRecipeCreatorCache().getConditionsCache().getSelectNewPage();
            conditions = conditions.subList(page * CONDITIONS_PER_PAGE, size);
            for (int slot = 0; slot < conditions.size(); slot++) {
                Map.Entry<NamespacedKey, Condition.AbstractGUIComponent<?>> entry = conditions.get(slot);
                registerConditionAddBtn(entry.getKey(), entry.getValue());
                update.setButton(slot, "icon_" + entry.getKey().toString("_"));
            }
            int maxPages = (int) Math.floor(size / (double) CONDITIONS_PER_PAGE);
        }
        update.setButton(49, ClusterMain.BACK_BOTTOM);
    }
}
