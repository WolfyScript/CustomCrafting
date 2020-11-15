package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.recipes.types.IShapedCraftingRecipe;
import me.wolfyscript.customcrafting.utils.geom.Vec2d;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapedCraftRecipe extends AdvancedCraftingRecipe implements IShapedCraftingRecipe {

    private String[] shape, shapeMirrorHorizontal, shapeMirrorVertical, shapeRotated;
    private boolean mirrorHorizontal, mirrorVertical, mirrorRotation;

    public ShapedCraftRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.shapeless = false;
        constructShape();
        JsonNode mirrorNode = node.path("mirror");
        this.mirrorHorizontal = mirrorNode.get("horizontal").asBoolean(true);
        this.mirrorVertical = mirrorNode.get("vertical").asBoolean(false);
        this.mirrorRotation = mirrorNode.get("rotation").asBoolean(false);
    }

    public ShapedCraftRecipe() {
        super();
        this.shapeless = false;
        this.mirrorHorizontal = true;
        this.mirrorVertical = false;
        this.mirrorRotation = false;
    }

    public ShapedCraftRecipe(ShapedCraftRecipe craftingRecipe) {
        super(craftingRecipe);
        this.shapeless = false;
        constructShape();
        this.mirrorHorizontal = craftingRecipe.mirrorHorizontal();
        this.mirrorVertical = craftingRecipe.mirrorVertical();
        this.mirrorRotation = craftingRecipe.mirrorRotation();
    }

    public ShapedCraftRecipe(AdvancedCraftingRecipe craftingRecipe) {
        super(craftingRecipe);
        this.shapeless = false;
        constructShape();
        this.mirrorHorizontal = true;
        this.mirrorVertical = false;
        this.mirrorRotation = false;
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
    public CraftingData check(List<List<ItemStack>> matrix) {
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
        if (mirrorHorizontal() && mirrorVertical() && mirrorRotation()) {
            craftingData = checkShape(matrix, getShapeRotated());
            return craftingData;
        }
        return null;

    }

    private CraftingData checkShape(List<List<ItemStack>> matrix, String[] shape) {
        List<Character> containedKeys = new ArrayList<>();
        HashMap<Vec2d, CustomItem> foundItems = new HashMap<>();
        if (getIngredients() == null || getIngredients().isEmpty()) {
            return null;
        }
        for (int c = 0; c < matrix.size(); c++) {
            for (int r = 0; r < matrix.get(c).size(); r++) {
                if ((matrix.get(c).get(r) != null && c < shape.length && r < shape[c].length() && shape[c].charAt(r) != ' ')) {
                    CustomItem item = checkIngredient(matrix.get(c).get(r), getIngredients().get(shape[c].charAt(r)));
                    if (item == null) return null;
                    foundItems.put(new Vec2d(r, c), item);
                    containedKeys.add(shape[c].charAt(r));
                } else if (!(matrix.get(c).get(r) == null && (c >= shape.length || r >= shape[c].length() || shape[c].charAt(r) == ' '))) {
                    return null;
                }
            }
        }
        return containedKeys.containsAll(getIngredients().keySet()) ? new CraftingData(this, foundItems) : null;
    }

    private CustomItem checkIngredient(ItemStack input, List<CustomItem> ingredients) {
        return ingredients.stream().filter(customItem -> customItem.isSimilar(input, isExactMeta())).findFirst().orElse(null);
    }

    @Override
    public ShapedCraftRecipe clone() {
        return new ShapedCraftRecipe(this);
    }

    @Override
    public void constructShape() {
        Map<Character, List<CustomItem>> ingredients = getIngredients();
        String[] shape = new String[3];
        int index = 0;
        int row = 0;
        for (int i = 0; i < 9; i++) {
            char ingrd = LETTERS[i];
            List<CustomItem> items = ingredients.get(ingrd);
            if (items == null || InventoryUtils.isCustomItemsListEmpty(items)) {
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
        this.shape = WolfyUtilities.formatShape(shape).toArray(new String[0]);
        this.shapeMirrorVertical = new String[]{"   ", "   ", "   "};
        int j = 0;
        for (int i = shape.length - 1; i > 0; i--) {
            this.shapeMirrorVertical[j] = shape[i];
            j++;
        }
        this.shapeMirrorVertical = WolfyUtilities.formatShape(this.shapeMirrorVertical).toArray(new String[0]);
        this.shapeMirrorHorizontal = this.shape.clone();
        for (int i = 0; i < this.shapeMirrorHorizontal.length; i++) {
            this.shapeMirrorHorizontal[i] = new StringBuilder(this.shapeMirrorHorizontal[i]).reverse().toString();
        }
        this.shapeRotated = this.shapeMirrorVertical.clone();
        for (int i = 0; i < this.shapeRotated.length; i++) {
            this.shapeRotated[i] = new StringBuilder(this.shapeRotated[i]).reverse().toString();
        }
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
}