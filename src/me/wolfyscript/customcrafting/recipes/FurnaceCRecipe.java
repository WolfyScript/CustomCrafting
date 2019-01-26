package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.configs.custom_configs.FurnaceConfig;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FurnaceCRecipe extends FurnaceRecipe {

    private ItemStack source;
    private List<String> sourceData;

    public FurnaceCRecipe(FurnaceConfig config){
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult(), config.getSource().getType(), config.getXP(), config.getCookingTime());
        this.source = config.getSource();
        this.sourceData = config.getSourceData();
    }

    public ItemStack getSource() {
        return source;
    }
}
