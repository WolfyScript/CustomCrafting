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
import java.util.List;
import java.util.function.Function;

class CollectionValidatorImpl<T_VALUE> implements Validator<Collection<T_VALUE>> {

    private final NamespacedKey key;
    private final boolean required;
    private final int requiredOptional;
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

        values.stream()
                .map(value -> {
                    ValidationContainer<T_VALUE> result = elementValidator.validate(value);
                    container.update().children(List.of(result));
                    return result.type();
                }).distinct().forEach(resultType -> {
                    switch (resultType) {
                        case INVALID, PENDING -> container.update().type(resultType);
                        default -> { /* Do nothing! */ }
                    }
                });

        if (resultFunction != null) {
            resultFunction.apply(container);
        }

        return container;
    }

    @Override
    public ValidationContainerImpl<Collection<T_VALUE>> revalidate(ValidationContainerImpl<Collection<T_VALUE>> container) {
        if (container.type() == ValidationContainerImpl.ResultType.VALID || container.type() == ValidationContainerImpl.ResultType.INVALID)
            return container;

        for (ValidationContainerImpl<?> child : container.children()) {
            ValidationContainerImpl.ResultType type = child.revalidate().type();
            if (type != container.type()) {
                if (container.type() == ValidationContainerImpl.ResultType.INVALID) continue;
                container.update().type(type);
            }
        }
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
