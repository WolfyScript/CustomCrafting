package me.wolfyscript.customcrafting.data.cache;

public class TagSettingsCache {

    private int listPage;
    private int chooseListPage;

    public TagSettingsCache() {
        this.chooseListPage = 0;
        this.listPage = 0;
    }

    public int getListPage() {
        return listPage;
    }

    public void setListPage(int listPage) {
        this.listPage = listPage;
    }

    public int getChooseListPage() {
        return chooseListPage;
    }

    public void setChooseListPage(int chooseListPage) {
        this.chooseListPage = chooseListPage;
    }
}
