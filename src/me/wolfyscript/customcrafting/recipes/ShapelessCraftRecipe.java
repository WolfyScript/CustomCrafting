package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.configs.custom_configs.CraftConfig;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ShapelessCraftRecipe extends ShapelessRecipe implements CraftingRecipe{

    boolean permission;
    boolean advancedWorkbench;

    CraftConfig config;
    String id;
    ItemStack result;
    HashMap<Character, HashMap<ItemStack, List<String>>> ingredients;

    public ShapelessCraftRecipe(CraftConfig config){
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult());
        this.result = config.getResult();
        this.id = config.getId();
        this.config = config;
        this.ingredients = config.getIngredients();
    }

    public void load(){
        for(Character itemKey : ingredients.keySet()){
            Set<ItemStack> items = ingredients.get(itemKey).keySet();
            List<Material> materials = new ArrayList<>();
            items.forEach(itemStack -> materials.add(itemStack.getType()));
            addIngredient(new RecipeChoice.MaterialChoice(materials));
        }
    }

    public String getId() {
        return id;
    }

    public ItemStack getResult() {
        return result;
    }

    public boolean needsPermission() {
        return permission;
    }

    public boolean needsAdvancedWorkbench() {
        return advancedWorkbench;
    }

    public CraftConfig getConfig() {
        return config;
    }

}
