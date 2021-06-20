package me.wolfyscript.customcrafting.utils.recipe_item.target;

import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonAutoDetect;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.CustomTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.CustomTypeResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@JsonTypeResolver(CustomTypeResolver.class)
@JsonTypeIdResolver(CustomTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "key")
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

    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }

    void setSlots(int[] slots) {
        this.slots = slots;
    }

    /**
     * Called when the data is merged inside of crafting recipes.
     *
     * @param player
     * @param craftingData
     * @param result
     * @return The
     */
    public abstract ItemStack mergeCrafting(CraftingData craftingData, Player player, ItemStack result);


}
