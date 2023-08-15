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

public interface ValidatorBuilder<T> {

    /**
     * Initiates the builder for object validation
     *
     * @param <T> The type of the object
     * @return The init step for the validator builder
     */
    static <T> ValidatorBuilder.InitStep<T, ?> object(NamespacedKey key) {
        return new ValidatorBuilderImpl.InitStepImpl<>(null, new ValidatorBuilderImpl<>(key, null)) {

            @Override
            public ValidatorBuilderImpl<T> use(Validator<T> validator) {
                if (!(validator instanceof ObjectValidatorImpl<T> objectValidator)) throw new IllegalArgumentException("Validator must be an object validator!");
                return new ValidatorBuilderImpl<>(key, parent) {

                    @Override
                    public ObjectValidatorImpl<T> build() {

                        return new ObjectValidatorImpl<>(key, validationFunction, childValidators) {

                            @Override
                            public ValidationContainerImpl<T> validate(T value) {
                                ValidationContainerImpl<T> extendedContainer = objectValidator.validate(value);
                                ValidationContainerImpl<T> container = super.validate(value);
                                container.update().copyFrom(extendedContainer.update());
                                return container;
                            }

                            @Override
                            public ValidationContainerImpl<T> revalidate(ValidationContainerImpl<T> container) {
                                objectValidator.revalidate(container);
                                return super.revalidate(container);
                            }
                        };
                    }

                };
            }

        };
    }

    /**
     * Initiates the builder for collection validation
     *
     * @param <T> The type of the object contained in the collection
     * @return This init step for the validator builder
     */
    static <T> ValidatorBuilder.InitStep<Collection<T>, CollectionValidatorBuilder<T>> collection(NamespacedKey key) {
        return new ValidatorBuilderImpl.InitStepImpl<>(null, new ValidatorBuilderImpl.CollectionValidatorBuilderImpl<>(key, null)) {

            @Override
            public CollectionValidatorBuilder<T> use(Validator<Collection<T>> validator) {
                return new ValidatorBuilderImpl.CollectionValidatorBuilderImpl<>(key, parent) {

                    @Override
                    public CollectionValidatorImpl<T> build() {
                        return new CollectionValidatorImpl<>(key, validationFunction, null) {

                            @Override
                            public ValidationContainerImpl<Collection<T>> validate(Collection<T> value) {
                                ValidationContainerImpl<? super Collection<T>> extendedContainer = validator.validate(value);
                                ValidationContainerImpl<Collection<T>> container = super.validate(value);
                                container.update().copyFrom(extendedContainer.update());
                                return container;
                            }

                            @Override
                            public ValidationContainerImpl<Collection<T>> revalidate(ValidationContainerImpl<Collection<T>> container) {
                                validator.revalidate(container);
                                return super.revalidate(container);
                            }
                        };
                    }

                };

            }
        };
    }

    /**
     * Specifies the validation function that validates the value.
     *
     * @param validateFunction The validation function
     * @return This builder instance for chaining
     */
    ValidatorBuilder<T> validate(Function<ValidationContainerImpl<T>, ValidationContainer.UpdateStep<T>> validateFunction);

    /**
     * Adds a nested child object validation to this validator.
     * The getter provides a way to compute the child value from the current value.
     *
     * @param getter       The getter to get the child value
     * @param childBuilder The builder for the child validator
     * @param <C>          The child value type
     * @return This builder instance for chaining
     */
    <C> ValidatorBuilder<T> object(Function<T, C> getter, Function<ValidatorBuilder.InitStep<C, ?>, ValidatorBuilder<C>> childBuilder);

    /**
     * Adds a nested child collection validator. The getter provides a way to compute the collection from the current value.
     *
     * @param getter       The getter to get the collection
     * @param childBuilder The builder for the child validator
     * @param <C>          The type of the collection elements
     * @return This builder for chaining
     */
    <C> ValidatorBuilder<T> collection(Function<T, Collection<C>> getter, Function<ValidatorBuilder.InitStep<Collection<C>, CollectionValidatorBuilder<C>>, ValidatorBuilder<Collection<C>>> childBuilder);

    Validator<T> build();

    /**
     * Initiates the builder to use with default settings, or use existing validators.
     *
     * @param <T> The type of the value
     * @param <B> The type of the builder
     */
    interface InitStep<T, B extends ValidatorBuilder<T>> {

        /**
         * Uses the default builder as is without any extensions or manipulations.
         *
         * @return The default builder as is
         */
        B def();

        /**
         * Uses an existing validator together with the validator build using this builder.
         * Basically allowing to extend the existing validator with more child validators, etc.<br>
         * Extending it is not required, it can simply be used to reuse validators for child objects, etc.
         *
         * @param validator The existing validator that handles the super type of the type handled by this builder
         * @return A builder based on the specified validator
         */
        B use(Validator<T> validator);

    }

    interface CollectionValidatorBuilder<T> extends ValidatorBuilder<Collection<T>> {

        @Override
        CollectionValidatorBuilder<T> validate(Function<ValidationContainerImpl<Collection<T>>, ValidationContainer.UpdateStep<Collection<T>>> validateFunction);

        /**
         * Specifies the validator that is used to validate each element in the collection.
         *
         * @param childBuilder The element validator builder
         * @return This build instance for chaining
         */
        CollectionValidatorBuilder<T> forEach(Function<ValidatorBuilder.InitStep<T, ?>, ValidatorBuilder<T>> childBuilder);
    }

}
