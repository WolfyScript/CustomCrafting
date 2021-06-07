package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.RecipePacketType;
import me.wolfyscript.customcrafting.recipes.types.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.types.IShapelessCraftingRecipe;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public CraftingData check(ItemStack[] matrix, List<List<ItemStack>> ingredients) {
        List<Character> usedKeys = new ArrayList<>();
        Map<Vec2d, CustomItem> foundItems = new HashMap<>();
        for (int i = 0; i < ingredients.size(); i++) {
            for (int j = 0; j < ingredients.get(i).size(); j++) {
                CustomItem item = checkIngredient(getIngredients(), usedKeys, ingredients.get(i).get(j), isExactMeta());
                if (item != null) {
                    foundItems.put(new Vec2d(j, i), item);
                }
            }
        }
        return usedKeys.containsAll(getIngredients().keySet()) ? new CraftingData(this, foundItems, matrix) : null;
    }

    @Override
    public RecipePacketType getPacketType() {
        return RecipePacketType.CRAFTING_SHAPELESS;
    }

    @Override
    public ShapelessCraftRecipe clone() {
        return new ShapelessCraftRecipe(this);
    }

    @Override
    public ShapelessRecipe getVanillaRecipe() {
        if (!allowVanillaRecipe() && !getResult().isEmpty()) {
            ShapelessRecipe shapelessRecipe = new ShapelessRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getItemStack());
            for (Ingredient value : getIngredients().values()) {
                shapelessRecipe.addIngredient(new RecipeChoice.ExactChoice(value.getChoices().parallelStream().map(CustomItem::create).distinct().collect(Collectors.toList())));
            }
            shapelessRecipe.setGroup(getGroup());
            return shapelessRecipe;
        }
        return null;
    }
}
