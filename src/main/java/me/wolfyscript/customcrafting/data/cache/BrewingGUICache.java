package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.utilities.api.custom_items.CustomItem;

public class BrewingGUICache {

    int page;
    CustomItem selection;

    public BrewingGUICache() {
        this.page = 0;
        this.selection = null;

    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public CustomItem getSelection() {
        return selection;
    }

    public void setSelection(CustomItem selection) {
        this.selection = selection;
    }
}
