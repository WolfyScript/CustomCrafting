package me.wolfyscript.customcrafting.recipes.builder;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.customcrafting.utils.recipe_item.target.ResultTarget;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.NamespacedKey;

public abstract class RecipeBuilder<R extends CustomRecipe<R, T>, T extends ResultTarget, B extends RecipeBuilder<R, T, B>> {

    protected final WolfyUtilities api;
    protected NamespacedKey namespacedKey;
    protected boolean exactMeta;
    protected boolean hidden;
    protected RecipePriority priority;
    protected Conditions conditions;
    protected String group;
    protected Result<T> result;

    protected RecipeBuilder() {
        this.api = CustomCrafting.inst().getApi();
        this.namespacedKey = null;
        this.exactMeta = true;
        this.hidden = false;
        this.priority = RecipePriority.NORMAL;
        this.conditions = new Conditions();
        this.group = "";
        this.result = new Result<>();
    }

    protected RecipeBuilder(R recipe) {
        this.api = CustomCrafting.inst().getApi();
        this.namespacedKey = recipe.getNamespacedKey();
        this.exactMeta = recipe.isExactMeta();
        this.hidden = recipe.isHidden();
        this.priority = recipe.getPriority();
        this.conditions = recipe.getConditions();
        this.result = recipe.getResult().clone();
        this.group = "";
    }

    /**
     * Constructs the recipe with the configured settings.
     *
     * @return The created recipe
     */
    public abstract R create();

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public RecipeBuilder<R, T, B> setNamespacedKey(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
        return this;
    }

    public boolean isExactMeta() {
        return exactMeta;
    }

    public RecipeBuilder<R, T, B> setExactMeta(boolean exactMeta) {
        this.exactMeta = exactMeta;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    public RecipeBuilder<R, T, B> setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public RecipePriority getPriority() {
        return priority;
    }

    public RecipeBuilder<R, T, B> setPriority(RecipePriority priority) {
        this.priority = priority;
        return this;
    }

    public Conditions getConditions() {
        return conditions;
    }

    public RecipeBuilder<R, T, B> setConditions(Conditions conditions) {
        this.conditions = conditions;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public RecipeBuilder<R, T, B> setGroup(String group) {
        this.group = group;
        return this;
    }

    public Result<T> getResult() {
        return result;
    }

    public RecipeBuilder<R, T, B> setResult(Result<T> result) {
        this.result = result;
        return this;
    }
}
