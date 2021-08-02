package me.wolfyscript.customcrafting.listeners.customevents;

import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.utils.CraftManager;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomPreCraftEvent extends CustomCraftEvent {

    private static final HandlerList handlers = new HandlerList();
    private me.wolfyscript.customcrafting.utils.recipe_item.Result result;
    private List<List<ItemStack>> ingredients;
    private final CraftManager.MatrixData matrix;

    public CustomPreCraftEvent(CraftingRecipe<?, ?> craftingRecipe, Inventory inventory, CraftManager.MatrixData matrix) {
        super(craftingRecipe, inventory);
        this.result = craftingRecipe.getResult();
        this.ingredients = new ArrayList<>();
        this.matrix = matrix;
    }

    public @NotNull me.wolfyscript.customcrafting.utils.recipe_item.Result getResult() {
        return result;
    }

    public void setResult(@NotNull me.wolfyscript.customcrafting.utils.recipe_item.Result result) {
        this.result = result;
    }

    @Deprecated
    public List<List<ItemStack>> getIngredients() {
        return ingredients;
    }

    @Deprecated
    public void setIngredients(List<List<ItemStack>> ingredients) {
        this.ingredients = ingredients;
    }

    public CraftManager.MatrixData getMatrixData() {
        return matrix;
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
