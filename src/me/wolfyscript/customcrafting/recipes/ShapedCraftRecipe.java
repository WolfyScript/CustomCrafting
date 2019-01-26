package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.configs.custom_configs.CraftConfig;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ShapedCraftRecipe extends ShapedRecipe implements CraftingRecipe{

    boolean permission;
    boolean advancedWorkbench;

    CraftConfig config;
    String id;
    String group;
    ItemStack result;
    HashMap<Character, HashMap<ItemStack, List<String>>> ingredients;
    String[] shape;

    public ShapedCraftRecipe(CraftConfig config){
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult());
        this.result = config.getResult();
        this.id = config.getId();
        this.config = config;
        this.shape = config.getShape();
        this.ingredients = config.getIngredients();
        this.group = config.getGroup();
    }

    public void load(){
        System.out.println("SHAPE: ");
        System.out.println(shape[0]);
        System.out.println(shape[1]);
        shape(shape);
        System.out.println("ingredients:");
        for(Character itemKey : ingredients.keySet()){
            Set<ItemStack> items = ingredients.get(itemKey).keySet();
            System.out.println("    "+itemKey);
            System.out.println("        - "+items);
            List<Material> materials = new ArrayList<>();
            items.forEach(itemStack -> materials.add(itemStack.getType()));
            setIngredient(itemKey, new RecipeChoice.MaterialChoice(materials));
        }
        if(!this.group.isEmpty()){
            setGroup(group);
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
