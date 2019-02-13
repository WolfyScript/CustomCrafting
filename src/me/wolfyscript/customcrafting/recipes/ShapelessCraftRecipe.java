package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.configs.custom_configs.CraftConfig;
import net.minecraft.server.v1_13_R2.Item;
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

    private boolean permission;
    private boolean advancedWorkbench;

    private CraftConfig config;
    private String id;
    private ItemStack result;
    private String group;
    private HashMap<Character, HashMap<ItemStack, List<String>>> ingredients;

    public ShapelessCraftRecipe(CraftConfig config){
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult());
        this.result = config.getResult();
        this.id = config.getId();
        this.config = config;
        this.ingredients = config.getIngredients();
        this.permission = config.needPerm();
        this.advancedWorkbench = config.needWorkbench();
        this.group = config.getGroup();
    }

    @Override
    public void load(){
        for(Character itemKey : ingredients.keySet()){
            Set<ItemStack> items = ingredients.get(itemKey).keySet();
            List<Material> materials = new ArrayList<>();
            items.forEach(itemStack -> materials.add(itemStack.getType()));
            addIngredient(new RecipeChoice.MaterialChoice(materials));
        }
    }

    @Override
    public void save() {

    }

    @Override
    public boolean check(ItemStack[] matrix) {
        List<Character> allKeys = new ArrayList<>(ingredients.keySet());
        List<Character> usedKeys = new ArrayList<>();
        for(ItemStack itemStack : matrix){
            for(char key : allKeys){
                for (ItemStack ingredient : ingredients.get(key).keySet()){
                    if(ingredient.getType().equals(itemStack.getType())){
                        //TODO: EXTRA DATA CHECK!
                        if (ingredient.getAmount() >= itemStack.getAmount() && ingredient.isSimilar(itemStack)) {
                            usedKeys.add(key);
                            allKeys.remove(key);
                        }
                    }
                }
            }
        }
        return usedKeys.containsAll(ingredients.keySet());
    }

    public ItemStack getResult() {
        return result;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public boolean needsPermission() {
        return permission;
    }

    @Override
    public boolean needsAdvancedWorkbench() {
        return advancedWorkbench;
    }

    public CraftConfig getConfig() {
        return config;
    }

    @Override
    public String getGroup() {
        return group;
    }
}
