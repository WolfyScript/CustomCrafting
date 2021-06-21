package me.wolfyscript.customcrafting.utils.recipe_item.target;


import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonAutoDetect;
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
     * Merges nbt from crafting recipes into the result. Only for Crafting recipes.
     *
     * @param craftingData
     * @param player
     * @param result
     * @return
     */
    public ItemStack mergeCraftingData(CraftingData craftingData, Player player, CustomItem customItemResult, ItemStack result) {
        var currentItem = result;
        for (MergeOption mergeOption : mergeOptions) {
            currentItem = mergeOption.merge(craftingData, player, customItemResult, result);
        }
        return currentItem;
    }

    /**
     * Merges the nbt of other recipes, that usually have fixed slots.
     *
     * @param ingredients
     * @param player
     * @param result
     * @return
     */
    public ItemStack mergeMisc(ItemStack[] ingredients, @Nullable Player player, ItemStack result) {
        return null;
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
