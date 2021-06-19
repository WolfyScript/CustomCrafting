package me.wolfyscript.customcrafting.utils.recipe_item.target;

public class SlotResultTarget extends ResultTarget {

    private int slot;

    public SlotResultTarget(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }


}
