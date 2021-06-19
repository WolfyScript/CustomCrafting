package me.wolfyscript.customcrafting.utils.recipe_item.target;


import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class ResultTarget {

    private final List<MergeOption> mergeOptions;

    protected ResultTarget() {
        this.mergeOptions = new ArrayList<>();
    }

    protected ResultTarget(ResultTarget target) {
        this.mergeOptions = target.mergeOptions; //TODO: Clone correctly!
    }

    /**
     * Merges nbt from crafting recipes into the result. Only for Crafting recipes.
     *
     * @param craftingData
     * @param player
     * @param result
     * @return
     */
    public ItemStack mergeCraftingData(CraftingData craftingData, Player player, CustomItem result) {
        return null;
    }

    /**
     * Merges the nbt of other recipes, that usually have fixed slots.
     *
     * @param ingredients
     * @param player
     * @param result
     * @return
     */
    public ItemStack mergeMisc(ItemStack[] ingredients, @Nullable Player player, CustomItem result) {
        return null;
    }


}
