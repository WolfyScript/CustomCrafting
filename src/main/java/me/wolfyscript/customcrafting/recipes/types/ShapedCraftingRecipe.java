package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.recipes.crafting.CraftingData;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface ShapedCraftingRecipe<T extends CraftConfig> extends CraftingRecipe<T> {

    String[] getShapeMirrorHorizontal();

    String[] getShapeMirrorVertical();

    String[] getShapeRotated();

    String[] getShape();

    @Override
    default boolean isShapeless() {
        return false;
    }

    boolean mirrorHorizontal();

    boolean mirrorVertical();

    boolean mirrorRotate();

    @Override
    default CraftingData check(List<List<ItemStack>> matrix) {
        CraftingData craftingData = checkShape(matrix, getShape());
        if (craftingData != null) {
            return craftingData;
        }
        if (mirrorHorizontal()) {
            craftingData = checkShape(matrix, getShapeMirrorHorizontal());
            if (craftingData != null) {
                return craftingData;
            }
        }
        if (mirrorVertical()) {
            craftingData = checkShape(matrix, getShapeMirrorVertical());
            if (craftingData != null) {
                return craftingData;
            }
        }
        if (mirrorHorizontal() && mirrorVertical() && mirrorRotate()) {
            craftingData = checkShape(matrix, getShapeRotated());
            if (craftingData != null) {
                return craftingData;
            }
        }
        return null;
    }

    default CraftingData checkShape(List<List<ItemStack>> matrix, String[] shape) {
        List<Character> containedKeys = new ArrayList<>();
        HashMap<Vec2d, CustomItem> foundItems = new HashMap<>();
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                if ((matrix.get(i).get(j) != null && shape[i].charAt(j) != ' ')) {
                    CustomItem item = checkIngredient(matrix.get(i).get(j), getIngredients().get(shape[i].charAt(j)));
                    if (item == null) {
                        return null;
                    } else {
                        foundItems.put(new Vec2d(j, i), item);
                        containedKeys.add(shape[i].charAt(j));
                    }
                } else if (!(matrix.get(i).get(j) == null && shape[i].charAt(j) == ' ')) {
                    return null;
                }
            }
        }
        if (containedKeys.containsAll(getIngredients().keySet())) {
            return new CraftingData((CraftingRecipe<CraftConfig>) this, foundItems);
        }
        return null;
    }

    default CustomItem checkIngredient(ItemStack input, List<CustomItem> ingredients) {
        for (CustomItem ingredient : ingredients) {
            if (ingredient.isSimilar(input, isExactMeta())) {
                return ingredient.clone();
            }
        }
        return null;
    }
}
