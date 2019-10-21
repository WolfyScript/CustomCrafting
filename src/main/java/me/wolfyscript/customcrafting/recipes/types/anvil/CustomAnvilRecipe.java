package me.wolfyscript.customcrafting.recipes.types.anvil;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CustomAnvilRecipe implements CustomRecipe<AnvilConfig> {

    private boolean exactMeta;
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

    private String id;
    private AnvilConfig config;

    public CustomAnvilRecipe(AnvilConfig config) {
        this.config = config;
        this.id = config.getId();
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
    }

    public CustomAnvilRecipe(){
        this.config = null;
        this.id = "";
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

    public CustomAnvilRecipe save(ConfigAPI configAPI, String namespace, String key){
        AnvilConfig config;
        if (CustomCrafting.hasDataBaseHandler()) {
            config = new AnvilConfig("{}", configAPI, namespace, key);
        } else {
            config = new AnvilConfig(configAPI, namespace, key);
        }
        return save(config);
    }

    @Override
    public CustomAnvilRecipe save(AnvilConfig config){
        config.setBlockEnchant(isBlockEnchant());
        config.setBlockRename(isBlockRename());
        config.setBlockRepairing(isBlockRepair());
        config.setExactMeta(isExactMeta());
        config.setRepairCostMode(getRepairCostMode());
        config.setRepairCost(getRepairCost());
        config.setPriority(getPriority());
        config.setMode(getMode());
        config.setResult(getCustomResults());
        config.setDurability(getDurability());
        config.setInputLeft(getInputLeft());
        config.setInputRight(getInputRight());
        config.setConditions(getConditions());
        if (CustomCrafting.hasDataBaseHandler()) {
            CustomCrafting.getDataBaseHandler().updateRecipe(config);
        } else {
            config.reload(CustomCrafting.getConfigHandler().getConfig().isPrettyPrinting());
        }
        this.config = config;
        this.id = config.getId();
        return this;
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

    public void setInput(int slot, List<CustomItem> input){
        ingredients.put(slot, input);
    }

    public List<CustomItem> getInput(int slot){
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
