package me.wolfyscript.customcrafting.listeners.customevents;

import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomPreCraftEvent extends CustomCraftEvent {

    private static final HandlerList handlers = new HandlerList();
    private final boolean isRepair;
    private List<CustomItem> result;
    private List<List<ItemStack>> ingredients;

    public CustomPreCraftEvent(boolean cancelled, boolean isRepair, CraftingRecipe<?> craftingRecipe, Inventory inventory, List<List<ItemStack>> ingredients) {
        super(craftingRecipe, inventory);
        this.isRepair = isRepair;
        this.result = craftingRecipe.getResults();
        this.ingredients = ingredients;
        setCancelled(cancelled);
    }

    public boolean isRepair() {
        return isRepair;
    }

    public List<CustomItem> getResult() {
        return result;
    }

    public void setResult(List<CustomItem> result) {
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
