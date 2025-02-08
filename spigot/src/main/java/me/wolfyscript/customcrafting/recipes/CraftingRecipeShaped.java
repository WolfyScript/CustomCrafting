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

package me.wolfyscript.customcrafting.recipes;

import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import com.wolfyscript.utilities.verification.Verifier;
import com.wolfyscript.utilities.verification.VerifierBuilder;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JacksonInject;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.List;
import java.util.stream.Collectors;

public class CraftingRecipeShaped extends AbstractRecipeShaped<CraftingRecipeShaped, AdvancedRecipeSettings> implements ICustomVanillaRecipe<org.bukkit.inventory.ShapedRecipe> {

    static {
        final Verifier<CraftingRecipeShaped> VERIFIER = VerifierBuilder.<CraftingRecipeShaped>object(RecipeType.CRAFTING_SHAPED.getNamespacedKey(), AbstractRecipeShaped.validator())
                .name(container -> "Shaped Crafting Recipe" + container.value().map(customRecipeSmithing -> " [" + customRecipeSmithing.getNamespacedKey() + "]").orElse(""))
                .build();
        CustomCrafting.inst().getRegistries().getVerifiers().register(VERIFIER);
    }

    @Deprecated
    public CraftingRecipeShaped(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 3, AdvancedRecipeSettings.class);
    }

    @JsonCreator
    public CraftingRecipeShaped(@JsonProperty("key") @JacksonInject("key") NamespacedKey key, @JacksonInject("customcrafting") CustomCrafting customCrafting, @JsonProperty("symmetry") Symmetry symmetry, @JsonProperty(value = "keepShapeAsIs") boolean keepShapeAsIs, @JsonProperty("shape") String[] shape) {
        super(key, customCrafting, symmetry, keepShapeAsIs, shape, 3, new AdvancedRecipeSettings());
    }

    @Deprecated
    public CraftingRecipeShaped(NamespacedKey key, Symmetry symmetry, String[] shape) {
        this(key, CustomCrafting.inst(), symmetry, false, shape);
    }

    @Deprecated
    public CraftingRecipeShaped(NamespacedKey key) {
        super(key, CustomCrafting.inst(), new Symmetry(), false, 3, new AdvancedRecipeSettings());
    }

    private CraftingRecipeShaped(CraftingRecipeShaped craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public CraftingRecipeShaped clone() {
        return new CraftingRecipeShaped(this);
    }

    @Override
    public org.bukkit.inventory.ShapedRecipe getVanillaRecipe() {
        if (!getResult().isEmpty() && !ingredients.isEmpty()) {
            // Register placeholder recipe
            var placeholderRecipe = new org.bukkit.inventory.ShapedRecipe(ICustomVanillaRecipe.toPlaceholder(getNamespacedKey()).bukkit(), getResult().getItemStack());
            placeholderRecipe.shape(getShape());
            mappedIngredients.forEach((character, items) -> placeholderRecipe.setIngredient(character, getMaterialRecipeChoiceFor(items)));
            placeholderRecipe.setGroup(getGroup());

            if(Bukkit.getRecipe(placeholderRecipe.getKey())!=null) {
                Bukkit.removeRecipe(placeholderRecipe.getKey());
            }
            Bukkit.addRecipe(placeholderRecipe);

            // Return display recipe
            var recipe = new org.bukkit.inventory.ShapedRecipe(ICustomVanillaRecipe.toDisplayKey(getNamespacedKey()).bukkit(), getResult().getItemStack());
            recipe.shape(getShape());
            mappedIngredients.forEach((character, items) -> recipe.setIngredient(character, getExactRecipeChoiceFor(items)));
            recipe.setGroup(getGroup());
            return recipe;
        }
        return null;
    }

    private static RecipeChoice.ExactChoice getExactRecipeChoiceFor(Ingredient ingredient) {
        List<ItemStack> choices = ingredient.choices().stream().map(StackReference::referencedStack).distinct().collect(Collectors.toList());
        if (ingredient.isAllowEmpty()) choices.add(new ItemStack(Material.AIR));
        return new RecipeChoice.ExactChoice(choices);
    }

    private static RecipeChoice.MaterialChoice getMaterialRecipeChoiceFor(Ingredient ingredient) {
        List<Material> choices = ingredient.choices().stream().map(reference -> reference.referencedStack().getType()).distinct().collect(Collectors.toList());
        if (ingredient.isAllowEmpty()) choices.add(Material.AIR);
        return new RecipeChoice.MaterialChoice(choices);
    }

    @Override
    public boolean isVisibleVanillaBook() {
        return vanillaBook;
    }

    @Override
    public void setVisibleVanillaBook(boolean vanillaBook) {
        this.vanillaBook = vanillaBook;
    }

    @Override
    public boolean isAutoDiscover() {
        return autoDiscover;
    }

    @Override
    public void setAutoDiscover(boolean autoDiscover) {
        this.autoDiscover = autoDiscover;
    }
}