package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.CraftConfig;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class EliteCraftingRecipe implements CraftingRecipe {

    private boolean exactMeta;
    private RecipePriority priority;
    private Conditions conditions;

    private CraftConfig config;
    private String id;
    private String group;
    private List<CustomItem> result;
    private HashMap<Character, List<CustomItem>> ingredients;
    private WolfyUtilities api;

    public EliteCraftingRecipe(CraftConfig config) {
        this.result = config.getResult();
        this.id = config.getId();
        this.config = config;
        this.ingredients = config.getIngredients();
        this.group = config.getGroup();
        this.priority = config.getPriority();
        this.api = CustomCrafting.getApi();
        this.exactMeta = config.isExactMeta();
        this.conditions = config.getConditions();
    }

    @Override
    public void save() {
    }

    @Override
    public void load() {
    }

    @Override
    public void setIngredients(HashMap<Character, List<CustomItem>> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public HashMap<Character, List<CustomItem>> getIngredients() {
        return ingredients;
    }

    @Override
    public List<CustomItem> getIngredients(int slot) {
        return getIngredients().getOrDefault(LETTERS[slot], new ArrayList<>());
    }

    @Override
    public CustomItem getIngredient(int slot) {
        List<CustomItem> list = getIngredients(slot);
        return list.size() > 0 ? list.get(0) : null;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    @Override
    public CustomItem getCustomResult() {
        return getCustomResults().get(0);
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return result;
    }

    public CraftConfig getConfig() {
        return config;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isShapeless() {
        return false;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public RecipePriority getPriority() {
        return priority;
    }

    @Override
    public boolean isExactMeta() {
        return exactMeta;
    }

    @Override
    public Conditions getConditions() {
        return conditions;
    }
}
