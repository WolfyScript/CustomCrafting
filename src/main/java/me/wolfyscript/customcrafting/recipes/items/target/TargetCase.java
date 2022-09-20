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

package me.wolfyscript.customcrafting.recipes.items.target;

import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class TargetCase {

    private final APIReference caseReference;
    private final Result result;

    @JsonIgnore
    private CustomItem ingredient;

    @JsonCreator
    public TargetCase(@JsonProperty("case") APIReference caseReference, @JsonProperty("result") Result result) {
        this.caseReference = caseReference;
        this.result = result;
        build();
    }

    @JsonProperty("case")
    public APIReference getCaseReference() {
        return caseReference;
    }

    @JsonProperty("result")
    public Result getResult() {
        return result;
    }

    public void build() {
        ingredient = CustomItem.of(caseReference);
        result.buildChoices();
    }

    public Optional<Result> check(ItemStack itemStack) {
        return ingredient.isSimilar(itemStack) ? Optional.of(result) : Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetCase that = (TargetCase) o;
        return Objects.equals(caseReference, that.caseReference) && Objects.equals(result, that.result) && Objects.equals(ingredient, that.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseReference, result, ingredient);
    }
}
