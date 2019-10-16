package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;

public abstract class RecipeData {

    private boolean exactMeta;
    private RecipePriority priority;
    private Conditions conditions;

    public RecipeData(boolean exactMeta, RecipePriority priority, Conditions conditions) {
        this.exactMeta = exactMeta;
        this.priority = priority;
        this.conditions = conditions;
    }

    public RecipeData(boolean exactMeta, RecipePriority priority) {
        this.exactMeta = exactMeta;
        this.priority = priority;
        this.conditions = new Conditions();
    }

    public RecipeData(){
        this.exactMeta = true;
        this.priority = RecipePriority.NORMAL;
        this.conditions = new Conditions();
    }

    public boolean isExactMeta() {
        return exactMeta;
    }

    public void setExactMeta(boolean exactMeta) {
        this.exactMeta = exactMeta;
    }

    public RecipePriority getPriority() {
        return priority;
    }

    public void setPriority(RecipePriority priority) {
        this.priority = priority;
    }

    public Conditions getConditions() {
        return conditions;
    }

    public void setConditions(Conditions conditions) {
        this.conditions = conditions;
    }
}
