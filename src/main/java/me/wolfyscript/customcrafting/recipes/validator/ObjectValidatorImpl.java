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
    protected final Function<ValidationContainerImpl<T_VALUE>, ValidationContainer.UpdateStep<T_VALUE>> resultFunction;
    protected final List<ValidatorEntry<T_VALUE, ?>> childValidators;

    public ObjectValidatorImpl(NamespacedKey key, Function<ValidationContainerImpl<T_VALUE>, ValidationContainer.UpdateStep<T_VALUE>> resultFunction, List<ValidatorEntry<T_VALUE, ?>> childValidators) {
        this.key = key;
        this.resultFunction = resultFunction;
        this.childValidators = childValidators;
    }

    @Override
    public ValidationContainerImpl<T_VALUE> validate(T_VALUE value) {
        ValidationContainerImpl<T_VALUE> container = new ValidationContainerImpl<>(value, this);

        if (resultFunction != null) {
            resultFunction.apply(container);
        }

        childValidators.stream()
                .map(entry -> {
                    ValidationContainer<?> result = entry.applyNestedValidator(value);
                    container.update().children(List.of(result));
                    return result.type();
                })
                .distinct().forEach(resultType -> {
                    switch (resultType) {
                        case INVALID, PENDING -> container.update().type(resultType);
                        default -> { /* Do nothing! */ }
                    }
                });

        return container;
    }

    @Override
    public ValidationContainerImpl<T_VALUE> revalidate(ValidationContainerImpl<T_VALUE> container) {
        if (container.type() == ValidationContainerImpl.ResultType.VALID || container.type() == ValidationContainerImpl.ResultType.INVALID) return container;

        if (resultFunction != null) {
            resultFunction.apply(container);
        }

        for (ValidationContainerImpl<?> child : container.children()) {
            ValidationContainerImpl.ResultType type = child.revalidate().type();
            if (type != container.type()) {
                if (container.type() == ValidationContainerImpl.ResultType.INVALID) continue;
                container.update().type(type);
            }
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
