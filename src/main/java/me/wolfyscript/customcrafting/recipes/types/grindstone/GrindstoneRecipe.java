package me.wolfyscript.customcrafting.recipes.types.grindstone;

import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItem;

import java.util.List;

public class GrindstoneRecipe implements CustomRecipe<GrindstoneConfig> {

    private boolean exactMeta, hidden;

    private GrindstoneConfig config;
    private String id;
    private String group;

    private List<CustomItem> inputTop, inputBottom, result;
    private float xp;
    private RecipePriority priority;
    private Conditions conditions;

    public GrindstoneRecipe(GrindstoneConfig config) {
        this.result = config.getResult();
        this.id = config.getId();
        this.config = config;
        this.priority = config.getPriority();
        this.exactMeta = config.isExactMeta();
        this.inputTop = config.getInputTop();
        this.inputBottom = config.getInputBottom();
        this.conditions = config.getConditions();
        this.hidden = config.isHidden();
        this.group = config.getGroup();
        this.xp = config.getXP();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return result;
    }

    @Override
    public RecipePriority getPriority() {
        return priority;
    }

    @Override
    public void load() {

    }

    @Override
    public CustomRecipe save(ConfigAPI configAPI, String namespace, String key) {
        return null;
    }

    @Override
    public CustomRecipe save(GrindstoneConfig config) {
        return null;
    }

    @Override
    public GrindstoneConfig getConfig() {
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

    public List<CustomItem> getInputTop() {
        return inputTop;
    }

    public List<CustomItem> getInputBottom() {
        return inputBottom;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    public float getXp() {
        return xp;
    }
}
