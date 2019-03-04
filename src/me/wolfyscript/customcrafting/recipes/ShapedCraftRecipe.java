package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.configs.custom_configs.CraftConfig;
import me.wolfyscript.customcrafting.items.CustomItem;
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
        int index = 0;
        for (Character letter : shapeLine.toCharArray()) {
            if (!letter.equals(' ')) {
                boolean contains = false;
                for (ItemStack itemStack : ingredients.get(letter).keySet()) {
                    if (items.get(index).getAmount() >= itemStack.getAmount() && itemStack.isSimilar(items.get(index))) {
                        contains = true;
                    }
                }
                if (!contains) {
                    return false;
                }
            }
            index++;
        }
        return true;
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
