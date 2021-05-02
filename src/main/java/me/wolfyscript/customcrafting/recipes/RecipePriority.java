package me.wolfyscript.customcrafting.recipes;

public enum RecipePriority {

    HIGHEST(-2),
    HIGH(-1),
    NORMAL(0),
    LOW(1),
    LOWEST(2);

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
