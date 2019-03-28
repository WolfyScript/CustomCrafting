package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.configs.custom_configs.CraftConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.*;

public class ShapelessCraftRecipe extends ShapelessRecipe implements CraftingRecipe {

    private boolean permission;
    private boolean advancedWorkbench;

    private RecipePriority priority;

    private CraftConfig config;
    private String id;
    private CustomItem result;
    private String group;
    private HashMap<Character, List<CustomItem>> ingredients;

    public ShapelessCraftRecipe(CraftConfig config) {
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult());
        this.result = config.getResult();
        this.id = config.getId();
        this.config = config;
        this.ingredients = config.getIngredients();
        this.permission = config.needPerm();
        this.advancedWorkbench = config.needWorkbench();
        this.group = config.getGroup();
        this.priority = config.getPriority();
    }

    @Override
    public void load() {
        for (Character itemKey : getIngredients().keySet()) {
            List<CustomItem> items = getIngredients().get(itemKey);
            List<Material> materials = new ArrayList<>();
            items.forEach(itemStack -> materials.add(itemStack.getType()));
            addIngredient(new RecipeChoice.MaterialChoice(materials));
        }
    }

    @Override
    public void save() {

    }

    @Override
    public boolean check(List<List<ItemStack>> matrix) {
        List<Character> allKeys = new ArrayList<>(getIngredients().keySet());
        List<Character> usedKeys = new ArrayList<>();
        for (List<ItemStack> items : matrix) {
            for(ItemStack itemStack : items){
                if (itemStack != null) {
                    ItemStack result = checkIngredient(allKeys, usedKeys, itemStack);
                    if (result == null) {
                        return false;
                    }
                }
            }
        }
        return usedKeys.containsAll(getIngredients().keySet());
    }

    private ItemStack checkIngredient(List<Character> allKeys, List<Character> usedKeys, ItemStack item) {
        for (Character key : allKeys) {
            if (!usedKeys.contains(key)) {
                for (CustomItem ingredient : ingredients.get(key)) {
                    if (item.getAmount() >= ingredient.getAmount() && ingredient.isSimilar(item)) {
                        usedKeys.add(key);
                        return ingredient;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public CraftResult removeIngredients(List<List<ItemStack>> matrix, int totalAmount) {
        //MAYBE IMPROVEMENTS?!
        //TODO TEST IF IT WORKS!
        List<Character> allKeys = new ArrayList<>(getIngredients().keySet());
        List<Character> usedKeys = new ArrayList<>();
        ArrayList<ItemStack> results = new ArrayList<>();
        for(List<ItemStack> items : matrix){
            for(ItemStack itemStack : items){
                if (itemStack != null) {
                    ItemStack result = checkIngredient(allKeys, usedKeys, itemStack);
                    if (result != null) {
                        if (itemStack.getMaxStackSize() > 1) {
                            int amount = itemStack.getAmount() - result.getAmount() * totalAmount + 1;
                            itemStack.setAmount(amount);
                        }
                        //TEST FOR BUCKETS AND OTHER ITEMS!?
                        if (itemStack.getAmount() <= 0)
                            results.add(new ItemStack(Material.AIR));
                        else
                            results.add(itemStack);
                    } else {
                        results.add(new ItemStack(Material.AIR));
                    }
                }else{
                    results.add(new ItemStack(Material.AIR));
                }
            }
        }
        return new CraftResult(results.toArray(new ItemStack[0]), totalAmount);
    }

    @Override
    public int getAmountCraftable(List<List<ItemStack>> matrix) {
        List<Character> allKeys = new ArrayList<>(getIngredients().keySet());
        List<Character> usedKeys = new ArrayList<>();
        int totalAmount = -1;
        for(List<ItemStack> items : matrix){
            for(ItemStack itemStack : items){
                if (itemStack != null) {
                    ItemStack result = checkIngredient(allKeys, usedKeys, itemStack);
                    if (result != null) {
                        int possible = itemStack.getAmount() / result.getAmount();
                        if (possible < totalAmount || totalAmount == -1)
                            totalAmount = possible;
                    }
                }
            }
        }
        return totalAmount;
    }

    public void setIngredients(HashMap<Character, List<CustomItem>> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public HashMap<Character, List<CustomItem>> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean isSimilar(CraftingRecipe recipe) {
        return false;
    }

    @Override
    public boolean appliesToMatrix(ItemStack[] matrix) {
        return false;
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

    public CustomItem getCustomResult() {
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

    @Override
    public boolean isShapeless() {
        return true;
    }

    public CraftConfig getConfig() {
        return config;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public RecipePriority getPriority() {
        return priority;
    }
}
