package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.furnace.FurnaceConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class FurnaceCRecipe extends FurnaceRecipe implements CustomRecipe{

    private RecipePriority recipePriority;
    private CustomItem result;
    private CustomItem source;
    private float xp;
    private String id;
    private FurnaceConfig config;

    public FurnaceCRecipe(FurnaceConfig config){
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult(), new RecipeChoice.ExactChoice(config.getSource()), 0f, config.getCookingTime());
        this.id = config.getId();
        this.config = config;
        this.result = config.getResult();
        this.source = config.getSource();
        this.xp = config.getXP();
        this.recipePriority = config.getPriority();
    }



    public CustomItem getSource() {
        return source;
    }

    public float getXp(){
        return xp;
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
        return recipePriority;
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public void load() { }

    @Override
    public void save() {

    }

    @Override
    public CustomConfig getConfig() {
        return config;
    }

}
