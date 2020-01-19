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

import java.util.HashMap;
import java.util.UUID;

public class PlayerStatistics {

    private UUID uuid;
    private Setting setting;
    private String subSetting;

    private HashMap<String, Object> CACHE = new HashMap<>();

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

    public PlayerStatistics(UUID uuid) {
        this.uuid = uuid;
        this.setting = Setting.MAIN_MENU;
        this.subSetting = "";
        this.applyItem = null;
        setAmountCrafted(0);
        setAmountAdvancedCrafted(0);
        setAmountNormalCrafted(0);
    }

    public PlayerStatistics(UUID uuid, HashMap<String, Object> stats) {
        this(uuid);
        setStats(stats);
    }

    public UUID getUuid() {
        return uuid;
    }

    //Player Stats
    //Main Settings
    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    //Sub-Settings for GUIs
    public String getSubSetting() {
        return subSetting;
    }

    public void setSubSetting(String setting) {
        this.subSetting = setting;
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

    private Object getObjectOrDefault(String key, Object defaultValue) {
        return CACHE.getOrDefault(key, defaultValue);
    }

    private void setObject(String key, Object object) {
        CACHE.put(key, object);
    }

    public HashMap<String, Object> getStats() {
        return CACHE;
    }

    public void setStats(HashMap<String, Object> stats) {
        CACHE = stats;
    }

    public void setDarkMode(boolean darkMode) {
        CACHE.put("dark_mode", darkMode);
    }

    public boolean getDarkMode() {
        return (boolean) CACHE.getOrDefault("dark_mode", false);
    }

    public void addAmountCrafted(int amount) {
        setAmountCrafted(getAmountCrafted() + amount);
    }

    public void setAmountCrafted(int amount) {
        setObject("amount_crafted", amount);
    }

    public int getAmountCrafted() {
        return (int) getObjectOrDefault("amount_crafted", 0);
    }

    public void addAmountAdvancedCrafted(int amount) {
        setAmountAdvancedCrafted(getAmountAdvancedCrafted() + amount);
    }

    public void setAmountAdvancedCrafted(int amount) {
        setObject("amount_advanced_crafted", amount);
    }

    public int getAmountAdvancedCrafted() {
        return (int) getObjectOrDefault("amount_advanced_crafted", 0);
    }

    public void addAmountNormalCrafted(int amount) {
        setAmountNormalCrafted(getAmountNormalCrafted() + amount);
    }

    public void setAmountNormalCrafted(int amount) {
        setObject("amount_normal_crafted", amount);
    }

    public int getAmountNormalCrafted() {
        return (int) getObjectOrDefault("amount_normal_crafted", 0);
    }

    public HashMap<String, Integer> getRecipeCrafts() {
        return (HashMap<String, Integer>) getObjectOrDefault("recipe_crafts", new HashMap<String, Integer>());
    }

    public void addRecipeCrafts(String key) {
        setRecipeCrafts(key, getRecipeCrafts(key) + 1);
    }

    public void setRecipeCrafts(String key, int amount) {
        HashMap<String, Integer> recipeCrafts = getRecipeCrafts();
        recipeCrafts.put(key, amount);
        setObject("recipe_crafts", recipeCrafts);
    }

    public int getRecipeCrafts(String key) {
        HashMap<String, Integer> recipeCrafts = getRecipeCrafts();
        return recipeCrafts.getOrDefault(key, 0);
    }

    public ChatLists getChatLists() {
        return chatLists;
    }

    public AnvilConfig getAnvilConfig() {
        return anvilConfig;
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

    public EliteCraftConfig getEliteCraftConfig() {
        return eliteCraftConfig;
    }

    public FurnaceConfig getFurnaceConfig() {
        return furnaceConfig;
    }

    public void setFurnaceConfig(FurnaceConfig furnaceConfig) {
        this.furnaceConfig = furnaceConfig;
    }

    public BlastingConfig getBlastingConfig() {
        return blastingConfig;
    }

    public CampfireConfig getCampfireConfig() {
        return campfireConfig;
    }

    public CauldronConfig getCauldronConfig() {
        return cauldronConfig;
    }

    public SmokerConfig getSmokerConfig() {
        return smokerConfig;
    }

    public StonecutterConfig getStonecutterConfig() {
        return stonecutterConfig;
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
