package me.wolfyscript.customcrafting.recipes.types.elite_workbench;

import me.wolfyscript.customcrafting.recipes.types.ShapedCraftingRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;

public class ShapedEliteCraftRecipe extends EliteCraftingRecipe implements ShapedCraftingRecipe<EliteCraftConfig> {

    private String[] shape, shapeMirrorHorizontal, shapeMirrorVertical, shapeRotated;
    private boolean mirrorHorizontal, mirrorVertical, mirrorRotation;

    public ShapedEliteCraftRecipe(EliteCraftConfig config) {
        super(config);
        this.shape = WolfyUtilities.formatShape(config.getShape()).toArray(new String[0]);
        this.shapeMirrorVertical = new String[6];
        int j = 0;
        for (int i = this.shape.length - 1; i > 0; i--) {
            this.shapeMirrorVertical[j] = config.getShape()[i];
            j++;
        }
        this.shapeMirrorVertical = WolfyUtilities.formatShape(this.shapeMirrorVertical).toArray(new String[0]);
        this.shapeMirrorHorizontal = this.shape.clone();
        for (int i = 0; i < this.shapeMirrorHorizontal.length; i++) {
            this.shapeMirrorHorizontal[i] = new StringBuilder(this.shapeMirrorHorizontal[i]).reverse().toString();
        }
        this.shapeRotated = this.shapeMirrorVertical.clone();
        for (int i = 0; i < this.shapeRotated.length; i++) {
            this.shapeRotated[i] = new StringBuilder(this.shapeRotated[i]).reverse().toString();
        }
        this.mirrorHorizontal = config.mirrorHorizontal();
        this.mirrorVertical = config.mirrorVertical();
        this.mirrorRotation = config.mirrorRotation();

        int size = shape.length > shape[0].length() ? shape.length : shape[0].length();
        if(size <= 3){
            requiredGridSize = 3;
        }else if(size <= 4){
            requiredGridSize = 4;
        }else if(size <= 5){
            requiredGridSize = 5;
        }else if(size <= 6){
            requiredGridSize = 6;
        }

    }

    @Override
    public String[] getShapeMirrorHorizontal() {
        return shapeMirrorHorizontal;
    }

    @Override
    public String[] getShapeMirrorVertical() {
        return shapeMirrorVertical;
    }

    @Override
    public String[] getShapeRotated() {
        return shapeRotated;
    }

    @Override
    public String[] getShape() {
        return shape;
    }

    @Override
    public boolean mirrorHorizontal() {
        return mirrorHorizontal;
    }

    @Override
    public boolean mirrorVertical() {
        return mirrorVertical;
    }

    @Override
    public boolean mirrorRotate() {
        return mirrorRotation;
    }
}