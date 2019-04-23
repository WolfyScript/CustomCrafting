package me.wolfyscript.customcrafting.events.customevents;

import me.wolfyscript.customcrafting.recipes.workbench.CraftingRecipe;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class CustomCraftEvent extends Event implements Cancellable{

    private static final HandlerList handlers = new HandlerList();
    private ItemStack result;
    private CraftingInventory craftingInventory;
    private Recipe bukkitRecipe;
    private boolean cancelled;
    private CraftingRecipe craftingRecipe;

    public CustomCraftEvent(CraftingRecipe craftingRecipe, Recipe recipe, CraftingInventory craftingInventory){
        this.bukkitRecipe = recipe;
        this.craftingRecipe = craftingRecipe;
        this.craftingInventory = craftingInventory;
        this.result = craftingRecipe.getCustomResult();
    }

    public Recipe getBukkitRecipe() {
        return bukkitRecipe;
    }

    public CraftingInventory getCraftingInventory() {
        return craftingInventory;
    }

    public ItemStack getResult() {
        return result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public CraftingRecipe getRecipe() {
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

    public static HandlerList getHandlerList(){
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
