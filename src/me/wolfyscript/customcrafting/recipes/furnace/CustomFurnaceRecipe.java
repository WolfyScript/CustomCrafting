package me.wolfyscript.customcrafting.recipes.furnace;

import me.wolfyscript.customcrafting.configs.custom_configs.furnace.FurnaceConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class CustomFurnaceRecipe extends FurnaceRecipe implements CustomCookingRecipe<FurnaceConfig> {

    private boolean exactMeta;

    private RecipePriority priority;
    private CustomItem result;
    private CustomItem source;
    private String id;
    private FurnaceConfig config;

    public CustomFurnaceRecipe(FurnaceConfig config){
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult(), config.isExactMeta() ? new RecipeChoice.ExactChoice(config.getSource()) : new RecipeChoice.MaterialChoice(config.getSource().getType()), config.getXP(), config.getCookingTime());
        this.id = config.getId();
        this.config = config;
        this.result = config.getResult();
        this.source = config.getSource();
        this.priority = config.getPriority();
        this.exactMeta = config.isExactMeta();
        setGroup(config.getGroup());
    }

    public CustomItem getSource() {
        return source;
    }

    public boolean check(ItemStack source){
        return source.getAmount() >= getSource().getAmount() && getSource().isSimilar(source);
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
    public CustomItem getResult() {
        return result;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public FurnaceConfig getConfig() {
        return config;
    }

    @Override
    public boolean isExactMeta() {
        return exactMeta;
    }
}
