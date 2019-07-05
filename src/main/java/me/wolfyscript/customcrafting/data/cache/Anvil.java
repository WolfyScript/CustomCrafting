package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.anvil.CustomAnvilRecipe;
import org.bukkit.Material;

import java.util.HashMap;

public class Anvil {

    private boolean exactMeta;
    private RecipePriority priority;
    private boolean permissions;

    private HashMap<CustomItem, Boolean> inputLeft;
    private HashMap<CustomItem, Boolean> inputRight;

    //RESULT MODES
    private CustomAnvilRecipe.Mode mode;
    private int durability;
    private CustomItem result;

    private int repairCost;
    private boolean blockRepair;
    private boolean blockRename;
    private boolean blockEnchant;

    //GUI
    private Menu menu;

    public Anvil(){
        this.inputLeft = new HashMap<>();
        this.inputRight = new HashMap<>();
        this.result = new CustomItem(Material.AIR);
        this.priority = RecipePriority.NORMAL;
        this.exactMeta = true;
        this.permissions = true;
        this.mode = CustomAnvilRecipe.Mode.RESULT;
        this.durability = 0;
        this.result = new CustomItem(Material.AIR);
        this.repairCost = 0;
        this.blockEnchant = false;
        this.blockRename = false;
        this.blockRepair = false;
        this.menu = Menu.MAINMENU;
    }

    public boolean isExactMeta() {
        return exactMeta;
    }

    public void setExactMeta(boolean exactMeta) {
        this.exactMeta = exactMeta;
    }

    public RecipePriority getPriority() {
        return priority;
    }

    public void setPriority(RecipePriority priority) {
        this.priority = priority;
    }

    public CustomItem getResult() {
        return result;
    }

    public void setResult(CustomItem result) {
        this.result = result;
    }

    public HashMap<CustomItem, Boolean> getInputLeft() {
        return inputLeft;
    }

    public void setInputLeft(HashMap<CustomItem, Boolean> inputLeft) {
        this.inputLeft = inputLeft;
    }

    public HashMap<CustomItem, Boolean> getInputRight() {
        return inputRight;
    }

    public void setInputRight(HashMap<CustomItem, Boolean> inputRight) {
        this.inputRight = inputRight;
    }

    public boolean isPermissions() {
        return permissions;
    }

    public void setPermissions(boolean permissions) {
        this.permissions = permissions;
    }

    public CustomAnvilRecipe.Mode getMode() {
        return mode;
    }

    public void setMode(CustomAnvilRecipe.Mode mode) {
        this.mode = mode;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(int repairCost) {
        this.repairCost = repairCost;
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

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public enum Menu{
        MAINMENU, INPUT_LEFT, INPUT_RIGHT;
    }
}
