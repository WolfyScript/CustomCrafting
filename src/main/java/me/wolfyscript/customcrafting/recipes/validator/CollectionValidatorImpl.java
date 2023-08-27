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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class CollectionValidatorImpl<T_VALUE> implements Validator<Collection<T_VALUE>> {

    private final NamespacedKey key;
    final boolean required;
    final int requiredOptional;
    protected final Function<ValidationContainer<Collection<T_VALUE>>, ValidationContainer.UpdateStep<Collection<T_VALUE>>> resultFunction;
    protected final Validator<T_VALUE> elementValidator;
    protected Function<ValidationContainer<Collection<T_VALUE>>, String> nameConstructorFunction;

    public CollectionValidatorImpl(NamespacedKey key, boolean required, int requiredOptional, Function<ValidationContainer<Collection<T_VALUE>>, String> nameConstructorFunction, Function<ValidationContainer<Collection<T_VALUE>>, ValidationContainer.UpdateStep<Collection<T_VALUE>>> resultFunction, Validator<T_VALUE> elementValidator) {
        this.key = key;
        this.required = required;
        this.requiredOptional = requiredOptional;
        this.resultFunction = resultFunction;
        this.elementValidator = elementValidator;
        this.nameConstructorFunction = nameConstructorFunction;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }

    @Override
    public String getNameFor(ValidationContainer<Collection<T_VALUE>> container) {
        return nameConstructorFunction.apply(container);
    }

    @Override
    public ValidationContainerImpl<Collection<T_VALUE>> validate(Collection<T_VALUE> values) {
        ValidationContainerImpl<Collection<T_VALUE>> container = new ValidationContainerImpl<>(values, this);

        ValidationContainer.ResultType resultType;
        if (elementValidator.optional()) {
            Map<ValidationContainer.ResultType, Integer> counts = new EnumMap<>(ValidationContainer.ResultType.class);
            for (T_VALUE value : values) {
                ValidationContainer<T_VALUE> result = elementValidator.validate(value);
                container.update().children(List.of(result));
                counts.merge(result.type(), 1, Integer::sum);
            }

            if (counts.getOrDefault(ValidationContainer.ResultType.VALID, 0) >= requiredOptional) {
                if (counts.getOrDefault(ValidationContainer.ResultType.PENDING, 0) > 0) {
                    resultType = ValidationContainer.ResultType.PENDING;
                } else {
                    resultType = ValidationContainer.ResultType.VALID;
                }
            } else {
                resultType = ValidationContainer.ResultType.INVALID;
            }
        } else {
            resultType = values.stream()
                    .map(value -> {
                        ValidationContainer<T_VALUE> result = elementValidator.validate(value);
                        container.update().children(List.of(result));
                        return result.type();
                    })
                    .distinct()
                    .reduce(ValidationContainer.ResultType::combine)
                    .orElse(ValidationContainer.ResultType.INVALID);
        }

        container.update().type(resultType);

        if (resultFunction != null) {
            resultFunction.apply(container);
        }

        return container;
    }

    @Override
    public ValidationContainer<Collection<T_VALUE>> revalidate(ValidationContainer<Collection<T_VALUE>> container) {
        if (container.type() == ValidationContainerImpl.ResultType.VALID || container.type() == ValidationContainerImpl.ResultType.INVALID)
            return container;

        ValidationContainer.ResultType resultType;
        if (elementValidator.optional()) {

            Map<ValidationContainer.ResultType, Integer> counts = new EnumMap<>(ValidationContainer.ResultType.class);
            for (ValidationContainer<?> child : container.children()) {
                counts.merge(child.revalidate().type(), 1, Integer::sum);
            }

            if (counts.getOrDefault(ValidationContainer.ResultType.VALID, 0) >= requiredOptional) {
                if (counts.getOrDefault(ValidationContainer.ResultType.PENDING, 0) > 0) {
                    resultType = ValidationContainer.ResultType.PENDING;
                } else {
                    resultType = ValidationContainer.ResultType.VALID;
                }
            } else {
                resultType = ValidationContainer.ResultType.INVALID;
            }

        } else {
            resultType = container.children().stream()
                    .map(value -> value.revalidate().type())
                    .distinct()
                    .reduce(ValidationContainer.ResultType::combine2)
                    .orElse(ValidationContainer.ResultType.INVALID);
        }

        container.update().type(resultType);

        if (resultFunction != null) {
            resultFunction.apply(container);
        }

        return container;
    }

    @Override
    public boolean optional() {
        return !required;
    }
}
