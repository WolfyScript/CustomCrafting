package me.wolfyscript.customcrafting.utils.recipe_item.target;

import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonAutoDetect;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MergeOption {

    private int[] slots;

    private List<MergeAdapter> adapters;

    public ItemStack merge(CraftingData craftingData, Player player, ItemStack result) {
        var currentItem = result;
        for (MergeAdapter adapter : adapters) {
            currentItem = adapter.mergeCrafting(craftingData, player, currentItem);
        }
        return currentItem;
    }

    public int[] getSlots() {
        return slots;
    }

    public void setSlots(int[] slots) {
        this.slots = slots;
    }

    @JsonProperty("adapters")
    public void setAdapters(List<MergeAdapter> adapters) {
        this.adapters = adapters;
        this.adapters.forEach(adapter -> adapter.setSlots(slots));
    }
}
