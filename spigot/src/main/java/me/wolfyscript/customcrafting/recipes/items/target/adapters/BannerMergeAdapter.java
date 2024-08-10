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

import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.eval.context.EvalContextPlayer;
import me.wolfyscript.utilities.util.eval.operators.BoolOperator;
import me.wolfyscript.utilities.util.eval.operators.BoolOperatorConst;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProvider;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.Nullable;

public class BannerMergeAdapter extends MergeAdapter {

    private BoolOperator replacePattern = new BoolOperatorConst(false);
    private ValueProvider<Integer> insertAtIndex = null;
    private List<ElementOptionBannerPattern> patterns;
    private List<PatternOption> extra;
    private BoolOperator addExtraFirst;

    public BannerMergeAdapter() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "banner"));
    }

    public BannerMergeAdapter(BannerMergeAdapter adapter) {
        super(adapter);
    }

    public Optional<ValueProvider<Integer>> insertAtIndex() {
        return Optional.ofNullable(insertAtIndex);
    }

    public void setInsertAtIndex(ValueProvider<Integer> insertAtIndex) {
        this.insertAtIndex = insertAtIndex;
    }

    public ValueProvider<Integer> getInsertAtIndex() {
        return insertAtIndex;
    }

    public BoolOperator getReplacePattern() {
        return replacePattern;
    }

    public void setReplacePattern(BoolOperator replacePattern) {
        this.replacePattern = replacePattern;
    }

    public List<ElementOptionBannerPattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<ElementOptionBannerPattern> patterns) {
        this.patterns = patterns;
    }

    public List<PatternOption> getExtra() {
        return extra;
    }

    public void setExtra(List<PatternOption> extra) {
        this.extra = extra;
    }

    public BoolOperator getAddExtraFirst() {
        return addExtraFirst;
    }

    public void setAddExtraFirst(BoolOperator addExtraFirst) {
        this.addExtraFirst = addExtraFirst;
    }

    private List<Pattern> extra(EvalContext evalContext) {
       return extra.stream().map(patternOption -> new Pattern(patternOption.dyeColor, patternOption.type)).toList();
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, StackReference resultReference, ItemStack result) {
        var resultMeta = result.getItemMeta();
        if (!(resultMeta instanceof BannerMeta bannerResultMeta)) return result;
        var evalContext = player == null ? new EvalContext() : new EvalContextPlayer(player);
        List<Pattern> finalPatterns = new ArrayList<>();
        for (IngredientData data : recipeData.getBySlots(slots)) {
            var meta = data.itemStack().getItemMeta();
            if (!(meta instanceof BannerMeta bannerMeta)) continue;
            List<Pattern> targetPatterns = bannerMeta.getPatterns();
            for (ElementOptionBannerPattern line : patterns) {
                finalPatterns.addAll(line.readFromSource(targetPatterns, evalContext));
            }
        }
        List<Pattern> resultPatterns = new ArrayList<>(bannerResultMeta.getPatterns());
        if (replacePattern.evaluate(evalContext)) {
            resultPatterns = addExtraFirst.evaluate(evalContext) ? extra(evalContext) : new ArrayList<>();
            resultPatterns.addAll(finalPatterns);
        } else {
            if (addExtraFirst.evaluate(evalContext)) {
                resultPatterns.addAll(extra(evalContext));
            }
            int index = insertAtIndex().map(integerValueProvider -> integerValueProvider.getValue(evalContext)).orElse(resultPatterns.size());
            if (index < 0) {
                index = resultPatterns.size() + (index % (resultPatterns.size() + 1)); //Convert the negative index to a positive reverted index, that starts from the end.
            }
            index = index % (resultPatterns.size() + 1); //Prevent out of bounds
            if (index <= resultPatterns.size()) { // Shouldn't be false! Index out of bounds!
                resultPatterns.addAll(index, finalPatterns);
            }
        }
        if (!addExtraFirst.evaluate(evalContext)) {
            resultPatterns.addAll(extra(evalContext));
        }
        bannerResultMeta.setPatterns(resultPatterns);
        result.setItemMeta(resultMeta);
        return result;
    }

    @Override
    public MergeAdapter clone() {
        return new BannerMergeAdapter(this);
    }

    public static class PatternOption {

        private final PatternType type;
        private final DyeColor dyeColor;

        @JsonCreator
        public PatternOption(@JsonProperty("type") PatternType type, @JsonProperty("dyeColor") DyeColor dyeColor) {
            this.type = type;
            this.dyeColor = dyeColor;
        }

        public DyeColor getDyeColor() {
            return dyeColor;
        }

        public PatternType getType() {
            return type;
        }

    }

    public static class ElementOptionBannerPattern extends ElementOption<Pattern, PatternOption> {

        @Override
        public boolean isEqual(Pattern value, EvalContext evalContext) {
            boolean exclude = exclude().map(shouldExclude -> shouldExclude.evaluate(evalContext)).orElse(false);
            for (ValueProvider<PatternOption> valueProvider : values()) {
                PatternOption option = valueProvider.getValue(evalContext);
                if (option.type == value.getPattern() && option.dyeColor == value.getColor()) {
                    return !exclude;
                }
            }
            return exclude;
        }
    }

}
