package me.wolfyscript.customcrafting.events;

import me.wolfyscript.customcrafting.recipes.ShapelessCraftRecipe;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class CustomCraftEvent extends Event implements Cancellable{

    private static final HandlerList handlers = new HandlerList();
    private ItemStack outPut;
    private CraftingInventory craftingInventory;
    private Recipe recipe;
    private boolean isRepair;
    private boolean cancelled;
    private ShapelessCraftRecipe shapelessCraftRecipe;

    public CustomCraftEvent(boolean isRepair, ShapelessCraftRecipe shapelessCraftRecipe, Recipe recipe, CraftingInventory craftingInventory){
        this.shapelessCraftRecipe = shapelessCraftRecipe;
        this.recipe = recipe;
        this.craftingInventory = craftingInventory;
        this.isRepair = isRepair;
        this.outPut = recipe.getResult();
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public CraftingInventory getCraftingInventory() {
        return craftingInventory;
    }

    public ItemStack getOutPut() {
        return outPut;
    }

    public void setOutPut(ItemStack outPut) {
        this.outPut = outPut;
    }

    public boolean isRepair() {
        return isRepair;
    }

    public ShapelessCraftRecipe getCustomRecipe(){
        return this.shapelessCraftRecipe;
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
