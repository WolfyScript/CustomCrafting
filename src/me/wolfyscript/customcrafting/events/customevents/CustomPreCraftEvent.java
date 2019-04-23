package me.wolfyscript.customcrafting.events.customevents;

import me.wolfyscript.customcrafting.recipes.workbench.CraftingRecipe;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;

public class CustomPreCraftEvent extends CustomCraftEvent {

    private static final HandlerList handlers = new HandlerList();
    private boolean isRepair;
    private List<List<ItemStack>> ingredients;

    public CustomPreCraftEvent(boolean isRepair, CraftingRecipe craftingRecipe, Recipe recipe, CraftingInventory craftingInventory, List<List<ItemStack>> ingredients){
        super(craftingRecipe, recipe, craftingInventory);
        this.isRepair = isRepair;
        this.ingredients = ingredients;
    }

    public boolean isRepair() {
        return isRepair;
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

    public static HandlerList getHandlerList(){
        return handlers;
    }
}
