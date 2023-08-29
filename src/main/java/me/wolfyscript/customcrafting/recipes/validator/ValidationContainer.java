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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ValidationContainer<T> {
    ValidationContainer<T> revalidate();

    List<ValidationContainer<?>> children();

    boolean optional();

    Optional<T> value();

    ResultType type();

    Collection<String> faults();

    UpdateStep<T> update();

    interface UpdateStep<T> {

        ValidationContainer<T> owner();

        ValidationContainer.UpdateStep<T> copyFrom(UpdateStep<?> other);

        ValidationContainer.UpdateStep<T> fault(String message);

        ValidationContainer.UpdateStep<T> clearFaults();

        ValidationContainer.UpdateStep<T> type(ResultType type);

        ValidationContainer.UpdateStep<T> children(List<ValidationContainer<?>> children);

    }

    enum ResultType {

        VALID,
        INVALID,
        PENDING;

        public ResultType combine(ResultType newValidation) {
            if (newValidation == null) return this;
            if (newValidation == INVALID) return INVALID;
            return switch (this) {
                case INVALID, PENDING -> this;
                case VALID -> newValidation;
            };
        }

        public ResultType combine2(ResultType newValidation) {
            if (newValidation == INVALID) return INVALID;
            return switch (this) {
                case INVALID -> this;
                case PENDING, VALID -> newValidation;
            };
        }


    }
}
