package me.wolfyscript.customcrafting.recipes.smoker;

import me.wolfyscript.customcrafting.configs.custom_configs.smoker.SmokerConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmokingRecipe;

public class CustomSmokerRecipe extends SmokingRecipe implements CustomCookingRecipe<SmokerConfig> {

    private RecipePriority priority;
    private CustomItem result;
    private CustomItem source;
    private String id;
    private SmokerConfig config;

    public CustomSmokerRecipe(SmokerConfig config) {
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult(), new RecipeChoice.ExactChoice(config.getSource()), config.getXP(), config.getCookingTime());
        this.id = config.getId();
        this.config = config;
        this.result = config.getResult();
        this.source = config.getSource();
        this.priority = config.getPriority();
    }

    @Override
    public CustomItem getSource() {
        return source;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public CustomItem getCustomResult() {
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
    public void save() {

    }

    @Override
    public SmokerConfig getConfig() {
        return config;
    }
}
