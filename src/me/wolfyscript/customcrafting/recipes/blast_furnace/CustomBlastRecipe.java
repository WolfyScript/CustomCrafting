package me.wolfyscript.customcrafting.recipes.blast_furnace;

import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.blast_furnace.BlastingConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.furnace.FurnaceConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.RecipeChoice;

public class CustomBlastRecipe extends BlastingRecipe implements CustomCookingRecipe<BlastingConfig> {

    private RecipePriority priority;
    private CustomItem result;
    private CustomItem source;
    private String id;
    private BlastingConfig config;

    public CustomBlastRecipe(BlastingConfig config) {
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult(), new RecipeChoice.ExactChoice(config.getSource()), config.getXP(), config.getCookingTime());
        this.id = config.getId();
        this.config = config;
        this.result = config.getResult();
        this.source = config.getSource();
        this.priority = config.getPriority();
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
    public CustomItem getCustomResult() {
        return result;
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
    public BlastingConfig getConfig() {
        return config;
    }
}
