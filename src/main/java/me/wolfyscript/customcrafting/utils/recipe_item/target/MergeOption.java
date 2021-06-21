package me.wolfyscript.customcrafting.utils.recipe_item.target;

import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonAutoDetect;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MergeOption {

    private int[] slots;

    private List<MergeAdapter> adapters;

    protected MergeOption() {
    }

    public MergeOption(int[] slots, MergeAdapter... adapters) {
        this.slots = slots;
        this.adapters = Arrays.asList(adapters);
    }

    public MergeOption(MergeOption mergeOption) {
        this.slots = mergeOption.slots.clone();
        this.adapters = mergeOption.adapters.stream().map(MergeAdapter::clone).collect(Collectors.toList());
    }

    public ItemStack merge(CraftingData craftingData, Player player, CustomItem customResult, ItemStack result) {
        var currentItem = result;
        for (MergeAdapter adapter : adapters) {
            currentItem = adapter.mergeCrafting(craftingData, player, customResult, currentItem);
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

    public MergeOption clone() {
        return new MergeOption(this);
    }
}
