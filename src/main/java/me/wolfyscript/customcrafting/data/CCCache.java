package me.wolfyscript.customcrafting.data;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.cache.*;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.potions.ApplyPotionEffect;
import me.wolfyscript.customcrafting.data.cache.potions.PotionEffects;
import me.wolfyscript.customcrafting.data.cache.recipe_creator.RecipeCreatorCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;

public class CCCache extends CustomCache {

    private Setting setting;

    //RECIPE_LIST OF ALL RECIPE SAVED IN CACHE
    private final Map<RecipeType<?>, ICustomRecipe<?>> recipes = new HashMap<>();

    private final CustomCrafting customCrafting;
    private String subSetting;

    private final RecipeBookEditor recipeBookEditor = new RecipeBookEditor();

    private final Items items = new Items();
    private final RecipeList recipeList = new RecipeList();

    private final PotionEffects potionEffectCache = new PotionEffects();
    private final RecipeBookCache recipeBookCache = new RecipeBookCache();
    private EliteWorkbench eliteWorkbench = new EliteWorkbench();
    private final ChatLists chatLists = new ChatLists();
    private final ParticleCache particleCache = new ParticleCache();
    private final BrewingGUICache brewingGUICache = new BrewingGUICache();

    private ApplyItem applyItem;
    private ApplyPotionEffect applyPotionEffect;

    private final RecipeCreatorCache recipeCreatorCache = new RecipeCreatorCache();

    public CCCache() {
        super();
        this.customCrafting = CustomCrafting.inst();
        this.setting = Setting.MAIN_MENU;
        this.subSetting = "";
        this.applyItem = null;
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

    public RecipeBookCache getKnowledgeBook() {
        return recipeBookCache;
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

    @Deprecated
    public TagSettingsCache getTagSettingsCache() {
        return getRecipeCreatorCache().getTagSettingsCache();
    }

    @Deprecated
    public ConditionsCache getConditionsCache() {
        return getRecipeCreatorCache().getConditionsCache();
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

    public RecipeCreatorCache getRecipeCreatorCache() {
        return recipeCreatorCache;
    }

    @Deprecated
    public ICustomRecipe<?> getRecipe() {
        return getRecipe(recipeCreatorCache.getRecipeType());
    }

    @Deprecated
    public <T extends ICustomRecipe<?>> T getRecipe(RecipeType<T> recipeType) {
        return recipeType.getClazz().cast(recipes.get(recipeType));
    }

    @Deprecated
    public void resetRecipe(){
        //TODO: Create new method to replace this one
    }

}
