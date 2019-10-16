package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AdvancedCraftingRecipe implements CraftingRecipe {

    private boolean permission;
    private boolean advancedWorkbench;
    private boolean exactMeta;
    private RecipePriority priority;
    private Conditions conditions;

    private AdvancedCraftConfig config;
    private String id;
    private String group;
    private List<CustomItem> result;
    private HashMap<Character, List<CustomItem>> ingredients;
    private WolfyUtilities api;

    public AdvancedCraftingRecipe(AdvancedCraftConfig config) {
        this.result = config.getResult();
        this.id = config.getId();
        this.config = config;
        this.ingredients = config.getIngredients();
        this.permission = config.needsPermission();
        this.advancedWorkbench = config.needsAdvancedWorkbench();
        this.group = config.getGroup();
        this.priority = config.getPriority();
        this.api = CustomCrafting.getApi();
        this.exactMeta = config.isExactMeta();
        this.conditions = config.getConditions();
        load();
    }

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

    @Override
    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    public void setAdvancedWorkbench(boolean advancedWorkbench) {
        this.advancedWorkbench = advancedWorkbench;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public CustomItem getCustomResult() {
        return getCustomResults().get(0);
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return result;
    }

    @Override
    public String getId() {
        return id;
    }

    /*
    Return true if the recipe needs Permission!
     */
    public boolean needsPermission() {
        return permission;
    }

    /*
    Return true if the recipe can only be crafted in a advanced workbench!
     */
    public boolean needsAdvancedWorkbench() {
        return advancedWorkbench;
    }

    @Override
    public boolean isShapeless() {
        return true;
    }

    public AdvancedCraftConfig getConfig() {
        return config;
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
