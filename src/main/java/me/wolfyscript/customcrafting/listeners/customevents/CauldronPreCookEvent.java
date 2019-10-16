package me.wolfyscript.customcrafting.listeners.customevents;

import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CauldronPreCookEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private int cookingTime;
    private boolean dropItems;
    private CauldronRecipe recipe;

    public CauldronPreCookEvent(CauldronRecipe recipe){
        this.dropItems = recipe.dropItems();
        this.recipe = recipe;
        this.cookingTime = recipe.getCookingTime();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public boolean dropItems() {
        return dropItems;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    public CauldronRecipe getRecipe() {
        return recipe;
    }

    public void setRecipe(CauldronRecipe recipe) {
        this.recipe = recipe;
    }
}
