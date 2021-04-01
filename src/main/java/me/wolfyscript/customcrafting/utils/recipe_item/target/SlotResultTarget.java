package me.wolfyscript.customcrafting.utils.recipe_item.target;

import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class SlotResultTarget extends ResultTarget {

    private int slot;

    public SlotResultTarget(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public Optional<Result<NoneResultTarget>> get(ItemStack[] ingredients) {
        if (ingredients != null && slot > -1 && slot < ingredients.length) {
            return check(ingredients[slot]);
        }
        return Optional.empty();
    }
}
