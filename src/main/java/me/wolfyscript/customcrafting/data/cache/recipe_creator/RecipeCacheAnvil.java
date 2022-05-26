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

package me.wolfyscript.customcrafting.data.cache.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipeAnvil;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;

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

    RecipeCacheAnvil(CustomCrafting customCrafting) {
        super(customCrafting);
        this.blockRepair = false;
        this.blockRename = false;
        this.blockEnchant = false;

        this.mode = CustomRecipeAnvil.Mode.RESULT;
        this.repairCost = 1;
        this.applyRepairCost = false;
        this.repairCostMode = CustomRecipeAnvil.RepairCostMode.NONE;
        this.durability = 0;
        this.base = new Ingredient();
        this.addition = new Ingredient();
    }

    RecipeCacheAnvil(CustomCrafting customCrafting, CustomRecipeAnvil recipe) {
        super(customCrafting, recipe);
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
        recipe.setMode(mode);
        recipe.setBlockRepair(blockRepair);
        recipe.setBlockRename(blockRename);
        recipe.setBlockEnchant(blockEnchant);
        CustomRecipeAnvil anvil = super.create(recipe);
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
