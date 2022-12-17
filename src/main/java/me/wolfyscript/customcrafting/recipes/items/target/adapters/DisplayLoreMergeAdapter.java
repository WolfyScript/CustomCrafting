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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonInclude;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.eval.operators.BoolOperator;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProvider;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DisplayLoreMergeAdapter extends MergeAdapter {

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean replaceLore = false;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ValueProvider<Integer> insertAtIndex = null;
    private List<ElementOption> lines;
    private String extra;

    public DisplayLoreMergeAdapter() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "display_lore"));
        this.replaceLore = false;
        this.extra = null;
    }

    public DisplayLoreMergeAdapter(DisplayLoreMergeAdapter adapter) {
        super(adapter);
        this.replaceLore = adapter.replaceLore;
        this.extra = adapter.extra;
    }

    public void setLines(List<ElementOption> lines) {
        this.lines = lines;
    }

    public List<ElementOption> getLines() {
        return lines;
    }

    public void setReplaceLore(boolean replaceLore) {
        this.replaceLore = replaceLore;
    }

    public boolean isReplaceLore() {
        return replaceLore;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getExtra() {
        return extra;
    }

    public void setInsertAtIndex(ValueProvider<Integer> insertAtIndex) {
        this.insertAtIndex = insertAtIndex;
    }

    public ValueProvider<Integer> getInsertAtIndex() {
        return insertAtIndex;
    }

    public Optional<ValueProvider<Integer>> insertAtIndex() {
        return Optional.ofNullable(insertAtIndex);
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, CustomItem customResult, ItemStack result) {
        var resultMeta = result.getItemMeta();
        List<String> finalLore = new ArrayList<>();
        for (IngredientData data : recipeData.getBySlots(slots)) {
            var item = data.itemStack();
            var meta = item.getItemMeta();
            if (meta.hasLore()) {
                List<String> targetedLore = meta.getLore();
                assert targetedLore != null;
                for (ElementOption line : lines) {
                    if (line.include().map(boolOperator -> boolOperator.evaluate(new EvalContext())).orElse(true)) {
                        line.index().ifPresentOrElse(indexProvider -> {
                            int index = indexProvider.getValue();
                            if (index < 0) {
                                index = targetedLore.size() + (index % targetedLore.size()); //Convert the negative index to a positive reverted index, that starts from the end.
                            }
                            index = index % targetedLore.size(); //Prevent out of bounds
                            if (targetedLore.size() > index) {
                                String targetValue = targetedLore.get(index);
                                line.value().ifPresentOrElse(valueProvider -> {
                                    if (valueProvider.getValue().equals(targetValue)) {
                                        finalLore.add(targetValue);
                                    }
                                }, () -> finalLore.add(targetValue));
                            }
                        }, () -> line.value().ifPresentOrElse(valueProvider -> {
                            String value = valueProvider.getValue();
                            for (String targetValue : targetedLore) {
                                if (value.equals(targetValue)) {
                                    finalLore.add(targetValue);
                                }
                            }
                        }, () -> finalLore.addAll(targetedLore)));
                    }
                }
            }
        }
        List<String> resultLore = resultMeta.hasLore() ? resultMeta.getLore() : new ArrayList<>();
        assert resultLore != null;
        if (replaceLore) {
            resultLore = finalLore;
        } else {
            int index = insertAtIndex().map(integerValueProvider -> integerValueProvider.getValue()).orElse(resultLore.size());
            if (index < 0) {
                index = resultLore.size() + (index % (resultLore.size()+1)); //Convert the negative index to a positive reverted index, that starts from the end.
            }
            index = index % (resultLore.size() + 1); //Prevent out of bounds
            if (index <= resultLore.size()) { // Shouldn't be false! Index out of bounds!
                resultLore.addAll(index, finalLore);
            }
        }
        resultMeta.setLore(resultLore);
        result.setItemMeta(resultMeta);
        return result;
    }

    @Override
    public DisplayLoreMergeAdapter clone() {
        return new DisplayLoreMergeAdapter(this);
    }

    public static class ElementOption {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private ValueProvider<Integer> index;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private BoolOperator include;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private ValueProvider<String> value;

        public Optional<ValueProvider<Integer>> index() {
            return Optional.ofNullable(index);
        }

        public void setIndex(ValueProvider<Integer> index) {
            this.index = index;
        }

        public Optional<BoolOperator> include() {
            return Optional.ofNullable(include);
        }

        public void setInclude(BoolOperator include) {
            this.include = include;
        }

        public Optional<ValueProvider<String>> value() {
            return Optional.ofNullable(value);
        }

        public void setValue(ValueProvider<String> value) {
            this.value = value;
        }
    }
}
