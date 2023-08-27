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

import java.util.*;
import java.util.function.Function;

class ValidatorBuilderImpl<T> implements ValidatorBuilder<T> {

    protected final NamespacedKey key;
    protected final ValidatorBuilder<?> parentBuilder;
    protected Function<ValidationContainer<T>, ValidationContainer.UpdateStep<T>> validationFunction;
    protected final List<ValidatorEntry<T, ?>> childValidators = new ArrayList<>();
    protected boolean required = true;
    protected int requiresOptionals = 0;
    protected Function<ValidationContainer<T>, String> nameConstructorFunction = container -> container.value().map(value -> value.getClass().getSimpleName()).orElse("Unnamed");

    public ValidatorBuilderImpl(NamespacedKey key, ValidatorBuilder<?> parent) {
        this.key = key;
        this.parentBuilder = parent;
    }

    @Override
    public ValidatorBuilder<T> validate(Function<ValidationContainer<T>, ValidationContainer.UpdateStep<T>> validateFunction) {
        this.validationFunction = validateFunction;
        return this;
    }

    @Override
    public ValidatorBuilder<T> optional() {
        this.required = false;
        return this;
    }

    @Override
    public ValidatorBuilder<T> name(Function<ValidationContainer<T>, String> nameConstructor) {
        this.nameConstructorFunction = nameConstructor;
        return this;
    }

    @Override
    public ValidatorBuilder<T> require(int count) {
        this.requiresOptionals = count;
        return this;
    }

    @Override
    public <C> ValidatorBuilder<T> object(Function<T, C> getter, Function<InitStep<C, ?>, ValidatorBuilder<C>> childBuilderFunction) {
        var builderComplete = childBuilderFunction.apply(ValidatorBuilder.object(null));
        childValidators.add(new ValidatorEntry<>(builderComplete.build(), getter));
        return this;
    }

    @Override
    public <C> ValidatorBuilder<T> collection(Function<T, Collection<C>> getter, Function<InitStep<Collection<C>, CollectionValidatorBuilder<C>>, ValidatorBuilder<Collection<C>>> childBuilderFunction) {
        var builderComplete = childBuilderFunction.apply(ValidatorBuilder.collection(null));
        childValidators.add(new ValidatorEntry<>(builderComplete.build(), getter));
        return this;
    }

    @Override
    public Validator<T> build() {
        return new ObjectValidatorImpl<>(key, required, requiresOptionals, nameConstructorFunction, validationFunction, List.copyOf(childValidators));
    }

    static abstract class InitStepImpl<T, B extends ValidatorBuilder<T>> implements InitStep<T, B> {

        protected final ValidatorBuilder<?> parent;
        protected final B originalBuilder;

        InitStepImpl(ValidatorBuilder<?> parent, B builder) {
            this.parent = parent;
            this.originalBuilder = builder;
        }

        @Override
        public B def() {
            return originalBuilder;
        }

    }

}
