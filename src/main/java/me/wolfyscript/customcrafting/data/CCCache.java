/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.data;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.cache.BrewingGUICache;
import me.wolfyscript.customcrafting.data.cache.CacheCauldronWorkstation;
import me.wolfyscript.customcrafting.data.cache.CacheEliteCraftingTable;
import me.wolfyscript.customcrafting.data.cache.CacheRecipeView;
import me.wolfyscript.customcrafting.data.cache.ChatLists;
import me.wolfyscript.customcrafting.data.cache.ParticleCache;
import me.wolfyscript.customcrafting.data.cache.RecipeBookCache;
import me.wolfyscript.customcrafting.data.cache.RecipeBookEditor;
import me.wolfyscript.customcrafting.data.cache.RecipeList;
import me.wolfyscript.customcrafting.data.cache.items.ApplyItem;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.potions.ApplyPotionEffect;
import me.wolfyscript.customcrafting.data.cache.potions.PotionEffects;
import me.wolfyscript.customcrafting.data.cache.recipe_creator.RecipeCreatorCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import org.bukkit.potion.PotionEffect;

public class CCCache extends CustomCache {

    private final CustomCrafting customCrafting;

    private Setting setting;
    private String subSetting;

    private final RecipeBookEditor recipeBookEditor = new RecipeBookEditor();

    private final Items items = new Items();
    private final RecipeList recipeList = new RecipeList();

    private final PotionEffects potionEffectCache = new PotionEffects();
    private final RecipeBookCache recipeBookCache;
    private final CacheRecipeView cacheRecipeView = new CacheRecipeView();
    private CacheEliteCraftingTable cacheEliteCraftingTable = new CacheEliteCraftingTable();
    private final ChatLists chatLists = new ChatLists();
    private final ParticleCache particleCache = new ParticleCache();
    private final BrewingGUICache brewingGUICache = new BrewingGUICache();
    private final CacheCauldronWorkstation cauldronWorkstation = new CacheCauldronWorkstation();

    private ApplyItem applyItem;
    private ApplyPotionEffect applyPotionEffect;

    private final RecipeCreatorCache recipeCreatorCache;

    public CCCache() {
        super();
        this.customCrafting = CustomCrafting.inst(); //TODO: Dependency Injection
        this.setting = Setting.MAIN_MENU;
        this.subSetting = "";
        this.applyItem = null;

        this.recipeBookCache = new RecipeBookCache(customCrafting);

        this.recipeCreatorCache = new RecipeCreatorCache(customCrafting);
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

    public RecipeBookCache getRecipeBookCache() {
        return recipeBookCache;
    }

    @Deprecated
    public RecipeBookCache getKnowledgeBook() {
        return recipeBookCache;
    }

    public CacheRecipeView getCacheRecipeView() {
        return cacheRecipeView;
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

    public CacheEliteCraftingTable getEliteWorkbench() {
        return cacheEliteCraftingTable;
    }

    public void setEliteWorkbench(CacheEliteCraftingTable cacheEliteCraftingTable) {
        this.cacheEliteCraftingTable = cacheEliteCraftingTable;
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

    public CacheCauldronWorkstation getCauldronWorkstation() {
        return cauldronWorkstation;
    }
}
