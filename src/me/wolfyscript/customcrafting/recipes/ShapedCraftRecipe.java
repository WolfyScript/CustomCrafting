package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.configs.custom_configs.CraftConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ShapedCraftRecipe extends ShapedRecipe implements CraftingRecipe {

    private boolean permission;
    private boolean advancedWorkbench;

    private CraftConfig config;
    private String id;
    private String group;
    private CustomItem result;
    private HashMap<Character, List<CustomItem>> ingredients;
    private String[] shape;
    private String shapeLine;
    private RecipePriority priority;

    public ShapedCraftRecipe(CraftConfig config) {
        super(new NamespacedKey(config.getFolder(), config.getName()), config.getResult());
        this.result = config.getResult();
        this.id = config.getId();
        this.config = config;
        this.shape = WolfyUtilities.formatShape(config.getShape()).toArray(new String[0]);
        this.ingredients = config.getIngredients();
        this.group = config.getGroup();
        this.permission = config.needPerm();
        this.advancedWorkbench = config.needWorkbench();
        this.priority = config.getPriority();
    }

    @Override
    public void load() {
        this.shape(shape);
        StringBuilder stringBuilder = new StringBuilder();
        for (String row : shape) {
            for (Character letter : row.toCharArray()) {
                if (!letter.equals(' ')) {
                    stringBuilder.append(letter);
                }
            }
        }
        shapeLine = stringBuilder.toString();

        for (Character itemKey : shapeLine.toCharArray()) {
            if (itemKey != ' ') {
                List<CustomItem> items = ingredients.get(itemKey);
                List<Material> materials = new ArrayList<>();
                items.forEach(itemStack -> materials.add(itemStack.getType()));
                setIngredient(itemKey, new RecipeChoice.MaterialChoice(materials));
            }
        }
        if (!this.group.isEmpty()) {
            setGroup(group);
        }
    }

    @Override
    public void save() {

    }

    @Override
    public boolean check(ItemStack[] matrix) {
        System.out.println("Recipe: "+getID());
        List<Character> allKeys = new ArrayList<>(getIngredients().keySet());
        List<Character> usedKeys = new ArrayList<>();
        for (ItemStack input : matrix) {
            if (input != null) {
                System.out.println("check: "+input);
                if (checkIngredient(input, allKeys, usedKeys) == null) {
                    return false;
                }
            }
        }
        return usedKeys.containsAll(getIngredients().keySet());
    }

    public ItemStack checkIngredient(ItemStack item, List<Character> allKeys, List<Character> usedKeys) {
        for (Character key : allKeys) {
            if (!usedKeys.contains(key)) {
                for (CustomItem itemStack : ingredients.get(key)) {
                    if (item.getAmount() >= itemStack.getAmount() && itemStack.isSimilar(item)) {
                        usedKeys.add(key);
                        return itemStack;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public CraftResult removeIngredients(ItemStack[] matrix, int totalAmount) {
        List<Character> allKeys = new ArrayList<>(getIngredients().keySet());
        List<Character> usedKeys = new ArrayList<>();
        ArrayList<ItemStack> results = new ArrayList<>();
        for (ItemStack input : matrix) {
            if (input != null) {
                ItemStack result = checkIngredient(input, allKeys, usedKeys);
                if (result != null) {
                    if (input.getMaxStackSize() > 1) {
                        int amount = input.getAmount() - result.getAmount() * totalAmount + 1;
                        input.setAmount(amount);
                    }
                    //TEST FOR BUCKETS AND OTHER ITEMS!?
                    if (input.getAmount() <= 0)
                        results.add(new ItemStack(Material.AIR));
                    else
                        results.add(input);
                } else {
                    results.add(new ItemStack(Material.AIR));
                }
            } else {
                results.add(new ItemStack(Material.AIR));
            }

        }
        return new CraftResult(results.toArray(new ItemStack[0]), totalAmount);
    }

    @Override
    public int getAmountCraftable(ItemStack[] matrix) {
        int totalAmount = -1;
        List<Character> allKeys = new ArrayList<>(getIngredients().keySet());
        List<Character> usedKeys = new ArrayList<>();
        for (ItemStack input : matrix) {
            if (input != null) {
                ItemStack result = checkIngredient(input, allKeys, usedKeys);
                if (result != null) {
                    int possible = input.getAmount() / result.getAmount();
                    if (possible < totalAmount || totalAmount == -1)
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

    @Override
    public void setIngredients(HashMap<Character, List<CustomItem>> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public HashMap<Character, List<CustomItem>> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean isSimilar(CraftingRecipe recipe) {
        if (recipe.equals(this))
            return true;

        if (recipe instanceof ShapedCraftRecipe) {
            ShapedCraftRecipe craftRecipe = (ShapedCraftRecipe) recipe;
            if (craftRecipe.getShape().length == this.getShape().length && (craftRecipe.getShape()[0].length() == this.getShape()[0].length())) {
                if (craftRecipe.getIngredients().keySet().containsAll(this.getIngredients().keySet())) {


                }
            }
        }
        return false;
    }

    @Override
    public boolean appliesToMatrix(ItemStack[] matrix) {
        List<Character> foundKeys = new ArrayList<>();
        for (ItemStack input : matrix) {
            if (input != null) {

            }
        }
        return true;
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
    public CustomItem getCustomResult() {
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
    public boolean isShapeless() {
        return true;
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
