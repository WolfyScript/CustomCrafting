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

import java.util.Collection;
import java.util.function.Function;

class CollectionValidatorBuilderImpl<T> extends ValidatorBuilderImpl<Collection<T>> implements CollectionValidatorBuilder<T> {

    private Validator<T> elementValidator;

    CollectionValidatorBuilderImpl(NamespacedKey key, ValidatorBuilder<?> parent) {
        super(key, parent);
    }

    CollectionValidatorBuilderImpl(NamespacedKey key, ValidatorBuilder<?> parent, CollectionValidatorImpl<T> other) {
        super(key, parent);
        this.elementValidator = other.elementValidator;
        this.nameConstructorFunction = other.nameConstructorFunction;
        this.validationFunction = other.resultFunction;
        this.required = other.required;
        this.requiresOptionals = other.requiredOptional;
    }

    @Override
    public CollectionValidatorBuilder<T> forEach(Function<InitStep<T, ?>, ValidatorBuilder<T>> childBuilderFunction) {
        var builderComplete = childBuilderFunction.apply(ValidatorBuilder.object(key));
        elementValidator = builderComplete.build();
        return this;
    }

    @Override
    public CollectionValidatorBuilder<T> validate(Function<ValidationContainer<Collection<T>>, ValidationContainer.UpdateStep<Collection<T>>> validateFunction) {
        return (CollectionValidatorBuilder<T>) super.validate(validateFunction);
    }

    @Override
    public CollectionValidatorBuilder<T> name(Function<ValidationContainer<Collection<T>>, String> nameConstructor) {
        return (CollectionValidatorBuilder<T>) super.name(nameConstructor);
    }

    @Override
    public Validator<Collection<T>> build() {
        return new CollectionValidatorImpl<>(key, required, requiresOptionals, nameConstructorFunction, validationFunction, elementValidator);
    }
}
