package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.recipebook.Category;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiCluster;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CookingRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class KnowledgeBook {

    private final CustomCrafting customCrafting;
    private int page, subFolder, subFolderPage;
    private Category category;
    private final Setting setting;
    private WorkbenchFilter workbenchFilter;

    private int timerTask;
    private HashMap<Integer, Integer> timerTimings;

    private List<ICustomRecipe> subFolderRecipes;
    private List<CustomItem> recipeItems;
    private List<CustomItem> researchItems;

    public KnowledgeBook() {
        this.customCrafting = (CustomCrafting) Bukkit.getPluginManager().getPlugin("CustomCrafting");
        this.page = 0;
        this.subFolder = 0;
        this.subFolderPage = 0;
        this.category = null;
        this.setting = Setting.MAIN_MENU;
        this.researchItems = new ArrayList<>();
        this.recipeItems = new ArrayList<>();
        this.timerTask = -1;
        this.timerTimings = new HashMap<>();
        this.subFolderRecipes = new ArrayList<>();
        workbenchFilter = WorkbenchFilter.ALL;
    }

    public HashMap<Integer, Integer> getTimerTimings() {
        return timerTimings;
    }

    public void setTimerTask(int task) {
        this.timerTask = task;
    }

    public int getTimerTask() {
        return timerTask;
    }

    public void stopTimerTask() {
        if (timerTask != -1) {
            Bukkit.getScheduler().cancelTask(timerTask);
            timerTask = -1;
            timerTimings = new HashMap<>();
        }
    }

    public ICustomRecipe getCurrentRecipe() {
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

    public List<ICustomRecipe> getSubFolderRecipes() {
        return subFolderRecipes;
    }

    public void setSubFolderRecipes(List<ICustomRecipe> subFolderRecipes) {
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

    public void applyRecipeToButtons(GuiHandler guiHandler, ICustomRecipe recipe) {
        GuiCluster cluster = WolfyUtilities.getAPI(customCrafting).getInventoryAPI().getGuiCluster("recipe_book");
        Player player = guiHandler.getPlayer();
        switch (recipe.getRecipeType()) {
            case WORKBENCH:
                CraftingRecipe craftingRecipe = (CraftingRecipe) recipe;
                if (!craftingRecipe.getIngredients().isEmpty()) {
                    ((IngredientContainerButton) cluster.getButton("ingredient.container_25")).setVariants(guiHandler, craftingRecipe.getCustomResults());
                    int ingrSlot;
                    for (int i = 0; i < 9; i++) {
                        ingrSlot = 10 + i + (i / 3) * 6;
                        List<CustomItem> variants = new ArrayList<>();
                        craftingRecipe.getIngredients(i).forEach(item -> variants.add(item));
                        ((IngredientContainerButton) cluster.getButton("ingredient.container_" + ingrSlot)).setVariants(guiHandler, variants);
                    }
                }
                return;
            case ELITE_WORKBENCH:
                EliteCraftingRecipe eliteCraftingRecipe = (EliteCraftingRecipe) recipe;
                if (!eliteCraftingRecipe.getIngredients().isEmpty()) {
                    List<CustomItem> results = new ArrayList<>();
                    eliteCraftingRecipe.getCustomResults().forEach(item -> results.add(item));
                    ((IngredientContainerButton) cluster.getButton("ingredient.container_25")).setVariants(guiHandler, results);
                    int gridSize = 6;
                    int startSlot = 0;
                    int invSlot;
                    for (int i = 0; i < gridSize * gridSize; i++) {
                        invSlot = startSlot + i + (i / gridSize) * 3;
                        List<CustomItem> variants = new ArrayList<>();
                        eliteCraftingRecipe.getIngredients(i).forEach(item -> variants.add(item));
                        IngredientContainerButton button = (IngredientContainerButton) cluster.getButton("ingredient.container_" + invSlot);
                        button.setVariants(guiHandler, variants);
                    }
                }
                return;
            case FURNACE:
            case CAMPFIRE:
            case BLAST_FURNACE:
            case SMOKER:
                CustomCookingRecipe<CookingRecipe> furnaceRecipe = (CustomCookingRecipe) recipe;
                if (furnaceRecipe != null) {
                    ((IngredientContainerButton) cluster.getButton("ingredient.container_11")).setVariants(guiHandler, furnaceRecipe.getSource().stream().filter(customItem -> !customItem.hasPermission() || player.hasPermission(customItem.getPermission())).collect(Collectors.toList()));
                    ((IngredientContainerButton) cluster.getButton("ingredient.container_24")).setVariants(guiHandler, furnaceRecipe.getCustomResults().stream().filter(customItem -> !customItem.hasPermission() || player.hasPermission(customItem.getPermission())).collect(Collectors.toList()));
                }
                return;
            case ANVIL:
                CustomAnvilRecipe customAnvilRecipe = (CustomAnvilRecipe) recipe;
                List<CustomItem> inputLeft = customAnvilRecipe.getInputLeft().stream().collect(Collectors.toList());
                List<CustomItem> inputRight = customAnvilRecipe.getInputRight().stream().collect(Collectors.toList());

                ((IngredientContainerButton) cluster.getButton("ingredient.container_10")).setVariants(guiHandler, inputLeft);
                ((IngredientContainerButton) cluster.getButton("ingredient.container_13")).setVariants(guiHandler, inputRight);

                List<CustomItem> variants = Collections.singletonList(new CustomItem(Material.AIR));
                if (customAnvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                    variants = Collections.singletonList(customAnvilRecipe.getCustomResult());
                } else if (customAnvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.DURABILITY)) {
                    variants = inputLeft;
                }
                ((IngredientContainerButton) cluster.getButton("ingredient.container_34")).setVariants(guiHandler, variants);
                return;
            case STONECUTTER:
                CustomStonecutterRecipe stonecutterRecipe = (CustomStonecutterRecipe) recipe;
                ((IngredientContainerButton) cluster.getButton("ingredient.container_20")).setVariants(guiHandler, stonecutterRecipe.getSource());
                ((IngredientContainerButton) cluster.getButton("ingredient.container_24")).setVariants(guiHandler, Collections.singletonList(stonecutterRecipe.getCustomResult()));
                return;
            case CAULDRON:
                CauldronRecipe cauldronRecipe = (CauldronRecipe) recipe;
                List<CustomItem> ingredients = cauldronRecipe.getIngredients();
                int invSlot;
                for (int i = 0; i < 6; i++) {
                    invSlot = 10 + i + (i / 3) * 6;
                    if (i < ingredients.size()) {
                        ((IngredientContainerButton) cluster.getButton("ingredient.container_" + invSlot)).setVariants(guiHandler, Collections.singletonList(ingredients.get(i)));
                    } else {
                        ((IngredientContainerButton) cluster.getButton("ingredient.container_" + invSlot)).setVariants(guiHandler, Collections.singletonList(new CustomItem(Material.AIR)));
                    }
                }
                ((IngredientContainerButton) cluster.getButton("ingredient.container_25")).setVariants(guiHandler, Collections.singletonList(cauldronRecipe.getCustomResult()));
                return;
            case GRINDSTONE:
                GrindstoneRecipe grindstoneRecipe = (GrindstoneRecipe) recipe;
                ((IngredientContainerButton) cluster.getButton("ingredient.container_11")).setVariants(guiHandler, grindstoneRecipe.getInputTop());
                ((IngredientContainerButton) cluster.getButton("ingredient.container_29")).setVariants(guiHandler, grindstoneRecipe.getInputBottom());
                ((IngredientContainerButton) cluster.getButton("ingredient.container_24")).setVariants(guiHandler, grindstoneRecipe.getCustomResults());
                return;
            case BREWING:
                BrewingRecipe brewingRecipe = (BrewingRecipe) recipe;
                ((IngredientContainerButton) cluster.getButton("ingredient.container_11")).setVariants(guiHandler, brewingRecipe.getCustomResults());
                if (!brewingRecipe.getAllowedItems().isEmpty()) {
                    ((IngredientContainerButton) cluster.getButton("ingredient.container_29")).setVariants(guiHandler, brewingRecipe.getAllowedItems());
                }
        }
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
