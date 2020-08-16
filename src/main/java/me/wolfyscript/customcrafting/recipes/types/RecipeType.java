package me.wolfyscript.customcrafting.recipes.types;

import java.util.Locale;

public enum RecipeType {

    WORKBENCH,
    ELITE_WORKBENCH,
    FURNACE,
    ANVIL,
    BLAST_FURNACE,
    SMOKER,
    CAMPFIRE,
    STONECUTTER,
    CAULDRON,
    GRINDSTONE,
    BREWING,
    SMITHING;

    private final String id;

    RecipeType() {
        this.id = this.toString().toLowerCase(Locale.ROOT);
    }

    public String getId() {
        return id;
    }
}
