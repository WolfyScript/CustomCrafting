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

package me.wolfyscript.customcrafting.recipes.items;

import me.wolfyscript.customcrafting.recipes.validator.ValidationContainer;
import me.wolfyscript.customcrafting.recipes.validator.Validator;
import me.wolfyscript.customcrafting.recipes.validator.ValidatorBuilder;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Ingredient extends RecipeItemStack {

    public static final Validator<Ingredient> VALIDATOR;
    public static final Validator<Map.Entry<Character, Ingredient>> ENTRY_VALIDATOR;

    static {
        VALIDATOR = ValidatorBuilder.<Ingredient>object(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "recipe/ingredient")).use(RecipeItemStack.validatorFor()).build();
        ENTRY_VALIDATOR = ValidatorBuilder.<Map.Entry<Character, Ingredient>>object(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "recipe/ingredient_entry")).def()
                .name(container -> container.value().map(entry -> "Ingredient [" + entry.getKey() + "]").orElse("Ingredient [Unknown]"))
                .validate(entryContainer -> entryContainer.value()
                        .map(entry -> {
                            ValidationContainer<Ingredient> result = VALIDATOR.validate(entry.getValue());
                            return entryContainer.update().copyFrom(result.update());
                        })
                        .orElseGet(() -> entryContainer.update().type(ValidationContainer.ResultType.INVALID))).build();
    }

    private boolean replaceWithRemains = true;
    private boolean allowEmpty = false;

    public Ingredient() {
        super();
    }

    public Ingredient(Ingredient ingredient) {
        super(ingredient);
        this.replaceWithRemains = ingredient.replaceWithRemains;
    }

    @JsonCreator
    public Ingredient(@JsonProperty("items") List<APIReference> items, @JsonProperty("tags") Set<NamespacedKey> tags) {
        super(items, tags);
    }

    public Ingredient(Material... materials) {
        super(materials);
    }

    public Ingredient(ItemStack... items) {
        super(items);
    }

    public Ingredient(NamespacedKey... tags) {
        super(tags);
    }

    public Ingredient(APIReference... references) {
        super(references);
    }

    public boolean isReplaceWithRemains() {
        return replaceWithRemains;
    }

    public void setReplaceWithRemains(boolean replaceWithRemains) {
        this.replaceWithRemains = replaceWithRemains;
    }

    public boolean isAllowEmpty() {
        return allowEmpty;
    }

    public void setAllowEmpty(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
    }

    @Override
    public Ingredient clone() {
        return new Ingredient(this);
    }

    public boolean test(ItemStack itemStack, boolean exactMatch) {
        if (itemStack == null) return false;
        return choices.stream().anyMatch(customItem -> customItem.isSimilar(itemStack, exactMatch));
    }

    public Optional<CustomItem> check(ItemStack itemStack, boolean exactMatch) {
        if (itemStack == null) return Optional.empty();
        return choices.stream().filter(customItem -> customItem.isSimilar(itemStack, exactMatch)).findFirst();
    }
}
