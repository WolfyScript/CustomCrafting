package me.wolfyscript.customcrafting.events.customevents;

import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Recipe;

public class CustomPreCraftEvent extends CustomCraftEvent {

    private static final HandlerList handlers = new HandlerList();
    private boolean isRepair;

    public CustomPreCraftEvent(boolean isRepair, CraftingRecipe craftingRecipe, Recipe recipe, CraftingInventory craftingInventory){
        super(craftingRecipe, recipe, craftingInventory);
        this.isRepair = isRepair;
    }

    public boolean isRepair() {
        return isRepair;
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
