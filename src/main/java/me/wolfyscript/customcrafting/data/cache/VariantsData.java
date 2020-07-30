package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class VariantsData {

    private int slot;
    private List<CustomItem> variants;

    public VariantsData() {
        this.variants = new ArrayList<>();
    }

    public List<CustomItem> getVariants() {
        for (int i = variants.size() - 1; i > 0; i--) {
            if (variants.get(i) == null || variants.get(i).getItemStack().getType().equals(Material.AIR)) {
                variants.remove(i);
            }
        }
        return variants;
    }

    public void setVariants(List<CustomItem> variants) {
        this.variants = variants;
    }

    public void putVariant(int variantSlot, CustomItem variant) {
        if (getVariants() != null) {
            if (getVariants().size() > variantSlot) {
                getVariants().set(variantSlot, variant);
            } else {
                getVariants().add(variant);
            }
        }
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
