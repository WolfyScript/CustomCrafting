package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.ClusterRecipeCreator;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class RecipeCache<R extends CustomRecipe<?>> {

    protected NamespacedKey key;
    protected boolean exactMeta;
    protected boolean hidden;

    protected RecipePriority priority;
    protected Conditions conditions;
    protected String group;
    protected Result result;

    protected RecipeCache() {
        this.key = null;
        this.exactMeta = true;
        this.hidden = false;
        this.priority = RecipePriority.NORMAL;
        this.conditions = new Conditions();
        this.group = "";
        this.result = new Result();
    }

    protected RecipeCache(R customRecipe) {
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
            CustomRecipe<?> recipe = constructRecipe();
            recipe.save(player);
            CCRegistry.RECIPES.register(recipe);
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                if (player != null) {
                    api.getChat().sendKey(player, ClusterRecipeCreator.KEY, "loading.success");
                }
                if (customCrafting.getConfigHandler().getConfig().isResetCreatorAfterSave()) {
                    guiHandler.getCustomCache().getRecipeCreatorCache().reset();
                }
            });
        } catch (IllegalArgumentException ex) {
            //Invalid recipe values!
            api.getChat().sendMessage(player, "&cError saving recipe!");
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
