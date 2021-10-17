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
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

public class RecipeCreatorCraftingElite extends RecipeCreator {

    public static final String KEY = "elite_crafting";

    private static final String SETTINGS = "settings";

    public RecipeCreatorCraftingElite(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, KEY, 54, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        for (int i = 0; i < 37; i++) {
            registerButton(new ButtonRecipeIngredient(i));
        }
        registerButton(new ButtonRecipeResult());

        registerButton(new ActionButton<>(SETTINGS, Material.REDSTONE, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            guiHandler.openWindow(RecipeCreatorCraftingEliteSettings.KEY);
            return true;
        }));

        registerButton(new ToggleButton<>(ClusterRecipeCreator.SHAPELESS, (cache, g, p, gui, i) -> cache.getRecipeCreatorCache().getEliteCraftingCache().isShapeless(), new ButtonState<>(ClusterRecipeCreator.SHAPELESS_ENABLED, PlayerHeadUtils.getViaURL("f21d93da43863cb3759afefa9f7cc5c81f34d920ca97b7283b462f8b197f813"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getEliteCraftingCache().setShapeless(false);
            return true;
        }), new ButtonState<>(ClusterRecipeCreator.SHAPELESS_DISABLED, PlayerHeadUtils.getViaURL("1aae7e8222ddbee19d184b97e79067814b6ba3142a3bdcce8b93099a312"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getEliteCraftingCache().setShapeless(true);
            return true;
        })));

        registerButton(new ToggleButton<>(ClusterRecipeCreator.MIRROR_HORIZONTAL, (cache, g, p, gui, i) -> cache.getRecipeCreatorCache().getEliteCraftingCache().isMirrorHorizontal(), new ButtonState<>(ClusterRecipeCreator.MIRROR_HORIZONTAL_ENABLED, PlayerHeadUtils.getViaURL("956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getEliteCraftingCache().setMirrorHorizontal(false);
            return true;
        }), new ButtonState<>(ClusterRecipeCreator.MIRROR_HORIZONTAL_DISABLED, PlayerHeadUtils.getViaURL("956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getEliteCraftingCache().setMirrorHorizontal(true);
            return true;
        })));
        registerButton(new ToggleButton<>(ClusterRecipeCreator.MIRROR_VERTICAL, (cache, g, p, gui, i) -> cache.getRecipeCreatorCache().getEliteCraftingCache().isMirrorVertical(), new ButtonState<>(ClusterRecipeCreator.MIRROR_VERTICAL_ENABLED, PlayerHeadUtils.getViaURL("882faf9a584c4d676d730b23f8942bb997fa3dad46d4f65e288c39eb471ce7"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getEliteCraftingCache().setMirrorVertical(false);
            return true;
        }), new ButtonState<>(ClusterRecipeCreator.MIRROR_VERTICAL_DISABLED, PlayerHeadUtils.getViaURL("882faf9a584c4d676d730b23f8942bb997fa3dad46d4f65e288c39eb471ce7"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getEliteCraftingCache().setMirrorVertical(true);
            return true;
        })));
        registerButton(new ToggleButton<>(ClusterRecipeCreator.MIRROR_ROTATION, (cache, g, p, gui, i) -> cache.getRecipeCreatorCache().getEliteCraftingCache().isMirrorRotation(), new ButtonState<>(ClusterRecipeCreator.MIRROR_ROTATION_ENABLED, PlayerHeadUtils.getViaURL("e887cc388c8dcfcf1ba8aa5c3c102dce9cf7b1b63e786b34d4f1c3796d3e9d61"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getEliteCraftingCache().setMirrorRotation(false);
            return true;
        }), new ButtonState<>(ClusterRecipeCreator.MIRROR_ROTATION_DISABLED, PlayerHeadUtils.getViaURL("e887cc388c8dcfcf1ba8aa5c3c102dce9cf7b1b63e786b34d4f1c3796d3e9d61"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getEliteCraftingCache().setMirrorRotation(true);
            return true;
        })));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(6, BACK);
        CCCache cache = update.getGuiHandler().getCustomCache();
        var cacheCraftingElite = cache.getRecipeCreatorCache().getEliteCraftingCache();

        if (!cacheCraftingElite.isShapeless()) {
            if (cacheCraftingElite.isMirrorHorizontal() && cacheCraftingElite.isMirrorVertical()) {
                update.setButton(33, ClusterRecipeCreator.MIRROR_ROTATION);
            }
            update.setButton(42, ClusterRecipeCreator.MIRROR_HORIZONTAL);
            update.setButton(51, ClusterRecipeCreator.MIRROR_VERTICAL);
        }

        int slot;
        for (int i = 0; i < 36; i++) {
            slot = i + (i / 6) * 3;
            update.setButton(slot, "recipe.ingredient_" + i);
        }
        update.setButton(25, "recipe.result");
        update.setButton(24, ClusterRecipeCreator.SHAPELESS);

        update.setButton(44, SETTINGS);

        if (cacheCraftingElite.isSaved()) {
            update.setButton(52, ClusterRecipeCreator.SAVE);
        }
        update.setButton(53, ClusterRecipeCreator.SAVE_AS);
    }

}
