package me.wolfyscript.customcrafting.recipes.campfire;

import me.wolfyscript.customcrafting.configs.custom_configs.campfire.CampfireConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.RecipeChoice;

public class CustomCampfireRecipe extends CampfireRecipe implements CustomCookingRecipe<CampfireConfig>{

    private RecipePriority recipePriority;
    private CustomItem result;
    private CustomItem source;
    private String id;
    private CampfireConfig config;

    public CustomCampfireRecipe(CampfireConfig config) {
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
    public String getId() {
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
    public CampfireConfig getConfig() {
        return config;
    }
}
