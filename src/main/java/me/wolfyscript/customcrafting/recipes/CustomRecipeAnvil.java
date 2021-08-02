package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CustomRecipeAnvil extends CustomRecipe<CustomRecipeAnvil> {

    private boolean blockRepair;
    private boolean blockRename;
    private boolean blockEnchant;

    private Mode mode;
    private int repairCost;
    private boolean applyRepairCost;
    private RepairCostMode repairCostMode;
    private int durability;

    private Ingredient base;
    private Ingredient addition;

    public CustomRecipeAnvil(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        JsonNode modeNode = node.path("mode");
        this.durability = modeNode.path("durability").asInt(0);
        this.mode = Mode.valueOf(modeNode.get("usedMode").asText("DURABILITY"));
        this.result = ItemLoader.loadResult(modeNode.path("result"));
        readInput(node);
        this.blockEnchant = node.path("block_enchant").asBoolean(false);
        this.blockRename = node.path("block_rename").asBoolean(false);
        this.blockRepair = node.path("block_repair").asBoolean(false);
        JsonNode repairNode = node.path("repair_cost");
        this.repairCost = repairNode.path("amount").asInt(1);
        this.applyRepairCost = repairNode.path("apply_to_result").asBoolean(true);
        this.repairCostMode = RepairCostMode.valueOf(repairNode.path("mode").asText("NONE"));
    }

    public CustomRecipeAnvil(CustomRecipeAnvil recipe) {
        super(recipe);
        this.mode = recipe.getMode();
        this.base = recipe.base.clone();
        this.addition = recipe.addition.clone();
        this.durability = recipe.durability;
        this.repairCost = recipe.repairCost;
        this.applyRepairCost = recipe.applyRepairCost;
        this.repairCostMode = recipe.repairCostMode;
        this.blockEnchant = recipe.blockEnchant;
        this.blockRename = recipe.blockRename;
        this.blockRepair = recipe.blockRepair;
    }

    public CustomRecipeAnvil() {
        super();
        this.mode = Mode.RESULT;
        this.base = new Ingredient();
        this.addition = new Ingredient();
        this.durability = 0;
        this.repairCost = 1;
        this.applyRepairCost = false;
        this.repairCostMode = RepairCostMode.NONE;
        this.blockEnchant = false;
        this.blockRename = false;
        this.blockRepair = false;
    }

    private void readInput(JsonNode node) {
        if (node.has("input_left") || node.has("input_right")) {
            this.base = ItemLoader.loadIngredient(node.path("input_left"));
            this.addition = ItemLoader.loadIngredient(node.path("input_right"));
        } else {
            this.base = ItemLoader.loadIngredient(node.path("base"));
            this.addition = ItemLoader.loadIngredient(node.path("addition"));
        }
    }

    public int getDurability() {
        return durability;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public int getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(int repairCost) {
        this.repairCost = repairCost;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public Ingredient getInputLeft() {
        return getIngredient(0);
    }

    public Ingredient getInputRight() {
        return getIngredient(1);
    }

    public boolean hasInputLeft() {
        return !getInputLeft().isEmpty();
    }

    public boolean hasInputRight() {
        return !getInputRight().isEmpty();
    }

    public boolean isBlockRepair() {
        return blockRepair;
    }

    public void setBlockRepair(boolean blockRepair) {
        this.blockRepair = blockRepair;
    }

    public boolean isBlockRename() {
        return blockRename;
    }

    public void setBlockRename(boolean blockRename) {
        this.blockRename = blockRename;
    }

    public boolean isBlockEnchant() {
        return blockEnchant;
    }

    public void setBlockEnchant(boolean blockEnchant) {
        this.blockEnchant = blockEnchant;
    }

    public boolean isApplyRepairCost() {
        return applyRepairCost;
    }

    public void setApplyRepairCost(boolean applyRepairCost) {
        this.applyRepairCost = applyRepairCost;
    }

    public RepairCostMode getRepairCostMode() {
        return repairCostMode;
    }

    public void setRepairCostMode(RepairCostMode repairCostMode) {
        this.repairCostMode = repairCostMode;
    }

    @Override
    public RecipeType<CustomRecipeAnvil> getRecipeType() {
        return Types.ANVIL;
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredient) {
        if (slot == 0) {
            this.base = ingredient;
        } else {
            this.addition = ingredient;
        }
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return slot == 0 ? this.base : this.addition;
    }

    @Override
    public CustomRecipeAnvil clone() {
        return new CustomRecipeAnvil(this);
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeBooleanField("block_enchant", blockEnchant);
        gen.writeBooleanField("block_rename", blockRename);
        gen.writeBooleanField("block_repair", blockRepair);
        {
            gen.writeObjectFieldStart("repair_cost");
            gen.writeNumberField("amount", repairCost);
            gen.writeBooleanField("apply_to_result", applyRepairCost);
            gen.writeStringField("mode", repairCostMode.toString());
            gen.writeEndObject();
        }
        {
            gen.writeObjectFieldStart("mode");
            gen.writeNumberField("durability", durability);
            gen.writeStringField("usedMode", mode.toString());
            gen.writeObjectField("result", result);
            gen.writeEndObject();
        }
        gen.writeObjectField("base", this.base);
        gen.writeObjectField("addition", this.addition);
    }

    @Override
    public List<CustomItem> getRecipeBookItems() {
        return getMode().equals(CustomRecipeAnvil.Mode.RESULT) ? getResult().getChoices() : hasInputLeft() ? getInputLeft().getChoices() : getInputRight().getChoices();
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        Ingredient inputLeft = getInputLeft();
        Ingredient inputRight = getInputRight();
        ((IngredientContainerButton) cluster.getButton("ingredient.container_10")).setVariants(guiHandler, inputLeft);
        ((IngredientContainerButton) cluster.getButton("ingredient.container_13")).setVariants(guiHandler, inputRight);
        if (getMode().equals(CustomRecipeAnvil.Mode.RESULT)) {
            ((IngredientContainerButton) cluster.getButton("ingredient.container_34")).setVariants(guiHandler, getResult());
        } else if (getMode().equals(CustomRecipeAnvil.Mode.DURABILITY)) {
            ((IngredientContainerButton) cluster.getButton("ingredient.container_34")).setVariants(guiHandler, inputLeft);
        } else {
            ((IngredientContainerButton) cluster.getButton("ingredient.container_34")).setVariants(guiHandler, Collections.singletonList(new CustomItem(Material.AIR)));
        }
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        event.setButton(10, new NamespacedKey("recipe_book", "ingredient.container_10"));
        event.setButton(13, new NamespacedKey("recipe_book", "ingredient.container_13"));
        NamespacedKey glass = MainCluster.GLASS_GREEN;
        event.setButton(19, glass);
        event.setButton(22, glass);
        event.setButton(28, glass);
        event.setButton(29, glass);
        event.setButton(30, glass);
        event.setButton(32, glass);
        event.setButton(33, glass);
        if (getMode().equals(CustomRecipeAnvil.Mode.RESULT)) {
            event.setButton(31, new NamespacedKey("recipe_book", "anvil.result"));
        } else if (getMode().equals(CustomRecipeAnvil.Mode.DURABILITY)) {
            event.setButton(31, new NamespacedKey("recipe_book", "anvil.durability"));
        } else {
            event.setButton(31, new NamespacedKey("recipe_book", "anvil.none"));
        }
        event.setButton(34, new NamespacedKey("recipe_book", "ingredient.container_34"));
    }

    public enum Mode {
        DURABILITY(1), RESULT(2), NONE(0);

        private final int id;

        Mode(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Mode getById(int id) {
            for (Mode mode : Mode.values()) {
                if (mode.getId() == id)
                    return mode;
            }
            return NONE;
        }
    }

    public enum RepairCostMode {
        ADD(), MULTIPLY(), NONE();

        private static final List<RepairCostMode> modes = new ArrayList<>();

        public static List<RepairCostMode> getModes() {
            if (modes.isEmpty()) {
                modes.addAll(Arrays.asList(values()));
            }
            return modes;
        }
    }
}
