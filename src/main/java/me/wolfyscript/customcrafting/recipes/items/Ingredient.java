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

import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import com.wolfyscript.utilities.verification.ObjectVerifier;
import com.wolfyscript.utilities.verification.VerifierBuilder;
import com.wolfyscript.utilities.verification.VerifierContainer;
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

    public static final ObjectVerifier<Ingredient> VERIFIER;
    public static final ObjectVerifier<Map.Entry<Character, Ingredient>> ENTRY_VERIFIER;

    static {
        VERIFIER = VerifierBuilder.<Ingredient>object(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "recipe/ingredient"), RecipeItemStack.validatorFor()).build();

        ENTRY_VERIFIER = VerifierBuilder.<Map.Entry<Character, Ingredient>>object(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "recipe/ingredient_entry"))
                .name(container -> container.value().map(entry -> "Ingredient [" + entry.getKey() + "]").orElse("Ingredient [Unknown]"))
                .validate(entryContainer -> entryContainer.value()
                        .map(entry -> {
                            VerifierContainer<Ingredient> result = VERIFIER.validate(entry.getValue());
                            return entryContainer.update().type(result.type());
                        })
                        .orElseGet(() -> entryContainer.update().invalid())
                ).build();
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
    public Ingredient(@JsonProperty("items") Collection<StackReference> items, @JsonProperty("tags") Set<NamespacedKey> tags) {
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

    public Ingredient(StackReference... references) {
        super(references);
    }

    @Deprecated(forRemoval = true, since = "4.16.9")
    public Ingredient(APIReference... references) {
        super(Arrays.stream(references).map(APIReference::convertToStackReference).toArray(StackReference[]::new));
    }

    @Deprecated(forRemoval = true, since = "4.16.9")
    public Ingredient(@JsonProperty("items") List<APIReference> items, @JsonProperty("tags") Set<NamespacedKey> tags) {
        super(items.stream().map(APIReference::convertToStackReference).toList(), tags);
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
        return oldChoices.stream().anyMatch(customItem -> customItem.isSimilar(itemStack, exactMatch));
    }

    @Deprecated(forRemoval = true, since = "4.16.9")
    public Optional<CustomItem> check(ItemStack itemStack, boolean exactMatch) {
        if (itemStack == null) return Optional.empty();
        return oldChoices.stream().filter(customItem -> customItem.isSimilar(itemStack, exactMatch)).findFirst();
    }

    public Optional<StackReference> checkChoices(ItemStack itemStack, boolean exactMatch) {
        if (itemStack == null) return Optional.empty();
        return choices.stream().filter(reference -> reference.matches(itemStack, exactMatch)).findFirst();
    }
}
