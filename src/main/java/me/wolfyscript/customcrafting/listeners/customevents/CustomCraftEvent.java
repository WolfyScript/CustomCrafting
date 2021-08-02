package me.wolfyscript.customcrafting.listeners.customevents;

import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

public class CustomCraftEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Inventory inventory;
    private boolean cancelled;
    private final CraftingRecipe<?, ?> craftingRecipe;

    public CustomCraftEvent(CraftingRecipe<?, ?> craftingRecipe, Inventory inventory) {
        this.craftingRecipe = craftingRecipe;
        this.inventory = inventory;
        this.cancelled = false;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public CraftingRecipe<?, ?> getRecipe() {
        return craftingRecipe;
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
