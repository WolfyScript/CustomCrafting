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

public class CraftingRecipeShapeless extends AbstractRecipeShapeless<CraftingRecipeShapeless, AdvancedRecipeSettings> implements ICustomVanillaRecipe<org.bukkit.inventory.ShapelessRecipe> {

    static {
        final Verifier<CraftingRecipeShapeless> VERIFIER = VerifierBuilder.<CraftingRecipeShapeless>object(RecipeType.CRAFTING_SHAPELESS.getNamespacedKey(), AbstractRecipeShapeless.validator())
                .name(container -> "Shapeless Crafting Recipe" + container.value().map(customRecipeSmithing -> " [" + customRecipeSmithing.getNamespacedKey() + "]").orElse(""))
                .build();
        CustomCrafting.inst().getRegistries().getVerifiers().register(VERIFIER);
    }

    public CraftingRecipeShapeless(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node, 3, AdvancedRecipeSettings.class);
    }

    @JsonCreator
    public CraftingRecipeShapeless(@JsonProperty("key") @JacksonInject("key") NamespacedKey key, @JacksonInject("customcrafting") CustomCrafting customCrafting) {
        super(key, customCrafting, 3, new AdvancedRecipeSettings());
    }

    @Deprecated
    public CraftingRecipeShapeless(NamespacedKey key) {
        this(key, CustomCrafting.inst());
    }

    public CraftingRecipeShapeless(CraftingRecipeShapeless craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public CraftingRecipeShapeless clone() {
        return new CraftingRecipeShapeless(this);
    }

    @Override
    public org.bukkit.inventory.ShapelessRecipe getVanillaRecipe() {
        if (!getResult().isEmpty()) {
            // Register placeholder recipe
            var placeholderShapelessRecipe = new org.bukkit.inventory.ShapelessRecipe(ICustomVanillaRecipe.toPlaceholder(getNamespacedKey()).bukkit(), getResult().getItemStack());
            for (Ingredient value : ingredients) {
                placeholderShapelessRecipe.addIngredient(getMaterialRecipeChoiceFor(value));
            }
            placeholderShapelessRecipe.setGroup(getGroup());
            if(Bukkit.getRecipe(placeholderShapelessRecipe.getKey())!=null)
                Bukkit.removeRecipe(placeholderShapelessRecipe.getKey());

            Bukkit.addRecipe(placeholderShapelessRecipe);

            // Return display recipe
            var shapelessRecipe = new org.bukkit.inventory.ShapelessRecipe(ICustomVanillaRecipe.toDisplayKey(getNamespacedKey()).bukkit(), getResult().getItemStack());
            for (Ingredient value : ingredients) {
                shapelessRecipe.addIngredient(getExactRecipeChoiceFor(value));
            }
            shapelessRecipe.setGroup(getGroup());
            return shapelessRecipe;
        }
        return null;
    }

    private static RecipeChoice.ExactChoice getExactRecipeChoiceFor(Ingredient ingredient) {
        List<ItemStack> choices = ingredient.choices().stream().map(StackReference::referencedStack).distinct().collect(Collectors.toList());
        if (ingredient.isAllowEmpty()) choices.add(new ItemStack(Material.AIR));
        return new RecipeChoice.ExactChoice(choices);
    }

    private static RecipeChoice.MaterialChoice getMaterialRecipeChoiceFor(Ingredient ingredient) {
        List<Material> choices = ingredient.choices().stream().map(customItem -> customItem.referencedStack().getType()).distinct().collect(Collectors.toList());
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
