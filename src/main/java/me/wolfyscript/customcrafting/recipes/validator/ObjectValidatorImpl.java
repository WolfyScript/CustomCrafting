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

import java.util.List;
import java.util.function.Function;

class ObjectValidatorImpl<T_VALUE> implements Validator<T_VALUE> {

    private final NamespacedKey key;
    private final boolean required;
    private final int requiredOptional;
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

        ValidationContainer.ResultType resultType = container.type();
        int optionalsValidOrPending = 0;

        for (ValidatorEntry<T_VALUE, ?> entry : childValidators) {
            ValidationContainer<?> result = entry.applyNestedValidator(value);
            container.update().children(List.of(result));
            if (entry.validator().optional()) {
                // Invalid Optional children should not be taken into account!
                if (result.type() == ValidationContainer.ResultType.VALID || result.type() == ValidationContainer.ResultType.PENDING) {
                    resultType = resultType.combine(result.type());
                    optionalsValidOrPending++;
                }
                continue;
            }
            resultType = resultType.combine(result.type());
        }

        if (optionalsValidOrPending < requiredOptional) {
            resultType = resultType.combine(ValidationContainer.ResultType.INVALID);
        }
        container.update().type(resultType);

        if (resultFunction != null) {
            resultFunction.apply(container);
        }

        return container;
    }

    @Override
    public ValidationContainerImpl<T_VALUE> revalidate(ValidationContainerImpl<T_VALUE> container) {
        if (container.type() == ValidationContainerImpl.ResultType.VALID || container.type() == ValidationContainerImpl.ResultType.INVALID) return container;

        ValidationContainer.ResultType resultType = null;
        int optionalsValidOrPending = 0;

        for (ValidationContainerImpl<?> child : container.children()) {
            ValidationContainerImpl.ResultType type = child.revalidate().type();
            if (type != container.type()) {
                if (container.type() == ValidationContainerImpl.ResultType.INVALID) continue;
                if (child.optional()) {
                    if (type == ValidationContainer.ResultType.VALID || type == ValidationContainer.ResultType.PENDING) {
                        optionalsValidOrPending++;
                    }
                    continue;
                }
                resultType = resultType == null ? type : resultType.combine(type);
            }
        }

        if (optionalsValidOrPending < requiredOptional) {
            resultType = resultType.combine(ValidationContainer.ResultType.INVALID);
        }
        container.update().type(resultType);

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
