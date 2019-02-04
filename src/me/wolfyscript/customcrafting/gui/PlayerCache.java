package me.wolfyscript.customcrafting.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerCache {

    private UUID uuid;
    private Setting setting;
    private String recipeListSetting = "";

    private List<ItemStack> cachedCraftIngredients;
    private ItemStack cachedResult = new ItemStack(Material.AIR);

    public PlayerCache(UUID uuid){
        this.uuid = uuid;
        this.setting = Setting.MAIN_MENU;
        this.cachedCraftIngredients = Arrays.asList(new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR));
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public void setRecipeListSetting(String recipeListSetting) {
        this.recipeListSetting = recipeListSetting;
    }

    public String getRecipeListSetting() {
        return recipeListSetting;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<ItemStack> getCachedCraftIngredients() {
        return cachedCraftIngredients;
    }

    public void setCachedCraftIngredients(List<ItemStack> cachedCraftIngredients) {
        this.cachedCraftIngredients = cachedCraftIngredients;
    }

    public ItemStack getCachedResult() {
        return cachedResult;
    }

    public void setCachedResult(ItemStack cachedResult) {
        this.cachedResult = cachedResult;
    }
}
