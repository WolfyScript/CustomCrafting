package me.wolfyscript.customcrafting.recipes.builder;

import me.wolfyscript.customcrafting.recipes.types.AbstractShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.crafting.CraftingRecipeSettings;

public abstract class AbstractShapedRecipeBuilder<R extends AbstractShapedCraftRecipe<R, S>, S extends CraftingRecipeSettings, B extends AbstractShapedRecipeBuilder<R, S, B>> extends AbstractCraftingRecipeBuilder<R, S, B> {

    private String[] shape;
    private boolean mirrorHorizontal;
    private boolean mirrorVertical;
    private boolean mirrorRotation;

    protected AbstractShapedRecipeBuilder() {
        this.shape = null;
        this.mirrorHorizontal = true;
        this.mirrorVertical = false;
        this.mirrorRotation = false;
    }

    protected AbstractShapedRecipeBuilder(R recipe) {
        super(recipe);
        this.shape = recipe.getShape();
        this.mirrorHorizontal = recipe.mirrorHorizontal();
        this.mirrorVertical = recipe.mirrorVertical();
        this.mirrorRotation = recipe.mirrorRotation();
    }

    public String[] getShape() {
        return shape;
    }

    public AbstractShapedRecipeBuilder<R, S, B> setShape(String[] shape) {
        this.shape = shape;
        return this;
    }

    public boolean isMirrorHorizontal() {
        return mirrorHorizontal;
    }

    public AbstractShapedRecipeBuilder<R, S, B> setMirrorHorizontal(boolean mirrorHorizontal) {
        this.mirrorHorizontal = mirrorHorizontal;
        return this;
    }

    public boolean isMirrorVertical() {
        return mirrorVertical;
    }

    public AbstractShapedRecipeBuilder<R, S, B> setMirrorVertical(boolean mirrorVertical) {
        this.mirrorVertical = mirrorVertical;
        return this;
    }

    public boolean isMirrorRotation() {
        return mirrorRotation;
    }

    public AbstractShapedRecipeBuilder<R, S, B> setMirrorRotation(boolean mirrorRotation) {
        this.mirrorRotation = mirrorRotation;
        return this;
    }

}
