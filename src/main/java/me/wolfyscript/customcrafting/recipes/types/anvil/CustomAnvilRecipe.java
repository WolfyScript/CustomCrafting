package me.wolfyscript.customcrafting.recipes.types.anvil;

import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import org.bukkit.Material;

import java.util.*;

public class CustomAnvilRecipe implements CustomRecipe<AnvilConfig> {

    private boolean exactMeta, hidden;
    private boolean blockRepair;
    private boolean blockRename;
    private boolean blockEnchant;
    private RecipePriority priority;

    private Mode mode;
    private int repairCost;
    private boolean applyRepairCost;
    private RepairCostMode repairCostMode;
    private int durability;
    private Conditions conditions;

    private HashMap<Integer, List<CustomItem>> ingredients;

    private NamespacedKey namespacedKey;
    private AnvilConfig config;

    public CustomAnvilRecipe(AnvilConfig config) {
        this.config = config;
        this.namespacedKey = config.getNamespacedKey();
        this.ingredients = new HashMap<>();
        this.exactMeta = config.isExactMeta();
        this.blockEnchant = config.isBlockEnchant();
        this.blockRename = config.isBlockRename();
        this.blockRepair = config.isBlockRepairing();
        this.priority = config.getPriority();
        this.repairCost = config.getRepairCost();
        this.mode = config.getMode();
        this.applyRepairCost = config.isApplyRepairCost();
        this.repairCostMode = config.getRepairCostMode();
        this.durability = 0;
        ingredients.put(0, config.getInputLeft());
        ingredients.put(1, config.getInputRight());
        if (config.getMode().equals(Mode.DURABILITY)) {
            this.durability = config.getDurability();
        } else if (config.getMode().equals(Mode.RESULT)) {
            ingredients.put(2, config.getResult());
        }
        this.conditions = config.getConditions();
        this.hidden = config.isHidden();
    }

    public CustomAnvilRecipe() {
        this.config = null;
        this.namespacedKey = null;
        this.exactMeta = true;
        this.ingredients = new HashMap<>();
        this.ingredients.put(2, new ArrayList<>(Collections.singleton(new CustomItem(Material.AIR))));
        this.mode = Mode.RESULT;
        this.durability = 0;
        this.repairCost = 1;
        this.applyRepairCost = false;
        this.repairCostMode = RepairCostMode.NONE;
        this.blockEnchant = false;
        this.blockRename = false;
        this.blockRepair = false;
        this.priority = RecipePriority.NORMAL;
        this.conditions = new Conditions();
    }

    @Override
    @Deprecated
    public String getId() {
        return namespacedKey.toString();
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public int getDurability() {
        return durability;
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return ingredients.getOrDefault(2, Collections.singletonList(new CustomItem(Material.STONE)));
    }

    @Override
    public RecipePriority getPriority() {
        return priority;
    }

    @Override
    public AnvilConfig getConfig() {
        return config;
    }

    @Override
    public boolean isExactMeta() {
        return exactMeta;
    }

    @Override
    public Conditions getConditions() {
        return conditions;
    }

    public void setExactMeta(boolean exactMeta) {
        this.exactMeta = exactMeta;
    }

    public void setPriority(RecipePriority priority) {
        this.priority = priority;
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

    public void setResult(List<CustomItem> result) {
        ingredients.put(2, result);
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public void setInputLeft(List<CustomItem> inputLeft) {
        ingredients.put(0, inputLeft);
    }

    public List<CustomItem> getInputLeft() {
        return ingredients.get(0);
    }

    public void setInputRight(List<CustomItem> inputRight) {
        ingredients.put(1, inputRight);
    }

    public List<CustomItem> getInputRight() {
        return ingredients.get(1);
    }

    public void setInput(int slot, List<CustomItem> input) {
        ingredients.put(slot, input);
    }

    public List<CustomItem> getInput(int slot) {
        return ingredients.get(slot);
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
    @Deprecated
    public String getGroup() {
        return "";
    }

    public enum Mode {
        DURABILITY(1), RESULT(2), NONE(0);

        private int id;

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

        private static List<RepairCostMode> modes = new ArrayList<>();

        public static List<RepairCostMode> getModes() {
            if (modes.isEmpty()) {
                modes.addAll(Arrays.asList(values()));
            }
            return modes;
        }
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.ANVIL;
    }

    @Override
    public void renderMenu(GuiWindow guiWindow, GuiUpdateEvent event) {
        event.setButton(0, "back");
        event.setButton(10, "recipe_book", "ingredient.container_10");
        event.setButton(13, "recipe_book", "ingredient.container_13");
        event.setButton(19, "none", "glass_green");
        event.setButton(22, "none", "glass_green");
        event.setButton(28, "none", "glass_green");
        event.setButton(29, "none", "glass_green");
        event.setButton(30, "none", "glass_green");
        event.setButton(32, "none", "glass_green");
        event.setButton(33, "none", "glass_green");
        if (getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
            event.setButton(31, "recipe_book", "anvil.result");
        } else if (getMode().equals(CustomAnvilRecipe.Mode.DURABILITY)) {
            event.setButton(31, "recipe_book", "anvil.durability");
        } else {
            event.setButton(31, "recipe_book", "anvil.none");
        }
        event.setButton(34, "recipe_book", "ingredient.container_34");
    }
}
