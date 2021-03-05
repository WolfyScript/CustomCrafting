package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.recipes.types.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.types.IShapelessCraftingRecipe;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ShapelessCraftRecipe extends AdvancedCraftingRecipe implements IShapelessCraftingRecipe, ICustomVanillaRecipe<ShapelessRecipe> {

    public ShapelessCraftRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.shapeless = true;
    }

    public ShapelessCraftRecipe() {
        super();
        this.shapeless = true;
    }

    public ShapelessCraftRecipe(AdvancedCraftingRecipe craftingRecipe) {
        super(craftingRecipe);
        this.shapeless = true;
    }

    @Override
    public CraftingData check(List<List<ItemStack>> matrix) {
        List<Character> usedKeys = new ArrayList<>();
        HashMap<Vec2d, CustomItem> foundItems = new HashMap<>();
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                CustomItem item = checkIngredient(usedKeys, matrix.get(i).get(j));
                if (item != null) {
                    foundItems.put(new Vec2d(j, i), item);
                }
            }
        }
        return usedKeys.containsAll(getIngredients().keySet()) ? new CraftingData(this, foundItems) : null;
    }

    public CustomItem checkIngredient(List<Character> usedKeys, ItemStack item) {
        if (item == null) return null;
        for (Character key : getIngredients().keySet()) {
            if (usedKeys.contains(key)) continue;
            for (CustomItem ingredient : getIngredients().get(key)) {
                if (ingredient.isSimilar(item, isExactMeta())) {
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

    @Override
    public ShapelessRecipe getVanillaRecipe() {
        if (!allowVanillaRecipe()) {
            if (!ItemUtils.isAirOrNull(getResult())) {
                ShapelessRecipe shapelessRecipe = new ShapelessRecipe(getNamespacedKey().toBukkit(), getResult().create());
                for (List<CustomItem> value : getIngredients().values()) {
                    shapelessRecipe.addIngredient(new RecipeChoice.ExactChoice(value.parallelStream().map(CustomItem::create).distinct().collect(Collectors.toList())));
                }
                shapelessRecipe.setGroup(getGroup());
                return shapelessRecipe;
            }
        }
        return null;
    }
}
