package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.recipes.CustomRecipeAnvil;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;

public class RecipeCacheAnvil extends RecipeCache<CustomRecipeAnvil> {

    private boolean blockRepair;
    private boolean blockRename;
    private boolean blockEnchant;

    private CustomRecipeAnvil.Mode mode;
    private int repairCost;
    private boolean applyRepairCost;
    private CustomRecipeAnvil.RepairCostMode repairCostMode;
    private int durability;

    private Ingredient base;
    private Ingredient addition;

    public RecipeCacheAnvil(RecipeCreatorCache creatorCache) {
        super(creatorCache);
    }

    public RecipeCacheAnvil(RecipeCreatorCache creatorCache, CustomRecipeAnvil recipe) {
        super(creatorCache, recipe);
        this.blockRepair = recipe.isBlockRepair();
        this.blockRename = recipe.isBlockRename();
        this.blockEnchant = recipe.isBlockEnchant();

        this.mode = recipe.getMode();
        this.repairCost = recipe.getRepairCost();
        this.applyRepairCost = recipe.isApplyRepairCost();
        this.repairCostMode = recipe.getRepairCostMode();
        this.durability = recipe.getDurability();
        this.base = recipe.getIngredient(0);
        this.addition = recipe.getIngredient(1);
    }

    @Override
    protected CustomRecipeAnvil constructRecipe() {
        return create(new CustomRecipeAnvil(key));
    }

    @Override
    protected CustomRecipeAnvil create(CustomRecipeAnvil recipe) {
        CustomRecipeAnvil anvil = super.create(recipe);
        anvil.setBlockRepair(blockRepair);
        anvil.setBlockRename(blockRename);
        anvil.setBlockEnchant(blockEnchant);
        anvil.setMode(mode);
        anvil.setRepairCost(repairCost);
        anvil.setApplyRepairCost(applyRepairCost);
        anvil.setRepairCostMode(repairCostMode);
        anvil.setDurability(durability);
        anvil.setBase(base);
        anvil.setAddition(addition);
        return anvil;
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

    public CustomRecipeAnvil.Mode getMode() {
        return mode;
    }

    public void setMode(CustomRecipeAnvil.Mode mode) {
        this.mode = mode;
    }

    public int getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(int repairCost) {
        this.repairCost = repairCost;
    }

    public boolean isApplyRepairCost() {
        return applyRepairCost;
    }

    public void setApplyRepairCost(boolean applyRepairCost) {
        this.applyRepairCost = applyRepairCost;
    }

    public CustomRecipeAnvil.RepairCostMode getRepairCostMode() {
        return repairCostMode;
    }

    public void setRepairCostMode(CustomRecipeAnvil.RepairCostMode repairCostMode) {
        this.repairCostMode = repairCostMode;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public Ingredient getBase() {
        return base;
    }

    public void setBase(Ingredient base) {
        this.base = base;
    }

    public Ingredient getAddition() {
        return addition;
    }

    public void setAddition(Ingredient addition) {
        this.addition = addition;
    }
}
