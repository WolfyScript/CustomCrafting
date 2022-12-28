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

import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonAction;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import org.bukkit.Material;

public class RecipeCreatorSmithing extends RecipeCreator {

    private static final String CHANGE_MATERIAL = "change_material";
    private static final String PRESERVE_ENCHANTS = "preserve_enchants";
    private static final String PRESERVE_DAMAGE = "preserve_damage";

    public RecipeCreatorSmithing(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "smithing", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();
        var btnB = getButtonBuilder();
        ButtonRecipeIngredient.register(getButtonBuilder(), 0);
        ButtonRecipeIngredient.register(getButtonBuilder(), 1);
        ButtonRecipeResult.register(getButtonBuilder());
        btnB.toggle(CHANGE_MATERIAL).enabledState(s -> s.subKey("enabled").icon(Material.PAPER).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getSmithingCache().setOnlyChangeMaterial(false);
            return ButtonInteractionResult.cancel(true);
        })).disabledState(s -> s.subKey("disabled").icon(Material.WRITABLE_BOOK).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getSmithingCache().setOnlyChangeMaterial(true);
            return ButtonInteractionResult.cancel(true);
        })).register();
        btnB.toggle(PRESERVE_ENCHANTS).stateFunction((holder, cache, slot) -> cache.getRecipeCreatorCache().getSmithingCache().isPreserveEnchants()).enabledState(s -> s.subKey("enabled").icon(Material.ENCHANTED_BOOK).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getSmithingCache().setPreserveEnchants(false);
            return ButtonInteractionResult.cancel(true);
        })).disabledState(s -> s.subKey("disabled").icon(Material.BOOK).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getSmithingCache().setPreserveEnchants(true);
            return ButtonInteractionResult.cancel(true);
        })).register();
        btnB.toggle(PRESERVE_DAMAGE).stateFunction((holder, cache, slot) -> cache.getRecipeCreatorCache().getSmithingCache().isPreserveDamage()).enabledState(s -> s.subKey("enabled").icon(Material.LIME_CONCRETE).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getSmithingCache().setPreserveDamage(false);
            return ButtonInteractionResult.cancel(true);
        })).disabledState(s -> s.subKey("disabled").icon(Material.RED_CONCRETE).action((holder, cache, btn, slot, details) -> {
            cache.getRecipeCreatorCache().getSmithingCache().setPreserveDamage(true);
            return ButtonInteractionResult.cancel(true);
        })).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        CCCache cache = event.getGuiHandler().getCustomCache();
        event.setButton(0, BACK);
        var smithingRecipe = cache.getRecipeCreatorCache().getSmithingCache();
        event.setButton(1, ClusterRecipeCreator.HIDDEN);
        event.setButton(3, ClusterRecipeCreator.CONDITIONS);
        event.setButton(5, ClusterRecipeCreator.PRIORITY);
        event.setButton(7, ClusterRecipeCreator.EXACT_META);
        event.setButton(19, "recipe.ingredient_0");
        event.setButton(22, "recipe.ingredient_1");
        event.setButton(25, "recipe.result");

        event.setButton(37, PRESERVE_ENCHANTS);
        event.setButton(38, PRESERVE_DAMAGE);
        event.setButton(40, CHANGE_MATERIAL);

        event.setButton(42, ClusterRecipeCreator.GROUP);
        if (smithingRecipe.isSaved()) {
            event.setButton(43, ClusterRecipeCreator.SAVE);
        }
        event.setButton(44, ClusterRecipeCreator.SAVE_AS);
    }

}
