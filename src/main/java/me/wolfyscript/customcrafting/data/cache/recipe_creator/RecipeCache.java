package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.RecipeCreatorCluster;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class RecipeCache<R extends ICustomRecipe<?>> {

    protected RecipeCreatorCache creatorCache;

    protected NamespacedKey key;
    protected boolean exactMeta;
    protected boolean hidden;

    protected RecipePriority priority;
    protected Conditions conditions;
    protected String group;
    protected Result result;

    protected RecipeCache(RecipeCreatorCache creatorCache) {
        this.creatorCache = creatorCache;
        this.key = null;
        this.exactMeta = true;
        this.hidden = false;
        this.priority = RecipePriority.NORMAL;
        this.conditions = new Conditions();
        this.group = "";
        this.result = new Result();
    }

    protected RecipeCache(RecipeCreatorCache creatorCache, R customRecipe) {
        this.creatorCache = creatorCache;
        this.key = customRecipe.getNamespacedKey();
        this.exactMeta = customRecipe.isExactMeta();
        this.hidden = customRecipe.isHidden();
        this.priority = customRecipe.getPriority();
        this.conditions = customRecipe.getConditions();
        this.group = customRecipe.getGroup();
        this.result = customRecipe.getResult();
    }

    public boolean isSaved() {
        return getKey() != null;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public void setKey(NamespacedKey key) {
        this.key = key;
    }

    public boolean isExactMeta() {
        return exactMeta;
    }

    public void setExactMeta(boolean exactMeta) {
        this.exactMeta = exactMeta;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public void applyIngredientCache() {
        setIngredient(creatorCache.getIngredientCache().getSlot(), creatorCache.getIngredientCache().getIngredient());
    }

    public abstract void setIngredient(int slot, Ingredient ingredient);

    public abstract Ingredient getIngredient(int slot);

    /**
     * @return A new instance of the recipe with the values of the builder.
     */
    protected abstract R constructRecipe();

    /**
     * Creates an instance of the recipe of parent classes and applies the specific values to it.
     *
     * @param recipe The recipe to add the settings to.
     * @return The passed in recipe with added settings.
     */
    protected R create(R recipe) {
        recipe.setResult(result);
        recipe.setConditions(conditions);
        recipe.setHidden(hidden);
        recipe.setExactMeta(exactMeta);
        recipe.setPriority(priority);
        return recipe;
    }

    public boolean save(CustomCrafting customCrafting, Player player, GuiHandler<CCCache> guiHandler) {
        if (!isSaved()) {
            return false;
        }
        WolfyUtilities api = customCrafting.getApi();
        try {
            ICustomRecipe<?> recipe = constructRecipe();
            recipe.save();
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                CCRegistry.RECIPES.register(recipe);
                api.getChat().sendKey(player, RecipeCreatorCluster.KEY, "loading.success");

                if (customCrafting.getConfigHandler().getConfig().isResetCreatorAfterSave()) {
                    //TODO cache.resetRecipe();
                }
            });
        } catch (IllegalArgumentException ex) {
            //Invalid recipe values!
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        Bukkit.getScheduler().runTask(customCrafting, () -> guiHandler.openCluster("none"));
        return true;
    }
}
