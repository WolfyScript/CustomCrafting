package me.wolfyscript.customcrafting.data.cauldron;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronPreCookEvent;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import org.bukkit.event.Listener;

public class Cauldron implements Listener {

    private CauldronRecipe recipe;
    private int cookingTime;
    private int passedTicks;
    private boolean done;
    private boolean dropItems;

    public Cauldron(CauldronPreCookEvent event){
        this.recipe = event.getRecipe();
        this.dropItems = event.dropItems();
        this.cookingTime = event.getCookingTime();
        this.passedTicks = 0;
        this.done = false;
    }

    public Cauldron(CauldronRecipe recipe, int passedTicks, int cookingTime, boolean done, boolean dropItems){
        this.recipe = recipe;
        this.passedTicks = passedTicks;
        this.done = done;
        this.dropItems = dropItems;
        this.cookingTime = cookingTime;
    }

    @Override
    public String toString() {
        return recipe.getId()+";"+passedTicks+";"+cookingTime+";"+done+";"+dropItems;
    }

    public static Cauldron fromString(String data){
        if(data == null || data.isEmpty())
            return null;
        String[] args = data.split(";");
        CauldronRecipe recipe = (CauldronRecipe) CustomCrafting.getRecipeHandler().getRecipe(args[0]);
        if(recipe == null){
            return null;
        }
        return new Cauldron(recipe, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Boolean.parseBoolean(args[3]), Boolean.parseBoolean(args[4]));
    }

    public void increasePassedTicks(){
        this.passedTicks++;
    }

    public boolean isDone() {
        return done;
    }

    public CauldronRecipe getRecipe() {
        return recipe;
    }

    public void setRecipe(CauldronRecipe recipe) {
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
}
