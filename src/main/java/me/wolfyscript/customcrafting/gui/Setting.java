package me.wolfyscript.customcrafting.gui;

import java.util.Locale;

public enum Setting {

    MAIN_MENU,
    RECIPE_LIST,
    ITEM_LIST,
    ITEMS,
    RECIPE_CREATOR;

    private final String id;

    Setting() {
        this.id = this.toString().toLowerCase(Locale.ROOT);
    }

    public String getId() {
        return id;
    }
}
