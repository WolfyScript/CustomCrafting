package me.wolfyscript.customcrafting.utils.recipe_item.target;

import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.RandomCollection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

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
    public Optional<RandomCollection<CustomItem>> check(Player player, ItemStack[] ingredients) {
        return get(player, ingredients).map(noneResultTargetResult -> noneResultTargetResult.getRandomChoices(player, ingredients));
    }

    @Override
    public Optional<Result<NoneResultTarget>> get(@Nullable Player player, ItemStack[] ingredients) {
        if (slot > -1 && slot < ingredients.length) {
            return check(ingredients[slot]);
        }
        return Optional.empty();
    }
}
