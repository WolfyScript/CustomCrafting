package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.configs.custom_configs.FurnaceConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.apache.commons.lang.WordUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class FurnaceCRecipe extends FurnaceRecipe implements CustomRecipe{

    private CustomItem result;
    private CustomItem source;
    private String extend;
    private String id;
    private boolean needsAdvancedFurnace;

    public FurnaceCRecipe(FurnaceConfig config){
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult(), new RecipeChoice.ExactChoice(config.getSource().clone()), config.getXP(), config.getCookingTime());
        this.id = config.getId();
        this.extend = config.getExtend();
        this.result = config.getResult();
        this.source = config.getSource();
        this.needsAdvancedFurnace = config.needsAdvancedFurnace();
    }

    public CustomItem getSource() {
        return source;
    }

    public boolean needsAdvancedFurnace() {
        return needsAdvancedFurnace;
    }

    public boolean check(ItemStack source){
        return source.getAmount() >= getSource().getAmount() && getSource().isSimilar(source);
    }

    @Override
    public ItemStack getResult() {
        ItemStack itemStack = getCustomResult().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName((itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : "§r"+WordUtils.capitalizeFully(itemStack.getType().name().replace("_", " ")) )+ WolfyUtilities.hideString(";/id:"+id));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public CustomItem getCustomResult() {
        return result;
    }

    @Override
    public String getExtends() {
        return extend;
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
