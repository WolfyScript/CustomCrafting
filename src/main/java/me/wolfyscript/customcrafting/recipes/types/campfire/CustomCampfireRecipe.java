package me.wolfyscript.customcrafting.recipes.types.campfire;

import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class CustomCampfireRecipe extends CampfireRecipe implements CustomCookingRecipe<CampfireConfig> {

    private boolean exactMeta, hidden;

    private RecipePriority recipePriority;
    private List<CustomItem> result;
    private List<CustomItem> source;
    private String id;
    private CampfireConfig config;
    private Conditions conditions;

    public CustomCampfireRecipe(CampfireConfig config) {
        super(new NamespacedKey(config.getNamespace(), config.getName()), config.getResult().get(0), new RecipeChoice.ExactChoice(new ArrayList<>(config.getSource())), config.getXP(), config.getCookingTime());
        this.id = config.getId();
        this.config = config;
        this.result = config.getResult();
        this.source = config.getSource();
        this.recipePriority = config.getPriority();
        this.exactMeta = config.isExactMeta();
        this.conditions = config.getConditions();
        this.hidden = config.isHidden();
        setGroup(config.getGroup());
    }

    public List<CustomItem> getSource() {
        return source;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return result;
    }

    @Override
    public RecipePriority getPriority() {
        return recipePriority;
    }

    @Override
    public CampfireConfig getConfig() {
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

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.CAMPFIRE;
    }
}
