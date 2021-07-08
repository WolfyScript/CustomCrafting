package me.wolfyscript.customcrafting.utils.recipe_item.target;


import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ResultTarget {

    private final List<MergeOption> mergeOptions;

    protected ResultTarget() {
        this.mergeOptions = new ArrayList<>();
    }

    protected ResultTarget(ResultTarget target) {
        this.mergeOptions = target.mergeOptions.stream().map(MergeOption::clone).collect(Collectors.toList());
    }

    /**
     * Merges the nbt of other recipes, that usually have fixed slots.
     *
     * @param ingredients
     * @param player
     * @param result
     * @return
     */
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, CustomItem customItemResult, ItemStack result) {
        var currentItem = result;
        for (MergeOption mergeOption : mergeOptions) {
            currentItem = mergeOption.merge(recipeData, player, block, customItemResult, result);
        }
        return currentItem;
    }

    /**
     * Adds the specified {@link MergeOption} to the Target.
     *
     * @param option The MergeOption to add.
     * @return The {@link ResultTarget} for chaining this method.
     */
    public ResultTarget addOption(MergeOption option) {
        mergeOptions.add(option);
        return this;
    }


}
