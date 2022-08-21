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
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
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

        var btnB = getButtonBuilder();
        //We change the behaviour of the buttons without a new language entry. Instead, it uses the lang keys from the recipe book cluster.
        btnB.dummy(ClusterRecipeBook.COOKING_ICON.getKey()).state(s -> s.key(ClusterRecipeBook.COOKING_ICON).icon(Material.FURNACE).render((cache, guiHandler, player, guiInventory, itemStack, i) -> cache.getCacheRecipeView().getRecipe().map(customRecipe -> {
            RecipeType<?> recipeType = customRecipe.getRecipeType();
            itemStack.setType(Material.matchMaterial(recipeType.name()));
            TagResolver typePlaceholder = Placeholder.unparsed("type", StringUtils.capitalize(recipeType.getId().replace("_", " ")));
            if (customRecipe instanceof CustomRecipeCooking<?, ?> cookingRecipe) {
                return CallbackButtonRender.UpdateResult.of(TagResolver.resolver(typePlaceholder, Placeholder.unparsed("time", String.valueOf(cookingRecipe.getCookingTime())), Placeholder.unparsed("xp", String.valueOf(cookingRecipe.getExp()))));
            }
            return CallbackButtonRender.UpdateResult.of(typePlaceholder);
        }).orElseGet(CallbackButtonRender.UpdateResult::of))).register();
        btnB.dummy("anvil.durability").state(s -> s.key(new NamespacedKey(ClusterRecipeBook.KEY, "cooking.icon")).icon(Material.ANVIL).render((cache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("var", String.valueOf(((CustomRecipeAnvil) guiHandler.getCustomCache().getRecipeBookCache().getCurrentRecipe()).getDurability()))))).register();
        btnB.dummy("cauldron.water.enabled").state(s -> s.key(new NamespacedKey(ClusterRecipeBook.KEY, "cauldron.water.enabled")).icon(PlayerHeadUtils.getViaURL("848a19cdf42d748b41b72fb4376ae3f63c1165d2dce0651733df263446c77ba6")).render((cache, guiHandler, player, guiInventory, itemStack, i) -> guiHandler.getCustomCache().getCacheRecipeView().getRecipe().map(customRecipe -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("lvl", String.valueOf(((CustomRecipeCauldron) customRecipe).getFluidLevel())))).orElseGet(CallbackButtonRender.UpdateResult::of))).register();
        btnB.dummy("brewing.icon").state(s -> s.key(new NamespacedKey(ClusterRecipeBook.KEY, "cooking.icon")).icon(Material.BREWING_STAND).render((cache, guiHandler, player, guiInventory, itemStack, i) -> guiHandler.getCustomCache().getCacheRecipeView().getRecipe().map(customRecipe -> {
            if (customRecipe instanceof CustomRecipeBrewing recipeBrewing) {
                return CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("time", String.valueOf(recipeBrewing.getBrewTime())));
            }
            return null;
        }).orElseGet(CallbackButtonRender.UpdateResult::of))).register();
        btnB.dummy("brewing.potion_duration").state(s -> s.key(new NamespacedKey(ClusterRecipeBook.KEY, "brewing.potion_duration")).icon(Material.CLOCK).render((cache, guiHandler, player, inv, stack, i) -> guiHandler.getCustomCache().getCacheRecipeView().getRecipe().map(customRecipe -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("value", String.valueOf(((CustomRecipeBrewing) customRecipe).getDurationChange())))).orElseGet(CallbackButtonRender.UpdateResult::of))).register();
        btnB.dummy("brewing.potion_amplifier").state(s -> s.key(new NamespacedKey(ClusterRecipeBook.KEY, "brewing.potion_amplifier")).icon(Material.IRON_SWORD).render((cache, guiHandler, player, inv, stack, i) -> guiHandler.getCustomCache().getCacheRecipeView().getRecipe().map(customRecipe -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("value", String.valueOf(((CustomRecipeBrewing) customRecipe).getAmplifierChange())))).orElseGet(CallbackButtonRender.UpdateResult::of))).register();
        for (int i = 0; i < 37; i++) {
            registerButton(new ButtonContainerIngredient(customCrafting, i));
        }
        for (int i = 0; i < 45; i++) {
            registerButton(new ButtonContainerRecipeBook(i));
        }
        //registerConditionDisplays();
    }
}
