package me.wolfyscript.customcrafting.utils.recipe_item.target;

import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.RandomCollection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FixedResultTarget extends ResultTarget {

    @Override
    public Optional<RandomCollection<CustomItem>> check(Player player, ItemStack[] ingredients) {
        return get(player, ingredients).map(result -> result.getRandomChoices(player, ingredients));
    }

    @Override
    public Optional<Result<NoneResultTarget>> get(@Nullable Player player, ItemStack[] ingredients) {
        return check(ingredients[0]);
    }
}
