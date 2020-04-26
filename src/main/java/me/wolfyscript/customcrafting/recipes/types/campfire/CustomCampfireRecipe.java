package me.wolfyscript.customcrafting.recipes.types.campfire;

import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class CustomCampfireRecipe extends CampfireRecipe implements CustomCookingRecipe<CampfireConfig> {

    private boolean exactMeta, hidden;

    private RecipePriority recipePriority;
    private List<CustomItem> result;
    private List<CustomItem> source;
    private CampfireConfig config;
    private Conditions conditions;
    private NamespacedKey namespacedKey;

    public CustomCampfireRecipe(CampfireConfig config) {
        super(new org.bukkit.NamespacedKey(config.getNamespace(), config.getName()), config.getResult().get(0), new RecipeChoice.ExactChoice(new ArrayList<>(config.getSource())), config.getXP(), config.getCookingTime());
        this.namespacedKey = config.getNamespacedKey();
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
    @Deprecated
    public String getId() {
        return namespacedKey.toString();
    }

    @Override
    public me.wolfyscript.utilities.api.utils.NamespacedKey getNamespacedKey() {
        return namespacedKey;
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
