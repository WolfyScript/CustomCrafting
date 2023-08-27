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

import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;

class ObjectValidatorImpl<T_VALUE> implements Validator<T_VALUE> {

    private final NamespacedKey key;
    final boolean required;
    final int requiredOptional;
    protected final Function<ValidationContainer<T_VALUE>, ValidationContainer.UpdateStep<T_VALUE>> resultFunction;
    protected final List<ValidatorEntry<T_VALUE, ?>> childValidators;
    protected Function<ValidationContainer<T_VALUE>, String> nameConstructorFunction;

    public ObjectValidatorImpl(NamespacedKey key, boolean required, int requiredOptional, Function<ValidationContainer<T_VALUE>, String> nameConstructorFunction, Function<ValidationContainer<T_VALUE>, ValidationContainer.UpdateStep<T_VALUE>> resultFunction, List<ValidatorEntry<T_VALUE, ?>> childValidators) {
        this.key = key;
        this.required = required;
        this.requiredOptional = requiredOptional;
        this.resultFunction = resultFunction;
        this.childValidators = childValidators;
        this.nameConstructorFunction = nameConstructorFunction;
    }

    @Override
    public boolean optional() {
        return !required;
    }

    @Override
    public String getNameFor(ValidationContainer<T_VALUE> container) {
        return nameConstructorFunction.apply(container);
    }

    @Override
    public ValidationContainerImpl<T_VALUE> validate(T_VALUE value) {

        ValidationContainerImpl<T_VALUE> container = new ValidationContainerImpl<>(value, this);

        ValidationContainer.ResultType requiredType = container.type();

        EnumMap<ValidationContainer.ResultType, Integer> optionalCounts = new EnumMap<>(ValidationContainer.ResultType.class);

        for (ValidatorEntry<T_VALUE, ?> entry : childValidators) {
            ValidationContainer<?> result = entry.applyNestedValidator(value);
            container.update().children(List.of(result));
            if (entry.validator().optional()) {
                optionalCounts.merge(result.type(), 1, Integer::sum);
                continue;
            }
            requiredType = requiredType.combine(result.type());
        }
        ValidationContainer.ResultType optionalType = ValidationContainer.ResultType.INVALID;
        if (optionalCounts.getOrDefault(ValidationContainer.ResultType.VALID, 0) >= requiredOptional) {
            optionalType = ValidationContainer.ResultType.VALID;
        } else if (optionalCounts.getOrDefault(ValidationContainer.ResultType.PENDING, 0) >= requiredOptional) {
            optionalType = ValidationContainer.ResultType.PENDING;
        }
        requiredType = requiredType.combine(optionalType);

        container.update().type(requiredType);

        if (resultFunction != null) {
            resultFunction.apply(container);
        }

        return container;
    }

    @Override
    public ValidationContainer<T_VALUE> revalidate(ValidationContainer<T_VALUE> container) {
        if (container.type() == ValidationContainer.ResultType.VALID || container.type() == ValidationContainer.ResultType.INVALID)
            return container;

        ValidationContainer.ResultType requiredType = null;
        EnumMap<ValidationContainer.ResultType, Integer> optionalCounts = new EnumMap<>(ValidationContainer.ResultType.class);

        for (ValidationContainer<?> child : container.children()) {
            ValidationContainer.ResultType type = child.revalidate().type();
            if (child.optional()) {
                optionalCounts.merge(type, 1, Integer::sum);
                continue;
            }
            requiredType = requiredType == null ? type : requiredType.combine2(type);
        }
        if (requiredType == null) requiredType = ValidationContainer.ResultType.VALID;

        ValidationContainer.ResultType optionalType = ValidationContainer.ResultType.INVALID;
        if (optionalCounts.getOrDefault(ValidationContainer.ResultType.VALID, 0) >= requiredOptional) {
            optionalType = ValidationContainer.ResultType.VALID;
        } else if (optionalCounts.getOrDefault(ValidationContainer.ResultType.PENDING, 0) >= requiredOptional) {
            optionalType = ValidationContainer.ResultType.PENDING;
        }
        requiredType = requiredType.combine(optionalType);

        container.update().type(requiredType);

        if (resultFunction != null) {
            resultFunction.apply(container);
        }

        return container;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }

    @Override
    public String toString() {
        return "ValidatorImpl{" +
                "key=" + key +
                '}';
    }

}
