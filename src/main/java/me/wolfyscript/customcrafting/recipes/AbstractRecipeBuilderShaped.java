package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.settings.CraftingRecipeSettings;

public abstract class AbstractRecipeBuilderShaped<R extends AbstractRecipeShaped<R, S>, S extends CraftingRecipeSettings, B extends AbstractRecipeBuilderShaped<R, S, B>> extends AbstractCraftingRecipeBuilder<R, S, B> {

    private String[] shape;
    private boolean mirrorHorizontal;
    private boolean mirrorVertical;
    private boolean mirrorRotation;

    protected AbstractRecipeBuilderShaped() {
        this.shape = null;
        this.mirrorHorizontal = true;
        this.mirrorVertical = false;
        this.mirrorRotation = false;
    }

    protected AbstractRecipeBuilderShaped(R recipe) {
        super(recipe);
        this.shape = recipe.getShape();
        this.mirrorHorizontal = recipe.mirrorHorizontal();
        this.mirrorVertical = recipe.mirrorVertical();
        this.mirrorRotation = recipe.mirrorRotation();
    }

    public String[] getShape() {
        return shape;
    }

    public AbstractRecipeBuilderShaped<R, S, B> setShape(String[] shape) {
        this.shape = shape;
        return this;
    }

    public boolean isMirrorHorizontal() {
        return mirrorHorizontal;
    }

    public AbstractRecipeBuilderShaped<R, S, B> setMirrorHorizontal(boolean mirrorHorizontal) {
        this.mirrorHorizontal = mirrorHorizontal;
        return this;
    }

    public boolean isMirrorVertical() {
        return mirrorVertical;
    }

    public AbstractRecipeBuilderShaped<R, S, B> setMirrorVertical(boolean mirrorVertical) {
        this.mirrorVertical = mirrorVertical;
        return this;
    }

    public boolean isMirrorRotation() {
        return mirrorRotation;
    }

    public AbstractRecipeBuilderShaped<R, S, B> setMirrorRotation(boolean mirrorRotation) {
        this.mirrorRotation = mirrorRotation;
        return this;
    }

}
