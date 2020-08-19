package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.recipes.types.IShapelessCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShapelessEliteCraftRecipe extends EliteCraftingRecipe implements IShapelessCraftingRecipe {

    public ShapelessEliteCraftRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        shapeless = true;
        if(getIngredients().size() <= 9){
            requiredGridSize = 3;
        }else if (getIngredients().size() <= 16){
            requiredGridSize = 4;
        }else if (getIngredients().size() <= 25){
            requiredGridSize = 5;
        }else if (getIngredients().size() <= 36){
            requiredGridSize = 6;
        }
    }

    public ShapelessEliteCraftRecipe(){
        super();
        this.shapeless = true;
    }

    public ShapelessEliteCraftRecipe(EliteCraftingRecipe eliteCraftingRecipe){
        super(eliteCraftingRecipe);
        this.shapeless = true;
    }

    public ShapelessEliteCraftRecipe(ShapelessEliteCraftRecipe eliteCraftingRecipe){
        this((EliteCraftingRecipe) eliteCraftingRecipe);
    }

    @Override
    public CraftingData check(List<List<ItemStack>> matrix) {
        List<Character> usedKeys = new ArrayList<>();
        HashMap<Vec2d, CustomItem> foundItems = new HashMap<>();
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                ItemStack itemStack = matrix.get(i).get(j);
                if (itemStack == null) {
                    continue;
                }
                CustomItem item = checkIngredient(usedKeys, itemStack);
                if (item != null) {
                    foundItems.put(new Vec2d(j, i), item);
                }
            }
        }
        if (usedKeys.containsAll(getIngredients().keySet())) {
            return new CraftingData(this, foundItems);
        }
        return null;
    }

    public CustomItem checkIngredient(List<Character> usedKeys, ItemStack item) {
        for (Character key : getIngredients().keySet()) {
            if (!usedKeys.contains(key)) {
                for (CustomItem ingredient : getIngredients().get(key)) {
                    if (!ingredient.isSimilar(item, isExactMeta())) {
                        continue;
                    }
                    usedKeys.add(key);
                    return ingredient.clone();
                }
            }
        }
        return null;
    }

    @Override
    public ShapelessEliteCraftRecipe clone() {
        return new ShapelessEliteCraftRecipe(this);
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
    }
}
