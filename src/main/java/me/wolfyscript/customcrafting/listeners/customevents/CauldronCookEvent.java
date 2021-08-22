package me.wolfyscript.customcrafting.listeners.customevents;

import me.wolfyscript.customcrafting.data.cauldron.Cauldron;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCauldron;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CauldronCookEvent extends Event implements Cancellable {

    private boolean cancelled;
    private static final HandlerList handlers = new HandlerList();
    private boolean dropItems;
    private CustomItem result;
    private CustomRecipeCauldron recipe;

    public CauldronCookEvent(Cauldron cauldron) {
        this.recipe = cauldron.getRecipe();
        this.result = cauldron.getResult();
        this.dropItems = cauldron.dropItems();
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
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean dropItems() {
        return dropItems;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    public CustomItem getResult() {
        return result;
    }

    public void setResult(CustomItem result) {
        this.result = result;
    }

    public CustomRecipeCauldron getRecipe() {
        return recipe;
    }

    public void setRecipe(CustomRecipeCauldron recipe) {
        this.recipe = recipe;
    }
}
