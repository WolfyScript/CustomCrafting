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

package me.wolfyscript.customcrafting.recipes.validator;

import me.wolfyscript.utilities.util.NamespacedKey;

class ObjectValidatorBuilderImpl<T> extends ValidatorBuilderImpl<T> implements ObjectValidatorBuilder<T> {

    public ObjectValidatorBuilderImpl(NamespacedKey key, ValidatorBuilder<?> parent) {
        super(key, parent);
    }

    public ObjectValidatorBuilderImpl(NamespacedKey key, ValidatorBuilder<?> parent, ObjectValidatorImpl<T> other) {
        this(key, parent);
        this.validationFunction = other.resultFunction;
        this.childValidators.addAll(other.childValidators);
        this.required = other.required;
        this.requiresOptionals = other.requiredOptional;
        this.nameConstructorFunction = other.nameConstructorFunction;
    }
}
