package me.wolfyscript.customcrafting.utils.recipe_item.target;

import me.wolfyscript.customcrafting.recipes.data.CraftingData;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.*;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeResolver;
import org.bukkit.block.Block;
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
     * Called when the data is merged inside of recipes like Furnace, Smithing Table, etc.
     * <p>
     * </p>
     *
     * @param recipeData   The {@link RecipeData}, that contains all the data of the pre-crafted recipe, like ingredients and their slots, result, and the recipe itself.
     * @param player       The player that has crafted the item. <strong>Might be null! e.g. Furnaces, and other workstations without player interaction!</strong>
     * @param block        The block that has processed the recipe. <strong>Might be null! e.g. for the 2x2 player crafting grid!</strong>
     * @param customResult The {@link CustomItem} of the crafted item.
     * @param result       The actual manipulable result {@link ItemStack}. <strong>Previous adapters might have already manipulated this item!</strong>
     * @return The manipulated {@link ItemStack} that should be passed to the next adapter or set as the end result.
     */
    public abstract ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, CustomItem customResult, ItemStack result);

    public abstract MergeAdapter clone();

    /**
     * @deprecated Replaced with {@link #merge(RecipeData, Player, Block, CustomItem, ItemStack)}! All recipe types call that one method!
     */
    @Deprecated
    public ItemStack mergeCrafting(CraftingData craftingData, Player player, CustomItem customResult, ItemStack result) {
        return result;
    }

    /**
     * @deprecated Not called! Replaced with {@link #merge(RecipeData, Player, Block, CustomItem, ItemStack)}!
     */
    @Deprecated
    public ItemStack merge(ItemStack[] ingredients, @Nullable Player player, CustomItem customResult, ItemStack result) {
        return result;
    }
}
