package me.wolfyscript.customcrafting.recipes;

import com.mysql.fabric.xmlrpc.base.Array;
import me.wolfyscript.customcrafting.configs.custom_configs.CraftConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import net.minecraft.server.v1_13_R2.Item;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;

public class ShapelessCraftRecipe extends ShapelessRecipe implements CraftingRecipe{

    private boolean permission;
    private boolean advancedWorkbench;

    private CraftConfig config;
    private String id;
    private CustomItem result;
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
        for(Character itemKey : getIngredients().keySet()){
            Set<ItemStack> items = getIngredients().get(itemKey).keySet();
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
        List<Character> allKeys = new ArrayList<>(getIngredients().keySet());
        List<Character> usedKeys = new ArrayList<>();
        for(ItemStack itemStack : matrix){
            ItemStack result = checkIngredient(allKeys, usedKeys, itemStack);
            if(result == null){
                return false;
            }
        }
        return usedKeys.containsAll(getIngredients().keySet());
    }

    @Override
    public CraftResult removeIngredients(ItemStack[] matrix, int totalAmount) {
        //MAYBE IMPROVEMENTS?!
        List<Character> allKeys = new ArrayList<>(getIngredients().keySet());
        List<Character> usedKeys = new ArrayList<>();
        ArrayList<ItemStack> results = new ArrayList<>();
        for(ItemStack itemStack : matrix){
            ItemStack result = checkIngredient(allKeys, usedKeys, itemStack);
            if(result != null){
                if(itemStack.getMaxStackSize() > 1){
                    int amount = itemStack.getAmount() - result.getAmount()*totalAmount + 1;
                    itemStack.setAmount(amount);
                }
                //TEST FOR BUCKETS AND OTHER ITEMS!?
                if(itemStack.getAmount() <= 0)
                    results.add(new ItemStack(Material.AIR));
                else
                    results.add(itemStack);
            }else{
                results.add(new ItemStack(Material.AIR));
            }
        }
        return new CraftResult(results.toArray(new ItemStack[0]), totalAmount);
    }

    @Override
    public int getAmountCraftable(ItemStack[] matrix) {
        List<Character> allKeys = new ArrayList<>(getIngredients().keySet());
        List<Character> usedKeys = new ArrayList<>();
        int totalAmount = -1;
        for(ItemStack itemStack : matrix){
            ItemStack result = checkIngredient(allKeys, usedKeys, itemStack);
            if(result != null){
                int possible = itemStack.getAmount() / result.getAmount();
                if(possible < totalAmount || totalAmount == -1)
                    totalAmount = possible;
            }
        }
        return totalAmount;
    }

    private ItemStack checkIngredient(List<Character> allKeys, List<Character> usedKeys, ItemStack itemStack){
        if(itemStack != null && !itemStack.getType().equals(Material.AIR)){
            for(char key : allKeys){
                if(!usedKeys.contains(key)){
                    for (ItemStack ingredient : getIngredients().get((char)key).keySet()){
                        if(ingredient.getType().equals(itemStack.getType())){
                            if (itemStack.getAmount() >= ingredient.getAmount() && ingredient.isSimilar(itemStack)) {
                                usedKeys.add(key);
                                //System.out.println("ingr. : "+ingredient.getAmount());
                                return new ItemStack(ingredient);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public void setIngredients(HashMap<Character, HashMap<ItemStack, List<String>>> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public HashMap<Character, HashMap<ItemStack, List<String>>> getIngredients() {
        return ingredients;
    }

    public void setResult(ItemStack result) {
        this.result = new CustomItem(result);
    }

    @Override
    public void setAdvancedWorkbench(boolean advancedWorkbench) {
        this.advancedWorkbench = advancedWorkbench;
    }

    @Override
    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    @Override
    public void setGroup(String group) {
        this.group = group;
    }

    public CustomItem getResult() {
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
