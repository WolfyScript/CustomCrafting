package me.wolfyscript.customcrafting.listeners.customevents;

import me.wolfyscript.customcrafting.recipes.anvil.CustomAnvilRecipe;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class CustomPrepareAnvilEvent extends PrepareAnvilEvent implements Cancellable {

    private boolean cancelled;

    private CustomAnvilRecipe recipe;

    public CustomPrepareAnvilEvent(InventoryView transaction, ItemStack result, CustomAnvilRecipe recipe) {
        super(transaction, result);
        this.cancelled = false;
        this.recipe = recipe;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public CustomAnvilRecipe getRecipe() {
        return recipe;
    }
}
