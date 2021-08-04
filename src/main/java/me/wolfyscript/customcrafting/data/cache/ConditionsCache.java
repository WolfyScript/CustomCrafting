package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.utilities.util.NamespacedKey;

public class ConditionsCache {

    private int page;
    private int selectNewPage;
    private NamespacedKey selectedCondition;

    public ConditionsCache() {
        this.selectedCondition = null;
        this.page = 0;
        this.selectNewPage = 0;
    }

    public NamespacedKey getSelectedCondition() {
        return selectedCondition;
    }

    public void setSelectedCondition(NamespacedKey selectedCondition) {
        this.selectedCondition = selectedCondition;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSelectNewPage() {
        return selectNewPage;
    }

    public void setSelectNewPage(int selectNewPage) {
        this.selectNewPage = selectNewPage;
    }
}
