package me.wolfyscript.customcrafting.recipes.smoker;

import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.smoker.SmokerConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmokingRecipe;

public class CustomSmokerRecipe extends SmokingRecipe implements CustomRecipe {

    private RecipePriority recipePriority;
    private CustomItem result;
    private CustomItem source;
    private String id;
    private SmokerConfig config;

    public CustomSmokerRecipe(SmokerConfig config) {
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult(), new RecipeChoice.ExactChoice(config.getSource()), 0f, config.getCookingTime());
        this.id = config.getId();
        this.config = config;
        this.result = config.getResult();
        this.source = config.getSource();
        this.recipePriority = config.getPriority();
    }

    public CustomItem getSource() {
        return source;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public CustomItem getCustomResult() {
        return result;
    }

    @Override
    public RecipePriority getPriority() {
        return recipePriority;
    }

    @Override
    public void load() {

    }

    @Override
    public void save() {

    }

    @Override
    public CustomConfig getConfig() {
        return config;
    }
}
