package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.configs.custom_configs.FurnaceConfig;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FurnaceCRecipe extends FurnaceRecipe implements CustomRecipe{

    private ItemStack source;
    private String id;
    private boolean needsAdvancedFurnace;

    public FurnaceCRecipe(FurnaceConfig config){
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult(), config.getSource().getType(), config.getXP(), config.getCookingTime());
        this.id = config.getId();
        this.source = config.getSource();
        this.needsAdvancedFurnace = config.needsAdvancedFurnace();
    }

    public ItemStack getSource() {
        return source;
    }

    public boolean needsAdvancedFurnace() {
        return needsAdvancedFurnace;
    }

    public boolean check(ItemStack source){
        return source.getAmount() >= getSource().getAmount() && getSource().isSimilar(source);
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

}
