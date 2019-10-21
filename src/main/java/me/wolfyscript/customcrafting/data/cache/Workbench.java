package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.*;

public class Workbench extends RecipeData {

    private static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    private Map<Character, List<CustomItem>> ingredients;
    private List<CustomItem> result;
    private String extend;
    private List<String> overrides;
    private boolean shapeless;
    private boolean saved;
    private String id;
    private int resultCustomAmount;
    private HashMap<Integer, Integer> ingredientsCustomAmount;

    public Workbench() {
        super();
        this.ingredients = new TreeMap<>();
        this.result = new ArrayList<>(Collections.singletonList(new CustomItem(Material.AIR)));
        this.extend = "";
        this.overrides = new ArrayList<>();
        this.shapeless = false;
        this.saved = false;
        this.id = "";
        this.resultCustomAmount = 1;
        this.ingredientsCustomAmount = new HashMap<>();
    }

    public void initIngredients(int gridSize){
        ArrayList<ItemStack> list = new ArrayList<>();
        for(int i = 0; i < (gridSize*gridSize); i++){
            list.add(new ItemStack(Material.AIR));
        }
        setIngredients(list);
    }

    public Map<Character, List<CustomItem>> getIngredients() {
        return ingredients;
    }

    public void setIngredients(HashMap<Character, List<CustomItem>> ingredients) {
        this.ingredients = ingredients;
    }

    public List<CustomItem> getIngredients(char key) {
        return getIngredients().getOrDefault(key, new ArrayList<>(Collections.singleton(new CustomItem(Material.AIR))));
    }

    public List<CustomItem> getIngredients(int slot) {
        return getIngredients(LETTERS[slot]);
    }

    public CustomItem getIngredient(char key) {
        if (getIngredients(key).size() > 0) {
            return getIngredients(key).get(0);
        }
        return null;
    }

    public CustomItem getIngredient(int slot) {
        return getIngredient(LETTERS[slot]);
    }

    public void setIngredients(List<ItemStack> ingredients) {
        for (int i = 0; i < ingredients.size(); i++) {
            CustomItem customItem = CustomItem.getByItemStack(ingredients.get(i));
            if (customItem.getType().equals(Material.AIR)) {
                setIngredients(i, new ArrayList<>(Collections.singleton(customItem)));
            } else {
                setIngredient(i, customItem);
            }
        }
    }

    public void setIngredient(char key, CustomItem itemStack) {
        setIngredient(key, 0, itemStack);
    }

    public void setIngredient(int slot, int variant, CustomItem customItem) {
        setIngredient(LETTERS[slot], variant, customItem);
    }

    public void setIngredient(char key, int variant, CustomItem itemStack) {
        List<CustomItem> ingredient = getIngredients(key);
        if (variant < ingredient.size())
            ingredient.set(variant, itemStack);
        else
            ingredient.add(itemStack);
        getIngredients().put(key, ingredient);
    }

    public void setIngredients(char key, List<CustomItem> ingredients) {
        getIngredients().put(key, ingredients);
    }

    public void setIngredients(int slot, List<CustomItem> ingredients) {
        setIngredients(LETTERS[slot], ingredients);
    }

    public void addIngredient(char key, CustomItem itemStack) {
        List<CustomItem> ingredient = getIngredients(key);
        ingredient.add(itemStack);
        getIngredients().put(key, ingredient);
    }

    public void addIngredient(int slot, CustomItem itemStack) {
        addIngredient(LETTERS[slot], itemStack);
    }

    public void setIngredient(int slot, CustomItem itemStack) {
        setIngredient(LETTERS[slot], itemStack);
    }

    public void setResult(int variant, CustomItem result) {
        if (variant < getResult().size())
            getResult().set(variant, result);
        else
            getResult().add(result);
    }

    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    public List<CustomItem> getResult() {
        return result;
    }

    public void setShapeless(boolean shapeless) {
        this.shapeless = shapeless;
    }

    public boolean isShapeless() {
        return shapeless;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setOverride(String extend) {
        this.extend = extend;
    }

    public String getOverride() {
        return extend;
    }

    public void setOverrides(List<String> overrides) {
        this.overrides = overrides;
    }

    public List<String> getOverrides() {
        return overrides;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getResultCustomAmount() {
        return resultCustomAmount;
    }

    public void setResultCustomAmount(int resultCustomAmount) {
        this.resultCustomAmount = resultCustomAmount;
    }

    public HashMap<Integer, Integer> getIngredientsCustomAmount() {
        return ingredientsCustomAmount;
    }

    public void setIngredientsCustomAmount(HashMap<Integer, Integer> ingredientsCustomAmount) {
        this.ingredientsCustomAmount = ingredientsCustomAmount;
    }
}
