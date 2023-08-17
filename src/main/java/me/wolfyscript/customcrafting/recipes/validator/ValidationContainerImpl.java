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

import java.util.*;

public class ValidationContainerImpl<T> implements ValidationContainer<T> {

    private ResultType type;
    private final List<String> faults;
    private final T value;
    private final Validator<T> validator;
    private List<ValidationContainerImpl<?>> children;

    public ValidationContainerImpl(T value, Validator<T> validator) {
        this.type = ResultType.VALID;
        this.value = value;
        this.faults = new ArrayList<>();
        this.validator = validator;
        this.children = List.of();
    }

    @Override
    public ValidationContainerImpl<T> revalidate() {
        return validator.revalidate(this);
    }

    @Override
    public boolean optional() {
        return validator.optional();
    }

    @Override
    public List<ValidationContainerImpl<?>> children() {
        return children;
    }

    @Override
    public Optional<T> value() {
        return Optional.ofNullable(value);
    }

    @Override
    public ResultType type() {
        return type;
    }

    @Override
    public List<String> faults() {
        return faults;
    }

    @Override
    public UpdateStep<T> update() {
        return new UpdateStepImpl();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ValidationContainerImpl<?>) obj;
        return Objects.equals(this.type, that.type) &&
                Objects.equals(this.faults, that.faults);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, faults);
    }

    @Override
    public String toString() {
        return toString(0, "", new StringBuilder());
    }

    private String toString(int level, String prefix, StringBuilder out) {
        out.append(validator.getNameFor(this)).append("\n");
        for (String fault : faults()) {
            out.append(prefix).append("| ").append(fault).append('\n');
        }

        for (int i = 0; i < children.size(); i++) {
            ValidationContainerImpl<?> child = children.get(i);
            if (child.type() == ResultType.VALID) continue;
            out.append(prefix);
            if (i + 1 == children.size()) {
                out.append("\\-- ");
                child.toString(level + 1, prefix + "    ", out);
            } else {
                out.append("+-- ");
                child.toString(level + 1, prefix + "|   ", out);
            }
        }
        return out.toString();
    }

    public class UpdateStepImpl implements UpdateStep<T> {

        public ValidationContainerImpl<T> owner() {
            return ValidationContainerImpl.this;
        }

        public UpdateStep<T> copyFrom(UpdateStep<?> other) {
            ValidationContainerImpl.this.type = other.owner().type();

            List<ValidationContainerImpl<?>> copyChildren = new ArrayList<>();
            copyChildren.addAll(owner().children);
            copyChildren.addAll(other.owner().children());
            ValidationContainerImpl.this.children = Collections.unmodifiableList(copyChildren);

            ValidationContainerImpl.this.faults.addAll(other.owner().faults());
            return this;
        }

        public UpdateStep<T> fault(String message) {
            ValidationContainerImpl.this.faults.add(message);
            return this;
        }

        @Override
        public UpdateStep<T> clearFaults() {
            ValidationContainerImpl.this.faults.clear();
            return this;
        }

        public UpdateStep<T> type(ResultType type) {
            ValidationContainerImpl.this.type = type;
            return this;
        }

        public UpdateStep<T> children(List<ValidationContainer<?>> children) {
            List<ValidationContainerImpl<?>> copyChildren = new ArrayList<>();
            copyChildren.addAll(owner().children);
            for (ValidationContainer<?> child : children) {
                if (child instanceof ValidationContainerImpl<?> container) {
                    copyChildren.add(container);
                }
            }
            ValidationContainerImpl.this.children = Collections.unmodifiableList(copyChildren);
            return this;
        }

    }

}

