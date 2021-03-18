package me.wolfyscript.customcrafting.recipes.types;

public interface IShapedCraftingRecipe {

    String[] getShapeMirrorHorizontal();

    String[] getShapeMirrorVertical();

    String[] getShapeRotated();

    String[] getShape();

    int getWidth();

    int getHeight();

    void setMirrorHorizontal(boolean mirrorHorizontal);

    void setMirrorVertical(boolean mirrorHorizontal);

    void setMirrorRotation(boolean mirrorHorizontal);

    boolean mirrorHorizontal();

    boolean mirrorVertical();

    boolean mirrorRotation();

    void constructShape();
}
