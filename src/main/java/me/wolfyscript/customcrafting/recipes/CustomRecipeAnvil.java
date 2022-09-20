/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.recipes;

import com.google.common.base.Preconditions;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.gui.recipebook.ButtonContainerIngredient;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeBook;
import me.wolfyscript.customcrafting.recipes.anvil.RepairTask;
import me.wolfyscript.customcrafting.recipes.anvil.RepairTaskDefault;
import me.wolfyscript.customcrafting.recipes.anvil.RepairTaskDurability;
import me.wolfyscript.customcrafting.recipes.anvil.RepairTaskResult;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JacksonInject;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.lib.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties({"result"})
public class CustomRecipeAnvil extends CustomRecipe<CustomRecipeAnvil> {

    private boolean blockRepair;
    private boolean blockRename;
    private boolean blockEnchant;

    private RepairTask repairTask;
    private int repairCost;
    private boolean applyRepairCost;
    private RepairCostMode repairCostMode;

    private Ingredient base;
    private Ingredient addition;

    public CustomRecipeAnvil(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        // Convert old mode settings to new RepairMode
        JsonNode modeNode = node.path("mode");
        Mode mode = Mode.valueOf(modeNode.get("usedMode").asText("DURABILITY"));
        repairTask = switch (mode) {
            case NONE -> new RepairTaskDefault();
            case RESULT -> {
                var repairModeResult = new RepairTaskResult();
                repairModeResult.setResult(ItemLoader.loadResult(modeNode.path("result"), this.customCrafting));
                yield repairModeResult;
            }
            case DURABILITY -> {
                var durabilityMode = new RepairTaskDurability();
                durabilityMode.setDurability(modeNode.path("durability").asInt(0));
                yield durabilityMode;
            }
        };
        this.result = new Result();
        readInput(node);
        this.blockEnchant = node.path("block_enchant").asBoolean(false);
        this.blockRename = node.path("block_rename").asBoolean(false);
        this.blockRepair = node.path("block_repair").asBoolean(false);
        JsonNode repairNode = node.path("repair_cost");
        setRepairCost(repairNode.path("amount").asInt(1));
        this.applyRepairCost = repairNode.path("apply_to_result").asBoolean(true);
        this.repairCostMode = RepairCostMode.valueOf(repairNode.path("mode").asText("NONE"));
    }

    @JsonCreator
    public CustomRecipeAnvil(@JsonProperty("key") @JacksonInject("key") NamespacedKey key, @JacksonInject("customcrafting") CustomCrafting customCrafting) {
        super(key, customCrafting, RecipeType.ANVIL);
        this.repairTask = new RepairTaskDefault();
        this.repairCost = 1;
        this.applyRepairCost = false;
        this.repairCostMode = RepairCostMode.NONE;
        this.blockEnchant = false;
        this.blockRename = false;
        this.blockRepair = false;
    }

    @Deprecated
    public CustomRecipeAnvil(NamespacedKey key) {
        this(key, CustomCrafting.inst());
    }

    public CustomRecipeAnvil(CustomRecipeAnvil recipe) {
        super(recipe);
        this.repairTask = recipe.getRepairTask();
        this.base = recipe.base.clone();
        this.addition = recipe.addition.clone();
        this.repairCost = recipe.repairCost;
        this.applyRepairCost = recipe.applyRepairCost;
        this.repairCostMode = recipe.repairCostMode;
        this.blockEnchant = recipe.blockEnchant;
        this.blockRename = recipe.blockRename;
        this.blockRepair = recipe.blockRepair;
    }

    private void readInput(JsonNode node) {
        if (node.has("input_left") || node.has("input_right")) {
            setBase(ItemLoader.loadIngredient(node.path("input_left")));
            setAddition(ItemLoader.loadIngredient(node.path("input_right")));
        } else {
            setBase(ItemLoader.loadIngredient(node.path("base")));
            setAddition(ItemLoader.loadIngredient(node.path("addition")));
        }
    }

    public void setRepairTask(RepairTask repairTask) {
        this.repairTask = repairTask;
    }

    public RepairTask getRepairTask() {
        return repairTask;
    }

    @JsonIgnore
    @Override
    public void setResult(@NotNull Result result) {
        if (repairTask instanceof RepairTaskResult modeResult) {
            modeResult.setResult(result);
        }
    }

    @JsonIgnore
    @Override
    public Result getResult() {
        return repairTask instanceof RepairTaskResult modeResult ? modeResult.getResult() : super.getResult();
    }

