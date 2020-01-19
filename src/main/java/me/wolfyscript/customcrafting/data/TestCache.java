package me.wolfyscript.customcrafting.data;


import me.wolfyscript.customcrafting.data.cache.ChatLists;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.data.cache.VariantsData;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.types.CookingConfig;
import me.wolfyscript.customcrafting.recipes.types.CraftConfig;
import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.customcrafting.recipes.types.anvil.AnvilConfig;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.BlastingConfig;
import me.wolfyscript.customcrafting.recipes.types.campfire.CampfireConfig;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronConfig;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftConfig;
import me.wolfyscript.customcrafting.recipes.types.furnace.FurnaceConfig;
import me.wolfyscript.customcrafting.recipes.types.smoker.SmokerConfig;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.StonecutterConfig;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftConfig;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.cache.CustomCache;

public class TestCache extends CustomCache {

    private Setting setting;
    private String subSetting;

    private Items items = new Items();

    private KnowledgeBook knowledgeBook = new KnowledgeBook();

    private VariantsData variantsData = new VariantsData();

    private EliteWorkbench eliteWorkbench = new EliteWorkbench();

    private ChatLists chatLists = new ChatLists();

    private ApplyItem applyItem;

    //RECIPE_LIST OF ALL RECIPE CACHE

    private AnvilConfig anvilConfig = new AnvilConfig();
    private AdvancedCraftConfig advancedCraftConfig = new AdvancedCraftConfig();
    private EliteCraftConfig eliteCraftConfig = new EliteCraftConfig();
    private BlastingConfig blastingConfig = new BlastingConfig();
    private CampfireConfig campfireConfig = new CampfireConfig();
    private CauldronConfig cauldronConfig = new CauldronConfig();
    private SmokerConfig smokerConfig = new SmokerConfig();
    private StonecutterConfig stonecutterConfig = new StonecutterConfig();
    private FurnaceConfig furnaceConfig = new FurnaceConfig();

    public TestCache(){
        this.setting = Setting.MAIN_MENU;

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

    public AnvilConfig getAnvilConfig() {
        return anvilConfig;
    }

    public void resetAnvilConfig() {
        this.anvilConfig = new AnvilConfig();
    }

    public CraftConfig getCraftConfig() {
        if (getSetting().equals(Setting.ELITE_WORKBENCH))
            return getEliteCraftConfig();
        return getAdvancedCraftConfig();
    }

    public CookingConfig getCookingConfig() {
        switch (getSetting()) {
            case CAMPFIRE:
                return getCampfireConfig();
            case SMOKER:
                return getSmokerConfig();
            case FURNACE:
                return getFurnaceConfig();
            case BLAST_FURNACE:
                return getBlastingConfig();
        }
        return null;
    }

    public AdvancedCraftConfig getAdvancedCraftConfig() {
        return advancedCraftConfig;
    }

    public void resetAdvancedCraftConfig() {
        this.advancedCraftConfig = new AdvancedCraftConfig();
    }

    public EliteCraftConfig getEliteCraftConfig() {
        return eliteCraftConfig;
    }

    public void resetEliteCraftConfig() {
        this.eliteCraftConfig = new EliteCraftConfig();
    }

    public FurnaceConfig getFurnaceConfig() {
        return furnaceConfig;
    }

    public void setFurnaceConfig(FurnaceConfig furnaceConfig) {
        this.furnaceConfig = furnaceConfig;
    }

    public void resetFurnaceConfig() {
        this.furnaceConfig = new FurnaceConfig();
    }

    public BlastingConfig getBlastingConfig() {
        return blastingConfig;
    }

    public void resetBlastingConfig() {
        this.blastingConfig = new BlastingConfig();
    }

    public CampfireConfig getCampfireConfig() {
        return campfireConfig;
    }

    public void resetCampfireConfig() {
        this.campfireConfig = new CampfireConfig();
    }

    public CauldronConfig getCauldronConfig() {
        return cauldronConfig;
    }

    public void resetCauldronConfig() {
        this.cauldronConfig = new CauldronConfig();
    }

    public SmokerConfig getSmokerConfig() {
        return smokerConfig;
    }

    public void resetSmokerConfig() {
        this.smokerConfig = new SmokerConfig();
    }

    public StonecutterConfig getStonecutterConfig() {
        return stonecutterConfig;
    }

    public void resetStonecutterConfig() {
        this.stonecutterConfig = new StonecutterConfig();
    }

    public void setAnvilConfig(AnvilConfig anvilConfig) {
        this.anvilConfig = anvilConfig;
    }

    public void setAdvancedCraftConfig(AdvancedCraftConfig advancedCraftConfig) {
        this.advancedCraftConfig = advancedCraftConfig;
    }

    public void setEliteCraftConfig(EliteCraftConfig eliteCraftConfig) {
        this.eliteCraftConfig = eliteCraftConfig;
    }

    public void setBlastingConfig(BlastingConfig blastingConfig) {
        this.blastingConfig = blastingConfig;
    }

    public void setCampfireConfig(CampfireConfig campfireConfig) {
        this.campfireConfig = campfireConfig;
    }

    public void setCauldronConfig(CauldronConfig cauldronConfig) {
        this.cauldronConfig = cauldronConfig;
    }

    public void setSmokerConfig(SmokerConfig smokerConfig) {
        this.smokerConfig = smokerConfig;
    }

    public void setStonecutterConfig(StonecutterConfig stonecutterConfig) {
        this.stonecutterConfig = stonecutterConfig;
    }

    public void setCookingConfig(CookingConfig cookingConfig) {
        if (cookingConfig instanceof CampfireConfig) {
            setCampfireConfig((CampfireConfig) cookingConfig);
        } else if (cookingConfig instanceof SmokerConfig) {
            setSmokerConfig((SmokerConfig) cookingConfig);
        } else if (cookingConfig instanceof FurnaceConfig) {
            setFurnaceConfig((FurnaceConfig) cookingConfig);
        } else if (cookingConfig instanceof BlastingConfig) {
            setBlastingConfig((BlastingConfig) cookingConfig);
        }
    }

    public RecipeConfig getRecipeConfig() {
        switch (getSetting()) {
            case CAMPFIRE:
            case SMOKER:
            case FURNACE:
            case BLAST_FURNACE:
                return getCookingConfig();
            case ELITE_WORKBENCH:
            case WORKBENCH:
                return getCraftConfig();
            case ANVIL:
                return getAnvilConfig();
            case STONECUTTER:
                return getStonecutterConfig();
            case CAULDRON:
                return getCauldronConfig();
        }
        return null;
    }

    public EliteWorkbench getEliteWorkbench() {
        return eliteWorkbench;
    }

    public void setEliteWorkbench(EliteWorkbench eliteWorkbench) {
        this.eliteWorkbench = eliteWorkbench;
    }

}
