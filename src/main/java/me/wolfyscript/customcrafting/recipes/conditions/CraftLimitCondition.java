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

package me.wolfyscript.customcrafting.recipes.conditions;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonChatInput;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.recipe_creator.MenuConditions;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CraftLimitCondition extends Condition<CraftLimitCondition> {

    public static final NamespacedKey KEY = new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, "craft_limit");

    private long limit = 0;

    public CraftLimitCondition() {
        super(KEY);
        setAvailableOptions(Conditions.Option.EXACT, Conditions.Option.LOWER_EXACT, Conditions.Option.LOWER);
    }

    @Override
    public boolean isApplicable(CustomRecipe<?> recipe) {
        return recipe instanceof CraftingRecipe;
    }

    @Override
    public boolean check(CustomRecipe<?> recipe, Conditions.Data data) {
        Player player = data.getPlayer();
        if (player != null) {
            CCPlayerData playerStore = PlayerUtil.getStore(player);
            if (playerStore != null) {
                long amount = playerStore.getRecipeCrafts(recipe.getNamespacedKey());
                return switch (option) {
                    case LOWER_EXACT -> amount <= limit;
                    case LOWER -> amount < limit;
                    default -> false;
                };
            }
        }
        return true;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public static class GUIComponent extends FunctionalGUIComponent<CraftLimitCondition> {

        public GUIComponent() {
            super(Material.BARRIER, getLangKey(KEY.getKey(), "name"), getLangKey(KEY.getKey(), "description"),
                    (menu, api) -> {
                        menu.getButtonBuilder().chatInput("conditions.craft_limit.set").state(state -> state.icon(Material.BARRIER).render((cache, guiHandler, player, guiInventory, btn, itemStack, i) -> {
                            return CallbackButtonRender.UpdateResult.of(Placeholder.parsed("value", String.valueOf(cache.getRecipeCreatorCache().getRecipeCache().getConditions().getByType(CraftLimitCondition.class).getLimit())));
                        })).inputAction((guiHandler, player, s, strings) -> {
                            var conditions = guiHandler.getCustomCache().getRecipeCreatorCache().getRecipeCache().getConditions();
                            try {
                                conditions.getByType(CraftLimitCondition.class).setLimit(Long.parseLong(s));
                            } catch (NumberFormatException ex) {
                                api.getChat().sendKey(player, "recipe_creator", "valid_number");
                            }
                            return false;
                        }).register();
                    },
                    (update, cache, condition, recipe) -> {
                        update.setButton(30, "conditions.craft_limit.set");
                        update.setButton(32, MenuConditions.TOGGLE_MODE);
                    });
        }

        @Override
        public boolean shouldRender(RecipeType<?> type) {
            return RecipeType.Container.CRAFTING.has(type) || RecipeType.Container.ELITE_CRAFTING.has(type);
        }
    }
}
