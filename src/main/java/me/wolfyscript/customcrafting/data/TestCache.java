package me.wolfyscript.customcrafting.data;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.cache.*;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.customcrafting.recipes.types.campfire.CustomCampfireRecipe;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapedEliteCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.customcrafting.recipes.types.smoker.CustomSmokerRecipe;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapedCraftRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.cache.CustomCache;

import java.util.HashMap;

public class TestCache extends CustomCache {

    private Setting setting;
    private final CustomCrafting customCrafting;
    private String subSetting;

    private Items items = new Items();
    private final KnowledgeBook knowledgeBook = new KnowledgeBook();
    private final VariantsData variantsData = new VariantsData();
    private EliteWorkbench eliteWorkbench = new EliteWorkbench();
    private final ChatLists chatLists = new ChatLists();
    private final ParticleCache particleCache = new ParticleCache();
    private ApplyItem applyItem;

    //RECIPE_LIST OF ALL RECIPE CACHE

    private final HashMap<Class<? extends CustomRecipe>, CustomRecipe> recipes = new HashMap<>();

    public TestCache() {
        this.customCrafting = CustomCrafting.getInst();
        this.setting = Setting.MAIN_MENU;
        this.subSetting = "";
        this.applyItem = null;

        setCustomRecipe(new CustomAnvilRecipe());
        setCustomRecipe(CraftingRecipe.class, new ShapedCraftRecipe());
        setCustomRecipe(EliteCraftingRecipe.class, new ShapedEliteCraftRecipe());
        setCustomRecipe(new CustomBlastRecipe());
        setCustomRecipe(new CustomCampfireRecipe());
        setCustomRecipe(new CustomSmokerRecipe());
        setCustomRecipe(new CustomFurnaceRecipe());
        setCustomRecipe(new CustomStonecutterRecipe());
        setCustomRecipe(new GrindstoneRecipe());
        setCustomRecipe(new CauldronRecipe());
        setCustomRecipe(new BrewingRecipe());
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public String getSubSetting() {
        return subSetting;
    }

    public void setSubSetting(String setting) {
        this.subSetting = setting;
    }

    public ChatLists getChatLists() {
        return chatLists;
    }

    public VariantsData getVariantsData() {
        return variantsData;
    }

    public KnowledgeBook getKnowledgeBook() {
        return knowledgeBook;
    }

    public Items getItems() {
        return items;
    }

    public void setItems(Items items) {
        this.items = items;
    }

    public void setApplyItem(ApplyItem applyItem) {
        this.applyItem = applyItem;
    }

    public void applyItem(CustomItem customItem) {
        if (applyItem != null) {
            applyItem.applyItem(getItems(), this, customItem);
            applyItem = null;
        }
    }

    public ParticleCache getParticleCache() {
        return particleCache;
    }

    public EliteWorkbench getEliteWorkbench() {
        return eliteWorkbench;
    }

    public void setEliteWorkbench(EliteWorkbench eliteWorkbench) {
        this.eliteWorkbench = eliteWorkbench;
    }

    public <T extends CustomRecipe> void setCustomRecipe(T customRecipe){
        recipes.put(customRecipe.getClass(), customRecipe);
    }

    /*
    Used when multiple Objects of the same sub type exist and shouldn't exist parallel inside the Map
     */
    public <T extends CustomRecipe> void setCustomRecipe(Class<T> tClass, T customRecipe){
        recipes.put(tClass, customRecipe);
    }

    public <T extends CustomRecipe> T getCustomRecipe(Class<T> recipeType){
        return (T) recipes.get(recipeType);
    }

    /***************************************************************
     * Util methods to get specific kinds of Recipes that are cached into this class
     * Used for the GUI Recipe Creators!
     *
     ***************************************************************/

    public CraftingRecipe getCraftRecipe() {
        if (getSetting().equals(Setting.ELITE_WORKBENCH))
            return getCustomRecipe(EliteCraftingRecipe.class);
        return getCustomRecipe(CraftingRecipe.class);
    }

    public CustomCookingRecipe<?> getCookingRecipe() {
        switch (getSetting()) {
            case CAMPFIRE:
                return getCampfireRecipe();
            case SMOKER:
                return getSmokerRecipe();
            case FURNACE:
                return getFurnaceRecipe();
            case BLAST_FURNACE:
                return getBlastRecipe();
        }
        return null;
    }

    public void resetCookingRecipe() {
        switch (getSetting()) {
            case CAMPFIRE:
                setCustomRecipe(new CustomCampfireRecipe());
            case SMOKER:
                setCustomRecipe(new CustomSmokerRecipe());
            case FURNACE:
                setCustomRecipe(new CustomFurnaceRecipe());
            case BLAST_FURNACE:
                setCustomRecipe(new CustomBlastRecipe());
        }
    }

    public void resetRecipe(){
        switch (getSetting()) {
            case CAMPFIRE:
            case SMOKER:
            case FURNACE:
            case BLAST_FURNACE:
                resetCookingRecipe();
                break;
            case ELITE_WORKBENCH:
                setCustomRecipe(EliteCraftingRecipe.class, new ShapedEliteCraftRecipe());
                break;
            case WORKBENCH:
                setCustomRecipe(CraftingRecipe.class, new ShapedCraftRecipe());
                break;
            case ANVIL:
                setCustomRecipe(new CustomAnvilRecipe());
                break;
            case STONECUTTER:
                setCustomRecipe(new CustomStonecutterRecipe());
                break;
            case CAULDRON:
                setCustomRecipe(new CauldronRecipe());
                break;
            case GRINDSTONE:
                setCustomRecipe(new GrindstoneRecipe());
                break;
            case BREWING_STAND:
                setCustomRecipe(new BrewingRecipe());
        }
    }

    public CustomRecipe getRecipe() {
        switch (getSetting()) {
            case CAMPFIRE:
            case SMOKER:
            case FURNACE:
            case BLAST_FURNACE:
                return getCookingRecipe();
            case ELITE_WORKBENCH:
                return getEliteCraftingRecipe();
            case WORKBENCH:
                return getCraftRecipe();
            case ANVIL:
                return getAnvilRecipe();
            case STONECUTTER:
                return getStonecutterRecipe();
            case CAULDRON:
                return getCauldronRecipe();
            case GRINDSTONE:
                return getGrindstoneRecipe();
            case BREWING_STAND:
                return getBrewingRecipe();
        }
        return null;
    }

    /***************************************************************
     * Getters and setters for all the Recipes that are saved in this cache.
     * Usage for the GUI Creator!
     *
     ***************************************************************/

    public CraftingRecipe getCraftingRecipe() {
        return getCustomRecipe(CraftingRecipe.class);
    }

    public CustomAnvilRecipe getAnvilRecipe() {
        return getCustomRecipe(CustomAnvilRecipe.class);
    }

    public EliteCraftingRecipe getEliteCraftingRecipe() {
        return getCustomRecipe(EliteCraftingRecipe.class);
    }

    public CustomBlastRecipe getBlastRecipe() {
        return getCustomRecipe(CustomBlastRecipe.class);
    }

    public CustomCampfireRecipe getCampfireRecipe() {
        return getCustomRecipe(CustomCampfireRecipe.class);
    }

    public CauldronRecipe getCauldronRecipe() {
        return getCustomRecipe(CauldronRecipe.class);
    }

    public CustomSmokerRecipe getSmokerRecipe() {
        return getCustomRecipe(CustomSmokerRecipe.class);
    }

    public CustomStonecutterRecipe getStonecutterRecipe() {
        return getCustomRecipe(CustomStonecutterRecipe.class);
    }

    public CustomFurnaceRecipe getFurnaceRecipe() {
        return getCustomRecipe(CustomFurnaceRecipe.class);
    }

    public GrindstoneRecipe getGrindstoneRecipe() {
        return getCustomRecipe(GrindstoneRecipe.class);
    }

    public BrewingRecipe getBrewingRecipe() {
        return getCustomRecipe(BrewingRecipe.class);
    }
}
