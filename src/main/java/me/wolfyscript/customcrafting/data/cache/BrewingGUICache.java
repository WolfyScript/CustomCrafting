package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.utilities.api.custom_items.CustomItem;

public class BrewingGUICache {

    private String option;

    int page;
    CustomItem selection;

    public BrewingGUICache() {
        this.page = 0;
        this.selection = null;
        this.option = "";
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

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
