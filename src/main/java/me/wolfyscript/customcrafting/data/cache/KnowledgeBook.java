package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KnowledgeBook {

    private final CustomCrafting customCrafting;
    private int page, subFolder, subFolderPage;
    private Category category;
    private WorkbenchFilter workbenchFilter;

    private BukkitTask timerTask;
    private HashMap<Integer, Integer> timerTimings;

    private List<ICustomRecipe<?>> subFolderRecipes;
    private List<CustomItem> recipeItems;
    private List<CustomItem> researchItems;

    public KnowledgeBook() {
        this.customCrafting = (CustomCrafting) Bukkit.getPluginManager().getPlugin("CustomCrafting");
        this.page = 0;
        this.subFolder = 0;
        this.subFolderPage = 0;
        this.category = null;
        this.researchItems = new ArrayList<>();
        this.recipeItems = new ArrayList<>();
        this.timerTask = null;
        this.timerTimings = new HashMap<>();
        this.subFolderRecipes = new ArrayList<>();
        workbenchFilter = WorkbenchFilter.ALL;
    }

    public HashMap<Integer, Integer> getTimerTimings() {
        return timerTimings;
    }

    public BukkitTask getTimerTask() {
        return timerTask;
    }

    public void setTimerTask(BukkitTask task) {
        this.timerTask = task;
    }

    public void stopTimerTask() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTimings = new HashMap<>();
        }
    }

    public ICustomRecipe<?> getCurrentRecipe() {
        if (getSubFolderPage() < getSubFolderRecipes().size()) {
            return getSubFolderRecipes().get(getSubFolderPage());
        }
        return null;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public HashMap<Character, ArrayList<CustomItem>> getIngredients() {
        return new HashMap<>();
    }

    public WorkbenchFilter getWorkbenchFilter() {
        return workbenchFilter;
    }

    public void setWorkbenchFilter(WorkbenchFilter workbenchFilter) {
        this.workbenchFilter = workbenchFilter;
    }

    public int getSubFolder() {
        return subFolder;
    }

    public void setSubFolder(int subFolder) {
        this.subFolder = subFolder;
    }

    public List<ICustomRecipe<?>> getSubFolderRecipes() {
        return subFolderRecipes;
    }

    public void setSubFolderRecipes(List<ICustomRecipe<?>> subFolderRecipes) {
        this.subFolderRecipes = subFolderRecipes;
    }

    public int getSubFolderPage() {
        return subFolderPage;
    }

    public void setSubFolderPage(int subFolderPage) {
        this.subFolderPage = subFolderPage;
    }

    public List<CustomItem> getResearchItems() {
        return researchItems;
    }

    public void setResearchItems(List<CustomItem> researchItems) {
        this.researchItems = researchItems;
    }

    public CustomItem getResearchItem() {
        return getResearchItems().get(getSubFolder() - 1);
    }

    public List<CustomItem> getRecipeItems() {
        return recipeItems;
    }

    public void setRecipeItems(List<CustomItem> recipeItems) {
        this.recipeItems = recipeItems;
    }

    public void applyRecipeToButtons(GuiHandler<CCCache> guiHandler, ICustomRecipe<?> recipe) {
        recipe.prepareMenu(guiHandler, guiHandler.getInvAPI().getGuiCluster("recipe_book"));
    }

    public enum WorkbenchFilter {
        ALL,
        ADVANCED,
        NORMAL;

        public static WorkbenchFilter next(WorkbenchFilter filter) {
            switch (filter) {
                case ALL:
                    return ADVANCED;
                case ADVANCED:
                    return NORMAL;
                case NORMAL:
                    return ALL;
            }
            return filter;
        }
    }
}
