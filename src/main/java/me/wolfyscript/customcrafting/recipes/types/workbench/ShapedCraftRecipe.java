package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.RecipePacketType;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.AbstractShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomVanillaRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.stream.Collectors;

public class ShapedCraftRecipe extends AbstractShapedCraftRecipe<ShapedCraftRecipe> implements AdvancedCraftingRecipe, ICustomVanillaRecipe<ShapedRecipe> {

    public ShapedCraftRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
    }

    public ShapedCraftRecipe() {
        super();
    }

    public ShapedCraftRecipe(ShapedCraftRecipe craftingRecipe) {
        super(craftingRecipe);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setMirrorHorizontal(boolean mirrorHorizontal) {
        this.mirrorHorizontal = mirrorHorizontal;
    }

    @Override
    public boolean mirrorHorizontal() {
        return mirrorHorizontal;
    }

    @Override
    public void setMirrorVertical(boolean mirrorVertical) {
        this.mirrorVertical = mirrorVertical;
    }

    @Override
    public boolean mirrorVertical() {
        return mirrorVertical;
    }

    @Override
    public void setMirrorRotation(boolean mirrorRotation) {
        this.mirrorRotation = mirrorRotation;
    }

    @Override
    public boolean mirrorRotation() {
        return mirrorRotation;
    }

    @Override
    public CraftingData check(List<List<ItemStack>> ingredients) {
        CraftingData craftingData = checkShape(ingredients, getShape());
        if (craftingData != null) {
            return craftingData;
        }
        if (mirrorHorizontal()) {
            craftingData = checkShape(ingredients, getShapeMirrorHorizontal());
            if (craftingData != null) {
                return craftingData;
            }
        }
        if (mirrorVertical()) {
            craftingData = checkShape(ingredients, getShapeMirrorVertical());
            if (craftingData != null) {
                return craftingData;
            }
        }
        if (mirrorHorizontal() && mirrorVertical() && mirrorRotation()) {
            craftingData = checkShape(ingredients, getShapeRotated());
            return craftingData;
        }
        return null;
    }

    private CraftingData checkShape(List<List<ItemStack>> matrix, String[] shape) {
        return checkShape(this, getIngredients(), isExactMeta(), matrix, shape);
    }

    @Override
    public RecipePacketType getPacketType() {
        return RecipePacketType.CRAFTING_SHAPED;
    }

    @Override
    public ShapedCraftRecipe clone() {
        return new ShapedCraftRecipe(this);
    }

    @Override
    public ShapedRecipe getVanillaRecipe() {
        if (!getResult().isEmpty() && getShape().length > 0) {
            var recipe = new ShapedRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getItemStack());
            recipe.shape(getShape());
            getIngredients().forEach((character, items) -> recipe.setIngredient(character, new RecipeChoice.ExactChoice(items.getChoices().parallelStream().map(CustomItem::create).distinct().collect(Collectors.toList()))));
            recipe.setGroup(getGroup());
            return recipe;
        }
        return null;
    }

    @Override
    public boolean allowVanillaRecipe() {
        return false;
    }

    @Override
    public void setAllowVanillaRecipe(boolean vanillaRecipe) {

    }
}