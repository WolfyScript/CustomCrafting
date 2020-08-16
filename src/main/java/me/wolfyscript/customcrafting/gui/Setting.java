package me.wolfyscript.customcrafting.gui;

import java.util.Locale;

public enum Setting {

    MAIN_MENU,
    RECIPE_LIST,
    ITEMS,
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
    BREWING_STAND,
    SMITHING_TABLE;

    private final String id;

    Setting() {
        this.id = this.toString().toLowerCase(Locale.ROOT);
    }

    public String getId() {
        return id;
    }
}
