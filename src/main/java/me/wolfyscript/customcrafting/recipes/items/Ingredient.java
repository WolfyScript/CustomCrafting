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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Ingredient extends RecipeItemStack {

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
