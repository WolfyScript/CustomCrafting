package me.wolfyscript.customcrafting.data.cache;

public class RecipeList {

    private String namespace;
    private int page;

    public RecipeList() {
        this.namespace = null;
        this.page = 0;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public int getPage(int maxPages) {
        if (this.page > maxPages) {
            this.page = maxPages;
        }
        return this.page;
    }

    public int getMaxPages(int size) {
        return size / 45 + (size % 45 > 0 ? 1 : 0);
    }
}
