package me.wolfyscript.customcrafting.recipes.types.brewing;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.GuiWindow;

import javax.annotation.Nullable;
import java.util.List;

public class BrewingRecipe implements CustomRecipe<BrewingConfig> {

    private boolean exactMeta, hidden;
    private String id;
    private String group;
    private RecipePriority priority;
    private Conditions conditions;
    private BrewingConfig config;

    private List<CustomItem> ingredient, allowedItems;
    private int fuelCost;
    private int brewTime;

    public BrewingRecipe(BrewingConfig config) {
        this.config = config;
        this.id = config.getId();
        this.group = config.getGroup();
        this.priority = config.getPriority();
        this.exactMeta = config.isExactMeta();
        this.conditions = config.getConditions();
        this.hidden = config.isHidden();
        this.ingredient = config.getIngredient();
        this.allowedItems = config.getAllowedItems();
        this.fuelCost = config.getFuelCost();
        this.brewTime = config.getBrewTime();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.BREWING;
    }

    @Override
    public String getGroup() {
        return group;
    }

    /*
    Always returns null because this kind of recipe doesn't contain any result items!
     */
    @Override
    public List<CustomItem> getCustomResults() {
        return null;
    }

    /*
    Always returns null because this kind of recipe doesn't contain any result items!
     */
    @Nullable
    @Override
    public CustomItem getCustomResult() {
        return null;
    }

    @Override
    public RecipePriority getPriority() {
        return priority;
    }

    @Override
    public BrewingConfig getConfig() {
        return config;
    }

    @Override
    public boolean isExactMeta() {
        return exactMeta;
    }

    @Override
    public Conditions getConditions() {
        return conditions;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    public int getFuelCost() {
        return fuelCost;
    }

    public int getBrewTime() {
        return brewTime;
    }

    public List<CustomItem> getIngredient() {
        return ingredient;
    }

    public List<CustomItem> getAllowedItems() {
        return allowedItems;
    }

    @Override
    public void renderMenu(GuiWindow guiWindow, GuiUpdateEvent event) {
        //TODO MENU
        PlayerStatistics playerStatistics = CustomCrafting.getPlayerStatistics(event.getPlayer());
        KnowledgeBook book = ((TestCache) event.getGuiHandler().getCustomCache()).getKnowledgeBook();
        event.setButton(0, "back");
        event.setButton(22, "recipe_book", "brewing.icon");
        event.setButton(29, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
        event.setButton(20, "recipe_book", "ingredient.container_25");
        event.setButton(33, "recipe_book", "ingredient.container_34");

    }

    public enum Action {

    }

}
