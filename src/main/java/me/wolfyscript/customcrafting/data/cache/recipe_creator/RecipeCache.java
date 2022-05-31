/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.ClusterRecipeCreator;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
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

    protected final CustomCrafting customCrafting;

    protected NamespacedKey key;
    protected boolean checkAllNBT;
    protected boolean hidden;
    protected boolean vanillaBook;

    protected RecipePriority priority;
    protected Conditions conditions;
    protected String group;
    protected Result result;

    protected RecipeCache(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.key = null;
        this.checkAllNBT = false;
        this.hidden = false;
        this.vanillaBook = true;
        this.priority = RecipePriority.NORMAL;
        this.conditions = new Conditions(this.customCrafting);
        this.group = "";
        this.result = new Result();
    }

    protected RecipeCache(CustomCrafting customCrafting, R customRecipe) {
        this.customCrafting = customCrafting;
        this.key = customRecipe.getNamespacedKey();
        this.checkAllNBT = customRecipe.isCheckNBT();
        this.hidden = customRecipe.isHidden();
        this.vanillaBook = customRecipe instanceof ICustomVanillaRecipe<?> vanillaRecipe && vanillaRecipe.isVisibleVanillaBook();
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

    @Deprecated
    public boolean isExactMeta() {
        return checkAllNBT;
    }

    @Deprecated
    public void setExactMeta(boolean checkNBT) {
        this.checkAllNBT = checkNBT;
    }

    public void setCheckNBT(boolean checkAllNBT) {
        this.checkAllNBT = checkAllNBT;
    }

    public boolean isCheckNBT() {
        return checkAllNBT;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setVanillaBook(boolean vanillaBook) {
        this.vanillaBook = vanillaBook;
    }

    public boolean isVanillaBook() {
        return vanillaBook;
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
        result.buildChoices();
        recipe.setResult(result);
        recipe.setConditions(conditions);
        recipe.setGroup(group);
        recipe.setHidden(hidden);
        recipe.setCheckNBT(checkAllNBT);
        recipe.setPriority(priority);
        if (recipe instanceof ICustomVanillaRecipe<?> vanillaRecipe) {
            vanillaRecipe.setVisibleVanillaBook(vanillaBook);
        }
        return recipe;
    }

    public boolean save(CustomCrafting customCrafting, Player player, GuiHandler<CCCache> guiHandler) {
        if (!isSaved()) {
            return false;
        }
        WolfyUtilities api = customCrafting.getApi();
        try {
            CustomRecipe<?> recipe = constructRecipe();
            if (recipe.save(player)) {
                customCrafting.getRegistries().getRecipes().register(recipe);
                Bukkit.getScheduler().runTask(customCrafting, () -> {
                    if (player != null) {
                        api.getChat().sendKey(player, ClusterRecipeCreator.KEY, "loading.success");
                    }
                    if (customCrafting.getConfigHandler().getConfig().isResetCreatorAfterSave()) {
                        guiHandler.getCustomCache().getRecipeCreatorCache().reset();
                    }
                });
                return true;
            }
            api.getChat().sendMessage(player, "&cError saving recipe! Failed to save recipe.");
            return false;
        } catch (IllegalArgumentException ex) {
            //Invalid recipe values!
            api.getChat().sendMessage(player, "&cError saving recipe! " + ex.getMessage());
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
