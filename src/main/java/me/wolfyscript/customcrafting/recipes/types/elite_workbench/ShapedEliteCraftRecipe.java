package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.recipes.types.CraftConfig;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShapedEliteCraftRecipe extends EliteCraftingRecipe {

    private String[] shape;

    public ShapedEliteCraftRecipe(CraftConfig config) {
        super(config);
        this.shape = WolfyUtilities.formatShape(config.getShape()).toArray(new String[0]);
    }

    @Override
    public void save() { }

    @Override
    public void load() { }

    @Override
    public boolean check(List<List<ItemStack>> matrix) {
        List<Character> containedKeys = new ArrayList<>();
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                if ((matrix.get(i).get(j) != null && getShape()[i].charAt(j) != ' ')) {
                    if (checkIngredient(matrix.get(i).get(j), getIngredients().get(getShape()[i].charAt(j))) == null) {
                        return false;
                    } else {
                        containedKeys.add(getShape()[i].charAt(j));
                    }
                } else if (!(matrix.get(i).get(j) == null && getShape()[i].charAt(j) == ' ')) {
                    return false;
                }
            }
        }
        return containedKeys.containsAll(getIngredients().keySet());
    }

    private CustomItem checkIngredient(ItemStack input, List<CustomItem> ingredients) {
        for (CustomItem ingredient : ingredients) {
            if (ingredient.isSimilar(input, isExactMeta())) {
                return ingredient.clone();
            }
        }
        return null;
    }

    @Override
    public List<ItemStack> removeMatrix(List<List<ItemStack>> ingredientsInput, Inventory inventory, ItemStack[] matrix, boolean small, int totalAmount) {
        List<ItemStack> replacements = new ArrayList<>();
        int startIndex = 0;
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] != null) {
                startIndex = i;
                break;
            }
        }
        if (ingredientsInput.get(0).size() > 1) {
            for (int i = 0; i < ingredientsInput.get(0).size(); i++) {
                if (ingredientsInput.get(0).get(i) != null) {
                    startIndex = startIndex - i;
                    break;
                }
            }
        }
        int r = 0; //ROW
        int c = 0; //COLUMN
        for (int x = startIndex; x < matrix.length; x++) {
            if (r < ingredientsInput.size() && c < ingredientsInput.get(r).size()) {
                if ((ingredientsInput.get(r).get(c) != null && getShape()[r].charAt(c) != ' ')) {
                    ItemStack input = ingredientsInput.get(r).get(c);
                    CustomItem item = checkIngredient(input, getIngredients().get(getShape()[r].charAt(c)));
                    if (item != null) {
                        item.consumeItem(input, totalAmount, inventory);
                    }
                }
            }
            if (r >= ingredientsInput.size()) {
                break;
            }
            c++;
            if (c >= ingredientsInput.get(r).size()) {
                c = 0;
                r++;
            }
        }
        return replacements;
    }

    @Override
    public int getAmountCraftable(List<List<ItemStack>> matrix) {
        int totalAmount = -1;
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                if ((matrix.get(i).get(j) != null && getShape()[i].charAt(j) != ' ')) {
                    ItemStack item = checkIngredient(matrix.get(i).get(j), getIngredients().get(getShape()[i].charAt(j)));
                    if (item != null) {
                        int possible = matrix.get(i).get(j).getAmount() / item.getAmount();
                        if (possible < totalAmount || totalAmount == -1)
                            totalAmount = possible;
                    }
                }
            }
        }
        return totalAmount;
    }

    @Override
    public boolean isShapeless() {
        return false;
    }

    public String[] getShape() {
        return shape;
    }

    public void setShape(String[] shape) {
        this.shape = shape;
    }
}
