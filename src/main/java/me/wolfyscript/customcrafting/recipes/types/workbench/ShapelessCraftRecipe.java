package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.IShapelessCraftingRecipe;
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

public class ShapelessCraftRecipe extends AdvancedCraftingRecipe implements IShapelessCraftingRecipe, ICustomRecipe<AdvancedCraftingRecipe> {

    public ShapelessCraftRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.shapeless = true;
    }

    public ShapelessCraftRecipe(){
        super();
        this.shapeless = true;
    }

    public ShapelessCraftRecipe(AdvancedCraftingRecipe craftingRecipe){
        super(craftingRecipe);
        this.shapeless = true;
    }

    public ShapelessCraftRecipe(ShapelessCraftRecipe craftingRecipe){
        this((AdvancedCraftingRecipe) craftingRecipe);
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
    public ShapelessCraftRecipe clone() {
        return new ShapelessCraftRecipe(this);
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeBooleanField("shapeless", true);
    }
}
