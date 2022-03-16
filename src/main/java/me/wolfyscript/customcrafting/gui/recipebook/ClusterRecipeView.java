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

package me.wolfyscript.customcrafting.gui.recipebook;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCCluster;
import me.wolfyscript.customcrafting.recipes.CustomRecipeAnvil;
import me.wolfyscript.customcrafting.recipes.CustomRecipeBrewing;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCauldron;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCooking;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;

public class ClusterRecipeView extends CCCluster {

    public static final String KEY = "recipe_view";

    public static final NamespacedKey RECIPE_SINGLE = new NamespacedKey(KEY, MenuSingleRecipe.KEY);

    public ClusterRecipeView(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, KEY, customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new MenuSingleRecipe(this, customCrafting));
        setEntry(RECIPE_SINGLE);

        //We change the behaviour of the buttons without a new language entry. Instead, it uses the lang keys from the recipe book cluster.
        registerButton(new DummyButton<>(ClusterRecipeBook.COOKING_ICON.getKey(), new ButtonState<>(ClusterRecipeBook.COOKING_ICON, Material.FURNACE, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            cache.getCacheRecipeView().getRecipe().ifPresent(customRecipe -> {
                RecipeType<?> recipeType = customRecipe.getRecipeType();
                itemStack.setType(Material.matchMaterial(recipeType.name()));
                hashMap.put("%type%", StringUtils.capitalize(recipeType.getId().replace("_", " ")));
                if (customRecipe instanceof CustomRecipeCooking<?,?> cookingRecipe) {
                    hashMap.put("%time%", cookingRecipe.getCookingTime());
                    hashMap.put("%xp%", cookingRecipe.getExp());
                }
            });
            return itemStack;
        })));
        registerButton(new DummyButton<>("anvil.durability", new ButtonState<>(new NamespacedKey(ClusterRecipeBook.KEY, "cooking.icon"), Material.ANVIL, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%var%", ((CustomRecipeAnvil) guiHandler.getCustomCache().getRecipeBookCache().getCurrentRecipe()).getDurability());
            return itemStack;
        })));
        registerButton(new DummyButton<>("cauldron.water.enabled", new ButtonState<>(new NamespacedKey(ClusterRecipeBook.KEY, "cauldron.water.enabled"), PlayerHeadUtils.getViaURL("848a19cdf42d748b41b72fb4376ae3f63c1165d2dce0651733df263446c77ba6"), (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            guiHandler.getCustomCache().getCacheRecipeView().getRecipe().ifPresent(customRecipe -> hashMap.put("%lvl%", ((CustomRecipeCauldron) customRecipe).getWaterLevel()));
            return itemStack;
        })));
        registerButton(new DummyButton<>("brewing.icon", new ButtonState<>(new NamespacedKey(ClusterRecipeBook.KEY, "cooking.icon"), Material.BREWING_STAND, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            guiHandler.getCustomCache().getCacheRecipeView().getRecipe().ifPresent(customRecipe -> {
                if (customRecipe instanceof CustomRecipeBrewing recipeBrewing) {
                    hashMap.put("%time%", recipeBrewing.getBrewTime());
                    hashMap.put("%cost%", recipeBrewing.getFuelCost());
                }
            });
            return itemStack;
        })));
        registerButton(new DummyButton<>("brewing.potion_duration", new ButtonState<>(new NamespacedKey(ClusterRecipeBook.KEY, "brewing.potion_duration"), Material.CLOCK, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            guiHandler.getCustomCache().getCacheRecipeView().getRecipe().ifPresent(customRecipe -> hashMap.put("%value%", ((CustomRecipeBrewing)customRecipe).getDurationChange()));
            return itemStack;
        })));
        registerButton(new DummyButton<>("brewing.potion_amplifier", new ButtonState<>(new NamespacedKey(ClusterRecipeBook.KEY, "brewing.potion_amplifier"), Material.IRON_SWORD, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            guiHandler.getCustomCache().getCacheRecipeView().getRecipe().ifPresent(customRecipe -> hashMap.put("%value%", ((CustomRecipeBrewing) customRecipe).getAmplifierChange()));
            return itemStack;
        })));

        for (int i = 0; i < 37; i++) {
            registerButton(new ButtonContainerIngredient(customCrafting, i));
        }
        for (int i = 0; i < 45; i++) {
            registerButton(new ButtonContainerRecipeBook(i));
        }
        //registerConditionDisplays();
    }
}
