package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.types.IShapedCraftingRecipe;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.RecipeUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ShapedCraftRecipe extends AdvancedCraftingRecipe implements IShapedCraftingRecipe, ICustomVanillaRecipe<ShapedRecipe> {

    private int width;
    private int height;
    private String[] shape, shapeMirrorHorizontal, shapeMirrorVertical, shapeRotated;
    private boolean mirrorHorizontal, mirrorVertical, mirrorRotation;

    public ShapedCraftRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.shapeless = false;
        constructShape();
        JsonNode mirrorNode = node.path("mirror");
        this.mirrorHorizontal = mirrorNode.path("horizontal").asBoolean(true);
        this.mirrorVertical = mirrorNode.path("vertical").asBoolean(false);
        this.mirrorRotation = mirrorNode.path("rotation").asBoolean(false);
    }

    public ShapedCraftRecipe() {
        super();
        this.shapeless = false;
        this.mirrorHorizontal = true;
        this.mirrorVertical = false;
        this.mirrorRotation = false;
    }

    public ShapedCraftRecipe(AdvancedCraftingRecipe craftingRecipe) {
        super(craftingRecipe);
        this.shapeless = false;
        constructShape();
        if (craftingRecipe instanceof ShapedCraftRecipe) {
            this.width = ((ShapedCraftRecipe) craftingRecipe).width;
            this.height = ((ShapedCraftRecipe) craftingRecipe).height;
            this.mirrorHorizontal = ((ShapedCraftRecipe) craftingRecipe).mirrorHorizontal();
            this.mirrorVertical = ((ShapedCraftRecipe) craftingRecipe).mirrorVertical();
            this.mirrorRotation = ((ShapedCraftRecipe) craftingRecipe).mirrorRotation();
        } else {
            this.mirrorHorizontal = true;
            this.mirrorVertical = false;
            this.mirrorRotation = false;
        }
    }

    @Override
    public String[] getShapeMirrorHorizontal() {
        return shapeMirrorHorizontal;
    }

    @Override
    public String[] getShapeMirrorVertical() {
        return shapeMirrorVertical;
    }

    @Override
    public String[] getShapeRotated() {
        return shapeRotated;
    }

    @Override
    public String[] getShape() {
        return shape;
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
    public CraftingData check(ItemStack[] matrix, List<List<ItemStack>> ingredients) {
        CraftingData craftingData = checkShape(matrix, ingredients, getShape());
        if (craftingData != null) {
            return craftingData;
        }
        if (mirrorHorizontal()) {
            craftingData = checkShape(matrix, ingredients, getShapeMirrorHorizontal());
            if (craftingData != null) {
                return craftingData;
            }
        }
        if (mirrorVertical()) {
            craftingData = checkShape(matrix, ingredients, getShapeMirrorVertical());
            if (craftingData != null) {
                return craftingData;
            }
        }
        if (mirrorHorizontal() && mirrorVertical() && mirrorRotation()) {
            craftingData = checkShape(matrix, ingredients, getShapeRotated());
            return craftingData;
        }
        return null;
    }

    private CraftingData checkShape(ItemStack[] ingredients, List<List<ItemStack>> matrix, String[] shape) {
        if (getIngredients() == null || getIngredients().isEmpty() || matrix.size() != shape.length || matrix.get(0).size() != shape[0].length())
            return null;
        List<Character> containedKeys = new ArrayList<>();
        Map<Vec2d, CustomItem> foundItems = new HashMap<>();
        for (int column = 0; column < matrix.size(); column++) {
            for (int row = 0; row < matrix.get(column).size(); row++) {
                ItemStack targetItem = matrix.get(column).get(row);
                char key = shape[column].charAt(row);
                if ((targetItem == null && key != ' ') || (targetItem != null && key == ' ')) return null;
                if (targetItem != null) {
                    Ingredient ingredient = getIngredients(key);
                    if (ingredient != null) {
                        Optional<CustomItem> item = ingredient.check(targetItem, isExactMeta());
                        if (item.isPresent()) {
                            foundItems.put(new Vec2d(row, column), item.get());
                            containedKeys.add(key);
                        }
                    }
                }
            }
        }
        return containedKeys.containsAll(getIngredients().keySet()) ? new CraftingData(this, foundItems, ingredients) : null;
    }

    @Override
    public ShapedCraftRecipe clone() {
        return new ShapedCraftRecipe(this);
    }

    @Override
    public void constructShape() {
        Map<Character, Ingredient> ingredients = getIngredients();
        String[] shape = new String[3];
        int index = 0;
        int row = 0;
        for (int i = 0; i < 9; i++) {
            char ingrd = LETTERS.charAt(i);
            Ingredient items = ingredients.get(ingrd);
            if (items == null || items.isEmpty()) {
                if (shape[row] != null) {
                    shape[row] = shape[row] + " ";
                } else {
                    shape[row] = " ";
                }
            } else {
                if (shape[row] != null) {
                    shape[row] = shape[row] + ingrd;
                } else {
                    shape[row] = String.valueOf(ingrd);
                }
            }
            index++;
            if ((index % 3) == 0) {
                row++;
            }
        }
        this.shape = RecipeUtil.formatShape(shape).toArray(new String[0]);
        this.shapeMirrorVertical = new String[]{"   ", "   ", "   "};
        for (int i = shape.length - 1, j = 0; i > 0; i--, j++) {
            this.shapeMirrorVertical[j] = shape[i];
        }
        this.shapeMirrorVertical = RecipeUtil.formatShape(this.shapeMirrorVertical).toArray(new String[0]);
        this.shapeMirrorHorizontal = this.shape.clone();
        for (int i = 0; i < this.shapeMirrorHorizontal.length; i++) {
            this.shapeMirrorHorizontal[i] = new StringBuilder(this.shapeMirrorHorizontal[i]).reverse().toString();
        }
        this.shapeRotated = this.shapeMirrorVertical.clone();
        for (int i = 0; i < this.shapeRotated.length; i++) {
            this.shapeRotated[i] = new StringBuilder(this.shapeRotated[i]).reverse().toString();
        }
        this.width = this.shape.length > 0 ? this.shape[0].length() : 0;
        this.height = this.shape.length;
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeObjectFieldStart("mirror");
        gen.writeBooleanField("horizontal", this.mirrorHorizontal);
        gen.writeBooleanField("vertical", this.mirrorVertical);
        gen.writeBooleanField("rotation", this.mirrorRotation);
        gen.writeEndObject();
    }

    @Override
    public void writeToBuf(MCByteBuf byteBuf) {
        super.writeToBuf(byteBuf);
        byteBuf.writeVarInt(shape.length);
        for (String s : shape) {
            byteBuf.writeUtf(s, 3);
        }
    }

    @Override
    public ShapedRecipe getVanillaRecipe() {
        if (!allowVanillaRecipe() && !getResult().isEmpty() && this.width > 0) {
            ShapedRecipe recipe = new ShapedRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getItemStack());
            recipe.shape(shape);
            getIngredients().forEach((character, items) -> recipe.setIngredient(character, new RecipeChoice.ExactChoice(items.getChoices().parallelStream().map(CustomItem::create).distinct().collect(Collectors.toList()))));
            recipe.setGroup(getGroup());
            return recipe;
        }
        return null;
    }
}