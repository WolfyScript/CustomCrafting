package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.items.ItemUtils;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class KnowledgeBook {

    private int page;
    private Setting setting;
    private String recipeID;

    private static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private HashMap<Character, ArrayList<CustomItem>> ingredients;

    public KnowledgeBook(){
        this.page = 0;
        this.setting = Setting.MAIN_MENU;
        this.recipeID = "";
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

    public String getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }

    public HashMap<Character, ArrayList<CustomItem>> getIngredients() {
        return ingredients;
    }

    public void setIngredients(HashMap<Character, ArrayList<CustomItem>> ingredients) {
        this.ingredients = ingredients;
    }

    public List<CustomItem> getIngredients(int slot){
        return getIngredients().getOrDefault(LETTERS[slot], new ArrayList<>());
    }

    public CustomItem getIngredient(int slot){
        List<CustomItem> list = getIngredients(slot);
        return list.size() > 0 ? list.get(0) : null;
    }
}
