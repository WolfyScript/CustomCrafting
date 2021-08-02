package me.wolfyscript.customcrafting.data;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.cache.*;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.potions.ApplyPotionEffect;
import me.wolfyscript.customcrafting.data.cache.potions.PotionEffects;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.*;
import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;
import me.wolfyscript.customcrafting.recipes.settings.EliteRecipeSettings;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class CCCache extends CustomCache {

    private Setting setting;

    //RECIPE_LIST OF ALL RECIPE SAVED IN CACHE
    private final HashMap<RecipeType<?>, ICustomRecipe<?>> recipes = new HashMap<>();

    private final CustomCrafting customCrafting;
    private String subSetting;

    private final RecipeBookEditor recipeBookEditor = new RecipeBookEditor();

    private final Items items = new Items();
    private final RecipeList recipeList = new RecipeList();

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
        super();
        this.customCrafting = CustomCrafting.inst();
        this.setting = Setting.MAIN_MENU;
        this.subSetting = "";
        this.applyItem = null;
        this.recipeType = Types.WORKBENCH;

        setCustomRecipe(new CustomRecipeAnvil());
        setCustomRecipe(Types.WORKBENCH, new CustomRecipeShaped());
        setCustomRecipe(Types.ELITE_WORKBENCH, new CustomRecipeShapedElite());
        setCustomRecipe(new CustomRecipeBlasting());
        setCustomRecipe(new CustomRecipeCampfire());
        setCustomRecipe(new CustomRecipeSmoking());
        setCustomRecipe(new CustomRecipeFurnace());
        setCustomRecipe(new CustomRecipeStonecutter());
        setCustomRecipe(new CustomRecipeGrindstone());
        setCustomRecipe(new CustomRecipeCauldron());
        setCustomRecipe(new CustomRecipeBrewing());
        setCustomRecipe(new CustomRecipeSmithing());
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

    public RecipeList getRecipeList() {
        return recipeList;
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
    public void setCustomRecipe(ICustomRecipe<?> customRecipe) {
        recipes.put(customRecipe.getRecipeType(), customRecipe);
    }

    public <T extends ICustomRecipe<?>> void setCustomRecipe(RecipeType<T> type, T customRecipe) {
        recipes.put(type, customRecipe);
    }

    public ICustomRecipe<?> getRecipe() {
        return getRecipe(getRecipeType());
    }

    public <T extends ICustomRecipe<?>> T getRecipe(RecipeType<T> recipeType) {
        return recipeType.getClazz().cast(recipes.get(recipeType));
    }


    /***************************************************************
     * Util methods to get specific kinds of Recipes that are cached into this class
     * Used for the GUI Recipe Creators!
     *
     ***************************************************************/
    public CustomRecipeCooking<?,?> getCookingRecipe() {
        if (recipeType instanceof RecipeType.CookingRecipeType) {
            return getRecipe((RecipeType.CookingRecipeType<?>) recipeType);
        }
        return null;
    }

    public void resetRecipe(){
        switch (getRecipeType().getType()) {
            case ELITE_WORKBENCH:
                setCustomRecipe(new CustomRecipeShapedElite());
                break;
            case WORKBENCH:
                setCustomRecipe(new CustomRecipeShaped());
                break;
            default:
                try {
                    setCustomRecipe(getRecipeType().getClazz().getDeclaredConstructor().newInstance());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
        }
    }

    public CraftingRecipe<?, ?> getCraftingRecipe() {
        return (CraftingRecipe<?, ?>) getRecipe(getRecipeType());
    }

    /***************************************************************
     * Getters and setters for all the Recipes that are saved in this cache.
     * Usage for the GUI Creator!
     *
     ***************************************************************/
    public CraftingRecipe<?, AdvancedRecipeSettings> getAdvancedCraftingRecipe() {
        return getRecipe(Types.WORKBENCH);
    }

    public CustomRecipeAnvil getAnvilRecipe() {
        return getRecipe(Types.ANVIL);
    }

    public CraftingRecipe<?, EliteRecipeSettings> getEliteCraftingRecipe() {
        return getRecipe(Types.ELITE_WORKBENCH);
    }

    public CustomRecipeCauldron getCauldronRecipe() {
        return getRecipe(Types.CAULDRON);
    }

    public CustomRecipeStonecutter getStonecutterRecipe() {
        return getRecipe(Types.STONECUTTER);
    }

    public CustomRecipeGrindstone getGrindstoneRecipe() {
        return getRecipe(Types.GRINDSTONE);
    }

    public CustomRecipeBrewing getBrewingRecipe() {
        return getRecipe(Types.BREWING_STAND);
    }

    public CustomRecipeSmithing getSmithingRecipe() {
        return getRecipe(Types.SMITHING);
    }
}
