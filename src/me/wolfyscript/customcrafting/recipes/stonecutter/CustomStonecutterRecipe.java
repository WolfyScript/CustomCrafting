package me.wolfyscript.customcrafting.recipes.stonecutter;

import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.stonecutter.StonecutterConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;

public class CustomStonecutterRecipe extends StonecuttingRecipe implements CustomRecipe {

    private boolean exactMeta;

    private StonecutterConfig config;
    private String id;
    private CustomItem result;
    private CustomItem source;
    private RecipePriority priority;

    public CustomStonecutterRecipe(StonecutterConfig config) {
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult(), config.isExactMeta() ? new RecipeChoice.ExactChoice(config.getSource()) : new RecipeChoice.MaterialChoice(config.getSource().getType()));
        this.result = config.getResult();
        this.id = config.getId();
        this.config = config;
        this.priority = config.getPriority();
        this.exactMeta = config.isExactMeta();
        this.source = config.getSource();
        setGroup(config.getGroup());
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
        //NO LOADING NEEDED!
    }

    public CustomItem getSource() {
        return source;
    }

    @Override
    public void save() {
        //NO SAVING NEEDED!
    }

    @Override
    public CustomConfig getConfig() {
        return config;
    }

    @Override
    public boolean isExactMeta() {
        return exactMeta;
    }
}
