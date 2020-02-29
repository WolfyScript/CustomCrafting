package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.types.CookingConfig;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftingRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiCluster;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.utils.ItemCategory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class KnowledgeBook {

    private int page, subFolder, subFolderPage;
    private Setting setting;
    private WorkbenchFilter workbenchFilter;
    private ItemCategory itemCategory;

    private int timerTask;
    private HashMap<Integer, Integer> timerTimings;

    private List<CustomRecipe> subFolderRecipes;
    private List<CustomItem> recipeItems;
    private List<CustomItem> researchItems;

    public KnowledgeBook() {
        this.page = 0;
        this.subFolder = 0;
        this.subFolderPage = 0;
        this.setting = Setting.MAIN_MENU;
        this.researchItems = new ArrayList<>();
        this.recipeItems = new ArrayList<>();
        this.timerTask = -1;
        this.timerTimings = new HashMap<>();
        this.subFolderRecipes = new ArrayList<>();
        workbenchFilter = WorkbenchFilter.ALL;
        this.itemCategory = ItemCategory.SEARCH;
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

    public CustomRecipe getCurrentRecipe() {
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

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
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

    public void setItemCategory(ItemCategory itemCategory) {
        this.itemCategory = itemCategory;
    }

    public ItemCategory getItemCategory() {
        return itemCategory;
    }

    public int getSubFolder() {
        return subFolder;
    }

    public void setSubFolder(int subFolder) {
        this.subFolder = subFolder;
    }

    public List<CustomRecipe> getSubFolderRecipes() {
        return subFolderRecipes;
    }

    public void setSubFolderRecipes(List<CustomRecipe> subFolderRecipes) {
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

    public void applyRecipeToButtons(GuiHandler guiHandler, CustomRecipe recipe) {
        GuiCluster cluster = WolfyUtilities.getAPI(CustomCrafting.getInst()).getInventoryAPI().getGuiCluster("recipe_book");
        Player player = guiHandler.getPlayer();
        if (recipe instanceof AdvancedCraftingRecipe) {
            AdvancedCraftingRecipe craftingRecipe = (AdvancedCraftingRecipe) recipe;
            if (!craftingRecipe.getIngredients().isEmpty()) {
                List<CustomItem> results = new ArrayList<>();
                craftingRecipe.getCustomResults().forEach(item -> results.add(item.getRealItem()));
                ((IngredientContainerButton) cluster.getButton("ingredient.container_25")).setVariants(guiHandler, results);
                int ingrSlot;
                for (int i = 0; i < 9; i++) {
                    ingrSlot = 10 + i + (i / 3) * 6;
                    List<CustomItem> variants = new ArrayList<>();
                    craftingRecipe.getIngredients(i).forEach(item -> variants.add(item.getRealItem()));
                    ((IngredientContainerButton) cluster.getButton("ingredient.container_" + ingrSlot)).setVariants(guiHandler, variants);
                }
            }
        } else if (recipe instanceof EliteCraftingRecipe) {
            EliteCraftingRecipe eliteCraftingRecipe = (EliteCraftingRecipe) recipe;
            if (!eliteCraftingRecipe.getIngredients().isEmpty()) {
                List<CustomItem> results = new ArrayList<>();
                eliteCraftingRecipe.getCustomResults().forEach(item -> {
                    results.add(item.getRealItem());
                });
                ((IngredientContainerButton) cluster.getButton("ingredient.container_25")).setVariants(guiHandler, results);
                int gridSize = 6;
                int startSlot = 0;
                int invSlot;
                for (int i = 0; i < gridSize * gridSize; i++) {
                    invSlot = startSlot + i + (i / gridSize) * 3;
                    List<CustomItem> variants = new ArrayList<>();
                    eliteCraftingRecipe.getIngredients(i).forEach(item -> variants.add(item.getRealItem()));
                    ((IngredientContainerButton) cluster.getButton("ingredient.container_" + invSlot)).setVariants(guiHandler, variants);
                }
            }
        } else if (recipe instanceof CustomCookingRecipe) {
            CustomCookingRecipe<CookingConfig> furnaceRecipe = (CustomCookingRecipe<CookingConfig>) recipe;
            if (furnaceRecipe != null) {
                List<CustomItem> variantsSource = new ArrayList<>();
                for (CustomItem customItem : furnaceRecipe.getSource()) {
                    if (!customItem.hasPermission() || player.hasPermission(customItem.getPermission())) {
                        if (customItem.getType() != Material.AIR) {
                            variantsSource.add(customItem.getRealItem());
                        }
                    }
                }
                ((IngredientContainerButton) cluster.getButton("ingredient.container_20")).setVariants(guiHandler, variantsSource);

                List<CustomItem> variantsResult = new ArrayList<>();
                for (CustomItem customItem : furnaceRecipe.getCustomResults()) {
                    if (!customItem.hasPermission() || player.hasPermission(customItem.getPermission())) {
                        if (customItem.getType() != Material.AIR) {
                            variantsResult.add(customItem.getRealItem());
                        }
                    }
                }
                ((IngredientContainerButton) cluster.getButton("ingredient.container_33")).setVariants(guiHandler, variantsResult);
            }
        } else if (recipe instanceof CustomAnvilRecipe) {
            CustomAnvilRecipe customAnvilRecipe = (CustomAnvilRecipe) recipe;
            List<CustomItem> inputLeft = new ArrayList<>();
            customAnvilRecipe.getInputLeft().forEach(item -> inputLeft.add(item.getRealItem()));
            List<CustomItem> inputRight = new ArrayList<>();
            customAnvilRecipe.getInputRight().forEach(item -> inputLeft.add(item.getRealItem()));

            ((IngredientContainerButton) cluster.getButton("ingredient.container_10")).setVariants(guiHandler, inputLeft);
            ((IngredientContainerButton) cluster.getButton("ingredient.container_13")).setVariants(guiHandler, inputRight);

            List<CustomItem> variants = Collections.singletonList(new CustomItem(Material.AIR));
            if (customAnvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                variants = Collections.singletonList(customAnvilRecipe.getCustomResult());
            } else if (customAnvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.DURABILITY)) {
                variants = inputLeft;
            }
            ((IngredientContainerButton) cluster.getButton("ingredient.container_34")).setVariants(guiHandler, variants);
        } else if (recipe instanceof CustomStonecutterRecipe) {
            CustomStonecutterRecipe stonecutterRecipe = (CustomStonecutterRecipe) recipe;
            List<CustomItem> sources = new ArrayList<>();
            stonecutterRecipe.getSource().forEach(item -> sources.add(item.getRealItem()));

            ((IngredientContainerButton) cluster.getButton("ingredient.container_20")).setVariants(guiHandler, sources);
            ((IngredientContainerButton) cluster.getButton("ingredient.container_24")).setVariants(guiHandler, Collections.singletonList(stonecutterRecipe.getCustomResult().getRealItem()));
        } else if (recipe instanceof CauldronRecipe) {
            CauldronRecipe cauldronRecipe = (CauldronRecipe) recipe;
            List<CustomItem> ingredients = cauldronRecipe.getIngredients();
            int invSlot;
            for (int i = 0; i < 6; i++) {
                invSlot = 10 + i + (i / 3) * 6;

                if (i < ingredients.size()) {
                    ((IngredientContainerButton) cluster.getButton("ingredient.container_" + invSlot)).setVariants(guiHandler, Collections.singletonList(ingredients.get(i).getRealItem()));
                } else {
                    ((IngredientContainerButton) cluster.getButton("ingredient.container_" + invSlot)).setVariants(guiHandler, Collections.singletonList(new CustomItem(Material.AIR)));
                }
            }
            ((IngredientContainerButton) cluster.getButton("ingredient.container_34")).setVariants(guiHandler, Collections.singletonList(cauldronRecipe.getCustomResult().getRealItem()));
        } else if (recipe instanceof GrindstoneRecipe) {
            GrindstoneRecipe grindstoneRecipe = (GrindstoneRecipe) recipe;
            List<CustomItem> inputTop = new ArrayList<>();
            grindstoneRecipe.getInputTop().forEach(item -> inputTop.add(item.getRealItem()));
            List<CustomItem> inputBottom = new ArrayList<>();
            grindstoneRecipe.getInputBottom().forEach(item -> inputBottom.add(item.getRealItem()));
            List<CustomItem> results = new ArrayList<>();
            grindstoneRecipe.getCustomResults().forEach(item -> results.add(item.getRealItem()));
            ((IngredientContainerButton) cluster.getButton("ingredient.container_11")).setVariants(guiHandler, inputTop);
            ((IngredientContainerButton) cluster.getButton("ingredient.container_29")).setVariants(guiHandler, inputBottom);
            ((IngredientContainerButton) cluster.getButton("ingredient.container_24")).setVariants(guiHandler, results);
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
