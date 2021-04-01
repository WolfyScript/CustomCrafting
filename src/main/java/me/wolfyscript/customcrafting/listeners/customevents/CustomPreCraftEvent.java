package me.wolfyscript.customcrafting.listeners.customevents;

import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.utils.recipe_item.target.SlotResultTarget;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomPreCraftEvent extends CustomCraftEvent {

    private static final HandlerList handlers = new HandlerList();
    private me.wolfyscript.customcrafting.utils.recipe_item.Result<SlotResultTarget> result;
    private List<List<ItemStack>> ingredients;

    public CustomPreCraftEvent(CraftingRecipe<?> craftingRecipe, Inventory inventory, List<List<ItemStack>> ingredients) {
        super(craftingRecipe, inventory);
        this.result = craftingRecipe.getResult();
        this.ingredients = ingredients;
    }

    public @NotNull me.wolfyscript.customcrafting.utils.recipe_item.Result<SlotResultTarget> getResult() {
        return result;
    }

    public void setResult(@NotNull me.wolfyscript.customcrafting.utils.recipe_item.Result<SlotResultTarget> result) {
        this.result = result;
    }

    public List<List<ItemStack>> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<List<ItemStack>> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String getEventName() {
        return super.getEventName();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
