package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.inventory.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VariantsData {

    private int slot;
    private List<CustomItem> variants;

    public VariantsData() {
        this.variants = new ArrayList<>();
    }

    public List<CustomItem> getVariants() {
        return variants.parallelStream().filter(customItem -> !ItemUtils.isAirOrNull(customItem)).collect(Collectors.toList());
    }

    public void setVariants(List<CustomItem> variants) {
        this.variants = variants;
    }

    public void put(int variantSlot, CustomItem variant) {
        if (variants == null) {
            setVariants(new ArrayList<>());
        }
        if (variants.size() > variantSlot) {
            variants.set(variantSlot, variant);
        } else {
            variants.add(variant);
        }
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