    @JsonIgnore
    @Deprecated
    public int getDurability() {
        return repairTask instanceof RepairTaskDurability modeDurability ? modeDurability.getDurability() : 0;
    }

    @JsonIgnore
    @Deprecated
    public void setDurability(int durability) {
        if (repairTask instanceof RepairTaskDurability modeDurability) {
            modeDurability.setDurability(durability);
        }
    }

    @JsonIgnore
    @Deprecated
    public Mode getMode() {
        return repairTask.getMode();
    }

    @JsonIgnore
    @Deprecated
    public void setMode(Mode mode) {
        repairTask = switch (mode) {
            case NONE -> new RepairTaskDefault();
            case RESULT -> new RepairTaskResult();
            case DURABILITY -> new RepairTaskDurability();
        };
    }

    public int getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(int repairCost) {
        Preconditions.checkArgument(repairCost > 0, "Invalid repair cost! Repair cost must be bigger than 0!");
        this.repairCost = repairCost;
    }

    @JsonIgnore
    public Ingredient getInputLeft() {
        return getIngredient(0);
    }

    @JsonIgnore
    public Ingredient getInputRight() {
        return getIngredient(1);
    }

    @JsonIgnore
    public boolean hasInputLeft() {
        return !getInputLeft().isEmpty();
    }

    @JsonIgnore
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

    public void setBase(@NotNull Ingredient base) {
        Preconditions.checkArgument(!base.isEmpty() || !addition.isEmpty(), "Recipe must have at least one non-air base or addition!");
        this.base = base;
    }

    public void setAddition(@NotNull Ingredient addition) {
        Preconditions.checkArgument(!addition.isEmpty() || !base.isEmpty(), "Recipe must have at least one non-air base or addition!");
        this.addition = addition;
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
        gen.writeObjectField("mode", repairTask);
        gen.writeObjectField("base", this.base);
        gen.writeObjectField("addition", this.addition);
    }

    @JsonIgnore
    @Override
    public List<CustomItem> getRecipeBookItems() {
        return getMode().equals(CustomRecipeAnvil.Mode.RESULT) ? getResult().getChoices() : hasInputLeft() ? getInputLeft().getChoices() : getInputRight().getChoices();
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        Ingredient inputLeft = getInputLeft();
        Ingredient inputRight = getInputRight();
        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(10))).setVariants(guiHandler, inputLeft);
        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(13))).setVariants(guiHandler, inputRight);
        if (getMode().equals(CustomRecipeAnvil.Mode.RESULT)) {
            ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(34))).setVariants(guiHandler, getResult());
        } else if (getMode().equals(CustomRecipeAnvil.Mode.DURABILITY)) {
            ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(34))).setVariants(guiHandler, inputLeft);
        } else {
            ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(34))).setVariants(guiHandler, Collections.singletonList(new CustomItem(Material.AIR)));
        }
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        var cluster = guiWindow.getCluster();
        event.setButton(10, ButtonContainerIngredient.key(cluster, 10));
        event.setButton(13, ButtonContainerIngredient.key(cluster, 13));
        NamespacedKey glass = ClusterMain.GLASS_GREEN;
        event.setButton(19, glass);
        event.setButton(22, glass);
        event.setButton(28, glass);
        event.setButton(29, glass);
        event.setButton(30, glass);
        event.setButton(32, glass);
        event.setButton(33, glass);
        if (getMode().equals(CustomRecipeAnvil.Mode.RESULT)) {
            event.setButton(31, new NamespacedKey(ClusterRecipeBook.KEY, "anvil.result"));
        } else if (getMode().equals(CustomRecipeAnvil.Mode.DURABILITY)) {
            event.setButton(31, new NamespacedKey(cluster.getId(), "anvil.durability"));
        } else {
            event.setButton(31, new NamespacedKey(ClusterRecipeBook.KEY, "anvil.none"));
        }
        event.setButton(34, ButtonContainerIngredient.key(cluster, 34));
    }

    /**
     * The mode of the anvil recipe result computation.
     */
    public enum Mode {
        /**
         * Same as {@link #NONE} plus it provides an option to specify the durability that the
         * left item gets repaired with.
         */
        DURABILITY(1),
        /**
         * Always uses the specified result as the result, no matter what input.
         */
        RESULT(2),
        /**
         * Processes Rename, Repair, and Enchant Blocks of the recipe, but no further computations/settings.
         */
        NONE(0);

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

    /**
     * The mode how the repair cost of the current recipe is applied to the result item.
     */
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
