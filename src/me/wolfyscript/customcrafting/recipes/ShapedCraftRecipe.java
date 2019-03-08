package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.configs.custom_configs.CraftConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ShapedCraftRecipe extends ShapedRecipe implements CraftingRecipe {

    private boolean permission;
    private boolean advancedWorkbench;

    private CraftConfig config;
    private String id;
    private String group;
    private CustomItem result;
    private HashMap<Character, HashMap<ItemStack, List<String>>> ingredients;
    private String[] shape;
    private String shapeLine;

    public ShapedCraftRecipe(CraftConfig config) {
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult());
        this.result = config.getResult();
        this.id = config.getId();
        this.config = config;
        this.shape = config.getShape();
        this.ingredients = config.getIngredients();
        this.group = config.getGroup();
        this.permission = config.needPerm();
        this.advancedWorkbench = config.needWorkbench();
    }

    @Override
    public void load() {
        this.shape(shape);
        for (Character itemKey : ingredients.keySet()) {
            Set<ItemStack> items = ingredients.get(itemKey).keySet();
            List<Material> materials = new ArrayList<>();
            items.forEach(itemStack -> materials.add(itemStack.getType()));
            setIngredient(itemKey, new RecipeChoice.MaterialChoice(materials));
        }
        if (!this.group.isEmpty()) {
            setGroup(group);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String row : shape) {
            for (Character letter : row.toCharArray()) {
                if (!letter.equals(' ')) {
                    stringBuilder.append(letter);
                }
            }
        }
        shapeLine = stringBuilder.toString();
    }

    @Override
    public void save() {

    }

    @Override
    public boolean check(ItemStack[] matrix) {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack itemStack : matrix) {
            if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                items.add(itemStack);
            }
        }
        char[] keys = shapeLine.toCharArray();
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != ' ') {
                if(checkIngredient(keys[i], items.get(i)) == null)
                    return false;
            }
        }
        return true;
    }

    public ItemStack checkIngredient(Character key, ItemStack item) {
        for (ItemStack itemStack : ingredients.get(key).keySet()) {
            if (item.getAmount() >= itemStack.getAmount() && itemStack.isSimilar(item)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public CraftResult removeIngredients(ItemStack[] matrix, int totalAmount) {
        List<ItemStack> items = new ArrayList<>();
        ArrayList<ItemStack> results = new ArrayList<>();
        for (ItemStack itemStack : matrix) {
            if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                items.add(itemStack);
            }
        }
        char[] keys = shapeLine.toCharArray();
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != ' ') {
                ItemStack itemStack = items.get(i);
                ItemStack result = checkIngredient(keys[i], itemStack);
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
        }
        return new CraftResult(results.toArray(new ItemStack[0]), totalAmount);
    }

    @Override
    public int getAmountCraftable(ItemStack[] matrix) {
        List<ItemStack> items = new ArrayList<>();
        int totalAmount = -1;
        for (ItemStack itemStack : matrix) {
            if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                items.add(itemStack);
            }
        }
        char[] keys = shapeLine.toCharArray();
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != ' ') {
                ItemStack result = checkIngredient(keys[i], items.get(i));
                if(result != null){
                    int possible = items.get(i).getAmount() / result.getAmount();
                    if(possible < totalAmount || totalAmount == -1)
                        totalAmount = possible;
                }
            }
        }
        return totalAmount;
    }

    @Override
    public void setPermission(boolean perm) {
        this.permission = perm;
    }

    @Override
    public void setAdvancedWorkbench(boolean workbench) {
        this.advancedWorkbench = workbench;
    }

    public void setIngredients(HashMap<Character, HashMap<ItemStack, List<String>>> ingredients) {
        this.ingredients = ingredients;
    }

    public HashMap<Character, HashMap<ItemStack, List<String>>> getIngredients() {
        return ingredients;
    }

    @Override
    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public void setResult(ItemStack result) {
        this.result = new CustomItem(result);
    }

    @Override
    public CustomItem getResult() {
        return result;
    }

    public CraftConfig getConfig() {
        return config;
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

    @Override
    public String getGroup() {
        return group;
    }
}
