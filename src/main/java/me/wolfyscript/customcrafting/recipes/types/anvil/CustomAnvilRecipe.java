package me.wolfyscript.customcrafting.recipes.types.anvil;

import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CustomAnvilRecipe implements CustomRecipe {

    private boolean permission;
    private boolean exactMeta;
    private boolean blockRepair;
    private boolean blockRename;
    private boolean blockEnchant;
    private RecipePriority priority;

    private String id;
    private AnvilConfig config;

    private Mode mode;
    private int repairCost;
    private boolean applyRepairCost;
    private RepairCostMode repairCostMode;
    private int durability;
    private Conditions conditions;

    private HashMap<Integer, List<CustomItem>> ingredients;

    public CustomAnvilRecipe(AnvilConfig config) {
        this.ingredients = new HashMap<>();
        this.config = config;
        this.permission = config.needPerm();
        this.exactMeta = config.isExactMeta();
        this.blockEnchant = config.isBlockEnchant();
        this.blockRename = config.isBlockRename();
        this.blockRepair = config.isBlockRepairing();
        this.priority = config.getPriority();

        this.id = config.getId();

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
    }

    @Override
    public String getId() {
        return id;
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
    public void load() {
    }

    @Override
    public void save() {
    }

    @Override
    public RecipeConfig getConfig() {
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

    public boolean isPermission() {
        return permission;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
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
    public ItemStack getResult() {
        return ingredients.getOrDefault(2, Collections.singletonList(new CustomItem(Material.AIR))).get(0);
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
}
