package me.wolfyscript.customcrafting.recipes;

public enum RecipePriority {

    LOWEST(-2),
    LOW(-1),
    NORMAL(0),
    HIGH(1),
    HIGHEST(2);

    int order;

    RecipePriority(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public static RecipePriority getByOrder(int order) {
        for (RecipePriority priority : RecipePriority.values()) {
            if (priority.getOrder() == order)
                return priority;
        }
        return NORMAL;
    }
}
