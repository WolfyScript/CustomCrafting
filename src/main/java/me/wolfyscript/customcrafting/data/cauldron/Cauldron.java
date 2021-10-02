package me.wolfyscript.customcrafting.data.cauldron;

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronPreCookEvent;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCauldron;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.event.Listener;

public class Cauldron implements Listener {

    private CustomRecipeCauldron recipe;
    private final int cookingTime;
    private int passedTicks;
    private boolean done;
    private boolean forRemoval;
    private final boolean dropItems;
    private CustomItem result;

    public Cauldron(CauldronPreCookEvent event) {
        this.recipe = event.getRecipe();
        this.result = recipe.getResult().getItem(event.getCauldron()).orElse(new CustomItem(Material.AIR));
        this.recipe.getResult().removeCachedItem(event.getCauldron());

        this.dropItems = event.dropItems();
        this.cookingTime = event.getCookingTime();
        this.passedTicks = 0;
        this.done = false;
        this.forRemoval = false;
    }

    public Cauldron(CustomRecipeCauldron recipe, int passedTicks, int cookingTime, boolean done, boolean dropItems) {
        this.recipe = recipe;
        this.passedTicks = passedTicks;
        this.done = done;
        this.dropItems = dropItems;
        this.cookingTime = cookingTime;
    }

    public static Cauldron fromString(String data) {
        if (data == null || data.isEmpty())
            return null;
        String[] args = data.split(";");
        CustomRecipeCauldron recipe = (CustomRecipeCauldron) CCRegistry.RECIPES.get(NamespacedKey.of(args[0]));
        if (recipe == null) {
            return null;
        }
        return new Cauldron(recipe, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Boolean.parseBoolean(args[3]), Boolean.parseBoolean(args[4]));
    }

    @Override
    public String toString() {
        return String.format("%s;%s;%s;%s;%s", recipe.getNamespacedKey(), passedTicks, cookingTime, done, dropItems);
    }

    public void increasePassedTicks() {
        this.passedTicks++;
    }

    public void decreasePassedTicks(int amount) {
        if (this.passedTicks >= amount) {
            this.passedTicks -= amount;
        } else {
            this.passedTicks = 0;
        }
    }

    public boolean isDone() {
        return done;
    }

    public CustomRecipeCauldron getRecipe() {
        return recipe;
    }

    public void setRecipe(CustomRecipeCauldron recipe) {
        this.recipe = recipe;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setPassedTicks(int passedTicks) {
        this.passedTicks = passedTicks;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public int getPassedTicks() {
        return passedTicks;
    }

    public boolean dropItems() {
        return dropItems;
    }

    public CustomItem getResult() {
        return result;
    }

    public void setResult(CustomItem result) {
        this.result = result;
    }

    public boolean isForRemoval() {
        return forRemoval;
    }

    public void setForRemoval(boolean forRemoval) {
        this.forRemoval = forRemoval;
    }
}
