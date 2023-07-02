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

package me.wolfyscript.customcrafting.recipes.items.target.adapters;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.*;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.eval.operators.BoolOperator;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class ElementOption<O, C> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @JsonAlias("index")
    private List<ValueProvider<Integer>> indices;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BoolOperator condition;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @JsonAlias("value")
    private List<ValueProvider<C>> values = List.of();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BoolOperator exclude;

    public Optional<BoolOperator> condition() {
        return Optional.ofNullable(condition);
    }

    @JsonGetter
    private BoolOperator getCondition() {
        return condition;
    }

    public void setCondition(BoolOperator condition) {
        this.condition = condition;
    }

    @JsonGetter("indices")
    public List<ValueProvider<Integer>> indices() {
        return indices;
    }

    public void setIndices(List<ValueProvider<Integer>> indices) {
        this.indices = List.copyOf(indices);
    }

    @JsonGetter("values")
    public List<ValueProvider<C>> values() {
        return values;
    }

    public void setValues(List<ValueProvider<C>> values) {
        this.values = List.copyOf(values);
    }

    public Optional<BoolOperator> exclude() {
        return Optional.of(exclude);
    }

    @JsonGetter
    private BoolOperator getExclude() {
        return exclude;
    }

    public void setExclude(BoolOperator exclude) {
        this.exclude = exclude;
    }

    @Deprecated(forRemoval = true)
    public Optional<ValueProvider<Integer>> index() {
        return Optional.ofNullable(indices.get(0));
    }

    @Deprecated(forRemoval = true)
    @JsonIgnore
    public void setIndex(ValueProvider<Integer> index) {
        if (indices.isEmpty()) {
            indices.add(index);
        } else {
            this.indices.set(0, index);
        }
    }

    @Deprecated(forRemoval = true)
    public Optional<ValueProvider<C>> value() {
        return Optional.ofNullable(!values.isEmpty() ? values.get(0) : null);
    }

    @Deprecated(forRemoval = true)
    @JsonIgnore
    public void setValue(ValueProvider<C> value) {
        if (values.isEmpty()) {
            values.add(value);
        } else {
            this.values.set(0, value);
        }
    }

    public abstract boolean isEqual(O value, EvalContext evalContext);

    private boolean checkEquality(O value, EvalContext evalContext) {
        return isEqual(value, evalContext);
    }

    public List<O> readFromSource(List<O> source, EvalContext evalContext) {
        List<O> result = new ArrayList<>();
        if (condition().map(boolOperator -> boolOperator.evaluate(evalContext)).orElse(true)) {
            boolean shouldExclude = exclude().map(boolOperator -> boolOperator.evaluate(evalContext)).orElse(false);
            if (!indices.isEmpty()) {
                IntList indicesUsed = new IntArrayList();
                for (ValueProvider<Integer> indexProvider : indices) {
                    int index = indexProvider.getValue(evalContext);
                    if (index < 0) {
                        index = source.size() + (index % source.size()); //Convert the negative index to a positive reverted index, that starts from the end.
                    }
                    index = index % source.size(); //Prevent out of bounds
                    if (source.size() > index) {
                        O targetValue = source.get(index);
                        if (checkEquality(targetValue, evalContext)) {
                            indicesUsed.add(index);
                        } if (values.isEmpty() && shouldExclude) {
                            indicesUsed.add(index);
                        }
                    }
                }
                indicesUsed.sort(IntComparators.OPPOSITE_COMPARATOR);

                if (shouldExclude) {
                    result.addAll(source);
                }
                for (int index : indicesUsed) {
                    if (shouldExclude) {
                        result.remove(index);
                    } else {
                        result.add(source.get(index));
                    }
                }
                if (!shouldExclude) {
                    Collections.reverse(result);
                }
                return result;
            }

            if (!values.isEmpty()) {
                for (O targetValue : source) {
                    if (checkEquality(targetValue, evalContext) && !shouldExclude) {
                        result.add(targetValue);
                    }
                }
                return result;
            }
            result.addAll(source);
        }
        return result;
    }

}

