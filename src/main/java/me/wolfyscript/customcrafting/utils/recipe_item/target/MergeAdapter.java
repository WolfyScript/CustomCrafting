package me.wolfyscript.customcrafting.utils.recipe_item.target;

import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.*;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "key")
@JsonPropertyOrder("key")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class MergeAdapter implements Keyed {

    @JsonProperty("key")
    protected NamespacedKey key;

    /**
     * These are the slots selected by the merge option.
     * For crafting recipes they indicate the Ingredient id/s.
     * For any other recipe with fixed slots they indicate the inventory slot.
     */
    @JsonIgnore
    protected int[] slots;

    protected MergeAdapter(NamespacedKey key) {
        this.key = key;
    }

    protected MergeAdapter(MergeAdapter adapter) {
        this.key = new NamespacedKey(adapter.key.getNamespace(), adapter.key.getKey());
        this.slots = adapter.slots.clone();
    }

    @JsonIgnore
    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }

    void setSlots(int[] slots) {
        this.slots = slots;
    }

    /**
     * Called when the data is merged inside of crafting recipes.
     * <br>
     * Crafting Recipes have no reliable positions of ingredients as they can be flipped or have no order at all!
     * Therefore this separate method is used with additional data of Ingredient positions and content.
     *
     * @param player       The player that crafted the item.
     * @param craftingData The {@link CraftingData} containing all the info of the grid state.
     * @param customResult The {@link CustomItem} of the crafted item.
     * @param result       The actual manipulable result ItemStack. (Previous adapters might have already manipulated this item!)
     * @return The manipulated {@link ItemStack} that should be passed to the next adapter or set as the end result.
     */
    public abstract ItemStack mergeCrafting(CraftingData craftingData, Player player, CustomItem customResult, ItemStack result);

    /**
     * Called when the data is merged inside of recipes with fixed slots like Furnace, Smithing Table, etc.
     * <p><strong>
     * Not called for crafting recipes! Use {@link #mergeCrafting(CraftingData, Player, CustomItem, ItemStack)} for merging of Crafting Recipe items!
     * </strong>
     * </p>
     *
     * @param player       The player that has crafted the item. <strong>Might be null in case of the Furnace!</strong>
     * @param customResult The {@link CustomItem} of the crafted item.
     * @param result       The actual manipulable result {@link ItemStack}. (Previous adapters might have already manipulated this item!)
     * @return The manipulated {@link ItemStack} that should be passed to the next adapter or set as the end result.
     */
    public abstract ItemStack merge(ItemStack[] ingredients, @Nullable Player player, CustomItem customResult, ItemStack result);

    public abstract MergeAdapter clone();
}
