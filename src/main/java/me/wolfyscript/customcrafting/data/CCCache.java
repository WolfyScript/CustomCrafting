package me.wolfyscript.customcrafting.data;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.cache.*;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.potions.ApplyPotionEffect;
import me.wolfyscript.customcrafting.data.cache.potions.PotionEffects;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.customcrafting.recipes.types.campfire.CustomCampfireRecipe;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapedEliteCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.customcrafting.recipes.types.smithing.CustomSmithingRecipe;
import me.wolfyscript.customcrafting.recipes.types.smoker.CustomSmokerRecipe;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapedCraftRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class CCCache extends CustomCache {

    private Setting setting;

    //RECIPE_LIST OF ALL RECIPE SAVED IN CACHE
    private final HashMap<RecipeType<?>, ICustomRecipe<?,?>> recipes = new HashMap<>();

    private final CustomCrafting customCrafting;
    private String subSetting;

    private final RecipeBookEditor recipeBookEditor = new RecipeBookEditor();

    private final Items items = new Items();
    private final PotionEffects potionEffectCache = new PotionEffects();
    private final KnowledgeBook knowledgeBook = new KnowledgeBook();
    private EliteWorkbench eliteWorkbench = new EliteWorkbench();
    private final ChatLists chatLists = new ChatLists();
    private final ParticleCache particleCache = new ParticleCache();
    private final BrewingGUICache brewingGUICache = new BrewingGUICache();

    private final IngredientData ingredientData = new IngredientData();
    private final TagSettingsCache tagSettingsCache = new TagSettingsCache();

    private ApplyItem applyItem;
    private ApplyPotionEffect applyPotionEffect;

    private RecipeType<?> recipeType;

    public CCCache() {
        this.customCrafting = CustomCrafting.inst();
        this.setting = Setting.MAIN_MENU;
        this.subSetting = "";
        this.applyItem = null;
        this.recipeType = Types.WORKBENCH;

        setCustomRecipe(new CustomAnvilRecipe());
        setCustomRecipe(new ShapedCraftRecipe());
        setCustomRecipe(new ShapedEliteCraftRecipe());
        setCustomRecipe(new CustomBlastRecipe());
        setCustomRecipe(new CustomCampfireRecipe());
        setCustomRecipe(new CustomSmokerRecipe());
        setCustomRecipe(new CustomFurnaceRecipe());
        setCustomRecipe(new CustomStonecutterRecipe());
        setCustomRecipe(new GrindstoneRecipe());
        setCustomRecipe(new CauldronRecipe());
        setCustomRecipe(new BrewingRecipe());
        setCustomRecipe(new CustomSmithingRecipe());
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public RecipeType<?> getRecipeType() {
        return recipeType;
    }

    public void setRecipeType(RecipeType<?> recipeType) {
        this.recipeType = recipeType;
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

    public KnowledgeBook getKnowledgeBook() {
        return knowledgeBook;
    }

    public Items getItems() {
        return items;
    }

    public PotionEffects getPotionEffectCache() {
        return potionEffectCache;
    }

    public IngredientData getIngredientData() {
        return ingredientData;
    }

    public TagSettingsCache getTagSettingsCache() {
        return tagSettingsCache;
    }

    public void setApplyPotionEffect(ApplyPotionEffect applyPotionEffect) {
        this.applyPotionEffect = applyPotionEffect;
    }

    public void applyPotionEffect(PotionEffect potionEffect) {
        if (applyPotionEffect != null) {
            applyPotionEffect.applyPotionEffect(getPotionEffectCache(), this, potionEffect);
            applyPotionEffect = null;
        }
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

    public BrewingGUICache getBrewingGUICache() {
        return brewingGUICache;
    }

    public RecipeBookEditor getRecipeBookEditor() {
        return recipeBookEditor;
    }

    //Recipes
    public void setCustomRecipe(ICustomRecipe<?,?> customRecipe) {
        recipes.put(customRecipe.getRecipeType(), customRecipe);
    }

    public ICustomRecipe<?,?> getRecipe() {
        return getRecipe(getRecipeType());
    }

    public <T extends ICustomRecipe<?,?>> T getRecipe(RecipeType<T> recipeType) {
        return recipeType.getClazz().cast(recipes.get(recipeType));
    }


    /***************************************************************
     * Util methods to get specific kinds of Recipes that are cached into this class
     * Used for the GUI Recipe Creators!
     *
     ***************************************************************/
    public CustomCookingRecipe<?,?> getCookingRecipe() {
        if (recipeType instanceof RecipeType.CookingRecipeType) {
            return getRecipe((RecipeType.CookingRecipeType<?>) recipeType);
        }
        return null;
    }

    public void resetRecipe(){
        switch (getRecipeType().getType()) {
            case ELITE_WORKBENCH:
                setCustomRecipe(new ShapedEliteCraftRecipe());
                break;
            case WORKBENCH:
                setCustomRecipe(new ShapedCraftRecipe());
                break;
            case CAMPFIRE:
            case SMOKER:
            case FURNACE:
            case BLAST_FURNACE:
            case ANVIL:
            case STONECUTTER:
            case CAULDRON:
            case GRINDSTONE:
            case BREWING_STAND:
            case SMITHING:
                try {
                    setCustomRecipe(getRecipeType().getClazz().getDeclaredConstructor().newInstance());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
        }
    }

    public CraftingRecipe<?> getCraftingRecipe() {
        return (CraftingRecipe<?>) getRecipe(getRecipeType());
    }

    /***************************************************************
     * Getters and setters for all the Recipes that are saved in this cache.
     * Usage for the GUI Creator!
     *
     ***************************************************************/
    public AdvancedCraftingRecipe getAdvancedCraftingRecipe() {
        return getRecipe(Types.WORKBENCH);
    }

    public CustomAnvilRecipe getAnvilRecipe() {
        return getRecipe(Types.ANVIL);
    }

    public EliteCraftingRecipe getEliteCraftingRecipe() {
        return getRecipe(Types.ELITE_WORKBENCH);
    }

    public CustomBlastRecipe getBlastRecipe() {
        return getRecipe(Types.BLAST_FURNACE);
    }

    public CustomCampfireRecipe getCampfireRecipe() {
        return getRecipe(Types.CAMPFIRE);
    }

    public CauldronRecipe getCauldronRecipe() {
        return getRecipe(Types.CAULDRON);
    }

    public CustomSmokerRecipe getSmokerRecipe() {
        return getRecipe(Types.SMOKER);
    }

    public CustomStonecutterRecipe getStonecutterRecipe() {
        return getRecipe(Types.STONECUTTER);
    }

    public CustomFurnaceRecipe getFurnaceRecipe() {
        return getRecipe(Types.FURNACE);
    }

    public GrindstoneRecipe getGrindstoneRecipe() {
        return getRecipe(Types.GRINDSTONE);
    }

    public BrewingRecipe getBrewingRecipe() {
        return getRecipe(Types.BREWING_STAND);
    }

    public CustomSmithingRecipe getSmithingRecipe() {
        return getRecipe(Types.SMITHING);
    }
}
